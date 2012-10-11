package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;

/**
 * 
 * A strategy for looking up a set of VOMS Attribute Certificates from a certificate chain.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSACLookupStrategy {
	
	/**
	 * 
	 * @return
	 */
	public List<AttributeCertificate> lookupVOMSAttributeCertificates(X509Certificate[] certChain);

}
