package org.italiangrid.voms.cred;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;

import org.bouncycastle.openssl.PasswordFinder;
import org.italiangrid.voms.credential.VOMSEnvironmentVariables;
import org.italiangrid.voms.credential.impl.DefaultLoadCredentialsStrategy;
import org.italiangrid.voms.credential.impl.DefaultProxyPathBuilder;
import org.junit.Assert;
import org.junit.Test;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;
import eu.emi.security.authn.x509.impl.X500NameUtils;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;
import eu.emi.security.authn.x509.proxy.ProxyType;

public class TestLoadCredential {

	public static final String keyPassword = "pass";
	
	public static final String pemCert = "src/test/resources/certs/test0.cert.pem";
	public static final String pemKey = "src/test/resources/certs/test0.key.pem";
	public static final String pkcs12Cred = "src/test/resources/certs/test0.p12"; 

	public static final String TEST_CERT_SUBJECT = "CN=test0, O=IGI, C=IT";
	public static final String PROXY_TMP_PATH = "/tmp/tempProxy";
	
	public static final String emptyHome = "src/test/resources/homes/empty";
	public static final String emptyGlobusHome = "src/test/resources/homes/empty.globus";
	public static final String pemCredsHome = "src/test/resources/homes/pem-creds";
	public static final String pkcs12CredsHome = "src/test/resources/homes/pkcs12-creds";
	
	static class TestPasswordFinder implements PasswordFinder{

		public char[] getPassword() {
			
			return keyPassword.toCharArray();
		}
	}
	
	static class NullPasswordFinder implements PasswordFinder{
		
		public char[] getPassword() {
			
			return null;
		}
	}
	
	@Test
	public void testNoCredentialsFoundSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyHome);
		X509Credential cred = strategy.loadCredentials(new NullPasswordFinder());
		Assert.assertNull(cred);
	}
	
	@Test
	public void testNoCredentialsFoundEmptyGlobusSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(emptyGlobusHome);
		X509Credential cred = strategy.loadCredentials(new NullPasswordFinder());
		Assert.assertNull(cred);
	}
	
	@Test
	public void testPEMCredentialLoadingSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pemCredsHome);
		X509Credential cred = strategy.loadCredentials(new TestPasswordFinder());
		Assert.assertNotNull(cred);
		Assert.assertTrue(X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
	}
	
	
	@Test
	public void testPKCS12CredentialLoadingSuccess() {
	
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pkcs12CredsHome);
		X509Credential cred = strategy.loadCredentials(new TestPasswordFinder());
		Assert.assertNotNull(cred);
		Assert.assertTrue(X500NameUtils.equal(cred.getCertificate().getSubjectX500Principal(), TEST_CERT_SUBJECT));
	}	
	
	@Test
	public void testLoadProxyFromENV() throws FileNotFoundException, IOException, InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, IllegalArgumentException, IllegalStateException{
		
		ProxyCertificate proxy = buildAndSaveProxy(pemCert, pemKey, keyPassword.toCharArray(), PROXY_TMP_PATH);
		System.setProperty(VOMSEnvironmentVariables.X509_USER_PROXY,PROXY_TMP_PATH);
		
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pemCredsHome);
		X509Credential cred = strategy.loadCredentials(new NullPasswordFinder());
		Assert.assertTrue(cred.getCertificate().getSubjectX500Principal().equals(proxy.getCertificateChain()[0].getSubjectX500Principal()));
	}
	
	@Test
	public void testLoadProxyFromUID() throws InvalidKeyException, CertificateParsingException, FileNotFoundException, SignatureException, NoSuchAlgorithmException, IOException{
		
		Integer twelve = new Integer(12);
		String tmpPath = System.getProperty("java.io.tmpdir");
		System.setProperty(VOMSEnvironmentVariables.VOMS_USER_ID, twelve.toString());
		DefaultProxyPathBuilder proxyPathBuilder = new DefaultProxyPathBuilder();
		String proxyPath = proxyPathBuilder.buildProxyFilePath(tmpPath, twelve);
		
		ProxyCertificate proxy = buildAndSaveProxy(pemCert, pemKey, keyPassword.toCharArray(), proxyPath);
		
		DefaultLoadCredentialsStrategy strategy = new DefaultLoadCredentialsStrategy(pemCredsHome);
		X509Credential cred = strategy.loadCredentials(new NullPasswordFinder());
		Assert.assertTrue(cred.getCertificate().getSubjectX500Principal().equals(proxy.getCertificateChain()[0].getSubjectX500Principal()));
	}
	
	
	private ProxyCertificate buildAndSaveProxy(String certFile, String keyFile, char[] keyPassword, String proxyFile) throws FileNotFoundException, IOException, InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException{
		
		X509Certificate cert = CertificateUtils.loadCertificate(new FileInputStream(certFile), Encoding.PEM);
		PrivateKey key = CertificateUtils.loadPrivateKey(new FileInputStream(keyFile), Encoding.PEM, keyPassword);
		
		ProxyCertificateOptions po = new ProxyCertificateOptions(new X509Certificate[]{cert});
		po.setType(ProxyType.LEGACY);
		ProxyCertificate proxy = ProxyGenerator.generate(po, key);
		
		FileOutputStream fos = new FileOutputStream(proxyFile);
		
		CertificateUtils.saveCertificate(fos, proxy.getCertificateChain()[0], Encoding.PEM);
		CertificateUtils.saveCertificate(fos, proxy.getCertificateChain()[1], Encoding.PEM);
		CertificateUtils.savePrivateKey(fos, proxy.getPrivateKey(), Encoding.PEM, null, null);
		
		fos.close();
		
		return proxy;
	}
}
