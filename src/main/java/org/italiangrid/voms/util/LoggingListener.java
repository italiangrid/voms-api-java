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
import java.util.Arrays;
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

import eu.emi.security.authn.x509.helpers.trust.OpensslTrustAnchorStore;
import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * This class provides a listener implementation
 * for all VOMS API Java listeners that will print received events
 * to standard output 
 * 
 * @author andreaceccanti
 *
 */
public class LoggingListener implements ACLookupListener,
		ValidationResultListener, VOMSServerInfoStoreListener,
		LoadCredentialsEventListener, VOMSTrustStoreStatusListener,
		VOMSTrustStoreUpdateListener, UncaughtExceptionHandler,
		VOMSRequestListener{

	public void notifyACLookupEvent(X509Certificate[] chain, int chainLevel) {
		String readableSubject = X500NameUtils
				.getReadableForm(chain[chainLevel].getSubjectX500Principal());
		
		
		String format = "Looking for VOMS AC at certificate chain position %d of %d: %s\n";
		System.out.format(format, chainLevel, chain.length, readableSubject);

	}

	public void notifyACParseEvent(X509Certificate[] chain, int chainLevel) {
		String readableSubject = X500NameUtils
				.getReadableForm(chain[chainLevel].getSubjectX500Principal());

		String format = "Found VOMS AC at certificate chain position %d of %d: %s\n";
		System.out.format(format,chainLevel, chain.length, readableSubject);

	}

	public void notifyValidationResult(VOMSValidationResult result,
			VOMSAttribute attributes) {

		System.out.format("%s %s", result, attributes);

	}

	public void notifyNoValidVOMSESError(List<String> searchedPaths) {
		
		System.out.println("No valid VOMSES information found on the local machine while looking in: "
				+ searchedPaths);
	}

	public void notifyVOMSESlookup(String vomsesPath) {

		System.out.println("Looking for VOMSES information in "+vomsesPath);
	}

	public void notifyVOMSESInformationLoaded(String vomsesPath,
			VOMSServerInfo info) {

		if (vomsesPath == null)
			System.out.println("Loaded "+info);
		else
			System.out.format("Loaded %s from %s\n", info, vomsesPath);
	}

	public void notifyCredentialLookup(String... locations) {
		System.out.format("Looking for user credentials in (%s)...\n",
				Arrays.toString(locations));
	}

	public void notifyLoadCredentialSuccess(String... locations) {
		System.out.format("Credentials loaded succesfully (%s)\n",
				Arrays.toString(locations));
	}

	public void notifyLoadCredentialFailure(Throwable error,
			String... locations) {
		
		System.out.format("Error loading credentials (%s). Reason: %s\n",
				Arrays.toString(locations), error.getMessage());

	}

	public void notifyCertficateLookupEvent(String dir) {

		System.out.println("Looking for VOMS AA certificates in directory: "+dir);
	}

	public void notifyLSCLookupEvent(String dir) {

		System.out.println("Looking for VOMS LSC files in directory: "+dir);

	}

	public void notifyCertificateLoadEvent(X509Certificate cert, File f) {
		String readableSubject = X500NameUtils.getReadableForm(cert
				.getSubjectX500Principal());

		String certHash = OpensslTrustAnchorStore.getOpenSSLCAHash(cert
				.getSubjectX500Principal());

		System.out.format("Loaded VOMS AA certificate '%s' from file '%s' with subject hash '%s'\n",
				readableSubject, f.getAbsolutePath(), certHash);

	}

	public void notifyLSCLoadEvent(LSCInfo lsc, File f) {
		System.out.format("Loaded VOMS LSC information from %s: %s\n",
				f.getAbsolutePath(), lsc.toString());
	}

	public void notifyTrustStoreUpdate(VOMSTrustStore store) {

		System.out.format("VOMS trust store %s has been updated.\n", store);

	}

	public void uncaughtException(Thread t, Throwable e) {
		System.out.format("Uncaught exception in thread %s: %s", t.getName(),
				e.getMessage());
	}

	public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si) {
		System.out.format("Contacting server %s for VO %s\n", si.getURL(), request.getVoName());
		
	}

	public void notifyVOMSRequestSuccess(VOMSACRequest request,
			VOMSServerInfo si) {
		
		System.out.format("Contacted server %s for VO %s succesfully\n", si.getURL(), request.getVoName());
		
	}

	public void notifyVOMSRequestFailure(VOMSACRequest request,
			VOMSServerInfo endpoint, Throwable error) {
		System.out.format("Request to server %s for VO %s failed! %s\n", endpoint.getURL(), 
				request.getVoName(), error.getMessage());
		
	}

	public void notifyErrorsInVOMSReponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSErrorMessage[] errors) {
		
		System.out.format("Errors found in VOMS response: %s", Arrays.toString(errors));
	}

	public void notifyWarningsInVOMSResponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSWarningMessage[] warnings) {
		System.out.format("Warnings found in VOMS response: %s", Arrays.toString(warnings));
		
	}
}
