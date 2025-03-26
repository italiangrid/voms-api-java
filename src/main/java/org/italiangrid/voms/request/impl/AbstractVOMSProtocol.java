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
package org.italiangrid.voms.request.impl;

import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.request.SSLSocketFactoryProvider;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolListener;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.X509Credential;

/**
 * Abstract base class providing a skeletal implementation of the VOMS client-server protocol. This
 * class handles SSL authentication, connection timeouts, and hostname verification.
 */
public abstract class AbstractVOMSProtocol implements VOMSProtocol {

  /**
   * Enabled TLS protocols for VOMS legacy connections.
   */
  public static final String[] VOMS_LEGACY_ENABLED_PROTOCOLS = {"TLSv1", "TLSv1.1", "TLSv1.2"};

  /**
   * The default value for the socket connection timeout (in milliseconds).
   */
  public static final int DEFAULT_CONNECT_TIMEOUT = 5000;

  /**
   * The default value for the socket read timeout (in milliseconds).
   */
  public static final int DEFAULT_READ_TIMEOUT = 5000;

  /**
   * The default policy for skipping hostname verification.
   */
  public static final boolean DEFAULT_SKIP_HOSTNAME_CHECKS = false;

  /**
   * Listener for protocol events.
   */
  protected VOMSProtocolListener listener = NullListener.INSTANCE;

  /**
   * Validator used for SSL authentication.
   */
  protected X509CertChainValidatorExt validator;

  /**
   * TCP connection timeout in milliseconds.
   */
  protected int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

  /**
   * Socket read timeout in milliseconds.
   */
  protected int readTimeout = DEFAULT_READ_TIMEOUT;

  /**
   * Flag indicating whether hostname verification is disabled.
   */
  protected boolean skipHostnameChecks = DEFAULT_SKIP_HOSTNAME_CHECKS;

  /**
   * Constructor initializing the protocol with a certificate validator.
   *
   * @param validator the certificate validator for SSL authentication
   */
  public AbstractVOMSProtocol(X509CertChainValidatorExt validator) {

    this.validator = validator;
  }

  /**
   * Constructor initializing the protocol with a validator, listener, and timeout settings.
   *
   * @param validator the certificate validator for SSL authentication
   * @param listener the listener for protocol events
   * @param connectTimeout the socket connection timeout in milliseconds
   * @param readTimeout the socket read timeout in milliseconds
   */
  public AbstractVOMSProtocol(X509CertChainValidatorExt validator, VOMSProtocolListener listener,
      int connectTimeout, int readTimeout) {

    this.validator = validator;
    this.connectTimeout = connectTimeout;
    this.readTimeout = readTimeout;
    this.listener = listener;
  }

  /**
   * Creates an SSL socket factory using the provided credential and validator.
   *
   * @param credential the client credential for SSL authentication
   * @return an SSL socket factory configured with the given credential and validator
   */
  protected SSLSocketFactory getSSLSocketFactory(X509Credential credential) {

    SSLSocketFactoryProvider sslSocketFactoryProvider =
        new SSLSocketFactoryProvider(credential, validator, skipHostnameChecks);
    return sslSocketFactoryProvider.getSSLSockectFactory();
  }

  /**
   * Retrieves the connection timeout value.
   *
   * @return the connection timeout in milliseconds
   */
  public int getConnectTimeout() {

    return connectTimeout;
  }

  /**
   * Sets the connection timeout for the underlying socket.
   *
   * @param connectTimeout the connection timeout in milliseconds
   */
  public void setConnectTimeout(int connectTimeout) {

    this.connectTimeout = connectTimeout;
  }

  /**
   * Retrieves the read timeout value.
   *
   * @return the read timeout in milliseconds
   */
  public int getReadTimeout() {

    return readTimeout;
  }

  /**
   * Sets the read timeout for the underlying socket.
   *
   * @param readTimeout the read timeout in milliseconds
   */
  public void setReadTimeout(int readTimeout) {

    this.readTimeout = readTimeout;
  }

  /**
   * Checks whether hostname verification is disabled.
   *
   * @return {@code true} if hostname checks are skipped, {@code false} otherwise
   */
  public boolean isSkipHostnameChecks() {

    return skipHostnameChecks;
  }

  /**
   * Configures whether SSL hostname verification should be skipped.
   *
   * @param skipHostnameChecks {@code true} to disable hostname verification, {@code false} to
   *        enable it
   */
  public void setSkipHostnameChecks(boolean skipHostnameChecks) {

    this.skipHostnameChecks = skipHostnameChecks;
  }
}
