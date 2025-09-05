// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

/**
 * 
 */
package org.italiangrid.voms.test;

import static java.util.Objects.isNull;
import static org.italiangrid.voms.store.impl.DefaultVOMSTrustStore.buildDefaultTrustedDirs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.LSCInfo;
import org.italiangrid.voms.store.impl.VOTrustStore;
import org.italiangrid.voms.util.NullListener;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;


public class TestVOTrustStore {

  private static final String TEST_VO = "test.vo";

  @Test(expected = VOMSError.class)
  public void testEmptyTrustDirsFailure() {

    @SuppressWarnings({"unused", "unchecked"})
    VOTrustStore store = new VOTrustStore(Collections.EMPTY_LIST, TEST_VO, NullListener.INSTANCE);

  }

  @Test(expected = VOMSError.class)
  public void testNonExistentTrustDirsFailure() {

    List<String> trustDirs =
        Arrays.asList(new String[] {"/etc/do/not/exist", "/etc/grid-security/vomsdir"});

    @SuppressWarnings("unused")
    VOTrustStore store = new VOTrustStore(trustDirs, TEST_VO, NullListener.INSTANCE);
  }

  // FIXME: This test assumes /etc/grid-security/vomsdir exists in
  // the machine where the test run. Disabling it for now.
  public void testDefaultTrustDir() {

    VOTrustStore store =
        new VOTrustStore(buildDefaultTrustedDirs(), TEST_VO, NullListener.INSTANCE);

    List<String> trustDirs = store.getLocalTrustedDirectories();

    assertEquals(1, trustDirs.size());
    assertEquals(VOTrustStore.DEFAULT_VOMS_DIR, trustDirs.get(0));

  }

  @Test
  public void testEmptyTrustDir() {

    List<String> trustDirs = Arrays.asList("src/test/resources/empty-vomsdir");

    @SuppressWarnings("unused")
    VOTrustStore store = new VOTrustStore(trustDirs, TEST_VO, NullListener.INSTANCE);

  }

  @Test
  public void testCertificateParsing() throws FileNotFoundException, IOException {

    String vomsDir = "src/test/resources/vomsdir";
    String certFileName = "src/test/resources/vomsdir/test-host.cnaf.infn.it.pem";
    X509Certificate cert =
        CertificateUtils.loadCertificate(new FileInputStream(certFileName), Encoding.PEM);

    List<String> trustDirs = Arrays.asList(new String[] {vomsDir});

    VOTrustStore store = new VOTrustStore(trustDirs, TEST_VO, NullListener.INSTANCE);

    assertEquals(1, store.getLocalAACertificates().size());

    assertTrue(cert.getSubjectX500Principal()
      .equals(store.getLocalAACertificates().get(0).getSubjectX500Principal()));
  }

  @Test
  public void testLSCInStore() {

    List<String> trustDirs = Arrays.asList("src/test/resources/vomsdir");

    VOTrustStore store = new VOTrustStore(trustDirs, TEST_VO, NullListener.INSTANCE);
    
    assertFalse(isNull(store.getLSC(TEST_VO, "test-host.cnaf.infn.it")));
    assertFalse(isNull(store.getLSC(TEST_VO, "test-multichain.cnaf.infn.it")));

  }
  
  @Test
  public void testLSCNotInStore() {

    List<String> trustDirs = Arrays.asList("src/test/resources/vomsdir");

    VOTrustStore store = new VOTrustStore(trustDirs, TEST_VO, NullListener.INSTANCE);
    
    assertTrue(isNull(store.getLSC("test.vo.1", "test-host.cnaf.infn.it")));
    assertTrue(isNull(store.getLSC("test.vo.1", "test-multichain.cnaf.infn.it")));

  }
  
}
