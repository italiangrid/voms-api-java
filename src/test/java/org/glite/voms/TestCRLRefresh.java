package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class TestCRLRefresh extends TestCase implements TestFixture{
	
	public static final Logger logger = Logger.getLogger(TestCRLRefresh.class); 
	
	public void testCRLAreFunctional() throws CertificateException, CRLException, IOException, InterruptedException{
				
		PKIStore caStore = PKIStoreFactory.getStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = PKIStoreFactory.getStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		caStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(30));
		vomsTrustStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(30));
				
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(testCert);
		
		boolean valid  = verifier.verify(theCert);
		assertTrue("Certificate found invalid when it was supposed to be valid", valid);
		
		Utils.setCRL(testCertRevokedCRL);
		
		caStore.refresh();
		valid  = verifier.verify(theCert);
		assertFalse("Certificate found valid after CRL that revokes it was put in place.", valid);
		
		verifier.cleanup();	
	}
	
	@Override
	protected void setUp() throws Exception {
		Utils.setCRL(defaultCRL);
	}
	
	@Override
	protected void tearDown() throws Exception {
		Utils.setCRL(defaultCRL);
	}
	
	public void testExpiredCRLCertificateRejection() throws IOException, InterruptedException, CertificateException, CRLException{
		
		Utils.setCRL(expiredCRL);
		
		PKIStore caStore = PKIStoreFactory.getStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = PKIStoreFactory.getStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		// Force update to avoid optimizations that may change the order of setup
		caStore.refresh();
		
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		
		X509Certificate[] revokedCert = PKIUtils.loadCertificates(testCert);
		X509Certificate[] validCertChain = PKIUtils.loadCertificates(validCert);
		
		boolean valid = verifier.verify(revokedCert);
		
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
		valid = verifier.verify(validCertChain);
				
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
	}
}
