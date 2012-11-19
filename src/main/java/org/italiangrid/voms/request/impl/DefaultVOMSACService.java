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
package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.util.Set;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSESLookupStrategy;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.italiangrid.voms.request.VOMSServerInfoStoreListener;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.LoggingListener;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

/** 
 * The default implementation of the {@link VOMSACService}.
 * 
 * 
 * @author Valerio Venturi
 * @author Andrea Ceccanti
 *
 */
public class DefaultVOMSACService implements VOMSACService {

	/**
	 * The listener that will be informed about request events
	 */
	private VOMSRequestListener requestListener;
		
	/**
	 * The validator used for the SSL handshake
	 */
	private AbstractValidator validator;
	
	/**
	 * The store used to keep VOMS server contact information.
	 */
	private VOMSServerInfoStore serverInfoStore;
	
	/**
	 * Ctor. 
	 * 
	 * @param validator the validator used for the SSL handshake
	 * @param listener the listener that will be informed about request events
	 * @param serverInfoStoreListener the listener that will be informed about server info store events
	 */
	public DefaultVOMSACService(AbstractValidator validator,
			VOMSRequestListener listener, 
			VOMSESLookupStrategy lookupStrategy,
			VOMSServerInfoStoreListener serverInfoStoreListener) {
		
		this.requestListener = listener;
		this.validator = validator;
		
		serverInfoStore = new DefaultVOMSServerInfoStore(lookupStrategy,serverInfoStoreListener);
	}

	public DefaultVOMSACService() {
		this.validator = CertificateValidatorBuilder.buildCertificateValidator();
		
		LoggingListener logListener = new LoggingListener();
		this.requestListener = logListener;
		
		serverInfoStore = new DefaultVOMSServerInfoStore(logListener);
		
	}
	
	/**
	 * Extracts an AC from a VOMS response
	 * 
	 * @param request the request
	 * @param response the received response
	 * @return a possibly <code>null</code> {@link AttributeCertificate} object
	 */
	protected AttributeCertificate getACFromResponse(VOMSACRequest request, VOMSResponse response){
		byte[] acBytes = response.getAC();

		ASN1InputStream asn1InputStream = new ASN1InputStream(acBytes);

		AttributeCertificate attributeCertificate = null;

		try {

			attributeCertificate = AttributeCertificate
					.getInstance(asn1InputStream.readObject());

			asn1InputStream.close();
			return attributeCertificate;

		} catch (IOException e) {

			requestListener.notifyVOMSRequestFailure(request, null, e);
			return null;
		}
	}
	
	/**
	 * Executes the request using the VOMS REST protocol
	 * 
	 * @param request the request
	 * @param serverInfo the VOMS server endpoint information
	 * @param credential the credentials used to authenticate to the server
	 * @return a {@link VOMSResponse}
	 */
	protected VOMSResponse doRESTRequest(VOMSACRequest request, VOMSServerInfo serverInfo, X509Credential credential){
		
		RESTProtocol restProtocol = new RESTProtocol(serverInfo, validator);
		return restProtocol.doRequest(credential, request);
		
	}
	
	/**
	 * Executes the request using the VOMS legacy protocol
	 * @param request the request
	 * @param serverInfo the VOMS server endpoint information
	 * @param credential the credentials used to authenticate to the server
	 * @return a {@link VOMSResponse}
	 */
	protected VOMSResponse doLegacyRequest(VOMSACRequest request, VOMSServerInfo serverInfo, X509Credential credential){
		
		LegacyProtocol legacyProtocol = new LegacyProtocol(serverInfo, validator);
		return legacyProtocol.doRequest(credential, request);
		
	}
	
	/**
	 * Handles errors included in the VOMS response
	 * @param request the request
	 * @param si the VOMS server endpoint information
	 * @param response the received {@link VOMSResponse}
	 */
	protected void handleErrorsInResponse(VOMSACRequest request, VOMSServerInfo si, VOMSResponse response){
		
		if (response.hasErrors())
			requestListener.notifyErrorsInVOMSReponse(request, si, response.errorMessages());		
		
	}
	/**
	 * Handles warnings included in the VOMS response
	 * @param request the request
	 * @param si the VOMS server endpoint information
	 * @param response the received {@link VOMSResponse}
	 */
	protected void handleWarningsInResponse(VOMSACRequest request, VOMSServerInfo si, VOMSResponse response){
		if (response.hasWarnings())
			requestListener.notifyWarningsInVOMSResponse(request, si, response.warningMessages());
	}
	
	public AttributeCertificate getVOMSAttributeCertificate(
			X509Credential credential, VOMSACRequest request) {

		Set<VOMSServerInfo> vomsServerInfos = getVOMSServerInfos(request);
		
		if (vomsServerInfos.isEmpty())
			throw new VOMSError("VOMS server for VO "+request.getVoName()+" is not known! Check your vomses configuration.");
		
		VOMSResponse response = null;

		for (VOMSServerInfo vomsServerInfo : vomsServerInfos) {

			requestListener.notifyVOMSRequestStart(request,  vomsServerInfo);
			
			response = doRESTRequest(request, vomsServerInfo, credential);

			if (response == null)
				response = doLegacyRequest(request, vomsServerInfo, credential);

			if (response != null){
				requestListener.notifyVOMSRequestSuccess(request, vomsServerInfo);
				
				handleErrorsInResponse(request, vomsServerInfo, response);
				handleWarningsInResponse(request, vomsServerInfo, response);
				
				break;
			}
			
			requestListener.notifyVOMSRequestFailure(request, vomsServerInfo, null);
		}

		if (response == null) {
			requestListener.notifyVOMSRequestFailure(request, null, null);
			return null;
		}
		
		return getACFromResponse(request, response);
	}

	/**
	 * Get VOMS server endpoint information that matches with the {@link VOMSACRequest} passed
	 * as argument
	 * @param request the request
	 * @return a possibly empty {@link Set} of {@link VOMSServerInfo} objects
	 */
	protected Set<VOMSServerInfo> getVOMSServerInfos(VOMSACRequest request) {

		Set<VOMSServerInfo> vomsServerInfos = serverInfoStore
				.getVOMSServerInfo(request.getVoName());

		return vomsServerInfos;
	}
}
