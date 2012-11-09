package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.util.Set;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.X509Credential;

/**
 * Default implementation for the VOMS AC Service.
 * 
 * @author valerioventuri
 *
 */
public class DefaultVOMSACService implements VOMSACService {

  private static Logger log = LoggerFactory.getLogger(DefaultVOMSACService.class);

  public AttributeCertificate getVOMSAttributeCertificate(X509Credential credential, VOMSACRequest request) {
    
    Set<VOMSServerInfo> vomsServerInfos = getVOMSServerInfos(request);
    
    VOMSResponse response = null;
    
    for(VOMSServerInfo vomsServerInfo : vomsServerInfos) {
      
      RESTProtocol restProtocol = new RESTProtocol(vomsServerInfo);
      response = restProtocol.doRequest(credential, request);

      if(response == null) {
        
        LegacyProtocol legacyProtocol = new LegacyProtocol(vomsServerInfo);
        response = legacyProtocol.doRequest(credential, request);
      }
        
      if(response != null)
        break;
      
      log.info("Can't get a response from {}:{} for VO alias {}",
    		  new Object[]{vomsServerInfo.getURL().getHost(),vomsServerInfo.getURL().getPort(),
    		  vomsServerInfo.getAlias()});
      
    }
    
    if(response == null) {
      
      log.info("Can't get a response from any server for VO " + request.getVoName());
      return null;
    }
      
    byte[] acBytes = response.getAC();
    
    ASN1InputStream asn1InputStream = new ASN1InputStream(acBytes);
    
    AttributeCertificate attributeCertificate = null;
  
    try {
    
      attributeCertificate = AttributeCertificate.getInstance(asn1InputStream.readObject());
    
      asn1InputStream.close();
      
    } catch (IOException e) {

      log.info("Error in parsing attribute certificate: " + e.getMessage());
      return null;
    }
    
    return attributeCertificate;
  }

  private Set<VOMSServerInfo> getVOMSServerInfos(VOMSACRequest request) {
    
    VOMSServerInfoStore vomsServerInfoStore = new DefaultVOMSServerInfoStore();
    Set<VOMSServerInfo> vomsServerInfos = vomsServerInfoStore.getVOMSServerInfo(request.getVoName());
    
    return vomsServerInfos;
  }

}
