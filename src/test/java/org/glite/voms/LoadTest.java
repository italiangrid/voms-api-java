package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.glite.voms.ac.ACValidator;
import org.glite.voms.contact.UserCredentials;
import org.glite.voms.contact.VOMSProxyInit;
import org.glite.voms.contact.VOMSRequestOptions;

public class LoadTest extends TestCase {

	private List<X509Certificate[]> chains = new ArrayList<X509Certificate[]>();
	
	private int NUM_ITERATIONS = 100000;
	
	private String VO_NAME = "test.vo";
	
	protected X509Certificate[] createVOMSProxyChain(String cert, String key, String passphrase) {
		
		UserCredentials credentials = UserCredentials.instance(cert, key, passphrase);

		VOMSProxyInit proxyInit = VOMSProxyInit.instance(credentials);
		
		Map<String, VOMSRequestOptions> vomsOptions = new HashMap<String, VOMSRequestOptions>();

		VOMSRequestOptions requestOptions = new VOMSRequestOptions();
		requestOptions.setVoName(VO_NAME);
		vomsOptions.put(VO_NAME, requestOptions);

		return proxyInit.getVomsProxy(vomsOptions.values()).getUserChain();
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	
		X509Certificate[] aChain = createVOMSProxyChain(TestFixture.validCert, TestFixture.validCertKey, TestFixture.passphrase);
		chains.add(aChain);
		
		X509Certificate[] anotherChain = createVOMSProxyChain(TestFixture.anotherValidCert, TestFixture.anotherValidCertKey, TestFixture.passphrase);
		chains.add(anotherChain);
	}
	
	/**
	 * Load tests a pattern of use for voms validation. In this pattern
	 * a new validator is created for each validation.
	 * 
	 * 
	 * @throws CertificateException
	 */
	public void testNewValidatorForEachValidation() throws CertificateException {

		for (int i = 0; i < NUM_ITERATIONS; i++) {

			VOMSValidator validator = new VOMSValidator(chains.get(i%2));
			
			String[] fullyQualifiedAttributes = validator.validate().getAllFullyQualifiedAttributes();
			
			assertTrue(fullyQualifiedAttributes.length > 0);
		}

	}

	/**
	 * 
	 * Load tests a pattern of use for voms validation. In this pattern
	 * a shared validator is created once and used for each validation.
	 *
	 * @throws CertificateException
	 * @throws CRLException
	 * @throws IOException
	 */
	public void testSharedValidator() throws CertificateException, CRLException,
			IOException {

		PKIStore caStore = new PKIStore(PKIStore.TYPE_CADIR);
		PKIStore vomsTrustStore = new PKIStore(PKIStore.TYPE_VOMSDIR);

		caStore.rescheduleRefresh((int) TimeUnit.SECONDS.toMillis(5));

		PKIVerifier verifier = new PKIVerifier(vomsTrustStore, caStore);
		ACValidator acValidator = new ACValidator(verifier);

		VOMSValidator validator = new VOMSValidator(chains.get(0), acValidator);

		for (int i = 0; i < NUM_ITERATIONS; i++) {

			assertTrue(verifier.verify(chains.get(i%2)));
			
			assertTrue(validator.validate().getAllFullyQualifiedAttributes().length > 0);
		}

	}

	/**
	 * 
	 * Load tests a pattern of use for voms validation. In this pattern
	 * a shared validator is created once and used for each validation, setting
	 * the voms truststore using the deprecated 
	 * {@link VOMSValidator#setTrustStore(org.glite.voms.ac.VOMSTrustStore)}. 
	 * Used in CREAM.
	 * 
	 *
	 * @throws CertificateException
	 * @throws CRLException
	 * @throws IOException
	 */
	public void testYetAnotherPattern() throws CertificateException, CRLException, IOException {

		PKIStore caStore = new PKIStore(PKIStore.TYPE_CADIR);
		caStore.rescheduleRefresh((int) TimeUnit.SECONDS.toMillis(5));

		PKIStore vomsTrustStore = new PKIStore(PKIStore.TYPE_VOMSDIR);

		PKIVerifier verifier = new PKIVerifier(vomsTrustStore, caStore);
		
		ACValidator acValidator = ACValidator.getInstance(vomsTrustStore);

		VOMSValidator.setTrustStore(vomsTrustStore);
		
		VOMSValidator validator = new VOMSValidator(chains.get(0), acValidator);

		for (int i = 0; i < NUM_ITERATIONS; i++) {

			assertTrue(verifier.verify(chains.get(i%2)));

			assertTrue(validator.validate().getAllFullyQualifiedAttributes().length > 0);

		}
		
	}

}
