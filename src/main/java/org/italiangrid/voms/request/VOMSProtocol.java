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

import eu.emi.security.authn.x509.X509Credential;

/**
 * The request/response protocol for VOMS, with a methods accepting a rerquest and 
 * returning a response. 
 * 
 * @author valerioventuri
 *
 */
public interface VOMSProtocol {

  /**
   * Makes a request, get the response.
   * 
   * @param request the request.
   * @param credentials the credentials.
   * @return a {@link VOMSResponse} containing the response.
   */
  public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request);
  
}
