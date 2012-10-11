package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.glite.voms.v2.VOMSAttributes;

@Deprecated
public interface VOMSMultipleACValidator extends VOMSACValidator{

	/**
	 * Parses and validates the VOMS attributes found in the certificate chain passed 
	 * as argument (which is assumed to be validated already).
	 * 
	 * This method provides validation for all the Attribute Certificate (AC) contained in a given VOMS extension. This functionality
	 * confuses the VOMS authorization model and is provided only for backwards compatibility. It will be removed in the next major
	 * version of the APIs.
	 * 
	 * @return a {@link VOMSAttributes} object providing access to the validated VOMS attributes for all Attribute Certificates contained in 
	 * the VOMS extension
	 * 
	 * @deprecated
	 */
	public List<VOMSAttributes> validateAll();
	
	/**
	 * Parses and validates VOMS attributes in the certificate chain passed as argument.
	 * 
	 * This method provides validation for all the Attribute Certificate (AC) contained in a given VOMS extension. This functionality
	 * confuses the VOMS authorization model and is provided only for backwards compatibility. It will be removed in the next major
	 * version of the APIs.
	 * 
	 * @param validatedChain a validated X.509 certificate chain
	 * @return a {@link VOMSAttributes} object providing access to the parsed VOMS attributes for all Attribute Certificates contained in 
	 * the VOMS extension
	 * 
	 * @deprecated
	 */
	public List<VOMSAttributes> validateAll(X509Certificate[] validatedChain);
}
