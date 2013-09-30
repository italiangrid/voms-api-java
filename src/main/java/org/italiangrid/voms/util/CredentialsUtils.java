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
package org.italiangrid.voms.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.helpers.CertificateHelpers;
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
	 * Serializes a private key to an output stream
	 * following the pkcs8 encoding.
	 * 
	 * This method just delegates to canl, but provides a much
	 * more understandable signature.
	 * 
	 * @param os
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private static void savePrivateKeyPKCS8(OutputStream os,
		PrivateKey key) throws IllegalArgumentException, IOException{
		
		CertificateUtils.savePrivateKey(os, 
			key, 
			Encoding.PEM, 
			null, 
			null);
		
	}
	
	/**
	 * Serializes a private key to an output stream
	 * following the pkcs1 encoding.
	 * 
	 * This method just delegates to canl, but provides a much
	 * more understandable signature.
	 * 
	 * @param os 
	 * @param key
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	private static void savePrivateKeyPKCS1(OutputStream os, 
		PrivateKey key) throws 
		IllegalArgumentException, IOException{
			
			CertificateUtils.savePrivateKey(os, 
				key, 
				Encoding.PEM, 
				null, 
				new char[0],
				true);
		
	}
	
	
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
	public static void saveProxyCredentials(OutputStream os, X509Credential uc)
			throws UnrecoverableKeyException, KeyStoreException, IllegalArgumentException,
			NoSuchAlgorithmException, IOException, NoSuchProviderException, CertificateException {

		X509Certificate[] chain = CertificateHelpers.sortChain(Arrays.asList(uc.getCertificateChain()));

		PrivateKey key = uc.getKey();
		X509Certificate cert = uc.getCertificate();
				
		CertificateUtils.saveCertificate(os, cert, Encoding.PEM);
				
		if (key != null)
			// Defaults to pkcs1 to avoid breaking dCache clients.
			savePrivateKeyPKCS1(os, key);
				
		X509Certificate c = null;
		for (int index=1;index<chain.length;index++){
					
			c=chain[index];
					
			int basicConstraints = c.getBasicConstraints();
					
			// Only save non-CA certs to proxy file
			if (basicConstraints < 0)
				CertificateUtils.saveCertificate(os,c, Encoding.PEM);
					
		}

		os.flush();
	}
	
	
	/**
	 * Saves proxy credentials to a file. This method ensures that the stored proxy is saved with the
	 * appropriate file permissions.
	 * 
	 * @param proxyFileName
	 * @param uc
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws IllegalArgumentException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
	public static void saveProxyCredentials(String proxyFileName, X509Credential uc) throws IOException, UnrecoverableKeyException, KeyStoreException, IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException, CertificateException{
			
		File f = new File(proxyFileName);
		RandomAccessFile raf = new RandomAccessFile(f, "rws");
		FileChannel channel = raf.getChannel();
		FilePermissionHelper.setProxyPermissions(proxyFileName);
		channel.truncate(0);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		saveProxyCredentials(baos, uc);
		
		baos.close();
		channel.write(ByteBuffer.wrap(baos.toByteArray()));
		
		channel.close();		
		raf.close();
		
	}
}
