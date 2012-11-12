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
	 * This method defines how a set of VOMS Attribute Certificates is looked for in a certificate
	 * chain.
	 * 
	 * @return a {@link List} of {@link ACParsingContext} that describes the lookup outcome
	 */
	public List<ACParsingContext> lookupVOMSAttributeCertificates(X509Certificate[] certChain);

}
