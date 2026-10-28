// SPDX-FileCopyrightText: 2026 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import eu.emi.security.authn.x509.ProxySupport;
import eu.emi.security.authn.x509.RevocationParameters;
import eu.emi.security.authn.x509.StoreUpdateListener;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import org.junit.jupiter.api.Test;

class TestCachingCertificateValidator {

  private final X509CertChainValidatorExt delegate = mock(X509CertChainValidatorExt.class);
  private final CachingCertificateValidator validator =
      new CachingCertificateValidator(delegate, 60000);

  private X509Certificate certificate(byte encoding) throws Exception {
    X509Certificate cert = mock(X509Certificate.class);
    when(cert.getEncoded()).thenReturn(new byte[] {encoding});
    return cert;
  }

  @Test
  void rejectsNullAndEmptyChainsBeforeCallingDelegate() {
    assertThrows(
        IllegalArgumentException.class, () -> validator.validate((X509Certificate[]) null));
    assertThrows(IllegalArgumentException.class, () -> validator.validate(new X509Certificate[0]));
    verifyNoInteractions(delegate);
  }

  @Test
  void cachesSuccessfulValidationByCertificateEncoding() throws Exception {
    X509Certificate[] first = {certificate((byte) 1)};
    X509Certificate[] equivalent = {certificate((byte) 1)};
    ValidationResult result = new ValidationResult(true);
    when(delegate.validate(first)).thenReturn(result);
    assertSame(result, validator.validate(first));
    assertSame(result, validator.validate(equivalent));
    verify(delegate).validate(first);
    verifyNoMoreInteractions(delegate);
  }

  @Test
  void cachesFailedValidationToo() throws Exception {
    X509Certificate[] chain = {certificate((byte) 1)};
    ValidationResult result = new ValidationResult(false);
    when(delegate.validate(chain)).thenReturn(result);
    assertSame(result, validator.validate(chain));
    assertSame(result, validator.validate(chain));
    verify(delegate).validate(chain);
  }

  @Test
  void differentCertificatesHaveIndependentEntries() throws Exception {
    X509Certificate[] first = {certificate((byte) 1)};
    X509Certificate[] second = {certificate((byte) 2)};
    ValidationResult valid = new ValidationResult(true);
    ValidationResult invalid = new ValidationResult(false);
    when(delegate.validate(first)).thenReturn(valid);
    when(delegate.validate(second)).thenReturn(invalid);
    assertSame(valid, validator.validate(first));
    assertSame(invalid, validator.validate(second));
    assertSame(valid, validator.validate(first));
    assertSame(invalid, validator.validate(second));
    verify(delegate).validate(first);
    verify(delegate).validate(second);
  }

  @Test
  void expiredEntryIsReplacedAndTheReplacementIsReused() throws Exception {
    X509Certificate[] chain = {certificate((byte) 1)};
    ValidationResult oldResult = new ValidationResult(true);
    ValidationResult newResult = new ValidationResult(false);
    String key = FingerprintHelper.getFingerprint(chain[0]);
    CachedValidationResult expired = new CachedValidationResult(key, oldResult);
    expired.setTimestamp(0);
    validator.validationResultsCache.put(key, expired);
    when(delegate.validate(chain)).thenReturn(newResult);
    assertSame(newResult, validator.validate(chain));
    assertSame(newResult, validator.validate(chain));
    assertNotSame(expired, validator.validationResultsCache.get(key));
    verify(delegate).validate(chain);
  }

  @Test
  void expiryBoundaryIsStrictlyAfterLifetime() {
    CachedValidationResult entry =
        new CachedValidationResult("fingerprint", new ValidationResult(true));
    entry.setTimestamp(1000);
    assertFalse(validator.cachedValidationResultHasExpired(entry, 60999));
    assertFalse(validator.cachedValidationResultHasExpired(entry, 61000));
    assertTrue(validator.cachedValidationResultHasExpired(entry, 61001));
  }

  @Test
  void delegateExceptionsAreNotCached() throws Exception {
    X509Certificate[] chain = {certificate((byte) 1)};
    RuntimeException failure = new IllegalStateException("validation unavailable");
    ValidationResult result = new ValidationResult(true);
    when(delegate.validate(chain)).thenThrow(failure).thenReturn(result);
    assertSame(failure, assertThrows(IllegalStateException.class, () -> validator.validate(chain)));
    assertSame(result, validator.validate(chain));
    assertSame(result, validator.validate(chain));
    verify(delegate, times(2)).validate(chain);
  }

  @Test
  void certPathsAreDelegatedWithoutCaching() {
    CertPath path = mock(CertPath.class);
    ValidationResult first = new ValidationResult(true);
    ValidationResult second = new ValidationResult(false);
    when(delegate.validate(path)).thenReturn(first, second);
    assertSame(first, validator.validate(path));
    assertSame(second, validator.validate(path));
    verify(delegate, times(2)).validate(path);
  }

  @Test
  void delegatesConfigurationListenersAndDisposal() {
    X509Certificate[] issuers = new X509Certificate[0];
    RevocationParameters revocation = new RevocationParameters();
    when(delegate.getTrustedIssuers()).thenReturn(issuers);
    when(delegate.getRevocationCheckingMode()).thenReturn(revocation);
    when(delegate.getProxySupport()).thenReturn(ProxySupport.ALLOW);
    assertSame(issuers, validator.getTrustedIssuers());
    assertSame(revocation, validator.getRevocationCheckingMode());
    assertEquals(ProxySupport.ALLOW, validator.getProxySupport());
    ValidationErrorListener validationListener = mock(ValidationErrorListener.class);
    StoreUpdateListener updateListener = mock(StoreUpdateListener.class);
    validator.addValidationListener(validationListener);
    validator.removeValidationListener(validationListener);
    validator.addUpdateListener(updateListener);
    validator.removeUpdateListener(updateListener);
    validator.dispose();
    verify(delegate).addValidationListener(validationListener);
    verify(delegate).removeValidationListener(validationListener);
    verify(delegate).addUpdateListener(updateListener);
    verify(delegate).removeUpdateListener(updateListener);
    verify(delegate).dispose();
  }
}
