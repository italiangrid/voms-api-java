package org.glite.voms;

import java.io.File;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.glite.voms.contact.cli.VomsProxyInitClient;

public class TestCerts extends TestCase implements TestFixture {

	public static final Logger log = Logger.getLogger(TestCerts.class);

	private final String proxyFileName = File.separator + "tmp"
			+ File.separator + "x509up_u_" + System.getProperty("user.name");

	private final String password = "pass";

	public void testProxyWithParenthesesInDN() throws CertificateException,
			CRLException, IOException, InterruptedException {

		final String voName = "test.vo";

		final String fqan = "/test.vo";

		String[] args = { "-usercert", dnWithParenthesisCert, "-userkey",
				dnWithParenthesisKey, "-voms", voName + ":" + fqan,
				"-password", password };

		log.info("TestCerts.testProxyWithParenthesesInDN");

		new VomsProxyInitClient(args);

		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR,
				true);

		PKIVerifier verifier = new PKIVerifier(vomsTrustStore, caStore);

		X509Certificate[] proxyCertificateChain = PKIUtils
				.loadCertificates(proxyFileName);

		boolean validChain = verifier.verify(proxyCertificateChain);

		log.info("Cert chain is valid? " + validChain);

		assertTrue("Certificate validation failed", validChain);

		verifier.cleanup();

		FileUtils.deleteQuietly(new File(proxyFileName));

	}
}
