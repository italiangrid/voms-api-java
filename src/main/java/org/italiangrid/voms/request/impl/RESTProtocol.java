// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;

/**
 * Protocol implementing the REST-style interface.
 * 
 * @author valerioventuri
 * 
 */
public class RESTProtocol extends AbstractVOMSProtocol implements VOMSProtocol {

  public RESTProtocol(X509CertChainValidatorExt validator,
    VOMSProtocolListener listener, int connectTimeout, int readTimeout) {

    super(validator, listener, connectTimeout, readTimeout);
  }

  public VOMSResponse doRequest(VOMSServerInfo endpoint,
    X509Credential credential, VOMSACRequest request) {

    RESTServiceURLBuilder restQueryBuilder = new RESTServiceURLBuilder();
    URL serviceUrl = restQueryBuilder.build(endpoint, request);
    RESTVOMSResponseParsingStrategy responseParsingStrategy = new RESTVOMSResponseParsingStrategy();

    HttpsURLConnection connection = null;

    try {

      connection = (HttpsURLConnection) serviceUrl.openConnection();
      
      if (isSkipHostnameChecks()){
        connection.setHostnameVerifier(new HostnameVerifier() {
          public boolean verify(String arg0, SSLSession arg1) {
            return true;
          }
        });
      }

      connection.setConnectTimeout(connectTimeout);
      connection.setReadTimeout(readTimeout);

    } catch (IOException e) {

      throw new VOMSProtocolError(e.getMessage(), endpoint, request,
        credential, e);
    }

    connection.setSSLSocketFactory(getSSLSocketFactory(credential));

    listener.notifyHTTPRequest(serviceUrl.toExternalForm());

    try {

      connection.connect();

    } catch (IOException e) {

      throw new VOMSProtocolError(e.getMessage(), endpoint, request,
        credential, e);

    }

    InputStream is = null;

    try {
      if (connection.getResponseCode() != 200) {
        is = connection.getErrorStream();
      } else
        is = connection.getInputStream();

    } catch (IOException e) {

      throw new VOMSProtocolError(e.getMessage(), endpoint, request,
        credential, e);
    }

    VOMSResponse response = responseParsingStrategy.parse(is);

    listener.notifyReceivedResponse(response);
    connection.disconnect();

    return response;
  }

}
