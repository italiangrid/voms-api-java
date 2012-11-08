package org.italiangrid.voms.credential.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.BadPaddingException;

import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;

import eu.emi.security.authn.x509.helpers.AbstractDelegatingX509Credential;
import eu.emi.security.authn.x509.helpers.FlexiblePEMReader;
import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;
import eu.emi.security.authn.x509.impl.KeyAndCertCredential;
/**
 * This class leverages BouncyCastle support for password finders to route around
 * canl {@link PEMCredential} limitations which hide such support.
 * 
 * Once <a href="https://github.com/eu-emi/canl-java/issues/21">this issue</a> is 
 * fixed, this class can be removed.
 * 
 * @author andreaceccanti
 *
 */
public class VOMSPEMCredential extends AbstractDelegatingX509Credential {

	public VOMSPEMCredential(String keyPath, 
			String certificatePath,
			PasswordFinder pf) throws IOException, 
			KeyStoreException, CertificateException {
		
		
		FileInputStream certificateStream = new FileInputStream(certificatePath);
		FileInputStream keyStream = new FileInputStream(keyPath);
		
		init(keyStream, certificateStream, pf);
		
	}

	private void init(InputStream privateKeyStream, InputStream certificateStream, 
			PasswordFinder pf) throws IOException, KeyStoreException, CertificateException
	{
		X509Certificate []chain = CertificateUtils.loadCertificateChain(
				certificateStream, Encoding.PEM);
		
		Reader reader = new InputStreamReader(privateKeyStream, 
				Charset.forName("US-ASCII"));
		
		FlexiblePEMReader pemReader = new FlexiblePEMReader(reader, pf);
		PrivateKey pk = internalLoadPK(pemReader, "PEM");
		privateKeyStream.close();
		certificateStream.close();
		delegate = new KeyAndCertCredential(pk, chain);
	}
	
	
	private PrivateKey internalLoadPK(PEMReader pemReader, String type) throws IOException{
		
		Object ret = null;
		try
		{
			ret = pemReader.readObject();
		} catch (IOException e)
		{
			if (e.getCause() != null && e.getCause() instanceof BadPaddingException)
			{
				throw new IOException("Can not load " + type + " private key: the password is " +
						"incorrect or the " + type + " data is corrupted.", e);
			}
			throw new IOException("Can not load the " + type + " private key: " + e);
		}
		if (ret instanceof PrivateKey)
			return (PrivateKey) ret;
		if (ret instanceof KeyPair)
		{
			KeyPair kp = (KeyPair) ret;
			return kp.getPrivate();
		}
		
		throw new IOException("The " + type + " input does not contain a private key, " +
				"it was parsed as " + ret.getClass().getName());
	}
}
