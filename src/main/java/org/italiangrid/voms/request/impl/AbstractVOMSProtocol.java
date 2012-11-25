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

import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.request.SSLSocketFactoryProvider;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;

public abstract class AbstractVOMSProtocol implements VOMSProtocol {

	public static final String[] VOMS_LEGACY_PROTOCOLS = {"SSLv3"};
	
	/** 
	 * The default value for the socket connection timeout
	 */
	public static final int DEFAULT_CONNECT_TIMEOUT = 2000;
	
	/** 
	 * The default value for the socket read timeout
	 */
	public static final int DEFAULT_READ_TIMEOUT = 5000;
	
	/**
	 * The remote server endpoint information
	 */
	protected VOMSServerInfo serverInfo;
	
	protected VOMSProtocolListener listener = new NullListener();

	/**
	 * The CAnL validator used to manage SSL authentication.
	 */
	protected X509CertChainValidatorExt validator;
	
	/**
	 * The tcp connection timeout (in milliseconds)
	 */
	protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;
	
	/**
	 * The socket read timeout (in milliseconds)
	 */
	protected int readTimeout = DEFAULT_READ_TIMEOUT;
	
	
	/**
	 * Ctor.
	 * 
	 * @param vomsServerInfo
	 *            the info for the endpoint.
	 */
	public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo) {

		this(vomsServerInfo, CertificateValidatorBuilder.buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, 60000L));
	}

	/**
	 * Ctor.
	 * 
	 * @param vomsServerInfo
	 *            the info for the remote VOMS server endpoint
	 * @param validator
	 *            the validator used to manage the SSL authentication
	 */
	public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo, X509CertChainValidatorExt validator) {

		this.serverInfo = vomsServerInfo;
		this.validator = validator;
	}
	
	/**
	 * Ctor.
	 * 
	 * @param vomsServerInfo
	 *            the info for the remote VOMS server endpoint
	 * @param validator
	 *            the validator used to manage the SSL authentication
	 * @param listener 
	 *            the listener informed of low-level protocol details
	 * @param connectTimeout
	 * 			  sets the socket connection timeout
	 * @param readTimeout
	 * 			  sets the socket read timeout	
	 */
	public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo, 
			X509CertChainValidatorExt validator,
			VOMSProtocolListener listener,
			int connectTimeout, 
			int readTimeout)
			{

		this.serverInfo = vomsServerInfo;
		this.validator = validator;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.listener = listener;
	}
	
	/**
	 * Builds an SSL socket factory based on the credential passed as argument and the validator
	 * configured for this {@link AbstractVOMSProtocol}
	 * @param credential the client credential used for the socket factory being created
	 * @return an {@link SSLSocketFactory} 
	 */
	protected SSLSocketFactory getSSLSocketFactory(X509Credential credential){
		SSLSocketFactoryProvider sslSocketFactoryProvider = new SSLSocketFactoryProvider(credential, validator);
	    return sslSocketFactoryProvider.getSSLSockectFactory();
	}

	/**
	 * @return The connect timeout value (in milliseconds)
	 */
	public int getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * Sets the connection timeout value for the underlying socket of this {@link AbstractVOMSProtocol}
	 * 
	 * @param connectTimeout the connection timeout in milliseconds
	 */
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	/**
	 * @return the read timeout value (in milliseconds)
	 */
	public int getReadTimeout() {
		return readTimeout;
	}

	/**
	 * Sets the read timeout value for the underlying socket 
	 * 
	 * @param readTimeout the read timeout in milliseconds
	 */
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	

}
