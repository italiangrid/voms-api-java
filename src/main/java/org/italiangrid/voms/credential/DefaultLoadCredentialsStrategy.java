package org.italiangrid.voms.credential;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.italiangrid.voms.VOMSError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.KeystoreCredential;
import eu.emi.security.authn.x509.impl.PEMCredential;

/**
 * The default strategy used to load user credentials when no file is explicitly
 * pointed out by the user of this API.
 * 
 * Credentials are searched in the following places (in sequence):
 * 
 * <ul>
 * <li>If the <code>X509_USER_PROXY</code> <b>environment variable</b>
 * is set, its value is used to load the user proxy credentials</li>
 * 
 * <li>If the <code>X509_USER_PROXY</code> <b>system property</b>
 * is set, its value is used to load the user proxy credentials</li>
 * 
 * <li>If the <code>X509_USER_CERT</code> and <code>X509_USER_KEY</code>
 * <b>environment variables</b> are set, their values are used to load the user
 * credentials</li>
 * 
 * <li>If the <code>X509_USER_CERT</code> and <code>X509_USER_KEY</code>
 * <b>system properties</b> are set, their values are used to load the user
 * credentials</li>
 * 
 * <li>If the <code>PKCS12_USER_CERT</code> <b>environment variable</b> is set, its
 * value is used to load the user credentials.</li>
 * 
 * <li>If the <code>PKCS12_USER_CERT</code> <b>system property</b> is set, its
 * value is used to load the user credentials.</li>
 * 
 * <li>The content of the <code>.globus</code> directory in the user's home
 * is searched for a PEM certificate (in the <code>usercert.pem</code> and
 * <code>userkey.pem</code> files).</li>
 * 
 * <li>The content of the .globus directory in the user's home is searched
 * for a PKC12 certificate (in the <code>usercert.p12</code> file).</li>
 * </ul>
 *  
 */
public class DefaultLoadCredentialsStrategy implements
		LoadCredentialsStrategy {

	public static final Logger log = LoggerFactory.getLogger(DefaultLoadCredentialsStrategy.class);
	
	private static final String X509_USER_PROXY = "X509_USER_PROXY";
	private static final String X509_USER_CERT = "X509_USER_CERT";
	private static final String X509_USER_KEY = "X509_USER_KEY";
	private static final String PKCS12_USER_CERT = "PKCS12_USER_CERT";
	
	private static final String GLOBUS_PKCS12_CRED_PATH_SUFFIX = ".globus/usercred.p12";
	private static final String GLOBUS_PEM_CERT_PATH_SUFFIX = ".globus/usercert.pem";
	private static final String GLOBUS_PEM_KEY_PATH_SUFFIX = ".globus/userkey.pem";
	
	private static final String HOME_PROPERTY = "user.home";
	
	private String home;
	
	public DefaultLoadCredentialsStrategy(String homeFolder) {
		this.home = homeFolder;
	}
	
	public DefaultLoadCredentialsStrategy() {
		home = System.getProperty(HOME_PROPERTY);
		if (home == null)
			throw new VOMSError(HOME_PROPERTY+" not found in system properties!");
	}
	/**
	 * Looks for the value of a given property in the environment or in the
	 * system properties
	 * @param propName the property that will be looked for
	 * @return the property value, or null if no property was found
	 */
	public String getFromEnvOrSystemProperty(String propName){
		
		String val = System.getenv(propName);
		if (val == null)
			val = System.getProperty(propName);
		return val;
	}
	
	private boolean fileExistsAndIsReadable(String filename){
		File f = new File(filename);
		return f.exists() && f.isFile() && f.canRead();
	}

	protected X509Credential loadProxyCertificate(String proxyCertFile) throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		PEMCredential cred = null;
		if (fileExistsAndIsReadable(proxyCertFile))
			cred = new PEMCredential(new FileInputStream(proxyCertFile), null);
		return cred;
	}
	
	protected X509Credential loadPEMCredential(String certFile, String keyFile, char[] keyPassword) throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		PEMCredential cred = null;
		if (fileExistsAndIsReadable(certFile) && fileExistsAndIsReadable(keyFile))
			cred = new PEMCredential(new FileInputStream(keyFile), new FileInputStream(certFile), keyPassword);
		return cred;
	}
	
	protected X509Credential loadPKCS12Credential(String credFile, char[] keyPassword) throws KeyStoreException, IOException{
		KeystoreCredential cred = null;
		if (fileExistsAndIsReadable(credFile))
			cred = new KeystoreCredential(credFile, keyPassword, keyPassword, null, "PKCS12");
		return cred;
	}
	
	
	
	public X509Credential loadCredentials(char[] keyPassword) {
			
		try {
			
			X509Credential cred = loadProxyFromEnv();
			
			if (cred == null)
				cred = loadPEMCredentialFromEnv(keyPassword);
			
			if (cred == null)
				cred = loadPKCS12CredentialFromEnv(keyPassword);
			
			if (cred == null)
				cred = loadPEMCredentialsFromGlobusDir(keyPassword);
			
			if (cred == null)
				cred = loadPKCS12CredentialsFromGlobusDir(keyPassword);
			
			return cred;
		
		} catch (Exception e) {
			throw new VOMSError("Error loading credential: "+e.getMessage(),e);
		}
	}


	private X509Credential loadProxyFromEnv() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		
		String proxyPath = getFromEnvOrSystemProperty(X509_USER_PROXY);
		if (proxyPath != null)
			return loadProxyCertificate(proxyPath);
		
		return null;
		
	}
	
	private X509Credential loadPEMCredentialFromEnv(char[] keyPassword) throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		String certPath = getFromEnvOrSystemProperty(X509_USER_CERT);
		String keyPath = getFromEnvOrSystemProperty(X509_USER_KEY);
		
		if (certPath != null && keyPath != null)
			return loadPEMCredential(certPath, keyPath, keyPassword);
		
		return null;
	}
	
	private X509Credential loadPKCS12CredentialFromEnv(char[] keyPassword) throws KeyStoreException, IOException{
		String pkcs12Path = getFromEnvOrSystemProperty(PKCS12_USER_CERT);
		
		if (pkcs12Path != null)
			return loadPKCS12Credential(pkcs12Path, keyPassword);
		
		return null;
	}
	
	
	
	private X509Credential loadPKCS12CredentialsFromGlobusDir(char[] keyPassword) throws KeyStoreException, IOException {
		
		String credPath = String.format("%s/%s", home, GLOBUS_PKCS12_CRED_PATH_SUFFIX);
		return loadPKCS12Credential(credPath, keyPassword);
		
	}


	private X509Credential loadPEMCredentialsFromGlobusDir(char[] keyPassword) throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
		
		String certPath  = String.format("%s/%s", home, GLOBUS_PEM_CERT_PATH_SUFFIX);
		String keyPath = String.format("%s/%s", home, GLOBUS_PEM_KEY_PATH_SUFFIX);
		
		return loadPEMCredential(certPath, keyPath, keyPassword);
	}
	
	

}
