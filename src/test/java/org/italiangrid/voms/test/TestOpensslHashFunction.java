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
package org.italiangrid.voms.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.CertificateValidatorBuilder.OpensslHashFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class TestOpensslHashFunction {

  static final String trustAnchorsDir = "src/test/resources/trust-anchors";
  static final String md5TrustAnchorsDir = "src/test/resources/md5-trust-anchors";
  static final String sha1TrustAnchorsDir = "src/test/resources/sha1-trust-anchors";

  static final String keyPassword = "pass";
  static final String userCert = "src/test/resources/certs/test0.cert.pem";
  static final String userKey = "src/test/resources/certs/test0.key.pem";

  static PEMCredential cred;

  @BeforeAll
  public static void init() throws KeyStoreException, CertificateException, IOException {

    cred = new PEMCredential(userKey, userCert, keyPassword.toCharArray());
  }

  @Test
  public void testDefaultHashIsMD5() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(md5TrustAnchorsDir);

    ValidationResult result = builder.build().validate(cred.getCertificateChain());

    assertTrue(result.isValid());

  }

  @Test
  public void testSHA1Hash() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(sha1TrustAnchorsDir).opensslHashFunction(OpensslHashFunction.SHA1);

    ValidationResult result = builder.build().validate(cred.getCertificateChain());

    assertTrue(result.isValid());
  }

  @Test
  public void testMD5HashFailsOnSHA1Dir() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(sha1TrustAnchorsDir);

    ValidationResult result = builder.build().validate(cred.getCertificateChain());

    assertFalse(result.isValid());
    assertEquals(2, result.getErrors().size());
    assertEquals("No trusted CA certificate was found for the certificate chain",
        result.getErrors().get(0).getMessage());

    assertEquals("Trusted issuer of this certificate was not established",
        result.getErrors().get(1).getMessage());

    assertEquals(cred.getCertificate().getSubjectX500Principal(),
        result.getErrors().get(1).getChain()[0].getSubjectX500Principal());

  }

  @Test
  public void testSHA1FailsOnMD5Dir() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(md5TrustAnchorsDir).opensslHashFunction(OpensslHashFunction.SHA1);

    ValidationResult result = builder.build().validate(cred.getCertificateChain());

    assertFalse(result.isValid());
    assertEquals(2, result.getErrors().size());
    assertEquals("No trusted CA certificate was found for the certificate chain",
        result.getErrors().get(0).getMessage());

    assertEquals("Trusted issuer of this certificate was not established",
        result.getErrors().get(1).getMessage());

    assertEquals(cred.getCertificate().getSubjectX500Principal(),
        result.getErrors().get(1).getChain()[0].getSubjectX500Principal());

  }

}
