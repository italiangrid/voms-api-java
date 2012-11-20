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

/**
 * 
 * A listener which swallows notification.
 * 
 * @author andreaceccanti
 *
 */
public class NullListener implements ACLookupListener,
		ValidationResultListener, VOMSServerInfoStoreListener,
		LoadCredentialsEventListener, VOMSTrustStoreStatusListener,
		VOMSTrustStoreUpdateListener, UncaughtExceptionHandler,
		VOMSRequestListener {

	public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si) {
		// TODO Auto-generated method stub

	}

	public void notifyVOMSRequestSuccess(VOMSACRequest request,
			VOMSServerInfo endpoint) {
		// TODO Auto-generated method stub

	}

	public void notifyVOMSRequestFailure(VOMSACRequest request,
			VOMSServerInfo endpoint, Throwable error) {
		// TODO Auto-generated method stub

	}

	public void notifyErrorsInVOMSReponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSErrorMessage[] errors) {
		// TODO Auto-generated method stub

	}

	public void notifyWarningsInVOMSResponse(VOMSACRequest request,
			VOMSServerInfo si, VOMSWarningMessage[] warnings) {
		// TODO Auto-generated method stub

	}

	public void uncaughtException(Thread t, Throwable e) {
		// TODO Auto-generated method stub

	}

	public void notifyTrustStoreUpdate(VOMSTrustStore store) {
		// TODO Auto-generated method stub

	}

	public void notifyCertficateLookupEvent(String dir) {
		// TODO Auto-generated method stub

	}

	public void notifyLSCLookupEvent(String dir) {
		// TODO Auto-generated method stub

	}

	public void notifyCertificateLoadEvent(X509Certificate cert, File f) {
		// TODO Auto-generated method stub

	}

	public void notifyLSCLoadEvent(LSCInfo lsc, File f) {
		// TODO Auto-generated method stub

	}

	public void notifyCredentialLookup(String... locations) {
		// TODO Auto-generated method stub

	}

	public void notifyLoadCredentialSuccess(String... locations) {
		// TODO Auto-generated method stub

	}

	public void notifyLoadCredentialFailure(Throwable error,
			String... locations) {
		// TODO Auto-generated method stub

	}

	public void notifyNoValidVOMSESError(List<String> searchedPaths) {
		// TODO Auto-generated method stub

	}

	public void notifyVOMSESlookup(String vomsesPath) {
		// TODO Auto-generated method stub

	}

	public void notifyVOMSESInformationLoaded(String vomsesPath,
			VOMSServerInfo info) {
		// TODO Auto-generated method stub

	}

	public void notifyValidationResult(VOMSValidationResult result,
			VOMSAttribute attributes) {
		// TODO Auto-generated method stub

	}

	public void notifyACLookupEvent(X509Certificate[] chain, int chainLevel) {
		// TODO Auto-generated method stub

	}

	public void notifyACParseEvent(X509Certificate[] chain, int chainLevel) {
		// TODO Auto-generated method stub

	}

}
