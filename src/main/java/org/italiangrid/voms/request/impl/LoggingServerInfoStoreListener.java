package org.italiangrid.voms.request.impl;

import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingServerInfoStoreListener implements
		VOMSServerInfoStoreListener {

	public static final Logger log = LoggerFactory.getLogger(LoggingServerInfoStoreListener.class);
	

	public void serverInfoLoaded(String vomsesPath, VOMSServerInfo info) {
		
		if (vomsesPath == null)
			log.debug("Loaded %s", info);
		else
			log.debug("Loaded %s from %s", info, vomsesPath);
	}

}
