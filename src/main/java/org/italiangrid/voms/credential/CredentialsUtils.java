package org.italiangrid.voms.credential;

import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;

/**
 * An utility class for handling credentials
 * 
 * @author Daniele Andreotti
 * @author Andrea Ceccanti
 * 
 */
public class CredentialsUtils {
	
	/**
	 * Saves user credentials as a plain text PEM data. <br>
	 * Writes the user certificate chain first, then the user key.
	 * 
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws IllegalArgumentException
	 * @throws KeyStoreException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
	public static void saveCredentials(OutputStream os, X509Credential uc) throws UnrecoverableKeyException,
			KeyStoreException, IllegalArgumentException, NoSuchAlgorithmException, IOException, NoSuchProviderException,
			CertificateException {
		
		X509Certificate[] chain = uc.getCertificateChain();
		
		for (X509Certificate c: chain)
			CertificateUtils.saveCertificate(os, c, Encoding.PEM);
		
		PrivateKey key = uc.getKey();
		
		if (key != null)
			CertificateUtils.savePrivateKey(os, key, Encoding.PEM, null, null);
		
		os.flush();
	}
}
