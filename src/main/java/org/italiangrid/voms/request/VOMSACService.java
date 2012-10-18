package org.italiangrid.voms.request;

import org.bouncycastle.asn1.x509.AttributeCertificate;
/**
 * The {@link VOMSACService} interface.
 *  
 * @author andreaceccanti
 *
 */
public interface VOMSACService {
	
	/**
	 * Returns an {@link AttributeCertificate} given a {@link VOMSACRequest} for VOMS attributes.
	 * 
	 * @param request the request for VOMS attributes
	 * @return a possibly null {@link AttributeCertificate} containing (a subset of) the requested attributes.
	 */
	public AttributeCertificate getVOMSAttributeCertificate(VOMSACRequest request);

}
