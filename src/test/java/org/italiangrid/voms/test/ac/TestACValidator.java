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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.impl.LocalHostnameResolver;
import org.italiangrid.voms.asn1.VOMSACGenerator.ACGenerationProperties;
import org.italiangrid.voms.error.VOMSValidationErrorCode;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.italiangrid.voms.test.utils.VOMSAA;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;

public class TestACValidator implements Fixture {

  static PEMCredential holder, holder2;
  static VOMSACValidator validator;

  @BeforeClass
  public static void setup() throws KeyStoreException, CertificateException, IOException {

    holder = Utils.getTestUserCredential();
    holder2 = Utils.getTest1UserCredential();
    validator = Utils.getVOMSValidator();

  }

  @Test
  public void testValidityCheckSuccess() throws Exception {

    ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, defaultVOFqans);
    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    assertTrue(results.size() == 1);
    assertTrue(results.get(0).isValid());
    assertEquals(defaultVOFqans, results.get(0).getAttributes().getFQANs());

  }

  @Test
  public void testTimeValidityFailure() throws Exception {

    Date start = Utils.getDate(1975, 12, 1);
    Date end = Utils.getDate(1975, 12, 2);

    ProxyCertificate proxy = Utils.getVOMSAA()
      .setAcNotBefore(start)
      .setAcNotAfter(end)
      .createVOMSProxy(holder, defaultVOFqans);

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);

    VOMSValidationResult result = results.get(0);

    Assert.assertFalse(result.isValid());
    Assert.assertTrue(result.getValidationErrors().size() == 1);
    VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
    Assert.assertEquals(VOMSValidationErrorCode.acNotValidAtCurrentTime, m.getErrorCode());
  }

  @Test
  public void testHolderCheckFailure() throws Exception {

    ProxyCertificate proxy =
        Utils.getVOMSAA().createVOMSProxy(holder, holder2, defaultVOFqans, null, null);
    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    assertTrue(results.size() == 1);

    VOMSValidationResult result = results.get(0);
    Assert.assertFalse(result.isValid());
    Assert.assertTrue(result.getValidationErrors().size() == 1);
    VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
    Assert.assertEquals(VOMSValidationErrorCode.acHolderDoesntMatchCertChain, m.getErrorCode());
  }

  @Test
  public void testSignatureCheckFailure() throws Exception {

    ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, defaultVOFqans);
    VOMSACValidator validator = Utils.getVOMSValidator(vomsdir_fake_aa_cert);
    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);
    VOMSValidationResult result = results.get(0);
    Assert.assertFalse(result.isValid());
    Assert.assertTrue(result.getValidationErrors().size() == 2);

    Assert.assertEquals(VOMSValidationErrorCode.lscFileNotFound,
        result.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals(VOMSValidationErrorCode.other,
        result.getValidationErrors().get(1).getErrorCode());

    Assert.assertEquals(
        "Validation error: AuthorityKeyIdentifier in the AC  does not match AA certificate subject key identifier!",
        result.getValidationErrors().get(1).getMessage());

  }

  @Test
  public void testExpiredAACredFailure() throws Exception {

    ProxyCertificate proxy = Utils.getVOMSAA()
      .setCredential(Utils.getExpiredCredential())
      .createVOMSProxy(holder, defaultVOFqans);

    X509CertChainValidatorExt certValidator = Utils.getCertificateValidator();

    VOMSACValidator validator = VOMSValidators.newValidator(
        new DefaultVOMSTrustStore(Arrays.asList(vomsdir_expired_aa_cert)), certValidator);

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);
    VOMSValidationResult result = results.get(0);
    Assert.assertFalse(result.isValid());

    Assert.assertEquals(4, result.getValidationErrors().size());

    Assert.assertEquals(VOMSValidationErrorCode.lscFileNotFound,
        result.getValidationErrors().get(0).getErrorCode());

    // Certificate expired notification from CAnL
    Assert.assertEquals(VOMSValidationErrorCode.canlError,
        result.getValidationErrors().get(1).getErrorCode());

    // This is probably a bug in CAnL: No valid CRL was found for the CA which
    // issued the chain. But this happens only when validating the expired cert.
    Assert.assertEquals(VOMSValidationErrorCode.canlError,
        result.getValidationErrors().get(2).getErrorCode());

    Assert.assertEquals(VOMSValidationErrorCode.invalidAaCert,
        result.getValidationErrors().get(3).getErrorCode());

  }

  @Test
  public void testEmptyACCertsExtensionSuccess() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.INCLUDE_EMPTY_AC_CERTS_EXTENSION));

    VOMSACValidator validator = Utils.getVOMSValidator();

    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertTrue(r.isValid());
    Assert.assertEquals(1, r.getValidationErrors().size());

    Assert.assertEquals(VOMSValidationErrorCode.emptyAcCertsExtension,
        r.getValidationErrors().get(0).getErrorCode());
  }

  @Test
  public void testMissingACCertsExtensionFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.SKIP_AC_CERTS_EXTENSION));

    aa.setVoName("test.vo.2");
    aa.setHost("wilco.cnaf.infn.it");
    aa.setCredential(Utils.getAACredential2());

    VOMSACValidator validator = Utils.getVOMSValidator();

    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo.2"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());

    Assert.assertEquals(2, r.getValidationErrors().size());

    Assert.assertEquals(VOMSValidationErrorCode.emptyAcCertsExtension,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals(VOMSValidationErrorCode.aaCertNotFound,
        r.getValidationErrors().get(1).getErrorCode());

  }

  @Test
  public void testInvalidLSCSignatureFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setVoName("test.vo.2");
    aa.setHost("wilco.cnaf.infn.it");
    aa.setCredential(Utils.getAACredential2());
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.FAKE_SIGNATURE_BITS));

    VOMSACValidator validator = Utils.getVOMSValidator();

    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo.2"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());
    Assert.assertEquals(2, r.getValidationErrors().size());

    Assert.assertEquals(VOMSValidationErrorCode.acCertFailsSignatureVerification,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals(VOMSValidationErrorCode.aaCertNotFound,
        r.getValidationErrors().get(1).getErrorCode());
  }

  @Test
  public void testUnknownCriticalExtensionFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.INCLUDE_FAKE_CRITICAL_EXTENSION));

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());
    Assert.assertEquals(1, r.getValidationErrors().size());

    Assert.assertEquals(VOMSValidationErrorCode.other,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals(
        "Validation error: unknown critical extension found in VOMS AC: 1.3.6.1.4.1.8005.100.120.82",
        r.getValidationErrors().get(0).getMessage());
  }

  @Test
  public void testCriticalAKIDFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.INCLUDE_CRITICAL_AKID_EXTENSION));

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());

    Assert.assertEquals(VOMSValidationErrorCode.other,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals("Validation error: AuthorityKeyIdentifier AC extension cannot be critical!",
        r.getValidationErrors().get(0).getMessage());
  }

  @Test
  public void testCriticalNoRevAvailFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    aa.setGenerationProperties(
        EnumSet.of(ACGenerationProperties.INCLUDE_CRITICAL_NO_REV_AVAIL_EXTENSION));

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());

    Assert.assertEquals(VOMSValidationErrorCode.other,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals("Validation error: NoRevAvail AC extension cannot be critical!",
        r.getValidationErrors().get(0).getMessage());
  }

  @Test
  public void testTargetValidationSuccess() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    String localhostName;

    try {

      localhostName = InetAddress.getLocalHost().getCanonicalHostName();

    } catch (UnknownHostException e) {
      throw new VOMSError("Error resolving local hostname: " + e.getMessage(), e);
    }

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy = aa.createVOMSProxy(Utils.getTestUserCredential(),
        Arrays.asList("/test.vo"), null, Arrays.asList(localhostName));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertTrue(r.isValid());
  }

  @Test
  public void testTargetValidationFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy = aa.createVOMSProxy(Utils.getTestUserCredential(),
        Arrays.asList("/test.vo"), null, Arrays.asList("camaghe.cnaf.infn.it"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());
    Assert.assertEquals(1, r.getValidationErrors().size());
    Assert.assertEquals(VOMSValidationErrorCode.localhostDoesntMatchAcTarget,
        r.getValidationErrors().get(0).getErrorCode());

  }

  @Test
  public void testResolveHostnameException() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    VOMSACValidator validator = Utils.getVOMSValidator(new LocalHostnameResolver() {

      public String resolveLocalHostname() throws UnknownHostException {

        throw new UnknownHostException("misconfigured machine!");
      }
    });

    ProxyCertificate proxy = aa.createVOMSProxy(Utils.getTestUserCredential(),
        Arrays.asList("/test.vo"), null, Arrays.asList("camaghe.cnaf.infn.it"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assert.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assert.assertFalse(r.isValid());
    Assert.assertEquals(1, r.getValidationErrors().size());
    Assert.assertEquals(VOMSValidationErrorCode.other,
        r.getValidationErrors().get(0).getErrorCode());

    Assert.assertEquals("Validation error: Error resolving localhost name: misconfigured machine!",
        r.getValidationErrors().get(0).getMessage());
  }
}
