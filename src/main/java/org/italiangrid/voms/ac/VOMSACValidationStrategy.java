package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;

import org.italiangrid.voms.VOMSAttribute;

/**
 * The strategy implemented to perform the validation of a VOMS attribute certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACValidationStrategy {

	/**
	 * Validates a VOMS Attribute Certificate
	 * @param ac a  parsed VOMS Attribute Certificate
	 * @param theChain the certificate chain from which the AC was parsed
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(VOMSAttribute attributes, X509Certificate[] theChain);
	
	
	/**
	 * Validates attribute certificates not extracted from a certificate chain (e.g., as returned
	 * from the VOMS server)
	 * 
	 * @param acs a list of VOMS acs
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(VOMSAttribute attributes);
}
