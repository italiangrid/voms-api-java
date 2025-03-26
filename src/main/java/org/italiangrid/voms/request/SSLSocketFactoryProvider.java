/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
package org.italiangrid.voms.request;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.ssl.DisabledNameMismatchCallback;
import eu.emi.security.authn.x509.helpers.ssl.EnforcingNameMismatchCallback;
import eu.emi.security.authn.x509.impl.SocketFactoryCreator2;

/**
 * Provides an SSL socket factory configured using CAnL.
 *
 * This class is responsible for creating an {@link SSLSocketFactory} that is configured with a
 * given X.509 credential and certificate validator. It supports optional hostname verification.
 *
 */
public class SSLSocketFactoryProvider {

  /** The X.509 credential used for SSL connections. */
  private X509Credential credential;

  /** The certificate chain validator. */
  private X509CertChainValidatorExt validator;

  /** Flag indicating whether hostname checks should be skipped. */
  private boolean skipHostnameChecks;

  /**
   * Constructs an {@link SSLSocketFactoryProvider} with the given credential, validator, and
   * hostname check setting.
   *
   * @param credential the X.509 credential
   * @param validator the certificate chain validator
   * @param skipHostnameChecks true to disable hostname verification, false otherwise
   */
  public SSLSocketFactoryProvider(X509Credential credential, X509CertChainValidatorExt validator,
      boolean skipHostnameChecks) {

    this.credential = credential;
    this.validator = validator;
    this.skipHostnameChecks = skipHostnameChecks;
  }

  /**
   * Constructs an {@link SSLSocketFactoryProvider} with the given credential and validator, with
   * hostname verification enabled.
   *
   * @param credential the X.509 credential
   * @param validator the certificate chain validator
   */
  public SSLSocketFactoryProvider(X509Credential credential, X509CertChainValidatorExt validator) {

    this(credential, validator, false);
  }

  /**
   * Constructs an {@link SSLSocketFactoryProvider} with the given credential and a default
   * validator.
   *
   * @param credential the X.509 credential
   */
  public SSLSocketFactoryProvider(X509Credential credential) {

    this(credential, new CertificateValidatorBuilder().trustAnchorsUpdateInterval(60000L).build());
  }

  /**
   * Returns an SSL socket factory configured with the provided credential and validator.
   *
   * @return the {@link SSLSocketFactory} object
   */
  public SSLSocketFactory getSSLSockectFactory() {

    SSLContext context = null;

    try {
      context = SSLContext.getInstance("TLS");
    } catch (NoSuchAlgorithmException e) {
      throw new VOMSError(e.getMessage(), e);
    }

    KeyManager[] keyManagers = new KeyManager[] {credential.getKeyManager()};

    SocketFactoryCreator2 factory = new SocketFactoryCreator2(credential, validator,
        skipHostnameChecks ? new DisabledNameMismatchCallback()
            : new EnforcingNameMismatchCallback());
    X509TrustManager trustManager = factory.getSSLTrustManager();

    TrustManager[] trustManagers = new TrustManager[] {trustManager};

    // Using new SecureRandom instead of SecureRandom.getInstance("SHA1PRNG") to avoid unnecessary
    // blocking
    SecureRandom secureRandom = new SecureRandom();

    try {
      context.init(keyManagers, trustManagers, secureRandom);
    } catch (KeyManagementException e) {
      throw new VOMSError(e.getMessage(), e);
    }

    return context.getSocketFactory();
  }

}
