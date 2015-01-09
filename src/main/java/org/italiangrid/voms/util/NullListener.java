/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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

import org.italiangrid.voms.ac.ACLookupListener;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.italiangrid.voms.request.VOMSWarningMessage;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;

/**
 * 
 * A Singleton Listener which swallows notification.
 * 
 * @author andreaceccanti
 *
 */
public enum NullListener implements ACLookupListener, ValidationResultListener,
  VOMSServerInfoStoreListener, LoadCredentialsEventListener,
  VOMSTrustStoreStatusListener, UncaughtExceptionHandler, VOMSRequestListener,
  VOMSProtocolListener {

  INSTANCE;

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

  public void uncaughtException(Thread t, Throwable e) {

  }

  public void notifyTrustStoreUpdate(VOMSTrustStore store) {

  }

  public void notifyCertficateLookupEvent(String dir) {

  }

  public void notifyLSCLookupEvent(String dir) {

  }

  public void notifyCertificateLoadEvent(X509Certificate cert, File f) {

  }

  public void notifyLSCLoadEvent(LSCInfo lsc, File f) {

  }

  public void notifyCredentialLookup(String... locations) {

  }

  public void notifyLoadCredentialSuccess(String... locations) {

  }

  public void notifyLoadCredentialFailure(Throwable error, String... locations) {

  }

  public void notifyNoValidVOMSESError(List<String> searchedPaths) {

  }

  public void notifyVOMSESlookup(String vomsesPath) {

  }

  public void notifyVOMSESInformationLoaded(String vomsesPath,
    VOMSServerInfo info) {

  }

  public void notifyValidationResult(VOMSValidationResult result) {

  }

  public void notifyACLookupEvent(X509Certificate[] chain, int chainLevel) {

  }

  public void notifyACParseEvent(X509Certificate[] chain, int chainLevel) {

  }

  public void notifyHTTPRequest(String url) {

  }

  public void notifyLegacyRequest(String xmlLegacyRequest) {

  }

  public void notifyReceivedResponse(VOMSResponse r) {

  }

}
