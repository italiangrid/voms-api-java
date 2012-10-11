package org.glite.voms.v2.ac;

import org.bouncycastle.asn1.x509.AttributeCertificate;

/**
 * The strategy implemented to perform the validation of a VOMS attribute certificate.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSACValidationStrategy {

	/**
	 * Validates a VOMS Attribute Certificate
	 * @param ac a VOMS Attribute Certificate
	 * @return a {@link VOMSValidationResult} object describing the outcome of the validation
	 */
	public VOMSValidationResult validateAC(AttributeCertificate ac);
}
