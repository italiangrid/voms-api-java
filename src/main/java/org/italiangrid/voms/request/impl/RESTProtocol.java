package org.italiangrid.voms.request.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

/**
 * Protocol implementing the REST-style interface.
 * 
 * @author valerioventuri
 * 
 */
public class RESTProtocol extends AbstractVOMSProtocol implements VOMSProtocol {

	public RESTProtocol(VOMSServerInfo vomsServerInfo, AbstractValidator validator) {
		super(vomsServerInfo, validator);
	}

	public RESTProtocol(VOMSServerInfo vomsServerInfo) {
		this(vomsServerInfo, CertificateValidatorBuilder.buildCertificateValidator(
				DefaultVOMSValidator.DEFAULT_TRUST_ANCHORS_DIR, null, 60000L));
	}

	public VOMSResponse doRequest(X509Credential credential, VOMSACRequest request) {

		RESTServiceURLBuilder restQueryBuilder = new RESTServiceURLBuilder();
		URL serviceUrl = restQueryBuilder.build(serverInfo.getURL(), request);

		HttpsURLConnection connection = null;

		try {

			connection = (HttpsURLConnection) serviceUrl.openConnection();

		} catch (IOException e) {
			
			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);
		}

		connection.setSSLSocketFactory(getSSLSocketFactory(credential));

		try {

			connection.connect();

		} catch (IOException e) {
			
			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);
			
		}

		InputStream inputStream = null;

		try {

			inputStream = (InputStream) connection.getContent();

		} catch (IOException e) {

			throw new VOMSProtocolError(e.getMessage(), serverInfo, request, credential, e);
		}

		RESTVOMSResponseParsingStrategy responseParsingStrategy = new RESTVOMSResponseParsingStrategy();

		VOMSResponse response = responseParsingStrategy.parse(inputStream);

		connection.disconnect();

		return response;
	}

}
