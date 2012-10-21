package org.italiangrid.voms.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;

import eu.emi.security.authn.x509.impl.PEMCredential;

/** 
 * A simple example showing how VOMS attributes validation is done with the new API
 * @author Andrea Ceccanti
 *
 */
public class ValidationExample {

	public ValidationExample() throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
		
		VOMSACValidator validator = VOMSValidators.newValidator();
		
		PEMCredential c = new PEMCredential(new FileInputStream("somefile"), null);
		
		X509Certificate[] chain = c.getCertificateChain();
		
		List<VOMSAttribute> attrs = validator.validate(chain);
		
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
	public static void main(String[] args) throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
		new ValidationExample();

	}

}
