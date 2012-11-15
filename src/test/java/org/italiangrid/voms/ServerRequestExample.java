/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    /* Save the proxy */

    OutputStream os = new FileOutputStream("/tmp/savedProxy");
    CredentialsUtils.saveCredentials(os, pxcert.getCredential());
  }
  
}
