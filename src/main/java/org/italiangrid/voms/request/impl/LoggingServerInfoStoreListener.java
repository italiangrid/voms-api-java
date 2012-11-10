package org.italiangrid.voms.request.impl;

import java.util.List;

import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingServerInfoStoreListener implements
		VOMSServerInfoStoreListener {

	public static final Logger log = LoggerFactory.getLogger(LoggingServerInfoStoreListener.class);
	

	public void serverInfoLoaded(String vomsesPath, VOMSServerInfo info) {
		
		if (vomsesPath == null)
			log.debug("Loaded {}", info);
		else
			log.debug("Loaded {} from {}", info, vomsesPath);
	}


	public void lookupNotification(String vomsesPath) {
		log.debug("Looking for VOMSES information in {}", vomsesPath);
		
	}

	public void noValidVomsesNotification(List<String> searchedPaths) {
		log.debug("No valid VOMSES information found on the local machine while looking in: "+searchedPaths);
		
	}

}
