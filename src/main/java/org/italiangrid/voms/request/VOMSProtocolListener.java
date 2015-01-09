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
package org.italiangrid.voms.request;

/**
 * A listener that is notified of low-level VOMS protocol messages
 * 
 * @author cecco
 *
 */
public interface VOMSProtocolListener {

  /**
   * Informs that a VOMS HTTP GET request is being issued for the URL passed as
   * argument
   * 
   * @param url
   *          the request url
   */
  public void notifyHTTPRequest(String url);

  /**
   * Informs that a VOMS legacy request is being issued
   * 
   * @param xmlLegacyRequest
   *          a string representation of the XML legacy request
   */
  public void notifyLegacyRequest(String xmlLegacyRequest);

  /**
   * Informs that a VOMSResponse was received from a remote VOMS server
   * 
   * @param r
   *          the received {@link VOMSResponse}
   */
  public void notifyReceivedResponse(VOMSResponse r);
}
