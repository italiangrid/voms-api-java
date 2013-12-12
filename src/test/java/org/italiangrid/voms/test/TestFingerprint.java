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
package org.italiangrid.voms.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.italiangrid.voms.util.FingerprintHelper;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.PEMCredential;


public class TestFingerprint {

	public static final String keyPassword = "pass";
	
	public static final String pemCert = "src/test/resources/certs/test0.cert.pem";
	public static final String pemKey = "src/test/resources/certs/test0.key.pem";
	
	
	@Test
	public void testGetFingerprint() throws KeyStoreException, CertificateException, FileNotFoundException, IOException, NoSuchAlgorithmException {
		
			PEMCredential cred = new PEMCredential(new FileInputStream(pemKey), 
				new FileInputStream(pemCert), keyPassword.toCharArray());
			
		
			String fingerprint = FingerprintHelper
				.getFingerprint(cred.getCertificate());
			
			System.out.println(fingerprint);
		
		
	}

}
