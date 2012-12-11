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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.ac.VOMSACValidator;

import eu.emi.security.authn.x509.impl.PEMCredential;

public class ValidationExample {

	public ValidationExample(String filename) throws FileNotFoundException, IOException, KeyStoreException, CertificateException {
		
		
		VOMSACValidator validator = VOMSValidators.newValidator();
		
		PEMCredential c = new PEMCredential(new FileInputStream(filename), (char[])null);
		
		X509Certificate[] certChain = c.getCertificateChain();	
		List<VOMSAttribute> attrs = validator.validate(certChain);
		
		for (VOMSAttribute a: attrs)
			System.out.println(a);

		validator.shutdown();
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 * @throws KeyStoreException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, KeyStoreException, CertificateException {
		new ValidationExample(args[0]);
	}

}
