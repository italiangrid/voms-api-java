package org.glite.voms;

import java.security.cert.X509Certificate;

import org.italiangrid.voms.ac.impl.LegacyVOMSValidatorAdapter;

/**
 * This class is deprecated, and is provided for partial backwards compatibility with
 * existing users of the VOMS Java APIs.
 * 
 * @author andreaceccanti
 * @deprecated
 *
 */
public class VOMSValidator extends LegacyVOMSValidatorAdapter {

	public VOMSValidator(X509Certificate cert) {
		super(cert);
		
	}

	public VOMSValidator(X509Certificate[] certChain) {
		super(certChain);
	}
}
