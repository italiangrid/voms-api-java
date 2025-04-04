// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

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
