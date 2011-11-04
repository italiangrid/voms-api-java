package org.glite.voms;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

public class Test extends TestCase {

	
	public void testExample(){
		
		assertTrue(true);
	}
	
	public void testRFCProxyValidation(){
		
		String rfcProxyFile = "/tmp/x509up_u507";
		VOMSValidator validator;
		
		try {
			X509Certificate[] certChain = PKIUtils.loadCertificates(rfcProxyFile);
			validator = new VOMSValidator(certChain);
			
			validator.validate();
			
			for (String s: validator.getAllFullyQualifiedAttributes())
				System.out.println(s);
			
			
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
