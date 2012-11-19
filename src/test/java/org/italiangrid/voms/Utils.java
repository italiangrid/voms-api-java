package org.italiangrid.voms;

import java.util.Arrays;

import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

public class Utils implements Fixture{

	private Utils() {}
	
	public static VOMSACValidator getVOMSValidator(){
		AbstractValidator validator = CertificateValidatorBuilder.buildCertificateValidator(trustAnchorsDir);
		return VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsdir)), validator);
		
	}

}
