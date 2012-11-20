/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
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

import eu.emi.security.authn.x509.helpers.pkipath.AbstractValidator;

/**
 * A factory for VOMS attributes validators and parsers.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSValidators {
	
	
	private VOMSValidators(){}
	
	public static VOMSACValidator newValidator(ValidationResultListener listener){
		return new DefaultVOMSValidator(listener);
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore trustStore, 
			AbstractValidator validator,
			ValidationResultListener vrListener){
		
		return new DefaultVOMSValidator(trustStore, validator, vrListener);
		
	}
	
	public static VOMSACValidator newValidator(VOMSTrustStore store,
			AbstractValidator validator,
			ValidationResultListener resultHandler,
			VOMSACLookupStrategy strategy) {
		
		return new DefaultVOMSValidator(store, validator, resultHandler,
				strategy);
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
