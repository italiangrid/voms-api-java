package org.italiangrid.voms.credential;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import org.bouncycastle.openssl.PEMWriter;

/**
 * An utility class for handling credentials
 * 
 * @author daniele
 * 
 */

public class CredentialsUtils {

	/**
	 * Saves user credentials as a plain text PEM data. <br>
	 * Writes the user certificate first, then the user key and finally all certs
	 * in the certificate chain.
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
	public static void saveCredentials(OutputStream os, UserCredentials uc) throws UnrecoverableKeyException,
			KeyStoreException, IllegalArgumentException, NoSuchAlgorithmException, IOException, NoSuchProviderException,
			CertificateException {


		/* Using the old save() method for writing the proxy */

		OutputStreamWriter osw = new OutputStreamWriter(os);
		PEMWriter writer = new PEMWriter(osw);

		writer.writeObject(uc.getUserCertificate());

		if (uc.getUserKey() != null)
			writer.writeObject(uc.getUserKey());

		for (int i = 1; i < uc.getUserChain().length; i++) {
			writer.writeObject(uc.getUserChain()[i]);
		}
		writer.flush();


		// /* New method for writing the proxy */
		//
		// String encryptionAlg = null;
		// String alias = "mykey";
		// char[] keyPassword = null;
		// char[] encryptionPassword = null;
		//
		// KeyStore ks = KeyStore.getInstance("PKCS12", "BC");
		//
		// ks.load(null, null);
		// ks.setKeyEntry(alias, uc.getUserKey(), keyPassword, uc.getUserChain());
		//
		// CertificateUtils.savePEMKeystore(os, ks, alias, encryptionAlg,
		// keyPassword, encryptionPassword);

	}
}
