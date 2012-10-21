package org.italiangrid.voms.ac.impl;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.asn1.VOMSConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyUtils;

/**
 * This strategy returns the leaf VOMS Attribute Certificate in a certificate chain, i.e.
 * the Attribute Certificate found in the latest delegation in the chain. 
 * 
 * @author Andrea Ceccanti
 *
 */
public class LeafACLookupStrategy implements VOMSACLookupStrategy, VOMSConstants {

	public static final Logger log = LoggerFactory.getLogger(LeafACLookupStrategy.class);
	
	public List<ACParsingContext> lookupVOMSAttributeCertificates(
			X509Certificate[] certChain) {
		
		List<ACParsingContext> parsedACs = new ArrayList<ACParsingContext>();
		
		if (certChain == null || certChain.length == 0)
			throw new VOMSError("Cannot extract VOMS Attribute Certificates from a null or empty certificate chain!");
		
		for (int index = 0; index < certChain.length; index++){
		
			X509Certificate cert  = certChain[index];
			String readableSubject = X500NameUtils.getReadableForm(cert.getSubjectX500Principal());
			
			log.debug("Looking for VOMS AC at certificate chain position {} of {}: {}",
					new Object[]{index,	certChain.length, readableSubject});
			
			try{
				
				if (ProxyUtils.isProxy(cert)){
			
					List<AttributeCertificate> vomsACs = VOMSACUtils.getACsFromCertificate(cert);
				
					// Break at the first AC found from the top of the chain
					if (!vomsACs.isEmpty()){
						
						log.debug("Found VOMS AC at certificate chain position {} of {}: {}", 
								new Object[]{index,	certChain.length, readableSubject});
						
						ACParsingContext ctx = new ACParsingContext(vomsACs, index, certChain);
						parsedACs.add(ctx);
						break;
					}
				}
				
			}catch (IOException e){
				
				log.error("Error extracting VOMS attribute certificates from certificate '{}': {}", 
					new Object[]{readableSubject, e.getMessage()});
			}
		}
		
		return parsedACs;
	}
}
