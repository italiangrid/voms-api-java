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
package org.italiangrid.voms.credential;

import org.bouncycastle.openssl.PasswordFinder;
import org.italiangrid.voms.credential.impl.DefaultLoadCredentialsStrategy;

import eu.emi.security.authn.x509.X509Credential;


/**
 * This class implements convenience methods to load X509 user credentials in PEM or
 * PKCS12 format.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class UserCredentials {

	private static LoadCredentialsStrategy loadCredentialsStrategy = new DefaultLoadCredentialsStrategy();
	
	public static void setLoadCredentialsStrategy(LoadCredentialsStrategy strategy){
		loadCredentialsStrategy = strategy;
	}

	public static X509Credential loadCredentials(){
		return loadCredentials((char[])null);
	}
	
	public static X509Credential loadCredentials(final char[] keyPassword){
		
		PasswordFinder pf = new PasswordFinder() {
			
			public char[] getPassword() {
				return keyPassword;
			}
		};
		
		return loadCredentialsStrategy.loadCredentials(pf);
	}

	public static X509Credential loadCredentials(PasswordFinder passwordFinder){
		
		return loadCredentialsStrategy.loadCredentials(passwordFinder); 
	}
}
