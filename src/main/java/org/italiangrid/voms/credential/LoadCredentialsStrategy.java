package org.italiangrid.voms.credential;

import eu.emi.security.authn.x509.X509Credential;

public interface LoadCredentialsStrategy {

	public X509Credential loadCredentials(char[] keyPassword);
}
