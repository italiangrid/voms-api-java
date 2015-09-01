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
 * A listener that informs about events related with a request to a VOMS server.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSRequestListener {

  /**
   * Informs of the start of a request to a VOMS server
   * 
   * @param request
   *          the request
   * @param si
   *          the VOMS server endpoint information
   */
  public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si);

  /**
   * Informs of the succesfull conclusion of a request to a VOMS server
   * 
   * @param request
   *          the request
   * @param endpoint
   *          the VOMS server endpoint information
   */
  public void notifyVOMSRequestSuccess(VOMSACRequest request,
    VOMSServerInfo endpoint);

  /**
   * Informs of a VOMS request failure
   * 
   * @param request
   *          the request
   * @param endpoint
   *          the VOMS server endpoint information
   * @param error
   *          the error related with the failure
   */
  public void notifyVOMSRequestFailure(VOMSACRequest request,
    VOMSServerInfo endpoint, Throwable error);

  /**
   * Informs that errors were included in the VOMS response produced by a VOMS
   * server
   * 
   * @param request
   *          the request related to the received response
   * @param si
   *          the VOMS server endpoint information
   * @param errors
   *          the error messages included in the response
   */
  public void notifyErrorsInVOMSReponse(VOMSACRequest request,
    VOMSServerInfo si, VOMSErrorMessage[] errors);

  /**
   * Informs that warnings were included in the VOMS response produced by a VOMS
   * server
   * 
   * @param request
   *          the request related to the received response
   * @param si
   *          the VOMS server endpoint information
   * @param warnings
   *          the warning messages included in the response
   */
  public void notifyWarningsInVOMSResponse(VOMSACRequest request,
    VOMSServerInfo si, VOMSWarningMessage[] warnings);

}
