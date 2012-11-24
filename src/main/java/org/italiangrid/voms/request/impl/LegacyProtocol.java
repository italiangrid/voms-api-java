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
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;

/**
 * Protocol implementing the legacy interface.
 * 
 * 
 */
public class LegacyProtocol extends AbstractVOMSProtocol implements VOMSProtocol {

	public LegacyProtocol(VOMSServerInfo vomsServerInfo) {
		super(vomsServerInfo, CertificateValidatorBuilder.buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, 60000L));
	}

	public LegacyProtocol(VOMSServerInfo vomsServerInfo, X509CertChainValidatorExt validator) {
		super(vomsServerInfo, validator);
	}
	
	public LegacyProtocol(VOMSServerInfo vomsServerInfo, X509CertChainValidatorExt validator, int connectTimeout, int readTimeout) {
		super(vomsServerInfo, validator, connectTimeout, readTimeout);
	}

	public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request) {

		SSLSocketFactory sslSocketFactory = getSSLSocketFactory(credential);

		SSLSocket sslSocket = null;

		try {

			
			sslSocket = (SSLSocket) sslSocketFactory.createSocket();
			sslSocket.setSoTimeout(readTimeout);
			
			SocketAddress sa = new InetSocketAddress(serverInfo.getURL().getHost(),
					serverInfo.getURL().getPort()); 
			
			sslSocket.connect(sa, connectTimeout);

		} catch (UnknownHostException e) {
			
			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);

		} catch (IOException e) {
			
			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);
		}
		
		sslSocket.setEnabledProtocols(VOMS_LEGACY_PROTOCOLS);

		LegacyRequestSender protocol = LegacyRequestSender.instance();

		VOMSResponse response = null;

		try {

			protocol.sendRequest(request, sslSocket.getOutputStream());

			InputStream inputStream = sslSocket.getInputStream();

			response = new LegacyVOMSResponseParsingStrategy().parse(inputStream);

			sslSocket.close();

		} catch (IOException e) {

			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);
		}

		return response;
	}

}
