package org.italiangrid.voms.request;

import org.bouncycastle.asn1.x509.AttributeCertificate;

import eu.emi.security.authn.x509.X509Credential;
/**
 * The {@link VOMSACService} interface.
 *  
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACService {
	
	/**
	 * Returns an {@link AttributeCertificate} given a {@link VOMSACRequest} for VOMS attributes.
	 * 
	 * @param credential the credential to be used when contacting the service
	 * @param request the request for VOMS attributes
	 * @return a possibly null {@link AttributeCertificate} containing (a subset of) the requested attributes.
	 */
	public AttributeCertificate getVOMSAttributeCertificate(X509Credential credential, VOMSACRequest request);

}
