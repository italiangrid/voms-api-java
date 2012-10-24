package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.SSLSocketFactoryProvider;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.X509Credential;

/**
 * Protocol implementing the REST-style interface. 
 * 
 * @author valerioventuri
 *
 */
public class RESTProtocol extends AbstractVOMSProtocol implements VOMSProtocol {
  
  private static Logger log = LoggerFactory.getLogger(VOMSProtocol.class);
  
  public RESTProtocol(VOMSServerInfo vomsServerInfo) {
    super(vomsServerInfo);
  }
  
  public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request) {

    SSLSocketFactoryProvider sslSocketFactoryProvider = new SSLSocketFactoryProvider(credential);
    SSLSocketFactory sslSocketFactory = sslSocketFactoryProvider.getSSLSockectFactory();
    
    RESTServiceURLBuilder restQueryBuilder = new RESTServiceURLBuilder();
    URL serviceUrl = restQueryBuilder.build(uri, request);
    
    HttpsURLConnection connection = null;
    
    try {
      
      connection = (HttpsURLConnection) serviceUrl.openConnection();

    } catch (IOException e) {
    
      log.error("Error opening connection to: " + serviceUrl);
      throw new VOMSError("Error opening connection to:" + serviceUrl, e);
    }
    
    connection.setSSLSocketFactory(sslSocketFactory);

    try {
     
      connection.connect();
    
    } catch (IOException e) {
      
      log.error("Error opening connection to: " + serviceUrl);
      throw new VOMSError("Error opening connection to:" + serviceUrl, e);
    }
    
    InputStream inputStream = null;
    
    try {
    
      inputStream = (InputStream) connection.getContent();
    
    } catch (IOException e) {
    
      log.error("No content in connection to " + serviceUrl + ": " +  e.getMessage());
      throw new VOMSError("No content in connection to " + serviceUrl + ": " +  e.getMessage(), e);
    }
    
    RESTVOMSResponseParsingStrategy responseParsingStrategy = 
        new RESTVOMSResponseParsingStrategy();
    
    VOMSResponse response = responseParsingStrategy.parse(inputStream);
    
    connection.disconnect();
  
    return response;
  }
  
}
