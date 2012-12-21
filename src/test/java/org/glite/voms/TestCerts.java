package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.glite.voms.contact.UserCredentials;
import org.glite.voms.contact.VOMSProxyInit;
import org.glite.voms.contact.VOMSRequestOptions;

public class TestCerts extends TestCase implements TestFixture {

	public static final Logger log = Logger.getLogger(TestCerts.class);

	private final String password = "pass";

	public void testProxyWithParenthesesInDN() throws CertificateException,
			CRLException, IOException, InterruptedException {

		final String voName = "test.vo";

		log.info("TestCerts.testProxyWithParenthesesInDN");

		UserCredentials credentials = UserCredentials.instance(
				dnWithParenthesisCert, dnWithParenthesisKey, password);

		VOMSProxyInit proxyInit = VOMSProxyInit.instance(credentials);

		Map<String, VOMSRequestOptions> vomsOptions = new HashMap<String, VOMSRequestOptions>();

		VOMSRequestOptions requestOptions = new VOMSRequestOptions();
		requestOptions.setVoName(voName);
		vomsOptions.put(voName, requestOptions);

		UserCredentials proxy = proxyInit.getVomsProxy(vomsOptions.values());

		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR,
				true);

		PKIVerifier verifier = new PKIVerifier(vomsTrustStore, caStore);

		X509Certificate[] proxyCertificateChain = proxy.getUserChain();

		boolean validChain = verifier.verify(proxyCertificateChain);

		log.info("Cert chain is valid? " + validChain);

		assertTrue("Certificate validation failed", validChain);

		verifier.cleanup();

	}
}
