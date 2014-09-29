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

import eu.emi.security.authn.x509.X509Credential;

/**
 * A strategy to load user credentials
 * @author andreaceccanti
 *
 */
public interface LoadCredentialsStrategy {

	/**
	 * Loads a user credential
	 * @param passwordFinder the password finder used to potentially decrypt the credential encrypted private key.
	 * 
	 * @return an {@link X509Credential}, or <code>null</code> if no credential was found
	 */
	public X509Credential loadCredentials(PasswordFinder passwordFinder);
}
