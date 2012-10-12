package org.glite.voms.v2.ac;

import org.glite.voms.v2.VOMSAttributes;


public interface ValidationResultListener {

	public void notifyValidationResult(VOMSValidationResult result, VOMSAttributes attributes);
}
