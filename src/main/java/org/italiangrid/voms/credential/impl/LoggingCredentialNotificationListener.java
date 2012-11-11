package org.italiangrid.voms.credential.impl;

import org.apache.commons.lang.StringUtils;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link LoadCredentialsEventListener} that logs load credential events.
 * 
 * @author andreaceccanti
 *
 */
public class LoggingCredentialNotificationListener implements
		LoadCredentialsEventListener {

	public static final Logger log = LoggerFactory.getLogger(LoggingCredentialNotificationListener.class);
	
	private static final String CRED_SEPARATOR = ",";


	public void notifyCredentialLookup(String... locations) {
		log.debug("Looking for user credentials in ({})...", StringUtils.join(locations, CRED_SEPARATOR));
	}

	public void notifyLoadCredentialSuccess(String... locations) {
		log.info("Credentials loaded succesfully ({})", StringUtils.join(locations,CRED_SEPARATOR));
	}

	public void notifyLoadCredentialFailure(Throwable error, String... locations) {
		log.warn("Error loading credentials ({}). Reason: {}", StringUtils.join(locations, CRED_SEPARATOR),error.getMessage());
		
		if (log.isDebugEnabled())
			log.warn("Error loading credentials ({}). Reason: {}", StringUtils.join(locations, CRED_SEPARATOR),error.getMessage(), error);
		
	}
}
