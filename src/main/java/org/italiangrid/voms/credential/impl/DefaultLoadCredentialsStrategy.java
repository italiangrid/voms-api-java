// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.credential.impl;

import eu.emi.security.authn.x509.helpers.PasswordSupplier;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.credential.ProxyNamingPolicy;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.X509Credential;

/**
 * The default strategy used to load user credentials when no file is explicitly pointed out by the
 * user of this API.
 * 
 * Credentials are searched in the following places (in sequence):
 * 
 * <ul>
 * 
 * <li>If the <code>X509_USER_CERT</code> and <code>X509_USER_KEY</code> <b>environment
 * variables</b> are set, their values are used to load the user credentials</li>
 * 
 * <li>If the <code>X509_USER_CERT</code> and <code>X509_USER_KEY</code> <b>system properties</b>
 * are set, their values are used to load the user credentials</li>
 * 
 * <li>If the <code>PKCS12_USER_CERT</code> <b>environment variable</b> is set, its value is used to
 * load the user credentials.</li>
 * 
 * <li>If the <code>PKCS12_USER_CERT</code> <b>system property</b> is set, its value is used to load
 * the user credentials.</li>
 * 
 * <li>The content of the <code>.globus</code> directory in the user's home is searched for a PEM
 * certificate (in the <code>usercert.pem</code> and <code>userkey.pem</code> files).</li>
 * 
 * <li>The content of the .globus directory in the user's home is searched for a PKC12 certificate
 * (in the <code>usercert.p12</code> file).</li>
 * </ul>
 * 
 */
public class DefaultLoadCredentialsStrategy extends AbstractLoadCredentialsStrategy {

  private static final String GLOBUS_PKCS12_CRED_PATH_SUFFIX = ".globus/usercred.p12";
  private static final String GLOBUS_PEM_CERT_PATH_SUFFIX = ".globus/usercert.pem";
  private static final String GLOBUS_PEM_KEY_PATH_SUFFIX = ".globus/userkey.pem";

  public static final String HOME_PROPERTY = "user.home";
  public static final String TMPDIR_PROPERTY = "java.io.tmpdir";
  public static final String TMPDIR_PATH = "/tmp";

  private static final ProxyNamingPolicy proxyPathBuilder = new DefaultProxyPathBuilder();

  private String home;
  private String tmpDir;

  /**
   * Constructs a strategy with specified home and temp directories and a listener.
   *
   * @param homeFolder the home directory path
   * @param tempDir the temporary directory path
   * @param listener event listener for credential loading
   */
  public DefaultLoadCredentialsStrategy(String homeFolder, String tempDir,
      LoadCredentialsEventListener listener) {

    super(listener);

    this.home = homeFolder;
    this.tmpDir = tempDir;

    if (home == null)
      throw new VOMSError(HOME_PROPERTY + " not found in system properties!");
  }

  /**
   * Constructs a strategy with a specified home directory.
   *
   * @param homeFolder the home directory path
   */
  public DefaultLoadCredentialsStrategy(String homeFolder) {

    this(homeFolder, System.getProperty(TMPDIR_PROPERTY), NullListener.INSTANCE);
  }

  /**
   * Constructs a strategy using default system properties.
   */
  public DefaultLoadCredentialsStrategy() {
    this(System.getProperty(HOME_PROPERTY), System.getProperty(TMPDIR_PROPERTY),
        NullListener.INSTANCE);
  }

  /**
   * Constructs a strategy with a specified event listener.
   *
   * @param listener the credential loading event listener
   */
  public DefaultLoadCredentialsStrategy(LoadCredentialsEventListener listener) {
    this(System.getProperty(HOME_PROPERTY), System.getProperty(TMPDIR_PROPERTY), listener);
  }

  /**
   * Looks for the value of a given property in the environment or system properties.
   *
   * @param propName the property to look for
   * @return the property value, or null if not found
   */
  public String getFromEnvOrSystemProperty(String propName) {
    String val = System.getenv(propName);
    if (val == null)
      val = System.getProperty(propName);
    return val;
  }

  /**
   * Loads user credentials using the available strategies.
   *
   * @param pf the password supplier
   * @return the loaded credentials
   */
  public X509Credential loadCredentials(PasswordSupplier pf) {
    if (pf == null)
      throw new IllegalArgumentException("Please provide a non-null password finder!");

    try {
      X509Credential cred = loadPEMCredentialFromEnv(pf);
      if (cred == null)
        cred = loadPKCS12CredentialFromEnv(pf);
      if (cred == null)
        cred = loadPEMCredentialsFromGlobusDir(pf);
      if (cred == null)
        cred = loadPKCS12CredentialsFromGlobusDir(pf);
      return cred;
    } catch (Exception e) {
      throw new VOMSError("Error loading credential: " + e.getMessage(), e);
    }
  }

  /**
   * Loads a proxy credential based on the user ID.
   *
   * @return the loaded proxy credential or null if not found
   */
  protected X509Credential loadProxyFromUID() {
    String uid = getFromEnvOrSystemProperty(VOMS_USER_ID);
    if (uid != null) {
      String proxyFile = proxyPathBuilder.buildProxyFileName(tmpDir, Integer.parseInt(uid));
      return loadProxyCredential(proxyFile);
    }
    return null;
  }

  /**
   * Loads a proxy credential from environment variables.
   *
   * @return the loaded proxy credential or null if not found
   */
  protected X509Credential loadProxyFromEnv() {
    String proxyPath = getFromEnvOrSystemProperty(X509_USER_PROXY);
    if (proxyPath != null)
      return loadProxyCredential(proxyPath);
    return null;
  }

  /**
   * Loads a PEM credential from environment variables.
   *
   * @param pf the password supplier
   * @return the loaded credential or null if not found
   */
  protected X509Credential loadPEMCredentialFromEnv(PasswordSupplier pf) {
    String certPath = getFromEnvOrSystemProperty(X509_USER_CERT);
    String keyPath = getFromEnvOrSystemProperty(X509_USER_KEY);
    if (certPath != null && keyPath != null) {
      return loadPEMCredential(keyPath, certPath, pf);
    }
    return null;
  }

  /**
   * Loads a PKCS12 credential from environment variables.
   *
   * @param pf the password supplier
   * @return the loaded credential or null if not found
   */
  protected X509Credential loadPKCS12CredentialFromEnv(PasswordSupplier pf) {
    String pkcs12Path = getFromEnvOrSystemProperty(PKCS12_USER_CERT);
    if (pkcs12Path != null) {
      return loadPKCS12Credential(pkcs12Path, pf);
    }
    return null;
  }

  /**
   * Loads a PKCS12 credential from the Globus directory.
   *
   * @param pf the password supplier
   * @return the loaded credential or null if not found
   */
  protected X509Credential loadPKCS12CredentialsFromGlobusDir(PasswordSupplier pf) {
    String credPath = String.format("%s/%s", home, GLOBUS_PKCS12_CRED_PATH_SUFFIX);
    return loadPKCS12Credential(credPath, pf);
  }

  /**
   * Loads a PEM credential from the Globus directory.
   *
   * @param pf the password supplier
   * @return the loaded credential or null if not found
   */
  protected X509Credential loadPEMCredentialsFromGlobusDir(PasswordSupplier pf) {
    String certPath = String.format("%s/%s", home, GLOBUS_PEM_CERT_PATH_SUFFIX);
    String keyPath = String.format("%s/%s", home, GLOBUS_PEM_KEY_PATH_SUFFIX);
    return loadPEMCredential(keyPath, certPath, pf);
  }
}
