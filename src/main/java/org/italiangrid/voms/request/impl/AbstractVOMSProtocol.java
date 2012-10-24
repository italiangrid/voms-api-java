package org.italiangrid.voms.request.impl;

import java.net.URI;

import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSServerInfo;

public abstract class AbstractVOMSProtocol implements VOMSProtocol {

  /**
   * The URI.
   */
  protected URI uri;
  
  /**
   * The dn of the certificate that the https peer should use.
   * 
   */
  protected String dn;
  
  /**
   * Ctor.
   * 
   * @param vomsServerInfo the info for the endpoint.
   */
  public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo) {
    
    uri = vomsServerInfo.getURL();
    dn = vomsServerInfo.getVOMSServerDN();
  }
  
}
