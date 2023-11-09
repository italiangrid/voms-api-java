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
package org.italiangrid.voms.test.cred;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.italiangrid.voms.credential.impl.AbstractLoadCredentialsStrategy;
import org.italiangrid.voms.credential.impl.DefaultLoadCredentialsStrategy;
import org.italiangrid.voms.util.FilePermissionHelper;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.PasswordSupplier;
import eu.emi.security.authn.x509.impl.X500NameUtils;

public class TestLoadCredential {

  public static final String keyPassword = "pass";

  public static final String pemCert = "src/test/resources/certs/test0.cert.pem";
  public static final String pemKey = "src/test/resources/certs/test0.key.pem";
  public static final String pkcs12Cred = "src/test/resources/certs/test0.p12";

  public static final String TEST_CERT_SUBJECT = "CN=test0, O=IGI, C=IT";
  public static final String PROXY_TMP_PATH = "/tmp/tempProxy";

  public static final String emptyHome = "src/test/resources/homes/empty";
  public static final String emptyGlobusHome = "src/test/resources/homes/empty.globus";
  public static final String pemCredsHome = "src/test/resources/homes/pem-creds";
  public static final String pkcs12CredsHome = "src/test/resources/homes/pkcs12-creds";

  @BeforeClass
  public static void setupFilePermissions() {

    FilePermissionHelper.setPrivateKeyPermissions(pemCredsHome + "/.globus/userkey.pem");
    FilePermissionHelper.setPKCS12Permissions(pkcs12CredsHome + "/.globus/usercred.p12");
  }

  static class TestPasswordFinder implements PasswordSupplier {

    public char[] getPassword() {

      return keyPassword.toCharArray();
    }
  }

  static class NullPasswordSupplier implements PasswordSupplier {

    public char[] getPassword() {

      return null;
    }
  }

  @Test
  public void testNoCredentialsFoundSuccess() {

    AbstractLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyHome);
    X509Credential cred = strategy.loadCredentials(new NullPasswordSupplier());
    assertNull(cred);
  }

  @Test
  public void testNoCredentialsFoundEmptyGlobusSuccess() {

    AbstractLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyGlobusHome);
    X509Credential cred = strategy.loadCredentials(new NullPasswordSupplier());
    assertNull(cred);
  }

  @Test
  public void testPEMCredentialLoadingSuccess() {

    AbstractLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pemCredsHome);
    X509Credential cred = strategy.loadCredentials(new TestPasswordFinder());
    assertNotNull(cred);
    assertTrue(
        X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
  }

  @Test
  public void testPKCS12CredentialLoadingSuccess() {

    AbstractLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pkcs12CredsHome);
    X509Credential cred = strategy.loadCredentials(new TestPasswordFinder());
    assertNotNull(cred);
    assertTrue(
        X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
  }

}
