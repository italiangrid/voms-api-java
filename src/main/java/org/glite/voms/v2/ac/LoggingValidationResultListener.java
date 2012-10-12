package org.glite.voms.v2.ac;

import org.glite.voms.v2.VOMSAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingValidationResultListener implements ValidationResultListener {

	public static final Logger log = LoggerFactory.getLogger(LoggingValidationResultListener.class);
	
	public void notifyValidationResult(VOMSValidationResult result, VOMSAttributes attributes) {
		
		log.info(result.toString());
		log.info(attributes.toString());
	}

}
