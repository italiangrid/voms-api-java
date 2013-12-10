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

import java.util.Arrays;

import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;

import eu.emi.security.authn.x509.CrlCheckingMode;
import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.OCSPCheckingMode;
import eu.emi.security.authn.x509.OCSPParametes;
import eu.emi.security.authn.x509.ProxySupport;
import eu.emi.security.authn.x509.StoreUpdateListener;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.CRLParameters;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.RevocationParametersExt;
import eu.emi.security.authn.x509.impl.ValidatorParams;
import eu.emi.security.authn.x509.impl.ValidatorParamsExt;

/**
 * A utility class which provides convenient methods to build a certificate
 * validator with defaults that are meaningful for VOMS usage.
 * 
 * @author cecco
 * 
 */
public class CertificateValidatorBuilder {

	/**
	 * The default CRL checking policy.
	 */
	public static final CrlCheckingMode DEFAULT_CRL_CHECKS = CrlCheckingMode.REQUIRE;
	/**
	 * The default OCSP checking policy.
	 */
	public static final OCSPCheckingMode DEFAULT_OCSP_CHECKS = OCSPCheckingMode.IGNORE;
	/**
	 * The default namespace checking policy.
	 */
	public static final NamespaceCheckingMode DEFAULT_NS_CHECKS = NamespaceCheckingMode.GLOBUS_EUGRIDPMA;

	/** Private constructor which prevents object instantiation **/
	private CertificateValidatorBuilder() {
	}
	
	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param updateInterval
	 *            the trust anchor store update interval
	 * @param namespaceChecks
	 *            the namespace checking policy
	 * @param crlChecks
	 *            the crl checking policy
	 * @param ocspChecks
	 *            the ocsp checking policy
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
		String trustAnchorsDir,
		ValidationErrorListener validationErrorListener,
		StoreUpdateListener storeUpdateListener, long updateInterval,
		NamespaceCheckingMode namespaceChecks, CrlCheckingMode crlChecks,
		OCSPCheckingMode ocspChecks) {
		
		return buildCertificateValidator(trustAnchorsDir,
			validationErrorListener,
			storeUpdateListener,
			updateInterval,
			namespaceChecks,
			crlChecks,
			ocspChecks,
			true);
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param updateInterval
	 *            the trust anchor store update interval
	 * @param namespaceChecks
	 *            the namespace checking policy
	 * @param crlChecks
	 *            the crl checking policy
	 * @param ocspChecks
	 *            the ocsp checking policy
	 * @param lazy
	 * 						whether the validator should be lazy in loading
	 * 						crls and certificates
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener,
			StoreUpdateListener storeUpdateListener, long updateInterval,
			NamespaceCheckingMode namespaceChecks, CrlCheckingMode crlChecks,
			OCSPCheckingMode ocspChecks,
			boolean lazy) {

		RevocationParametersExt revocationParameters = new RevocationParametersExt(
				crlChecks, new CRLParameters(), new OCSPParametes(ocspChecks));

		ValidatorParamsExt validationParams = new ValidatorParamsExt(
				revocationParameters, ProxySupport.ALLOW);

		if (storeUpdateListener != null)
			validationParams.setInitialListeners(Arrays
					.asList(storeUpdateListener));

		OpensslCertChainValidator validator = new OpensslCertChainValidator(
				trustAnchorsDir, false, namespaceChecks, updateInterval,
				validationParams, lazy);

		if (validationErrorListener != null)
			validator.addValidationListener(validationErrorListener);

		return validator;
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, null, 0L, DEFAULT_NS_CHECKS,
				DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param storeListener
	 *            the listener that will be informed of trust store load errors
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener,
			StoreUpdateListener storeListener) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, storeListener, 0L, DEFAULT_NS_CHECKS,
				DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param storeListener
	 *            the listener that will be informed of trust store load errors
	 * 
	 * @param updateInterval
	 *            the trust anchor store update interval
	 *            
	 * @param lazy
	 * 						whether the certificate validator should be lazy in loading
	 * 					  crls and CAs
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener,
			StoreUpdateListener storeListener,
			long updateInterval,
			boolean lazy) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, storeListener, 
				updateInterval, 
				DEFAULT_NS_CHECKS,
				DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS, lazy);
	}
	
	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param storeListener
	 *            the listener that will be informed of trust store load errors
	 * 
	 * @param updateInterval
	 *            the trust anchor store update interval
	 *            
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener,
			StoreUpdateListener storeListener,
			long updateInterval) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, storeListener, 
				updateInterval, 
				DEFAULT_NS_CHECKS,
				DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}
	
	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param updateInterval
	 *            the trust anchor store update interval
	 * @param lazy
	 * 						whether the certificate validator should be lazy in loading
	 * 					  crls and CAs
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener, 
			long updateInterval,
			boolean lazy) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, null, updateInterval,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS, lazy);
	}
	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @param validationErrorListener
	 *            the listener that will receive notification about validation
	 *            errors
	 * @param updateInterval
	 *            the trust anchor store update interval
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener, long updateInterval) {

		return buildCertificateValidator(trustAnchorsDir,
				validationErrorListener, null, updateInterval,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 *            
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir) {
		return buildCertificateValidator(trustAnchorsDir, null, null, 0L,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}

	/**
	 * Builds an Openssl-style certificate validator. 
	 * 
	 * @return an Openssl-style certificate validator configured as specified in
	 * the parameters
	 */
	public static X509CertChainValidatorExt buildCertificateValidator() {
		return buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, null, 0L,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}
}
