/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms.util;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACLookupListener;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.italiangrid.voms.request.VOMSWarningMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;
import org.italiangrid.voms.store.VOMSTrustStoreUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.helpers.trust.OpensslTrustAnchorStore;
import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * This class provides a default listener implementation
 * for all VOMS API Java listeners that will log received events 
 * 
 * @author andreaceccanti
 *
 */
public class LoggingListener implements ACLookupListener,
		ValidationResultListener, VOMSServerInfoStoreListener,
		LoadCredentialsEventListener, VOMSTrustStoreStatusListener,
		VOMSTrustStoreUpdateListener, UncaughtExceptionHandler,
		VOMSRequestListener{

	public static final Logger log = LoggerFactory
			.getLogger(LoggingListener.class);

	private static final String CRED_SEPARATOR = ",";

	public void notifyACLookupEvent(X509Certificate[] chain, int chainLevel) {
		String readableSubject = X500NameUtils
				.getReadableForm(chain[chainLevel].getSubjectX500Principal());

		log.debug(
				"Looking for VOMS AC at certificate chain position {} of {}: {}",
				new Object[] { chainLevel, chain.length, readableSubject });

	}

	public void notifyACParseEvent(X509Certificate[] chain, int chainLevel) {
		String readableSubject = X500NameUtils
				.getReadableForm(chain[chainLevel].getSubjectX500Principal());

		log.debug("Found VOMS AC at certificate chain position {} of {}: {}",
				new Object[] { chainLevel, chain.length, readableSubject });

	}

	public void notifyValidationResult(VOMSValidationResult result,
			VOMSAttribute attributes) {

		if (result.isValid()) {
			log.debug("{} {}", result, attributes);
		} else {
			log.warn("{} {}", result, attributes);
		}

	}

	public void notifyNoValidVOMSESError(List<String> searchedPaths) {
		log.debug("No valid VOMSES information found on the local machine while looking in: "
				+ searchedPaths);
	}

	public void notifyVOMSESlookup(String vomsesPath) {

		log.debug("Looking for VOMSES information in {}", vomsesPath);
	}

	public void notifyVOMSESInformationLoaded(String vomsesPath,
			VOMSServerInfo info) {

		if (vomsesPath == null)
			log.debug("Loaded {}", info);
		else
			log.debug("Loaded {} from {}", info, vomsesPath);
	}

	public void notifyCredentialLookup(String... locations) {
		log.debug("Looking for user credentials in ({})...",
				locations.toString());
	}

	public void notifyLoadCredentialSuccess(String... locations) {
		log.info("Credentials loaded succesfully ({})",
				locations.toString());
	}

	public void notifyLoadCredentialFailure(Throwable error,
			String... locations) {
		log.warn("Error loading credentials ({}). Reason: {}",
				locations.toString(), error.getMessage());

		if (log.isDebugEnabled())
			log.warn("Error loading credentials ({}). Reason: {}",
					locations.toString(),
					error.getMessage(), error);

	}

	public void notifyCertficateLookupEvent(String dir) {

		log.debug("Looking for VOMS AA certificates in directory: {}", dir);
	}

	public void notifyLSCLookupEvent(String dir) {

		log.debug("Looking for VOMS LSC files in directory: {}", dir);

	}

	public void notifyCertificateLoadEvent(X509Certificate cert, File f) {
		String readableSubject = X500NameUtils.getReadableForm(cert
				.getSubjectX500Principal());

		String certHash = OpensslTrustAnchorStore.getOpenSSLCAHash(cert
				.getSubjectX500Principal());

		log.debug(
				"Loaded VOMS AA certificate '{}' from file '{}' with subject hash '{}'",
				new Object[] { readableSubject, f.getAbsolutePath(), certHash });

	}

	public void notifyLSCLoadEvent(LSCInfo lsc, File f) {
		log.debug("Loaded VOMS LSC information from {}: {}",
				new Object[] { f.getAbsolutePath(), lsc.toString() });
	}

	public void notifyTrustStoreUpdate(VOMSTrustStore store) {

		log.debug("VOMS trust store {} has been updated.", store);

	}

	public void uncaughtException(Thread t, Throwable e) {
		log.error("Uncaught exception in thread {}: {}", t.getName(),
				e.getMessage(), e);
	}

	public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si) {
		
		
	}

	public void notifyVOMSRequestSuccess(VOMSACRequest request,
			VOMSServerInfo endpoint) {
		
		
	}

	public void notifyVOMSRequestFailure(VOMSACRequest request,
			VOMSServerInfo endpoint, Throwable error) {
		
		
	}

	public void notifyErrorsInVOMSReponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSErrorMessage[] errors) {
		
		
	}

	public void notifyWarningsInVOMSResponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSWarningMessage[] warnings) {
		
		
	}
}
