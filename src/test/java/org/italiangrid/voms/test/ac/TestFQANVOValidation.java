// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSValidationResult;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidationStrategy;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.error.VOMSValidationErrorMessage;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for issue #42: every FQAN root group must equal the AC's VO name.
 *
 * <p>ACs are generated, signed and parsed using the existing fixtures. Only PKIX certificate chain
 * validation is stubbed to avoid dependence on fixture certificate and CRL expiry. AC signature
 * verification, LSC matching and holder validation use the real implementation.
 */
public class TestFQANVOValidation implements Fixture {

  private PEMCredential holder;
  private DefaultVOMSValidationStrategy strategy;

  @BeforeEach
  public void setUp() throws Exception {
    holder = Utils.getTestUserCredential();
    X509CertChainValidatorExt certValidator = mock(X509CertChainValidatorExt.class);
    when(certValidator.validate(any(X509Certificate[].class)))
        .thenReturn(new ValidationResult(true));
    strategy =
        new DefaultVOMSValidationStrategy(
            new DefaultVOMSTrustStore(Collections.singletonList(vomsdir)), certValidator);
  }

  /** Accepts a root-only FQAN equal to the declared VO in both validation overloads. */
  @Test
  public void acceptsMatchingRoot() throws Exception {
    assertAccepted("/test.vo");
  }

  /** Accepts subgroups and role/capability components while preserving FQAN order. */
  @Test
  public void acceptsMatchingGroupsAndRoles() throws Exception {
    assertAccepted(
        "/test.vo/analysis/Role=production/Capability=NULL",
        "/test.vo",
        "/test.vo/analysis",
        "/test.vo/analysis/subgroup/Role=NULL/Capability=NULL");
  }

  /** Rejects a correctly signed AC containing a FQAN from another VO. */
  @Test
  public void rejectsDifferentVo() throws Exception {
    assertRejected("/other.vo/Role=production");
  }

  /** Requires a group boundary after the VO name, not merely a matching string prefix. */
  @Test
  public void rejectsVoNamePrefixCollision() throws Exception {
    assertRejected("/test.vo-other/Role=production");
  }

  /** Does not treat a longer dotted VO name as a subgroup of the declared VO. */
  @Test
  public void rejectsLongerDottedVoName() throws Exception {
    assertRejected("/test.vo.other");
  }

  /** Compares VO names exactly, without case folding. */
  @Test
  public void rejectsDifferentCase() throws Exception {
    assertRejected("/TEST.VO/Role=production");
  }

  /** Rejects a FQAN without the leading group separator. */
  @Test
  public void rejectsMissingLeadingSlash() throws Exception {
    assertRejected("test.vo/Role=production");
  }

  /** Checks secondary FQANs even when the primary FQAN belongs to the declared VO. */
  @Test
  public void rejectsForeignSecondaryFqan() throws Exception {
    assertRejected("/other.vo/Role=production", "/test.vo", "/other.vo/Role=production");
  }

  /** Rejects a foreign primary FQAN instead of promoting a later valid FQAN to primary. */
  @Test
  public void rejectsForeignPrimaryFqan() throws Exception {
    assertRejected("/other.vo", "/other.vo", "/test.vo");
  }

  /** Ensures the public validator does not expose a rejected AC as validated attributes. */
  @Test
  public void publicValidatorExcludesMixedVoAc() throws Exception {
    ProxyCertificate proxy = createProxy(Arrays.asList("/test.vo", "/other.vo"));
    DefaultVOMSValidator validator =
        new DefaultVOMSValidator.Builder().validationStrategy(strategy).build();

    List<VOMSValidationResult> results = validator.validateWithResult(proxy.getCertificateChain());
    assertEquals(1, results.size());
    assertMismatch(results.get(0), "/other.vo");
    assertTrue(validator.validate(proxy.getCertificateChain()).isEmpty());
  }

  private ProxyCertificate createProxy(List<String> fqans) throws Exception {
    return Utils.getVOMSAA().createVOMSProxy(holder, fqans);
  }

  private VOMSAttribute parse(ProxyCertificate proxy, List<String> fqans) {
    List<VOMSAttribute> attributes = new DefaultVOMSACParser().parse(proxy.getCertificateChain());
    assertEquals(1, attributes.size());
    VOMSAttribute attribute = attributes.get(0);
    assertEquals(defaultVO, attribute.getVO());
    assertEquals(fqans, attribute.getFQANs());
    return attribute;
  }

  private void assertAccepted(String... fqans) throws Exception {
    List<String> expected = Arrays.asList(fqans);
    ProxyCertificate proxy = createProxy(expected);
    VOMSAttribute attribute = parse(proxy, expected);

    assertAll(
        "Both validation overloads accept matching FQANs",
        () -> assertValid(strategy.validateAC(attribute)),
        () -> assertValid(strategy.validateAC(attribute, proxy.getCertificateChain())));
    assertEquals(expected, attribute.getFQANs());
    assertEquals(expected.get(0), attribute.getPrimaryFQAN());
  }

  private void assertRejected(String fqan) throws Exception {
    assertRejected(fqan, new String[] {fqan});
  }

  private void assertRejected(String offendingFqan, String... fqans) throws Exception {
    List<String> expected = Arrays.asList(fqans);
    ProxyCertificate proxy = createProxy(expected);
    VOMSAttribute attribute = parse(proxy, expected);

    assertAll(
        "Both validation overloads reject inconsistent FQANs",
        () -> assertMismatch(strategy.validateAC(attribute), offendingFqan),
        () ->
            assertMismatch(
                strategy.validateAC(attribute, proxy.getCertificateChain()), offendingFqan));
    // Validation must reject the AC without filtering or reordering its signed attributes.
    assertEquals(expected, attribute.getFQANs());
    assertEquals(expected.get(0), attribute.getPrimaryFQAN());
  }

  private void assertValid(VOMSValidationResult result) {
    assertTrue(result.isValid(), () -> result.getValidationErrors().toString());
    assertTrue(result.getValidationErrors().isEmpty());
  }

  private void assertMismatch(VOMSValidationResult result, String offendingFqan) {
    assertFalse(result.isValid(), "An AC with a foreign FQAN must be rejected");
    assertEquals(1, result.getValidationErrors().size());
    VOMSValidationErrorMessage error = result.getValidationErrors().get(0);
    // Compare names so this regression suite also compiles before the new enum value is added.
    assertEquals("fqanDoesntMatchVo", error.getErrorCode().name());
    assertEquals(offendingFqan, error.getParameters()[0]);
    assertEquals(defaultVO, error.getParameters()[1]);
    assertEquals(
        "FQAN \"" + offendingFqan + "\" does not belong to VO \"" + defaultVO + "\".",
        error.getMessage());
  }
}
