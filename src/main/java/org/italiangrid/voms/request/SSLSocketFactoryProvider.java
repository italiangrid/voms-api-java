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
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.SocketFactoryCreator;

/**
 * Provider for a SSL socket factory configured using CAnL. 
 * 
 * 
 * @author valerioventuri
 *
 */
public class SSLSocketFactoryProvider {

  private static Logger log = LoggerFactory.getLogger(SSLSocketFactoryProvider.class);

  private X509Credential credential;
  
  public SSLSocketFactoryProvider(X509Credential credential) {
    
    this.credential = credential;
  }
  
  /**
   * Get the SSL socket factory.
   * 
   * @return the {@link SSLSocketFactory} object
   */
  public SSLSocketFactory getSSLSockectFactory() {

    SSLContext context = null;

    try {

      context = SSLContext.getInstance("SSLv3");

    } catch (NoSuchAlgorithmException e) {

      log.error("No SSLv3 algorithm, cannot instanciate SSLContext");
      throw new VOMSError("No SSLv3 algorithm, cannot instanciate SSLContext", e);
    }

    KeyManager[] keyManagers = new KeyManager[] {credential.getKeyManager()};

    OpensslCertChainValidator validator = new OpensslCertChainValidator(
        DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS, 60000L);

    X509TrustManager trustManager = SocketFactoryCreator.getSSLTrustManager(validator);

    TrustManager[] trustManagers = new TrustManager[] {trustManager};

    SecureRandom secureRandom = null;

    try {

      secureRandom = SecureRandom.getInstance("SHA1PRNG");

    } catch (NoSuchAlgorithmException e) {

      log.error("No algorithm SHA1PRNG, unable to initialize SSL context");
      throw new VOMSError("No algorithm SHA1PRNG, unable to initialize SSL context", e);
    }

    try {

      context.init(keyManagers, trustManagers, secureRandom);

    } catch (KeyManagementException e) {

      log.error("No algorithm SHA1PRNG, unable to initialize SSL context");
      throw new VOMSError("No algorithm SHA1PRNG, unable to initialize SSL context", e);
    }

    return context.getSocketFactory();
  }

}
