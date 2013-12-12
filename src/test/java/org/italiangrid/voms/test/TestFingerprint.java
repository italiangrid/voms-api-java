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
