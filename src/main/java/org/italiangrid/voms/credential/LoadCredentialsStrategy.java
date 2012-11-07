package org.italiangrid.voms.credential;

import org.bouncycastle.openssl.PasswordFinder;

import eu.emi.security.authn.x509.X509Credential;

public interface LoadCredentialsStrategy {

	public X509Credential loadCredentials(PasswordFinder passwordFinder);
}
