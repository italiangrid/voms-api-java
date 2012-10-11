package org.glite.voms.v2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.glite.voms.v2.ac.DefaultVOMSACParser;

import eu.emi.security.authn.x509.impl.PEMCredential;

public class ValidationExample {

	
	public ValidationExample() throws FileNotFoundException, IOException, KeyStoreException, CertificateException {
		String file = "/tmp/x509up_u501";
		
		DefaultVOMSACParser parser = new DefaultVOMSACParser();
		
		PEMCredential c = new PEMCredential(new FileInputStream(file), null);
		
		X509Certificate[] certChain = c.getCertificateChain();
		VOMSAttributes attrs = parser.parse(certChain);
		
		System.out.println(attrs);
		
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
