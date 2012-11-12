package org.italiangrid.voms.ac;

import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface is used to notify interested listeners of VOMS attribute certificate validation outcome.
 * @author andreaceccanti
 *
 */
public interface ValidationResultListener {

	/**
	 * Informs of the result of the validation of a set of {@link VOMSAttribute}.
	 * 
	 * @param result the validation result
	 * @param attributes the validated attributes
	 */
	public void notifyValidationResult(VOMSValidationResult result, VOMSAttribute attributes);
}
