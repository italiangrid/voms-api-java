package org.italiangrid.voms.ac.impl;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This {@link ValidationResultListener} logs the outcome of validation results.
 * Succesful validations are logged in debug, while validator failures are logged as
 * warnings.
 * @author andreaceccanti
 *
 */
public class LoggingValidationResultListener implements ValidationResultListener {

	public static final Logger log = LoggerFactory.getLogger(LoggingValidationResultListener.class);
	
	public void notifyValidationResult(VOMSValidationResult result, VOMSAttribute attributes) {
		if (result.isValid()){
			log.debug("{} {}", result, attributes);
		}else{
			log.warn("{} {}", result, attributes);
		}
	}

}
