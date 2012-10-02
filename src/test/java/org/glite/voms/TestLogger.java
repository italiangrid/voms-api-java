package org.glite.voms;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogger {

	/**
	 * Create a certificate from the file name passed as argument and use it to
	 * obtain a VOMSValidator object.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Logger logger = LoggerFactory.getLogger(TestLogger.class);

		FileCertReader fcr = null;

		String cert_name = args[0];

		String cert = new String(cert_name);

		logger.info("Start test");

		try {
			fcr = new FileCertReader();

			Vector<X509Certificate> certs = fcr.readCerts(cert);
			VOMSValidator val = new VOMSValidator(certs.get(0));
		} catch (CertificateException e) {
			logger.error("Certificate issue");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("I/O issue");
			e.printStackTrace();
		}

	}

}
