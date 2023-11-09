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
  static final String defaultHost = "test-host.test.example";
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
