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
