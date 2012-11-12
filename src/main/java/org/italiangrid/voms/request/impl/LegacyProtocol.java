package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

/**
 * Protocol implementing the legacy interface.
 * 
 * 
 */
public class LegacyProtocol extends AbstractVOMSProtocol implements VOMSProtocol {

	public LegacyProtocol(VOMSServerInfo vomsServerInfo) {
		super(vomsServerInfo, CertificateValidatorBuilder.buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, 60000L));
	}

	public LegacyProtocol(VOMSServerInfo vomsServerInfo, AbstractValidator validator) {
		super(vomsServerInfo, validator);
	}

	public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request) {

		SSLSocketFactory sslSocketFactory = getSSLSocketFactory(credential);

		SSLSocket sslSocket = null;

		try {

			sslSocket = (SSLSocket) sslSocketFactory.createSocket(uri.getHost(), uri.getPort());

		} catch (UnknownHostException e) {

			throw new VOMSError("Error in creating socket: " + e.getMessage(), e);

		} catch (IOException e) {

			throw new VOMSError("Error in creating socket: " + e.getMessage(), e);
		}
		
		sslSocket.setEnabledProtocols(VOMS_LEGACY_PROTOCOLS);

		LegacyRequestSender protocol = LegacyRequestSender.instance();

		VOMSResponse response = null;

		try {

			protocol.sendRequest(request, sslSocket.getOutputStream());

			InputStream inputStream = sslSocket.getInputStream();

			response = new LegacyVOMSResponseParsingStrategy().parse(inputStream);

			sslSocket.close();

		} catch (IOException e) {

			throw new VOMSError("Error communicating with server " + uri + ": " + e.getMessage(), e);
		}

		return response;
	}

}
