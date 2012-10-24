package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.SSLSocketFactoryProvider;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.X509Credential;

/**
 * Protocol implementing the legacy interface. 
 * 
 * 
 */
public class LegacyProtocol  extends AbstractVOMSProtocol implements VOMSProtocol {

  private static Logger log = LoggerFactory.getLogger(DefaultVOMSACService.class);
    
  public LegacyProtocol(VOMSServerInfo vomsServerInfo) {
    super(vomsServerInfo);
  }
  
  public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request) {
    
    SSLSocketFactory sslSocketFactory = new SSLSocketFactoryProvider(credential).getSSLSockectFactory();

    SSLSocket sslSocket  = null;
    
    try {
      
      sslSocket = (SSLSocket) sslSocketFactory.createSocket(uri.getHost(), uri.getPort());
    
    } catch (UnknownHostException e) {
      
      log.error("Error in creating socket: " + e.getMessage());
      throw new VOMSError("Error in creating socket: " + e.getMessage(), e);
    
    } catch (IOException e) {
      
      log.error("Error in creating socket: " + e.getMessage());
      throw new VOMSError("Error in creating socket: " + e.getMessage(), e);
    }
    
    String[] protocols = { "SSLv3" };
    sslSocket.setEnabledProtocols(protocols);
    
    LegacyRequestSender protocol = LegacyRequestSender.instance();
    
    VOMSResponse response = null;
    
    try {

        protocol.sendRequest(request, sslSocket.getOutputStream());
        
        InputStream inputStream = sslSocket.getInputStream();
        
        response = new LegacyVOMSResponseParsingStrategy().parse(inputStream);
        
        sslSocket.close();
        
    } catch (IOException e) {
        
      log.error( "Error communicating with server " + uri + ": " + e.getMessage() );
        
      if (log.isDebugEnabled())
        log.error(e.getMessage(), e);
      
      throw new VOMSError("Error communicating with server " + uri + ": " + e.getMessage(),e);
    }
    
    return response;
  }

}
