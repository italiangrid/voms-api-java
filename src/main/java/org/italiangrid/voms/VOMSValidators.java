/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms;

import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.store.VOMSTrustStore;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;

/**
 * A factory for VOMS attributes validators and parsers.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSValidators {
	
	
	private VOMSValidators(){}
	
	public static VOMSACValidator newValidator(ValidationResultListener listener){
		return new DefaultVOMSValidator.Builder().validationListener(listener).build();
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore trustStore, 
			X509CertChainValidatorExt validator,
			ValidationResultListener vrListener){
		
		return new DefaultVOMSValidator.Builder()
			.trustStore(trustStore)
			.certChainValidator(validator)
			.validationListener(vrListener).build();
		
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore store,
			X509CertChainValidatorExt validator,
			ValidationResultListener vrListener,
			VOMSACLookupStrategy strategy) {
		
		return new DefaultVOMSValidator.Builder()
			.trustStore(store)
			.certChainValidator(validator)
			.validationListener(vrListener)
			.acLookupStrategy(strategy).build();
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore trustStore, 
			X509CertChainValidatorExt validator){
		
		return new DefaultVOMSValidator.Builder()
			.trustStore(trustStore)
			.certChainValidator(validator)
			.build();
	}
	
	public static VOMSACValidator newValidator(){
		return new DefaultVOMSValidator.Builder().build();
	}
	
	public static VOMSACParser newParser(){
		return new DefaultVOMSACParser();
	}

	

}
