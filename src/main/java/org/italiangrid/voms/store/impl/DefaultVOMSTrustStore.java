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
package org.italiangrid.voms.store.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.security.auth.x500.X500Principal;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;
import org.italiangrid.voms.util.NullListener;

import eu.emi.security.authn.x509.helpers.trust.OpensslTruststoreHelper;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;

/**
 * 
 * The default implementation for the VOMS trust store. This implementation
 * <b>does not</b> refresh the trust information on a periodic basis. For an
 * updating trust store see {@link DefaultUpdatingVOMSTrustStore}.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class DefaultVOMSTrustStore implements VOMSTrustStore {

  /**
   * The default directory where local VOMS trust information is rooted:
   * {@value #DEFAULT_VOMS_DIR}
   **/
  public static final String DEFAULT_VOMS_DIR = "/etc/grid-security/vomsdir";

  /**
   * The filename suffix used to match certificates in the VOMS local trust
   * directories
   **/
  public static final String CERTIFICATE_FILENAME_SUFFIX = ".pem";

  /**
   * The filename suffix used to match LSC files in the VOMS local trust
   * directories
   **/
  public static final String LSC_FILENAME_SUFFIX = ".lsc";

  /**
   * The list of local trusted directories that is searched for trust
   * information (certs or LSC files)
   **/
  private final List<String> localTrustedDirs;

  /** Map of local parsed AA certificates keyed by certificate subject hash **/
  private Map<String, X509Certificate> localAACertificatesByHash = new HashMap<String, X509Certificate>();

  /** The set of local parsed LSC information keyed by VO **/
  private Map<String, Set<LSCInfo>> localLSCInfo = new HashMap<String, Set<LSCInfo>>();

  /**
   * The trust store status listener that will be notified of changes in this
   * trust store
   **/
  private VOMSTrustStoreStatusListener listener;

  /** The read/write lock that implements thread safety for this store **/
  protected final ReadWriteLock rwLock = new ReentrantReadWriteLock();

  /** A reference to the read lock **/
  protected final Lock read = rwLock.readLock();

  /** A reference to the write lock **/
  protected final Lock write = rwLock.writeLock();

  /** A lock to guard the setting of the status listener **/
  protected final Object listenerLock = new Object();

  /**
   * Builds a list of trusted directories containing only
   * {@link #DEFAULT_VOMS_DIR}.
   * 
   * @return 
   *    a list of default trusted directory containing the 
   *    {@link #DEFAULT_VOMS_DIR}
   **/
  protected static List<String> buildDefaultTrustedDirs() {

    List<String> tDirs = new ArrayList<String>();
    tDirs.add(DEFAULT_VOMS_DIR);
    return tDirs;
  }

  /**
   * 
   * @param localTrustDirs
   *          a non-null list of local trust directories
   * @param listener
   *          the {@link VOMSTrustStoreStatusListener} to use for this 
   *          trust store
   * @throws IllegalArgumentException
   *           when the list passed as argument is null
   * 
   */
  public DefaultVOMSTrustStore(List<String> localTrustDirs,
    VOMSTrustStoreStatusListener listener) {

    if (localTrustDirs == null)
      throw new IllegalArgumentException(
        "Please provide a non-null list of local trust directories!");

    this.localTrustedDirs = localTrustDirs;
    this.listener = listener;
    loadTrustInformation();
  }

  public DefaultVOMSTrustStore(VOMSTrustStoreStatusListener listener) {

    this(buildDefaultTrustedDirs(), listener);
  }

  public DefaultVOMSTrustStore(List<String> localTrustDirs) {

    this(localTrustDirs, NullListener.INSTANCE);
  }

  /**
   * Default constructor.
   * 
   * Sets the local trusted directories to the default of
   * {@value #DEFAULT_VOMS_DIR}.
   * 
   * 
   */
  public DefaultVOMSTrustStore() {

    this(buildDefaultTrustedDirs());
  }

  public List<String> getLocalTrustedDirectories() {

    read.lock();
    try {
      return localTrustedDirs;
    } finally {
      read.unlock();
    }
  }

  public List<X509Certificate> getLocalAACertificates() {

    read.lock();

    try {
      return Collections.unmodifiableList(new ArrayList<X509Certificate>(
        localAACertificatesByHash.values()));
    } finally {
      read.unlock();
    }
  }

  public LSCInfo getLSC(String voName, String hostname) {

    read.lock();

    try {

      Set<LSCInfo> candidates = localLSCInfo.get(voName);

      if (candidates == null)
        return null;

      for (LSCInfo lsc : candidates) {

        if (lsc.getHostname().equals(hostname))
          return lsc;

      }

      return null;

    } finally {
      read.unlock();
    }
  }

  /**
   * Loads all the certificates in the local directory. Only files with the
   * extension matching the {@link #CERTIFICATE_FILENAME_PATTERN} are
   * considered.
   * 
   * @param directory
   */
  private void loadCertificatesFromDirectory(File directory) {

    directorySanityChecks(directory);

    synchronized (listenerLock) {
      listener.notifyCertficateLookupEvent(directory.getAbsolutePath());
    }

    File[] certFiles = directory.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name) {

        return name.endsWith(CERTIFICATE_FILENAME_SUFFIX);
      }
    });

    for (File f : certFiles)
      loadCertificateFromFile(f);

  }

  /**
   * Loads a VOMS AA certificate from a given file and stores this certificate
   * in the local map of trusted VOMS AA certificate.
   * 
   * @param file
   */
  private void loadCertificateFromFile(File file) {

    certificateFileSanityChecks(file);

    try {

      X509Certificate aaCert = CertificateUtils.loadCertificate(
        new FileInputStream(file), Encoding.PEM);

      // Get certificate subject hash, using the CANL implementation for CA
      // files
      String aaCertHash = getOpensslCAHash(aaCert.getSubjectX500Principal());

      // Store certificate in the local map
      localAACertificatesByHash.put(aaCertHash, aaCert);

      synchronized (listenerLock) {
        listener.notifyCertificateLoadEvent(aaCert, file);
      }

    } catch (IOException e) {
      String errorMessage = String.format(
        "Error parsing VOMS trusted certificate from %s. Reason: %s",
        file.getAbsolutePath(), e.getMessage());
      throw new VOMSError(errorMessage, e);
    }

  }

  /**
   * 
   * @param directory
   */
  private void loadLSCFromDirectory(File directory) {

    directorySanityChecks(directory);

    synchronized (listenerLock) {
      listener.notifyLSCLookupEvent(directory.getAbsolutePath());
    }

    File[] lscFiles = directory.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name) {

        return name.endsWith(LSC_FILENAME_SUFFIX);
      }
    });

    if (lscFiles.length == 0)
      return;

    DefaultLSCFileParser lscParser = new DefaultLSCFileParser();

    // In the VOMS trust anchor structure, LSC files are contained in a
    // directory named
    // as the VO the LSC belongs to
    String voName = directory.getName();

    for (File lsc : lscFiles) {

      String lscFileName = lsc.getName();

      // In the VOMS trust anchor structure, LSC files are named as
      // <hostname>.lsc where hostname
      // is the name of host where the VOMS AA is running
      String hostname = lscFileName.substring(0,
        lscFileName.indexOf(LSC_FILENAME_SUFFIX));

      LSCInfo info = null;

      info = lscParser.parse(voName, hostname, lsc);

      Set<LSCInfo> localLscForVo = localLSCInfo.get(voName);

      if (localLscForVo == null) {
        localLscForVo = new HashSet<LSCInfo>();
        localLSCInfo.put(voName, localLscForVo);
      }

      localLscForVo.add(info);

      listener.notifyLSCLoadEvent(info, lsc);

    }

  }

  /**
   * Performs basic sanity checks performed on a file supposed to hold a VOMS AA
   * certificate.
   * 
   * @param certFile
   */
  private void certificateFileSanityChecks(File certFile) {

    if (!certFile.exists())
      throw new VOMSError("Local VOMS trusted certificate does not exist:"
        + certFile.getAbsolutePath());

    if (!certFile.canRead())
      throw new VOMSError("Local VOMS trusted certificate is not readable:"
        + certFile.getAbsolutePath());

  }

  /**
   * Performs basic sanity checks on a directory that is supposed to contain
   * VOMS AA certificates and LSC files.
   * 
   * @param directory
   */
  private void directorySanityChecks(File directory) {

    if (!directory.exists())
      throw new VOMSError("Local trust directory does not exists:"
        + directory.getAbsolutePath());

    if (!directory.isDirectory())
      throw new VOMSError("Local trust directory is not a directory:"
        + directory.getAbsolutePath());

    if (!directory.canRead())
      throw new VOMSError("Local trust directory is not readable:"
        + directory.getAbsolutePath());

    if (!directory.canExecute())
      throw new VOMSError("Local trust directory is not traversable:"
        + directory.getAbsolutePath());

  }

  private void cleanupStores() {

    localAACertificatesByHash.clear();
    localLSCInfo.clear();

  }

  public void loadTrustInformation() {

    write.lock();

    try {

      if (localTrustedDirs.isEmpty()) {
        throw new VOMSError(
          "No local trust directory was specified for this trust store. Please provide at least one path where LSC and VOMS service certificates will be searched for.");
      }

      cleanupStores();

      for (String localDir : localTrustedDirs) {

        File baseTrustDir = new File(localDir);

        // Legacy VOMS dir structure put all the certificates in the base trust
        // directory
        loadCertificatesFromDirectory(baseTrustDir);

        // Load LSC and certificates files starting from each of the
        // sub-directory of the starting trust info directory
        File[] voDirs = baseTrustDir.listFiles(new FileFilter() {

          public boolean accept(File pathname) {

            return pathname.isDirectory();
          }
        });

        for (File voDir : voDirs) {
          loadLSCFromDirectory(voDir);
          loadCertificatesFromDirectory(voDir);
        }
      }

    } finally {
      write.unlock();
    }
  }

  private String getOpensslCAHash(X500Principal principal) {

    return OpensslTruststoreHelper.getOpenSSLCAHash(principal, false);

  }

  public X509Certificate getAACertificateBySubject(X500Principal aaCertSubject) {

    read.lock();
    try {
      String theCertHash = getOpensslCAHash(aaCertSubject);
      return localAACertificatesByHash.get(theCertHash);
    } finally {
      read.unlock();
    }
  }

  public Map<String, Set<LSCInfo>> getAllLSCInfo() {

    read.lock();
    try {
      return Collections.unmodifiableMap(localLSCInfo);
    } finally {
      read.unlock();
    }
  }

  public void setStatusListener(VOMSTrustStoreStatusListener statusListener) {

    synchronized (listenerLock) {
      this.listener = statusListener;
    }
  }
}
