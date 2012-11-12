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
