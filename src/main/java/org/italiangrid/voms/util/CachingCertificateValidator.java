/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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
import java.util.concurrent.TimeUnit;

import org.italiangrid.voms.VOMSError;

import eu.emi.security.authn.x509.ProxySupport;
import eu.emi.security.authn.x509.RevocationParameters;
import eu.emi.security.authn.x509.StoreUpdateListener;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.ValidationResult;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.FormatMode;

public class CachingCertificateValidator implements X509CertChainValidatorExt {

	/**
	 * Cache for
	 */
	ConcurrentHashMap<String, CachedValidationResult> validationResultsCache;
	X509CertChainValidatorExt validator;

	public CachingCertificateValidator(X509CertChainValidatorExt val) {

		validator = val;
		validationResultsCache = 
			new ConcurrentHashMap<String, CachedValidationResult>();
	}

	protected ValidationResult getCachedResult(String certFingerprint) {

			CachedValidationResult cvr = validationResultsCache.get(certFingerprint);
			
			if (cvr == null)
				return null;
			
			if (!cvr.isExpired(System.currentTimeMillis())){
				return cvr.getResult();
			}

			validationResultsCache.remove(certFingerprint, cvr);
			return null;
	}
	
	private void certChainSanityChecks(X509Certificate[] certChain){
		if (certChain == null)
			throw new IllegalArgumentException("Cannot validate a null cert chain.");
		
		if (certChain.length == 0)
			throw new IllegalArgumentException(
				"Cannot validate a cert chain of length 0.");
	}
	/**
	 * @param certChain
	 * @return
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#validate(java.security.cert.X509Certificate[])
	 */
	public ValidationResult validate(X509Certificate[] certChain) {
		
		certChainSanityChecks(certChain);

		String certFingerprint = null;
		
		try{

			certFingerprint = 
				FingerprintHelper.getFingerprint(	certChain[certChain.length-1]);
			
		}catch (Throwable t){
			
			String errorMsg = String.format("Error computing fingerprint for "
				+ "certificate: %s. Cause: %s",
				CertificateUtils.format(certChain[0], FormatMode.COMPACT_ONE_LINE),
				t.getMessage());

			throw new VOMSError(errorMsg, t);		

		}

		ValidationResult res = getCachedResult(certFingerprint);

		if (res == null){
			res = validator.validate(certChain);
			validationResultsCache.putIfAbsent(certFingerprint,
				new CachedValidationResult(certFingerprint, res));
		}

		return res;
			
	}

	/**
	 * 
	 * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#dispose()
	 */
	public void dispose() {

		validator.dispose();
	}

	/**
	 * @return
	 * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#getProxySupport()
	 */
	public ProxySupport getProxySupport() {

		return validator.getProxySupport();
	}

	/**
	 * @param certPath
	 * @return
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#validate(java.security.cert.CertPath)
	 */
	public ValidationResult validate(CertPath certPath) {

		return validator.validate(certPath);
	}

	/**
	 * @return
	 * @see eu.emi.security.authn.x509.X509CertChainValidatorExt#getRevocationCheckingMode()
	 */
	public RevocationParameters getRevocationCheckingMode() {

		return validator.getRevocationCheckingMode();
	}

	/**
	 * @return
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#getTrustedIssuers()
	 */
	public X509Certificate[] getTrustedIssuers() {

		return validator.getTrustedIssuers();
	}

	/**
	 * @param listener
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#addValidationListener(eu.emi.security.authn.x509.ValidationErrorListener)
	 */
	public void addValidationListener(ValidationErrorListener listener) {

		validator.addValidationListener(listener);
	}

	/**
	 * @param listener
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#removeValidationListener(eu.emi.security.authn.x509.ValidationErrorListener)
	 */
	public void removeValidationListener(ValidationErrorListener listener) {

		validator.removeValidationListener(listener);
	}

	/**
	 * @param listener
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#addUpdateListener(eu.emi.security.authn.x509.StoreUpdateListener)
	 */
	public void addUpdateListener(StoreUpdateListener listener) {

		validator.addUpdateListener(listener);
	}

	/**
	 * @param listener
	 * @see eu.emi.security.authn.x509.X509CertChainValidator#removeUpdateListener(eu.emi.security.authn.x509.StoreUpdateListener)
	 */
	public void removeUpdateListener(StoreUpdateListener listener) {

		validator.removeUpdateListener(listener);
	}

}

class CachedValidationResult {

	public static long MAX_CACHE_ENTRY_AGE_IN_MSEC = TimeUnit.MINUTES.toMillis(5);

	public CachedValidationResult(String certificateFingerprint,
		ValidationResult res) {

		certFingerprint = certificateFingerprint;
		result = res;
		timestamp = System.currentTimeMillis();
	}

	private String certFingerprint;
	private ValidationResult result;
	private long timestamp;

	public ValidationResult getResult() {

		return result;
	}

	public void setResult(ValidationResult result) {

		this.result = result;
	}

	public long getTimestamp() {

		return timestamp;
	}

	public void setTimestamp(long timestamp) {

		this.timestamp = timestamp;
	}

	public String getCertFingerprint() {

		return certFingerprint;
	}

	public void setCertFingerprint(String certFingerprint) {

		this.certFingerprint = certFingerprint;
	}

	boolean isExpired(long referenceTime) {

		return (referenceTime - timestamp > MAX_CACHE_ENTRY_AGE_IN_MSEC);
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