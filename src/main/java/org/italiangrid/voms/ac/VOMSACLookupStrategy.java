package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * 
 * A strategy for looking up a set of VOMS Attribute Certificates from a certificate chain.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACLookupStrategy {
	
	/**
	 * 
	 * @return
	 */
	public List<ACParsingContext> lookupVOMSAttributeCertificates(X509Certificate[] certChain);

}
