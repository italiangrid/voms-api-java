package org.italiangrid.voms.cred;

import org.italiangrid.voms.credential.DefaultLoadCredentialsStrategy;
import org.junit.Assert;
import org.junit.Test;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.X500NameUtils;

public class TestLoadCredential {

	public static final String keyPassword = "pass";
	
	public static final String pemCert = "src/test/resources/certs/test0.cert.pem";
	public static final String pemKey = "src/test/resources/certs/test0.key.pem";
	public static final String pkcs12Cred = "src/test/resources/certs/test0.p12"; 

	public static final String TEST_CERT_SUBJECT = "CN=test0, O=IGI, C=IT";
	
	public static final String emptyHome = "src/test/resources/homes/empty";
	public static final String emptyGlobusHome = "src/test/resources/homes/empty.globus";
	public static final String pemCredsHome = "src/test/resources/homes/pem-creds";
	public static final String pkcs12CredsHome = "src/test/resources/homes/pkcs12-creds";
	
	
	
	@Test
	public void testNoCredentialsFoundSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyHome);
		X509Credential cred = strategy.loadCredentials(null);
		Assert.assertNull(cred);
	}
	
	@Test
	public void testNoCredentialsFoundEmptyGlobusSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyGlobusHome);
		X509Credential cred = strategy.loadCredentials(null);
		Assert.assertNull(cred);
	}
	
	@Test
	public void testPEMCredentialLoadingSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pemCredsHome);
		X509Credential cred = strategy.loadCredentials(keyPassword.toCharArray());
		Assert.assertNotNull(cred);
		Assert.assertTrue(X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
	}
	
	
	@Test
	public void testPKCS12CredentialLoadingSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pkcs12CredsHome);
		X509Credential cred = strategy.loadCredentials(keyPassword.toCharArray());
		Assert.assertNotNull(cred);
		Assert.assertTrue(X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
	}	

}
