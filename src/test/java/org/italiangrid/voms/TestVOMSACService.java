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
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.request.impl.DefaultVOMSACService;
import org.junit.Assert;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

public class TestVOMSACService {

  @Test
  public void test() throws KeyStoreException, CertificateException, 
    IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnrecoverableKeyException, 
    IllegalArgumentException, NoSuchProviderException {

    PEMCredential credential = new PEMCredential("/Users/valerioventuri/.globus/userkey.pem", 
        "/Users/valerioventuri/.globus/usercert.pem", "eraldo".toCharArray());
    
    DefaultVOMSACRequest request = new DefaultVOMSACRequest();
    request.setLifetime(12);
    request.setVoName("testers.eu-emi.eu");
    
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
