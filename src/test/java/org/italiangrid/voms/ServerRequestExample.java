package org.italiangrid.voms;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.credential.CredentialsUtils;
import org.italiangrid.voms.credential.UserCredentials;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.request.impl.DefaultVOMSACService;
import org.junit.Assert;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

/**
 * Request an AC to a VOMS service, and builds a proxy, saving it under /tmp.
 * 
 * 
 * This works using credentials in default locations, i.e. $HOME/.globus/user*.pem, 
 * and a vomses file in one of the default places.
 * 
 * Set passphrase and vopName in the code,
 * 
 * @author valerioventuri
 *
 */
public class ServerRequestExample {

  public static void main(String[] args) throws KeyStoreException, CertificateException, 
    IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, 
    UnrecoverableKeyException, IllegalArgumentException, NoSuchProviderException, IllegalStateException {
    
    X509Credential credential = UserCredentials.loadCredentials("passphrase".toCharArray());
    
    DefaultVOMSACRequest request = new DefaultVOMSACRequest();
    request.setLifetime(12);
    request.setVoName("voName");
    
    VOMSACService service = new DefaultVOMSACService();
    
    AttributeCertificate attributeCertificate = service.getVOMSAttributeCertificate(credential, request);
  
    Assert.assertNotNull(attributeCertificate);
    
    ProxyCertificateOptions pxopt = new ProxyCertificateOptions(new X509Certificate[] {credential.getCertificate()});
    pxopt.setAttributeCertificates(new AttributeCertificate[] {attributeCertificate});

    ProxyCertificate pxcert = ProxyGenerator.generate(pxopt, credential.getKey());

    X509Certificate[] list = pxcert.getCertificateChain();

    /* Save the proxy */

    OutputStream os = new FileOutputStream("/tmp/savedProxy");
    CredentialsUtils.saveCredentials(os, pxcert.getCredential());
  }
  
}
