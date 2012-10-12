package org.glite.voms.v2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.glite.voms.contact.VOMSSocket;
import org.glite.voms.v2.ac.DefaultVOMSACParser;
import org.glite.voms.v2.ac.DefaultVOMSValidator;
import org.glite.voms.v2.store.UpdatingVOMSTrustStore;
import org.glite.voms.v2.store.VOMSTrustStore;

import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.impl.X500NameUtils;

public class ValidationExample {

	
	public ValidationExample() throws FileNotFoundException, IOException, KeyStoreException, CertificateException {
		String file = "/Users/Cecco/x509up_u501";
		String vomsdir = "/Users/Cecco/vomsdir";
		
		UpdatingVOMSTrustStore vomsStore = new UpdatingVOMSTrustStore();
		OpensslCertChainValidator certValidator =  new OpensslCertChainValidator("/etc/grid-security/certificates");
		
		DefaultVOMSValidator validator = new DefaultVOMSValidator(vomsStore,certValidator);
		
		PEMCredential c = new PEMCredential(new FileInputStream(file), null);
		
		X509Certificate[] certChain = c.getCertificateChain();
		VOMSAttributes attrs = validator.validate(certChain);
		
		System.out.println(attrs);
		System.out.println(attrs.getGenericAttributes());
		for (X509Certificate cert: attrs.getAACertificates()){
			System.out.println(X500NameUtils.getReadableForm(cert.getSubjectX500Principal()));
		}
		
		vomsStore.cancel();
		certValidator.dispose();
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
