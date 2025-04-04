// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.utils;

import java.util.Arrays;
import java.util.List;

public interface Fixture {

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

  static final String holderCert2 = "src/test/resources/certs/test1.cert.pem";
  static final String holderKey2 = "src/test/resources/certs/test1.key.pem";

  static final String vomsdir = "src/test/resources/vomsdir";
  static final String vomsdir_fake_aa_cert = "src/test/resources/vomsdir-fake-aa-cert";
  static final String vomsdir_expired_aa_cert = "src/test/resources/vomsdir-expired-aa-cert";

  static final String trustAnchorsDir = "src/test/resources/trust-anchors";

  static final String defaultVO = "test.vo";
  static final String defaultVOHost = "test-host.cnaf.infn.it";
  static final int defaultVOPort = 15000;

  static final List<String> defaultVOFqans = Arrays.asList("/test.vo");

}
