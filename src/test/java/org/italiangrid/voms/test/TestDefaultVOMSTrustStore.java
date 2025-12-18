// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

/**
 * 
 */
package org.italiangrid.voms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.util.NullListener;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;

/**
 * @author Andrea Ceccanti
 * 
 */
public class TestDefaultVOMSTrustStore {

  @Test(expected = VOMSError.class)
  public void testEmptyTrustDirsFailure() {

    @SuppressWarnings({"unused", "unchecked"})
    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(Collections.EMPTY_LIST);

  }

  @Test(expected = VOMSError.class)
  public void testNonExistentTrustDirsFailure() {

    List<String> trustDirs =
        Arrays.asList(new String[] {"/etc/do/not/exist", "/etc/grid-security/vomsdir"});

    @SuppressWarnings("unused")
    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);
  }

  // FIXME: This test assumes /etc/grid-security/vomsdir exists in the machine
  // where the test run. Disabling it
  // for now.
  public void testDefaultTrustDir() {

    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore();

    List<String> trustDirs = store.getLocalTrustedDirectories();

    assertEquals(1, trustDirs.size());
    assertEquals(DefaultVOMSTrustStore.DEFAULT_VOMS_DIR, trustDirs.get(0));

  }

  @Test
  public void testEmptyTrustDir() {

    List<String> trustDirs = Arrays.asList("src/test/resources/empty-vomsdir");

    @SuppressWarnings("unused")
    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);

  }

  @Test
  public void testCertificateParsing() throws FileNotFoundException, IOException {

    String vomsDir = "src/test/resources/vomsdir";
    String certFileName = "src/test/resources/vomsdir/test-host.cnaf.infn.it.pem";
    X509Certificate cert =
        CertificateUtils.loadCertificate(new FileInputStream(certFileName), Encoding.PEM);

    List<String> trustDirs = Arrays.asList(new String[] {vomsDir});

    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);

    assertEquals(1, store.getLocalAACertificates().size());

    assertTrue(cert.getSubjectX500Principal()
      .equals(store.getLocalAACertificates().get(0).getSubjectX500Principal()));
  }

  @Test
  public void testAllLSCInStore() {

    List<String> trustDirs = Arrays.asList("src/test/resources/vomsdir");

    DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs, NullListener.INSTANCE);

    assertNotNull(store.getLSC("test.vo", "test-host.cnaf.infn.it"));
    assertNotNull(store.getLSC("test.vo", "test-multichain.cnaf.infn.it"));
    assertNotNull(store.getLSC("test.vo.1", "wilco.cnaf.infn.it"));

  }

  @Test
  public void testLSCForVoInStore() {

    List<String> trustDirs = Arrays.asList("src/test/resources/vomsdir");

    DefaultVOMSTrustStore store =
        new DefaultVOMSTrustStore(trustDirs, Arrays.asList("test.vo"), NullListener.INSTANCE);

    assertNotNull(store.getLSC("test.vo", "test-host.cnaf.infn.it"));
    assertNotNull(store.getLSC("test.vo", "test-multichain.cnaf.infn.it"));
    assertNull(store.getLSC("test.vo.1", "wilco.cnaf.infn.it"));

  }

  public void testUpdatingVOMSTrustStore() {

  }
}
