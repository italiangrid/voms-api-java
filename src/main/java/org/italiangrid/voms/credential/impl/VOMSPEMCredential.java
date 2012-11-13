/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
