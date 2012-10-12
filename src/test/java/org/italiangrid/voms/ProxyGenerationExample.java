package org.italiangrid.voms;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

public class ProxyGenerationExample {

	/**
	 * Extracts VOMS AC from a given VOMS proxy (adoption of the new
	 * voms-api-java design) and use it to generate a new proxy by using the
	 * CNAL library (ProxyGenerator)
	 * 
	 * @param args
	 * @throws IOException
	 * @throws CertificateException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeyException
	 */
	public static void main(String[] args) throws IOException, CertificateException, KeyStoreException,
			InvalidKeyException, SignatureException, NoSuchAlgorithmException {

		Logger logger = LoggerFactory.getLogger(ProxyGenerationExample.class);

		char[] pwd = { 'p', 'a', 's', 's' };

		FileInputStream fcert = new FileInputStream("/home/daniele/.globus/usercert.pem");
		FileInputStream fpkey = new FileInputStream("/home/daniele/.globus/userkey.pem");
		FileInputStream fvoms_proxy = new FileInputStream("/home/daniele/x509up_u1000");

		X509Certificate[] certchain = CertificateUtils.loadCertificateChain(fcert, CertificateUtils.Encoding.PEM);
		PrivateKey pkey = CertificateUtils.loadPrivateKey(fpkey, CertificateUtils.Encoding.PEM, pwd);

		PEMCredential pc = new PEMCredential(fvoms_proxy, null);
		X509Certificate[] vomspx_chain = pc.getCertificateChain();
		X509Certificate voms_proxy = vomspx_chain[0];

		/* Get VOMS AC from the given proxy */
		List<AttributeCertificate> ac_list = org.italiangrid.voms.asn1.VOMSACUtils.getACsFromCertificate(voms_proxy);
		AttributeCertificate[] ac = ac_list.toArray(new AttributeCertificate[ac_list.size()]);


		/* Set VOMS AC for the new proxy */
		ProxyCertificateOptions pxopt = new ProxyCertificateOptions(certchain);
		pxopt.setAttributeCertificates(ac);


		ProxyCertificate pxcert = ProxyGenerator.generate(pxopt, pkey);
		X509Certificate[] list = pxcert.getCertificateChain();

		for (int i = 0; i < list.length; i++)
			logger.info(list[i].toString() + "\n\n#######################\n\n");


	}
}
