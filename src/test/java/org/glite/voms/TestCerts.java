package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class TestCerts extends TestCase implements TestFixture {

	public static final Logger log = Logger.getLogger(TestCerts.class);

	public void testProxyWithParenthesesInDN() throws CertificateException,
			CRLException, IOException, InterruptedException {

		log.info("TestCerts.testProxyWithParenthesesInDN");
		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR,
				true);

		PKIVerifier verifier = new PKIVerifier(vomsTrustStore, caStore);

		X509Certificate[] proxyChain = PKIUtils
				.loadCertificates("/tmp/x509up_u1000");

		boolean validChain = verifier.verify(proxyChain);

		log.info("Cert chain is valid? " + validChain);

		assertTrue("Certificate validation failed", validChain);

		verifier.cleanup();
	}
}
