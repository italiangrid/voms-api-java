package org.italiangrid.voms.request.impl;

import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.request.SSLSocketFactoryProvider;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

public abstract class AbstractVOMSProtocol implements VOMSProtocol {

	public static final String[] VOMS_LEGACY_PROTOCOLS = {"SSLv3"};
	
	/**
	 * The remote server endpoint information
	 */
	protected VOMSServerInfo serverInfo;
	

	/**
	 * The CAnL validator used to manage SSL authentication.
	 */
	protected AbstractValidator validator;

	/**
	 * Ctor.
	 * 
	 * @param vomsServerInfo
	 *            the info for the endpoint.
	 */
	public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo) {

		this(vomsServerInfo, CertificateValidatorBuilder.buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, 60000L));
	}

	/**
	 * Ctor.
	 * 
	 * @param vomsServerInfo
	 *            the info for the remote VOMS server endpoint
	 * @param validator
	 *            the validator used to manage the SSL authentication
	 */
	public AbstractVOMSProtocol(VOMSServerInfo vomsServerInfo, AbstractValidator validator) {

		this.serverInfo = vomsServerInfo;
		this.validator = validator;
	}
	
	/**
	 * Builds an SSL socket factory based on the credential passed as argument and the validator
	 * configured for this {@link AbstractVOMSProtocol}
	 * @param credential the client credential used for the socket factory being created
	 * @return
	 */
	protected SSLSocketFactory getSSLSocketFactory(X509Credential credential){
		SSLSocketFactoryProvider sslSocketFactoryProvider = new SSLSocketFactoryProvider(credential, validator);
	    return sslSocketFactoryProvider.getSSLSockectFactory();
	}

}
