package org.italiangrid.voms.store.impl;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Set;

import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;
import org.italiangrid.voms.store.VOMSTrustStoreUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.helpers.trust.OpensslTrustAnchorStore;
import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * A class  which logs the notifications about a {@link VOMSTrustStore} status changes and updates.
 * 
 * The messages are logged in debug.
 * 
 * @author Andrea Ceccanti
 *
 */
public class TrustStoreUpdatesLogger implements VOMSTrustStoreUpdateListener, VOMSTrustStoreStatusListener {

	public static final Logger log = LoggerFactory.getLogger(TrustStoreUpdatesLogger.class);
	
	/** A boolean flags that determines if the trust store contents should be logged **/
	final private boolean logTrustStoreContents;
	
	/**
	 * Default constructor. 
	 * @param verbose if <code>true</code> this {@link TrustStoreUpdatesLogger} will log the contents of
	 * the {@link VOMSTrustStore}.
	 */
	public TrustStoreUpdatesLogger(boolean verbose) {
		this.logTrustStoreContents = verbose;
	}

	
	public void notifyTrustStoreUpdate(VOMSTrustStore store) {
	
		log.debug("VOMS trust store {} has been updated.", store);
		if (logTrustStoreContents){
			log.debug("Trust directories: {}", store.getLocalTrustedDirectories());
						
			for (X509Certificate cert: store.getLocalAACertificates())
				log.debug("Trusted local VOMS cert: {}",X500NameUtils.getReadableForm(cert.getSubjectX500Principal()));
			
			Map<String, Set<LSCInfo>> lscInfo = store.getAllLSCInfo();
			
			for (String key: lscInfo.keySet())
				log.debug("LSC info for VO {}: {}", key, lscInfo.get(key));
		}
	}


	public void notifyCertficateLookupEvent(String dir) {
		
		log.debug("Looking for VOMS AA certificates in directory: {}", dir);
	}


	public void notifyLSCLookupEvent(String dir) {
		
		log.debug("Looking for VOMS LSC files in directory: {}", dir);

	}


	public void notifyCertificateLoadEvent(X509Certificate cert, File f) {
		String readableSubject = X500NameUtils.getReadableForm(cert.getSubjectX500Principal());
		
		String certHash = OpensslTrustAnchorStore.getOpenSSLCAHash(cert.getSubjectX500Principal());
		
		log.debug("Loaded VOMS AA certificate '{}' from file '{}' with subject hash '{}'", 
				new Object[]{readableSubject,f.getAbsolutePath(), certHash});
		
	}


	public void notifyLSCLoadEvent(LSCInfo lsc, File f) {
		log.debug("Loaded VOMS LSC information from {}: {}", 
				new Object[]{f.getAbsolutePath(), lsc.toString()});
	}

}
