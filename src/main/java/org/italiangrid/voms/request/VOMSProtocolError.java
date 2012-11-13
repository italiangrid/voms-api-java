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
package org.italiangrid.voms.request;

import org.italiangrid.voms.VOMSError;

import eu.emi.security.authn.x509.X509Credential;

/**
 * Exception used when errors are raised during the interaction
 * with a (possibly) remote VOMS server.
 *  
 * @author andreaceccanti
 *
 */
public class VOMSProtocolError extends VOMSError {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The request that is related with this error
	 */
	private VOMSACRequest request;
	
	/**
	 * The credential related with this error
	 */
	private X509Credential credential;
	
	/**
	 * The VOMS server endpoint information related with this error
	 */
	private VOMSServerInfo serverInfo;
	
	public VOMSProtocolError(String message, VOMSServerInfo serv, VOMSACRequest req, X509Credential cred, Throwable c) {
		super(message, c);
		this.request = req;
		this.credential = cred;
		this.serverInfo = serv;
		
	}

	/**
	 * @return the request that is related with this error
	 */
	public VOMSACRequest getRequest() {
		return request;
	}

	/**
	 * @return the client credential related with this error 
	 */
	public X509Credential getCredential() {
		return credential;
	}

	/**
	 * @return the VOMS serverInfo related with this error
	 */
	public VOMSServerInfo getServerInfo() {
		return serverInfo;
	}	
	
}
