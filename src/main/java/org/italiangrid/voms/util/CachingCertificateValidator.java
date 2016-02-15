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
package org.italiangrid.voms.util;

import java.security.cert.CertPath;
import java.security.cert.X509Certificate;
import java.util.concurrent.ConcurrentHashMap;

import org.italiangrid.voms.VOMSError;

import eu.emi.security.authn.x509.ProxySupport;
import eu.emi.security.authn.x509.RevocationParameters;
import eu.emi.security.authn.x509.StoreUpdateListener;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.FormatMode;

/**
 * A Certificate validator that caches validation results for a configurable
 * period of time. The cache is keyed by the fingerprint of the certificate at
 * the top of the chain (likely the EEC).
 *
 *
 * @author andreaceccanti
 *
 */
public class CachingCertificateValidator implements X509CertChainValidatorExt {

  /**
   * Simple concurrent cache for validation results
   */
  protected final ConcurrentHashMap<String, CachedValidationResult> validationResultsCache;

  /**
   * The wrapped CANL certificate validator
   */
  protected final X509CertChainValidatorExt validator;

  /**
   * The cache entry lifetime for this validator
   */
  protected final long cacheEntryLifetimeMsec;

  /**
   * Builds a caching validator wrapping the validator passed as argument.
   *
   * @param val
   *          The CANL validator to be wrapped.
   * @param maxCacheEntryLifetime
   *          the maximum cache entry lifetime (in msecs)
   */
  public CachingCertificateValidator(X509CertChainValidatorExt val,
    long maxCacheEntryLifetime) {

    cacheEntryLifetimeMsec = maxCacheEntryLifetime;
    validator = val;
    validationResultsCache = new ConcurrentHashMap<String, CachedValidationResult>();
  }

  /**
   * Checks whether the {@link CachedValidationResult} passed as argument has
   * expired with respect to the {@link #cacheEntryLifetimeMsec} defined for
   * this validator and the reference time passed as argument.
   *
   * @param cvr
   *          a {@link CachedValidationResult} object
   * @param referenceTime
   *          the reference time (msecs since the epoch)
   * @return <code>true</code> when expired, <code>false</code> otherwise
   */
  public boolean cachedValidationResultHasExpired(CachedValidationResult cvr,
    long referenceTime) {

    return (referenceTime - cvr.getTimestamp() > cacheEntryLifetimeMsec);
  }

  /**
   * Gets a validation result from the memory cache
   *
   * @param certFingerprint
   *          the certificate fingerprint for the certificate at the top of the
   *          chain
   * @return the validation result, if found. <code>null</code> otherwise.
   */
  protected ValidationResult getCachedResult(String certFingerprint) {

    CachedValidationResult cvr = validationResultsCache.get(certFingerprint);

    if (cvr == null)
      return null;

    if (!cachedValidationResultHasExpired(cvr, System.currentTimeMillis())) {
      return cvr.getResult();
    }

    validationResultsCache.remove(certFingerprint, cvr);
    return null;
  }

  /**
   * Obvious sanity checks on input certificate chain
   *
   * @param certChain
   *          the chain to be checked
   */
  private void certChainSanityChecks(X509Certificate[] certChain) {

    if (certChain == null)
      throw new IllegalArgumentException("Cannot validate a null cert chain.");

    if (certChain.length == 0)
      throw new IllegalArgumentException(
        "Cannot validate a cert chain of length 0.");
  }

  /**
   * Validates a certificate chain using the wrapped validator, caching the
   * result for future validation calls.
   *
   * @param certChain
   *          the certificate chain that will be validated
   * @return a possibly cached {@link ValidationResult}
   * @see eu.emi.security.authn.x509.X509CertChainValidator#validate(java.security.cert.X509Certificate[])
   */
  public ValidationResult validate(X509Certificate[] certChain) {

    certChainSanityChecks(certChain);

    String certFingerprint = null;

    try {
      certFingerprint = FingerprintHelper
        .getFingerprint(certChain[certChain.length - 1]);

    } catch (Throwable t) {

      String errorMsg = String.format("Error computing fingerprint for "
        + "certificate: %s. Cause: %s",
        CertificateUtils.format(certChain[0], FormatMode.COMPACT_ONE_LINE),
        t.getMessage());

      throw new VOMSError(errorMsg, t);

    }

    ValidationResult res = getCachedResult(certFingerprint);

    if (res == null) {
      res = validator.validate(certChain);
      validationResultsCache.putIfAbsent(certFingerprint,
        new CachedValidationResult(certFingerprint, res));
    }

    return res;

  }

  /**
   * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#dispose()
   */
  public void dispose() {

    validator.dispose();
  }

  /**
   * @return the proxy support information
   * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#getProxySupport()
   */
  public ProxySupport getProxySupport() {

    return validator.getProxySupport();
  }

  /**
   * @param certPath
   *          the certificate path that will be validated
   * @return the {@link ValidationResult}
   * @see eu.emi.security.authn.x509.X509CertChainValidator#validate(java.security.cert.CertPath)
   */
  public ValidationResult validate(CertPath certPath) {

    return validator.validate(certPath);
  }

  /**
   * @return revocation parameters for the wrapped validator
   * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#getRevocationCheckingMode()
   */
  public RevocationParameters getRevocationCheckingMode() {

    return validator.getRevocationCheckingMode();
  }

  /**
   * @return trusted issuers from the wrapped validator
   * @see eu.emi.security.authn.x509.X509CertChainValidator#getTrustedIssuers()
   */
  public X509Certificate[] getTrustedIssuers() {

    return validator.getTrustedIssuers();
  }

  /**
   * @param listener
   *          the {@link ValidationErrorListener} to be added to this validator
   *          
   * @see eu.emi.security.authn.x509.X509CertChainValidator#addValidationListener(eu.emi.security.authn.x509.ValidationErrorListener)
   */
  public void addValidationListener(ValidationErrorListener listener) {

    validator.addValidationListener(listener);
  }

  /**
   * @param listener
   *        the {@link ValidationErrorListener} that must be removed from 
   *        this validator
   * @see eu.emi.security.authn.x509.X509CertChainValidator#removeValidationListener(eu.emi.security.authn.x509.ValidationErrorListener)
   */
  public void removeValidationListener(ValidationErrorListener listener) {

    validator.removeValidationListener(listener);
  }

  /**
   * @param listener
   *          the {@link StoreUpdateListener} that must be added to this 
   *          validator
   *        
   * @see eu.emi.security.authn.x509.X509CertChainValidator#addUpdateListener(eu.emi.security.authn.x509.StoreUpdateListener)
   */
  public void addUpdateListener(StoreUpdateListener listener) {

    validator.addUpdateListener(listener);
  }

  /**
   * @param listener
   *          the {@link StoreUpdateListener} that must be removed from this 
   *          validator
   *          
   * @see eu.emi.security.authn.x509.X509CertChainValidator#removeUpdateListener(eu.emi.security.authn.x509.StoreUpdateListener)
   */
  public void removeUpdateListener(StoreUpdateListener listener) {

    validator.removeUpdateListener(listener);
  }

}

/**
 * A validation result cache entry.
 *
 * @author cecco
 *
 */
class CachedValidationResult {

  /**
   * Default constructor.
   *
   * @param certificateFingerprint
   *          the certificate fingerprint for this entry
   * @param res
   *          the validation result
   */
  public CachedValidationResult(String certificateFingerprint,
    ValidationResult res) {

    certFingerprint = certificateFingerprint;
    result = res;
    timestamp = System.currentTimeMillis();
  }

  /** The certificate fingerprint for this cache entry **/
  private String certFingerprint;

  /** The validation result for this cache entry **/
  private ValidationResult result;

  /** The cache entry creation timestamp **/
  private long timestamp;

  /**
   * Returns the validation result for this entry.
   *
   * @return a {@link ValidationResult}
   */
  public ValidationResult getResult() {

    return result;
  }

  /**
   * Sets the validation result for this entry
   *
   * @param result
   *          a {@link ValidationResult}
   */
  public void setResult(ValidationResult result) {

    this.result = result;
  }

  /**
   * Returns this entry creation timestamp.
   *
   * @return the timestamp expressed as milliseconds since epoch
   */
  public long getTimestamp() {

    return timestamp;
  }

  /**
   * Sets this entry creation timestamp (in milliseconds since the epoch).
   *
   * @param timestamp
   *          the timestamp
   */
  public void setTimestamp(long timestamp) {

    this.timestamp = timestamp;
  }

  /**
   * Returns the certificate fingerprint for this entry.
   *
   * The certificate fingerprint is the SHA1 hash of the DER encoding of the
   * certificate.
   *
   *
   *
   * @return the fingerprint for this entry
   * @see FingerprintHelper
   */
  public String getCertFingerprint() {

    return certFingerprint;
  }

  /**
   *
   * Sets the certificate finger for this entry. The certificate fingerprint is
   * the SHA1 hash of the DER encoding of the certificate.
   *
   * It can be computed with the
   * {@link FingerprintHelper#getFingerprint(X509Certificate)} method.
   *
   * @param certFingerprint
   *          a certificate fingerprint describing a certificate
   */
  public void setCertFingerprint(String certFingerprint) {

    this.certFingerprint = certFingerprint;
  }

  @Override
  public int hashCode() {

    final int prime = 31;
    int result = 1;
    result = prime * result
      + ((certFingerprint == null) ? 0 : certFingerprint.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {

    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    CachedValidationResult other = (CachedValidationResult) obj;
    if (certFingerprint == null) {
      if (other.certFingerprint != null)
        return false;
    } else if (!certFingerprint.equals(other.certFingerprint))
      return false;
    return true;
  }
}
