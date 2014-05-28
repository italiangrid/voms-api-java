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
	public static final CrlCheckingMode DEFAULT_CRL_CHECKS = 
		CrlCheckingMode.IF_VALID;
	
	/**
	 * The default OCSP checking policy.
	 */
	public static final OCSPCheckingMode DEFAULT_OCSP_CHECKS = 
		OCSPCheckingMode.IGNORE;
	
	/**
	 * The default namespace checking policy.
	 */
	public static final NamespaceCheckingMode DEFAULT_NS_CHECKS = 
		NamespaceCheckingMode.GLOBUS_EUGRIDPMA;
	
	/**
	 * The default trust anchors directory.
	 */
	public static final String DEFAULT_TRUST_ANCHORS_DIR = 
		"/etc/grid-security/certificates";

	/**
	 * By default this builder builds non-lazy validators
	 */
	public static final Boolean DEFAULT_VALIDATOR_IS_LAZY = Boolean.FALSE;

	/**
	 * Default validator trust anchor update interval.
	 */
	public static final long DEFAULT_TRUST_ANCHORS_UPDATE_INTERVAL = 0L;


	private String trustAnchorsDir = DEFAULT_TRUST_ANCHORS_DIR;
	private ValidationErrorListener validationErrorListener = null;
	private StoreUpdateListener storeUpdateListener = null;
	
	private long trustAnchorsUpdateInterval = 
		DEFAULT_TRUST_ANCHORS_UPDATE_INTERVAL;

	private boolean lazyAnchorsLoading = DEFAULT_VALIDATOR_IS_LAZY;
	private NamespaceCheckingMode namespaceChecks = DEFAULT_NS_CHECKS;
	private CrlCheckingMode crlChecks = DEFAULT_CRL_CHECKS;
	private OCSPCheckingMode ocspChecks = DEFAULT_OCSP_CHECKS;
	
	public CertificateValidatorBuilder(){}

	/**
	 * Sets the store update listener for this builder
	 * @param l the {@link StoreUpdateListener}
	 * @return the builder object
	 */
	public CertificateValidatorBuilder storeUpdateListener(StoreUpdateListener l){
		storeUpdateListener = l;
		return this;
	}

	/**
	 * Sets the trust anchors dir for this builder
	 * @param dir the trust anchors directory
	 * @return the builder object
	 */
	public CertificateValidatorBuilder trustAnchorsDir(String dir){
		trustAnchorsDir = dir;
		return this;
	}
	
	/**
	 * Sets the validation error listener for this builder
	 * @param l the {@link ValidationErrorListener}
	 * @return the builder object
	 */
	public CertificateValidatorBuilder validationErrorListener(ValidationErrorListener l){
		validationErrorListener = l;
		return this;
	}
	
	/**
	 * Sets the trust anchors update interval for this builder
	 * @param interval the update interval
	 * @return the builder object
	 */
	public CertificateValidatorBuilder trustAnchorsUpdateInterval(long interval){
		trustAnchorsUpdateInterval = interval;
		return this;
	}
	
	/**
	 * Sets whether the created validator will be lazy in loading anchors 
	 * @param lazyness 
	 * @return the builder object
	 */
	public CertificateValidatorBuilder lazyAnchorsLoading(boolean lazyness){
		lazyAnchorsLoading = lazyness;
		return this;
	}
	
	/**
	 * Sets the namespace checking mode for this builder
	 * @param nsChecks the {@link NamespaceCheckingMode}
	 * @return the builder object
	 */
	public CertificateValidatorBuilder namespaceChecks(NamespaceCheckingMode nsChecks){
		namespaceChecks = nsChecks;
		return this;
	}
	
	/**
	 * Sets the crl checking mode for this builder
	 * @param crl the {@link CrlCheckingMode}
	 * @return the builder object
	 */
	public CertificateValidatorBuilder crlChecks(CrlCheckingMode crl){
		crlChecks = crl;
		return this;
	}
	
	/**
	 * Sets the ocsp checking mode for this builder
	 * @param ocsp the {@link OCSPCheckingMode}
	 * @return the builder object
	 */
	public CertificateValidatorBuilder ocspChecks(OCSPCheckingMode ocsp){
		ocspChecks = ocsp;
		return this;
	}
	
	/**
	 * Builds an {@link OpensslCertChainValidator} according to the
	 * parameters set for this builder
	 * 
	 * @return the {@link X509CertChainValidatorExt} 
	 */
	public X509CertChainValidatorExt build(){
		RevocationParametersExt revocationParameters = new RevocationParametersExt(
			crlChecks, new CRLParameters(), new OCSPParametes(ocspChecks));

		ValidatorParamsExt validationParams = new ValidatorParamsExt(
			revocationParameters, ProxySupport.ALLOW);

		if (storeUpdateListener != null)
			validationParams.setInitialListeners(Arrays
				.asList(storeUpdateListener));

		OpensslCertChainValidator validator = new OpensslCertChainValidator(
			trustAnchorsDir, false, namespaceChecks, trustAnchorsUpdateInterval,
			validationParams, lazyAnchorsLoading);

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
	 * 
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
	 */
	public static X509CertChainValidatorExt buildCertificateValidator(
			String trustAnchorsDir,
			ValidationErrorListener validationErrorListener,
			StoreUpdateListener storeUpdateListener, long updateInterval,
			NamespaceCheckingMode namespaceChecks, CrlCheckingMode crlChecks,
			OCSPCheckingMode ocspChecks,
			boolean lazy) {
		
		CertificateValidatorBuilder builder = new CertificateValidatorBuilder();
		
		return builder
		.trustAnchorsDir(trustAnchorsDir)
		.validationErrorListener(validationErrorListener)
		.storeUpdateListener(storeUpdateListener)
		.trustAnchorsUpdateInterval(updateInterval)
		.namespaceChecks(namespaceChecks)
		.crlChecks(crlChecks)
		.ocspChecks(ocspChecks)
		.lazyAnchorsLoading(lazy)
		.build();
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
	 * 
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * 
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * 
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
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
	 * 
	 * @deprecated Create a {@link CertificateValidatorBuilder} object instead.
	 */
	public static X509CertChainValidatorExt buildCertificateValidator() {
		return buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, null, 0L,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}
}
