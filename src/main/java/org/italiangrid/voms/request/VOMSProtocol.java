// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request;

import eu.emi.security.authn.x509.X509Credential;

/**
 * The request/response protocol for VOMS, with a methods accepting a rerquest
 * and returning a response.
 * 
 * @author valerioventuri
 * 
 */
public interface VOMSProtocol {

  /**
   * Makes a request, get the response.
   * 
   * @param endpoint
   *          the voms server endpoint information
   * @param credential
   *          the credentials.
   * @param request
   *          the request.
   * 
   * @return a {@link VOMSResponse} containing the response.
   */
  public VOMSResponse doRequest(VOMSServerInfo endpoint,
    X509Credential credential, VOMSACRequest request);

}
