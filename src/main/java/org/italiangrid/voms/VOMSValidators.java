package org.italiangrid.voms;

import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.store.VOMSTrustStore;

import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

/**
 * A factory for VOMS attributes validators and parsers.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSValidators {
	
	public static VOMSACValidator newValidator(VOMSTrustStore trustStore, 
			AbstractValidator validator,
			ValidationResultListener vrListener){
		
		return new DefaultVOMSValidator(trustStore, validator, vrListener);
		
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore trustStore, 
			AbstractValidator validator){
		
		return new DefaultVOMSValidator(trustStore, validator);
		
	}
	public static VOMSACValidator newValidator(){
		return new DefaultVOMSValidator();
	}
	
	
	public static VOMSACParser newParser(){
		return new DefaultVOMSACParser();
	}

}
