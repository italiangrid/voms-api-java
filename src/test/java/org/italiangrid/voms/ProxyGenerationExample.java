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
package org.italiangrid.voms;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.credential.UserCredentials;
import org.italiangrid.voms.util.CredentialsUtils;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

public class ProxyGenerationExample {

	/**
	 * Extracts VOMS AC from a given VOMS proxy (adoption of the new voms-api-java design) and use it to generate a new
	 * proxy by using the CNAL library (ProxyGenerator)
	 * 
	 * @param args
	 * @throws IOException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 * @throws IllegalArgumentException
	 * @throws UnrecoverableKeyException
	 * @throws NoSuchProviderException
	 */
	public static void main(String[] args) throws IOException, CertificateException, KeyStoreException,
			InvalidKeyException, SignatureException, NoSuchAlgorithmException, UnrecoverableKeyException,
			IllegalArgumentException, NoSuchProviderException {


		char[] pwd = "pass".toCharArray();

		FileInputStream fvoms_proxy = new FileInputStream("/home/daniele/x509up_u1000");

		X509Credential cred = UserCredentials.loadCredentials(pwd);

		X509Certificate[] certchain = cred.getCertificateChain();
		PrivateKey pkey = cred.getKey();


		PEMCredential pc = new PEMCredential(fvoms_proxy, (char[])null);
		X509Certificate voms_proxy = pc.getCertificate();

		/* Get VOMS AC from the given proxy */
		List<AttributeCertificate> ac_list = org.italiangrid.voms.asn1.VOMSACUtils.getACsFromCertificate(voms_proxy);
		AttributeCertificate[] ac = ac_list.toArray(new AttributeCertificate[ac_list.size()]);


		/* Set VOMS AC for the new proxy */
		ProxyCertificateOptions pxopt = new ProxyCertificateOptions(certchain);
		pxopt.setAttributeCertificates(ac);


		ProxyCertificate pxcert = ProxyGenerator.generate(pxopt, pkey);

		X509Certificate[] list = pxcert.getCertificateChain();

		for (int i = 0; i < list.length; i++)
			System.out.println(list[i].toString() + "\n\n#######################\n\n");


		/* Save the proxy */
		OutputStream os = new FileOutputStream("/tmp/savedProxy");
		CredentialsUtils.saveProxyCredentials(os, pxcert.getCredential());

	}
}
