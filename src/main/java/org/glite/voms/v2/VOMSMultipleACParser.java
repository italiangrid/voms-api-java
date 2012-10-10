package org.glite.voms.v2;

import java.security.cert.X509Certificate;
import java.util.List;

@Deprecated
public interface VOMSMultipleACParser extends VOMSACParser{

	/**
	 * Looks for and parses VOMS attributes in the certificate chain passed as argument (which is assumed to be already validated).
	 * This method provides parsing for all the Attribute Certificate (AC) contained in a given VOMS extension. This functionality
	 * confuses the VOMS authorization model and is provided only for backwards compatibility. It will be removed in the next major
	 * version of the APIs.
	 * 
	 * @param validatedChain a validated X.509 certificate chain
	 * @returna {@link VOMSAttributes} object providing access to the parsed VOMS attributes for all Attribute Certificates contained in 
	 * the VOMS extension
	 * 
	 * @deprecated
	 */
	public List<VOMSAttributes> parseAll(X509Certificate[] validatedChain);
	
	/**
	 * Looks for and parses VOMS attributes in the certificate chain set by a former call to the {@link #setCertificateChain(X509Certificate[])}
	 * method.
	 * This method provides parsing for all the Attribute Certificate (AC) contained in a given VOMS extension. This functionality
	 * confuses the VOMS authorization model and is provided only for backwards compatibility. It will be removed in the next major
	 * version of the APIs.
	 * 
	 * @return {@link VOMSAttributes} object providing access to the parsed VOMS attributes for all Attribute Certificates contained in 
	 * the VOMS extension
	 * 
	 * @deprecated
	 */
	public List<VOMSAttributes> parseAll();
	
}
