package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;

import org.glite.voms.v2.VOMSAttributes;

/**
 * The strategy implemented to perform the validation of a VOMS attribute certificate.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSACValidationStrategy {

	/**
	 * Validates a VOMS Attribute Certificate
	 * @param ac a  parsed VOMS Attribute Certificate
	 * @param theChain the certificate chain from which the AC was parsed
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(VOMSAttributes attributes, X509Certificate[] theChain);
}
