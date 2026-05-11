// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.italiangrid.voms.test.utils.VOMSAA;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestACParser implements Fixture {

  static VOMSAA aa;
  static PEMCredential holder;

  @BeforeAll
  public static void setup() throws KeyStoreException, CertificateException, IOException {

    aa = Utils.getVOMSAA();
  }

  @Test
  public void test() throws Exception {

    PEMCredential holder = Utils.getTestUserCredential();
    ProxyCertificate proxy = aa.createVOMSProxy(holder, defaultVOFqans);

    DefaultVOMSACParser parser = new DefaultVOMSACParser();
    List<VOMSAttribute> attrs = parser.parse(proxy.getCertificateChain());
    Assertions.assertFalse(attrs.isEmpty());
    Assertions.assertEquals(1, attrs.size());
    Assertions.assertEquals(defaultVOFqans, attrs.get(0).getFQANs());
  }

  @Test
  public void testParseNullChainFailure() {

    DefaultVOMSACParser parser = new DefaultVOMSACParser();
    Assertions.assertThrows(NullPointerException.class, () -> parser.parse(null));
  }

  @Test
  public void testEmptyFqansParsing() throws Exception {

    PEMCredential holder = Utils.getTestUserCredential();
    List<String> fqans = Collections.emptyList();
    ProxyCertificate proxy = aa.createVOMSProxy(holder, fqans);

    DefaultVOMSACParser parser = new DefaultVOMSACParser();

    try {
      parser.parse(proxy.getCertificateChain());
    } catch (VOMSError e) {
      Assertions.assertEquals(
          "Non conformant VOMS Attribute certificate: unsupported attribute values encoding.",
          e.getMessage());
      return;
    }

    Assertions.fail("No exception raised when parsing invalid VOMS AC!");
  }
}
