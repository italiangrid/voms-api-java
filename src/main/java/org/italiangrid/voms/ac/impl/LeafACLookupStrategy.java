package org.italiangrid.voms.ac.impl;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.ACLookupListener;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.asn1.VOMSConstants;
import org.italiangrid.voms.util.LoggingListener;

import eu.emi.security.authn.x509.proxy.ProxyUtils;

/**
 * This strategy returns the leaf VOMS Attribute Certificate in a certificate chain, i.e.
 * the Attribute Certificate found in the latest delegation in the chain. 
 * 
 * @author Andrea Ceccanti
 *
 */
public class LeafACLookupStrategy implements VOMSACLookupStrategy, VOMSConstants {

	private ACLookupListener listener;
	
	public LeafACLookupStrategy(ACLookupListener l) {
		this.listener = l;
	}
	
	public LeafACLookupStrategy(){
		this(new LoggingListener());
	}
	
	
	public List<ACParsingContext> lookupVOMSAttributeCertificates(
			X509Certificate[] certChain) {
		
		List<ACParsingContext> parsedACs = new ArrayList<ACParsingContext>();
		
		if (certChain == null || certChain.length == 0)
			throw new VOMSError("Cannot extract VOMS Attribute Certificates from a null or empty certificate chain!");
		
		for (int index = 0; index < certChain.length; index++){
		
			X509Certificate cert  = certChain[index];
		
			listener.notifyACLookupEvent(certChain, index);
			
			try{
				
				if (ProxyUtils.isProxy(cert)){
			
					List<AttributeCertificate> vomsACs = VOMSACUtils.getACsFromCertificate(cert);
				
					// Break at the first AC found from the top of the chain
					if (!vomsACs.isEmpty()){
						
						listener.notifyACParseEvent(certChain, index);
						
						ACParsingContext ctx = new ACParsingContext(vomsACs, index, certChain);
						parsedACs.add(ctx);
						break;
					}
				}
				
			}catch (IOException e){
				throw new VOMSError(e.getMessage(),e);
			}
		}
		
		return parsedACs;
	}
}
