package org.italiangrid.voms.credential.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;

import org.bouncycastle.openssl.PasswordFinder;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.credential.LoadCredentialsEventListener;
import org.italiangrid.voms.credential.ProxyPathBuilder;
import org.italiangrid.voms.credential.VOMSEnvironmentVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.X509Credential;

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
public class DefaultLoadCredentialsStrategy  extends AbstractLoadCredentialsStrategy 
	implements VOMSEnvironmentVariables {

	public static final Logger log = LoggerFactory.getLogger(DefaultLoadCredentialsStrategy.class);
	
	private static final String GLOBUS_PKCS12_CRED_PATH_SUFFIX = ".globus/usercred.p12";
	private static final String GLOBUS_PEM_CERT_PATH_SUFFIX = ".globus/usercert.pem";
	private static final String GLOBUS_PEM_KEY_PATH_SUFFIX = ".globus/userkey.pem";
	
	private static final String HOME_PROPERTY = "user.home";
	private static final String TMPDIR_PROPERTY = "java.io.tmpdir";
	
	private static final ProxyPathBuilder proxyPathBuilder = new DefaultProxyPathBuilder();
	
	private String home;
	private String tmpDir;
	
	public DefaultLoadCredentialsStrategy(String homeFolder, String tempDir, LoadCredentialsEventListener listener) {
		super(listener);
		
		this.home = homeFolder;
		this.tmpDir = tempDir;
		
		if (home == null)
			throw new VOMSError(HOME_PROPERTY+" not found in system properties!");
	}
	
	public DefaultLoadCredentialsStrategy(String homeFolder) {
		this(homeFolder, System.getProperty(TMPDIR_PROPERTY), new LoggingCredentialNotificationListener());
	}
	
	
	public DefaultLoadCredentialsStrategy() {
		this(System.getProperty(HOME_PROPERTY), System.getProperty(TMPDIR_PROPERTY), new LoggingCredentialNotificationListener());
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
	
	public X509Credential loadCredentials(PasswordFinder pf) {
			
		if (pf == null)
			throw new IllegalArgumentException("Please provide a non-null password finder!");
		
		try {
			
			X509Credential cred = loadProxyFromEnv();
			
			if (cred == null)
				cred = loadProxyFromUID();
			
			if (cred == null)
				cred = loadPEMCredentialFromEnv(pf);
			
			if (cred == null)
				cred = loadPKCS12CredentialFromEnv(pf);
			
			if (cred == null)
				cred = loadPEMCredentialsFromGlobusDir(pf);
			
			if (cred == null)
				cred = loadPKCS12CredentialsFromGlobusDir(pf);
			
			return cred;
		
		} catch (Exception e) {
			throw new VOMSError("Error loading credential: "+e.getMessage(),e);
		}
	}


	private X509Credential loadProxyFromUID() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		String uid = getFromEnvOrSystemProperty(VOMS_USER_ID);
		
		if (uid != null){
			String proxyFile = proxyPathBuilder.buildProxyFilePath(tmpDir, Integer.parseInt(uid));
			return loadProxyCredential(proxyFile);
		}
		
		return null;
	}
	private X509Credential loadProxyFromEnv() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		
		String proxyPath = getFromEnvOrSystemProperty(X509_USER_PROXY);
		if (proxyPath != null)
			return loadProxyCredential(proxyPath);
		
		return null;
	}
	
	private X509Credential loadPEMCredentialFromEnv(PasswordFinder pf) throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		String certPath = getFromEnvOrSystemProperty(X509_USER_CERT);
		String keyPath = getFromEnvOrSystemProperty(X509_USER_KEY);
		
		if (certPath != null && keyPath != null){
			
			return loadPEMCredential(keyPath, certPath, pf);
		}
		return null;
	}
	
	private X509Credential loadPKCS12CredentialFromEnv(PasswordFinder pf) throws KeyStoreException, IOException{
		String pkcs12Path = getFromEnvOrSystemProperty(PKCS12_USER_CERT);
		
		if (pkcs12Path != null){
			return loadPKCS12Credential(pkcs12Path, pf);
		}
		return null;
	}
	
	
	
	private X509Credential loadPKCS12CredentialsFromGlobusDir(PasswordFinder pf) throws KeyStoreException, IOException {
		
		String credPath = String.format("%s/%s", home, GLOBUS_PKCS12_CRED_PATH_SUFFIX);
		return loadPKCS12Credential(credPath, pf);
		
	}


	private X509Credential loadPEMCredentialsFromGlobusDir(PasswordFinder pf) throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
		
		String certPath  = String.format("%s/%s", home, GLOBUS_PEM_CERT_PATH_SUFFIX);
		String keyPath = String.format("%s/%s", home, GLOBUS_PEM_KEY_PATH_SUFFIX);
		
		return loadPEMCredential(keyPath, certPath, pf);
	}
}
