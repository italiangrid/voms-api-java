// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class TestACValidator implements Fixture {

  static PEMCredential holder, holder2;
  static VOMSACValidator validator;

  @BeforeAll
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

    ProxyCertificate proxy =
        Utils.getVOMSAA()
            .setAcNotBefore(start)
            .setAcNotAfter(end)
            .createVOMSProxy(holder, defaultVOFqans);

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);

    VOMSValidationResult result = results.get(0);

    Assertions.assertFalse(result.isValid());
    Assertions.assertTrue(result.getValidationErrors().size() == 1);
    VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
    Assertions.assertEquals(VOMSValidationErrorCode.acNotValidAtCurrentTime, m.getErrorCode());
  }

  @Test
  public void testHolderCheckFailure() throws Exception {

    ProxyCertificate proxy =
        Utils.getVOMSAA().createVOMSProxy(holder, holder2, defaultVOFqans, null, null);
    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    assertTrue(results.size() == 1);

    VOMSValidationResult result = results.get(0);
    Assertions.assertFalse(result.isValid());
    Assertions.assertTrue(result.getValidationErrors().size() == 1);
    VOMSValidationErrorMessage m = result.getValidationErrors().get(0);
    Assertions.assertEquals(VOMSValidationErrorCode.acHolderDoesntMatchCertChain, m.getErrorCode());
  }

  @Test
  public void testSignatureCheckFailure() throws Exception {

    ProxyCertificate proxy = Utils.getVOMSAA().createVOMSProxy(holder, defaultVOFqans);
    VOMSACValidator validator = Utils.getVOMSValidator(vomsdir_fake_aa_cert);
    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);
    VOMSValidationResult result = results.get(0);
    Assertions.assertFalse(result.isValid());
    Assertions.assertTrue(result.getValidationErrors().size() == 2);

    Assertions.assertEquals(
        VOMSValidationErrorCode.lscFileNotFound,
        result.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        VOMSValidationErrorCode.other, result.getValidationErrors().get(1).getErrorCode());

    Assertions.assertEquals(
        "Validation error: AuthorityKeyIdentifier in the AC  does not match AA certificate subject key identifier!",
        result.getValidationErrors().get(1).getMessage());
  }

  @Test
  public void testExpiredAACredFailure() throws Exception {

    ProxyCertificate proxy =
        Utils.getVOMSAA()
            .setCredential(Utils.getExpiredCredential())
            .createVOMSProxy(holder, defaultVOFqans);

    X509CertChainValidatorExt certValidator = Utils.getCertificateValidator();

    VOMSACValidator validator =
        VOMSValidators.newValidator(
            new DefaultVOMSTrustStore(Arrays.asList(vomsdir_expired_aa_cert)), certValidator);

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    assertTrue(results.size() == 1);
    VOMSValidationResult result = results.get(0);
    Assertions.assertFalse(result.isValid());

    Assertions.assertEquals(4, result.getValidationErrors().size());

    Assertions.assertEquals(
        VOMSValidationErrorCode.lscFileNotFound,
        result.getValidationErrors().get(0).getErrorCode());

    // Certificate expired notification from CAnL
    Assertions.assertEquals(
        VOMSValidationErrorCode.canlError, result.getValidationErrors().get(1).getErrorCode());

    // This is probably a bug in CAnL: No valid CRL was found for the CA which
    // issued the chain. But this happens only when validating the expired cert.
    Assertions.assertEquals(
        VOMSValidationErrorCode.canlError, result.getValidationErrors().get(2).getErrorCode());

    Assertions.assertEquals(
        VOMSValidationErrorCode.invalidAaCert, result.getValidationErrors().get(3).getErrorCode());
  }

  @Test
  public void testEmptyACCertsExtensionSuccess() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.INCLUDE_EMPTY_AC_CERTS_EXTENSION));

    ProxyCertificate proxy = aa.createVOMSProxy(holder, defaultVOFqans);

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());

    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertTrue(r.isValid());
    Assertions.assertEquals(1, r.getValidationErrors().size());

    Assertions.assertEquals(
        VOMSValidationErrorCode.emptyAcCertsExtension,
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

    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());

    Assertions.assertEquals(2, r.getValidationErrors().size());

    Assertions.assertEquals(
        VOMSValidationErrorCode.emptyAcCertsExtension,
        r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        VOMSValidationErrorCode.aaCertNotFound, r.getValidationErrors().get(1).getErrorCode());
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

    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());
    Assertions.assertEquals(2, r.getValidationErrors().size());

    Assertions.assertEquals(
        VOMSValidationErrorCode.acCertFailsSignatureVerification,
        r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        VOMSValidationErrorCode.aaCertNotFound, r.getValidationErrors().get(1).getErrorCode());
  }

  @Test
  public void testUnknownCriticalExtensionFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();
    aa.setGenerationProperties(EnumSet.of(ACGenerationProperties.INCLUDE_FAKE_CRITICAL_EXTENSION));

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy =
        aa.createVOMSProxy(Utils.getTestUserCredential(), Arrays.asList("/test.vo"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());
    Assertions.assertEquals(1, r.getValidationErrors().size());

    Assertions.assertEquals(
        VOMSValidationErrorCode.other, r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
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
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());

    Assertions.assertEquals(
        VOMSValidationErrorCode.other, r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        "Validation error: AuthorityKeyIdentifier AC extension cannot be critical!",
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
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());

    Assertions.assertEquals(
        VOMSValidationErrorCode.other, r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        "Validation error: NoRevAvail AC extension cannot be critical!",
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
    ProxyCertificate proxy =
        aa.createVOMSProxy(
            Utils.getTestUserCredential(),
            Arrays.asList("/test.vo"),
            null,
            Arrays.asList(localhostName));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertTrue(r.isValid());
  }

  @Test
  public void testTargetValidationFailure() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    VOMSACValidator validator = Utils.getVOMSValidator();
    ProxyCertificate proxy =
        aa.createVOMSProxy(
            Utils.getTestUserCredential(),
            Arrays.asList("/test.vo"),
            null,
            Arrays.asList("camaghe.cnaf.infn.it"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());
    Assertions.assertEquals(1, r.getValidationErrors().size());
    Assertions.assertEquals(
        VOMSValidationErrorCode.localhostDoesntMatchAcTarget,
        r.getValidationErrors().get(0).getErrorCode());
  }

  @Test
  public void testResolveHostnameException() throws Exception {

    VOMSAA aa = Utils.getVOMSAA();

    VOMSACValidator validator =
        Utils.getVOMSValidator(
            new LocalHostnameResolver() {

              public String resolveLocalHostname() throws UnknownHostException {

                throw new UnknownHostException("misconfigured machine!");
              }
            });

    ProxyCertificate proxy =
        aa.createVOMSProxy(
            Utils.getTestUserCredential(),
            Arrays.asList("/test.vo"),
            null,
            Arrays.asList("camaghe.cnaf.infn.it"));

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    Assertions.assertEquals(1, results.size());
    VOMSValidationResult r = results.get(0);

    Assertions.assertFalse(r.isValid());
    Assertions.assertEquals(1, r.getValidationErrors().size());
    Assertions.assertEquals(
        VOMSValidationErrorCode.other, r.getValidationErrors().get(0).getErrorCode());

    Assertions.assertEquals(
        "Validation error: Error resolving localhost name: misconfigured machine!",
        r.getValidationErrors().get(0).getMessage());
  }
}
