// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import eu.emi.security.authn.x509.impl.PEMCredential;

public class TestACSupport {

  static final String keyPassword = "pass";

  static final String aaCert = "src/test/resources/certs/test_host_cnaf_infn_it.cert.pem";
  static final String aaKey = "src/test/resources/certs/test_host_cnaf_infn_it.key.pem";

  static final String aaCert2 = "src/test/resources/certs/wilco_cnaf_infn_it.cert.pem";
  static final String aaKey2 = "src/test/resources/certs/wilco_cnaf_infn_it.key.pem";

  static final String expiredCert = "src/test/resources/certs/expired.cert.pem";
  static final String expiredKey = "src/test/resources/certs/expired.key.pem";

  static final String revokedCert = "src/test/resources/certs/revoked.cert.pem";
  static final String revokedKey = "src/test/resources/certs/revoked.key.pem";

  static final String holderCert = "src/test/resources/certs/test0.cert.pem";
  static final String holderKey = "src/test/resources/certs/test0.key.pem";

  static final String defaultVO = "test.vo";
  static final String defaultHost = "test-host.cnaf.infn.it";
  static final int port = 15000;

  static final String vomsdir = "src/test/resources/vomsdir";
  static final String trustAnchorsDir = "src/test/resources/trust-anchors";
  
  static PEMCredential aaCredential;
  static PEMCredential holderCredential;
  
  static void initializeCredentials() throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
    aaCredential = new PEMCredential(new FileInputStream(aaKey),
        new FileInputStream(aaCert), (char[]) null);
    holderCredential = new PEMCredential(new FileInputStream(holderKey),
        new FileInputStream(holderCert), keyPassword.toCharArray());
  }

}
