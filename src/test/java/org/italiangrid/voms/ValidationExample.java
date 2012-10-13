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

	public ValidationExample() throws FileNotFoundException, IOException, KeyStoreException, CertificateException {
		String file = "/Users/cecco/x509up_u501";
		
		VOMSACValidator validator = VOMSValidators.newValidator();
		
		PEMCredential c = new PEMCredential(new FileInputStream(file), null);
		
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
		new ValidationExample();
	}

}
