package org.italiangrid.voms.request;

import org.bouncycastle.asn1.x509.AttributeCertificate;

public interface VOMSACService {
	
	public AttributeCertificate getVOMSAttributeCertificate(VOMSACRequest request);

}
