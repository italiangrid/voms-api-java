package org.italiangrid.voms.test;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.italiangrid.voms.util.CertificateValidatorBuilder.OpensslHashFunction;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

  @BeforeClass
  public static void init()
    throws KeyStoreException, CertificateException, IOException {

    cred = new PEMCredential(userKey, userCert, keyPassword.toCharArray());
  }

  @Test
  public void testDefaultHashIsMD5() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(md5TrustAnchorsDir);

    ValidationResult result = builder.build()
      .validate(cred.getCertificateChain());

    Assert.assertTrue(result.isValid());

  }

  @Test
  public void testSHA1Hash() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(sha1TrustAnchorsDir)
      .opensslHashFunction(OpensslHashFunction.SHA1);

    ValidationResult result = builder.build()
      .validate(cred.getCertificateChain());

    Assert.assertTrue(result.isValid());
  }

  @Test
  public void testMD5HashFailsOnSHA1Dir() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(sha1TrustAnchorsDir);

    ValidationResult result = builder.build()
      .validate(cred.getCertificateChain());

    Assert.assertFalse(result.isValid());
    Assert.assertEquals(2, result.getErrors().size());
    Assert.assertEquals(
      "No trusted CA certificate was found for the certificate chain",
      result.getErrors().get(0).getMessage());

    Assert.assertEquals(
      "Trusted issuer of this certificate was not established",
      result.getErrors().get(1).getMessage());

    Assert.assertEquals(cred.getCertificate().getSubjectDN(),
      result.getErrors().get(1).getChain()[0].getSubjectDN());

  }

  @Test
  public void testSHA1FailsOnMD5Dir() {

    CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
    builder.trustAnchorsDir(md5TrustAnchorsDir)
      .opensslHashFunction(OpensslHashFunction.SHA1);

    ValidationResult result = builder.build()
      .validate(cred.getCertificateChain());

    Assert.assertFalse(result.isValid());
    Assert.assertEquals(2, result.getErrors().size());
    Assert.assertEquals(
      "No trusted CA certificate was found for the certificate chain",
      result.getErrors().get(0).getMessage());

    Assert.assertEquals(
      "Trusted issuer of this certificate was not established",
      result.getErrors().get(1).getMessage());

    Assert.assertEquals(cred.getCertificate().getSubjectDN(),
      result.getErrors().get(1).getChain()[0].getSubjectDN());

  }

}
