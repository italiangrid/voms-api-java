package org.italiangrid.voms.ac;

import org.italiangrid.voms.VOMSAttribute;


public interface ValidationResultListener {

	public void notifyValidationResult(VOMSValidationResult result, VOMSAttribute attributes);
}
