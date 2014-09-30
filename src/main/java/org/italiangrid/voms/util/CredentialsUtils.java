/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
	 * 
	 * The encoding used to serialize proxy credentials private key.
	 *
	 */
	public enum PrivateKeyEncoding{
		PKCS_1,
		PKCS_8
	}
	
	/**
	 * The default encoding used when no encoding is specified
	 * by callers.
	 */
	public static final PrivateKeyEncoding DEFAULT_ENCONDING =
		PrivateKeyEncoding.PKCS_1;
	
	
	/**
	 * Serializes a private key to an output stream according to an encoding.
	 * 
	 * @param os
	 * @param key
	 * @param encoding
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	public static void savePrivateKey(OutputStream os, PrivateKey key, 
		PrivateKeyEncoding encoding) throws IllegalArgumentException, IOException{
		
		switch(encoding){
			case PKCS_1:
				savePrivateKeyPKCS1(os, key);
				break;
			case PKCS_8:
				savePrivateKeyPKCS8(os, key);
				break;
			default:
				throw new IllegalArgumentException("Unsupported private key encoding: "
					+encoding.name());
		}
	}
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
	 * @param os
	 * @param uc
	 * @param encoding
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws IllegalArgumentException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
	public static void saveProxyCredentials(OutputStream os, X509Credential uc, PrivateKeyEncoding encoding)
			throws UnrecoverableKeyException, KeyStoreException, IllegalArgumentException,
			NoSuchAlgorithmException, IOException, NoSuchProviderException, CertificateException {

		X509Certificate[] chain = CertificateHelpers.sortChain(Arrays.asList(uc.getCertificateChain()));

		PrivateKey key = uc.getKey();
		X509Certificate cert = uc.getCertificate();
				
		CertificateUtils.saveCertificate(os, cert, Encoding.PEM);
				
		if (key != null)
			savePrivateKey(os, key, encoding);
				
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
	 * 
	 * Saves user credentials as a plain text PEM data. <br>
	 * Writes the user certificate chain first, then the user key, using the
	 * default encoding specified in {@link #DEFAULT_ENCONDING}.
	 *
	 * @param os
	 * @param uc
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws IllegalArgumentException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
	public static void saveProxyCredentials(OutputStream os, X509Credential uc)
		throws UnrecoverableKeyException, KeyStoreException, IllegalArgumentException,
		NoSuchAlgorithmException, IOException, NoSuchProviderException, CertificateException {
		saveProxyCredentials(os, uc, DEFAULT_ENCONDING);
	}
	
	/**
	 * Saves proxy credentials to a file. This method ensures that the stored 
	 * proxy is saved with the appropriate file permissions.
	 * 
	 * @param proxyFileName the file where the proxy will be saved
	 * @param uc the credential to be saved
	 * @param encoding the private key encoding 
	 * @throws IOException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws IllegalArgumentException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 */
	public static void saveProxyCredentials(String proxyFileName, 
		X509Credential uc, PrivateKeyEncoding encoding) 
			throws IOException, UnrecoverableKeyException, KeyStoreException, 
			IllegalArgumentException, NoSuchAlgorithmException, 
			NoSuchProviderException, CertificateException{
			
		File f = new File(proxyFileName);
		RandomAccessFile raf = new RandomAccessFile(f, "rws");
		FileChannel channel = raf.getChannel();
		FilePermissionHelper.setProxyPermissions(proxyFileName);
		channel.truncate(0);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		saveProxyCredentials(baos, uc, encoding);
		
		baos.close();
		channel.write(ByteBuffer.wrap(baos.toByteArray()));
		
		channel.close();		
		raf.close();
	}
	
	/**
	 * 
	 * Saves proxy credentials to a file. This method ensures that the stored 
	 * proxy is saved with the appropriate file permissions, using the
	 * default encoding specified in {@link #DEFAULT_ENCONDING}.
	 * 
	 * @param proxyFileName
	 * @param uc
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws IllegalArgumentException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws CertificateException
	 * @throws IOException
	 */
	public static void saveProxyCredentials(String proxyFileName, 
		X509Credential uc) throws UnrecoverableKeyException, KeyStoreException, 
		IllegalArgumentException, NoSuchAlgorithmException, NoSuchProviderException, 
		CertificateException, IOException{
		
		saveProxyCredentials(proxyFileName, uc, DEFAULT_ENCONDING);
	}
}
