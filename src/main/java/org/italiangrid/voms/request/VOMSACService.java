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

import org.bouncycastle.asn1.x509.AttributeCertificate;

import eu.emi.security.authn.x509.X509Credential;
/**
 * The {@link VOMSACService} interface.
 *  
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACService {
	
	/**
	 * Returns an {@link AttributeCertificate} given a {@link VOMSACRequest} for VOMS attributes.
	 * 
	 * @param credential the credential to be used when contacting the service
	 * @param request the request for VOMS attributes
	 * @return a possibly null {@link AttributeCertificate} containing (a subset of) the requested attributes.
	 */
	public AttributeCertificate getVOMSAttributeCertificate(X509Credential credential, VOMSACRequest request);
	
	/**
	 * Sets the socket timeout parameter for this service
	 * @param timeout
	 */
	public void setConnectTimeout(int timeout);
	
	/**
	 * Sets the read timeout parameter for this service
	 * 
	 * @param timeout
	 */
	public void setReadTimeout(int timeout);

}
