package org.italiangrid.voms.util;

import eu.emi.security.authn.x509.CrlCheckingMode;
import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.OCSPCheckingMode;
import eu.emi.security.authn.x509.OCSPParametes;
import eu.emi.security.authn.x509.ProxySupport;
import eu.emi.security.authn.x509.ValidationErrorListener;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;
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
	 * @return
	 */
	public static AbstractValidator buildCertificateValidator(String trustAnchorsDir,
			ValidationErrorListener validationErrorListener, long updateInterval,
			NamespaceCheckingMode namespaceChecks, CrlCheckingMode crlChecks,
			OCSPCheckingMode ocspChecks) {

		RevocationParametersExt revocationParameters = new RevocationParametersExt(crlChecks,
				new CRLParameters(), new OCSPParametes(ocspChecks));

		ValidatorParamsExt validationParams = new ValidatorParamsExt(revocationParameters,
				ProxySupport.ALLOW);

		OpensslCertChainValidator validator = new OpensslCertChainValidator(trustAnchorsDir,
				namespaceChecks, updateInterval, validationParams);

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
	 * @return
	 */
	public static AbstractValidator buildCertificateValidator(String trustAnchorsDir,
			ValidationErrorListener validationErrorListener) {

		return buildCertificateValidator(trustAnchorsDir, validationErrorListener, 0L,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
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
	 * @return
	 */
	public static AbstractValidator buildCertificateValidator(String trustAnchorsDir,
			ValidationErrorListener validationErrorListener, long updateInterval) {

		return buildCertificateValidator(trustAnchorsDir, validationErrorListener, updateInterval,
				DEFAULT_NS_CHECKS, DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}

	/**
	 * Builds an Openssl-style certificate validator configured as specified in
	 * the parameters
	 * 
	 * @param trustAnchorsDir
	 *            the directory where trust anchors are loaded from
	 * @return
	 */
	public static AbstractValidator buildCertificateValidator(String trustAnchorsDir) {
		return buildCertificateValidator(trustAnchorsDir, null, 0L, DEFAULT_NS_CHECKS,
				DEFAULT_CRL_CHECKS, DEFAULT_OCSP_CHECKS);
	}
}
