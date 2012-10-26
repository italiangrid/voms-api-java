package org.glite.voms;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class TestCRL extends TestCase implements TestFixture{
	
	public static final Logger log = Logger.getLogger(TestCRL.class); 
	
	@Override
	protected void setUp() throws Exception {
		Utils.setCRL(defaultCRL);
	}
	
	public void testCRLAreFunctional() throws CertificateException, CRLException, IOException, InterruptedException{
				
		log.info("TestCRL.testCRLAreFunctional");
		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
				
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(testCert);
		
		boolean valid  = verifier.verify(theCert);
		assertTrue("Certificate found invalid when it was supposed to be valid", valid);
		
		verifier.cleanup();	
	}
	
	
	public void testCRLRevocationEffective() throws Exception{
		
		log.info("TestCRL.testCRLRevocationEffective");
		
		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
				
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(revokedCert);
		
		boolean valid  = verifier.verify(theCert);
		assertFalse("Certificate found valid when it was supposed to be revoked", valid);
		
		verifier.cleanup();
		
	}
	
	
	public void testExpiredCRLCertificateRejection() throws IOException, InterruptedException, CertificateException, CRLException{
		log.info("TestCRL.testExpiredCRLCertificateRejection");
		Utils.setCRL(expiredCRL);
			
		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		
		X509Certificate[] revokedCert = PKIUtils.loadCertificates(testCert);
		X509Certificate[] validCertChain = PKIUtils.loadCertificates(validCert);
		
		boolean valid = verifier.verify(revokedCert);
		
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
		valid = verifier.verify(validCertChain);
				
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
		verifier.cleanup();
		
	}
	
	public void testCRLUpdate() throws Exception{
		log.info("TestCRL.testCRLUpdate");
		
		PKIStore caStore = new PKIStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		caStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(5));
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(testCert);
		
		boolean valid  = verifier.verify(theCert);
		assertTrue("Certificate found invalid when it was supposed to be valid", valid);
		
		Utils.setCRL(testCertRevokedCRL);
		
		Thread.sleep(TimeUnit.SECONDS.toMillis(10));
		valid  = verifier.verify(theCert);
		assertFalse("Certificate found valid when it was supposed to be revoked", valid);
		
		verifier.cleanup();
	}
	
	public void testNoCRLFoundVerificationSuccess() throws Exception{
		log.info("TestCRL.testNoCRLFoundVerificationSuccess");
		
		PKIStore caStore = new PKIStore(noCRLsTrustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = new PKIStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(testCert);
		
		boolean valid  = verifier.verify(theCert);
		assertTrue("Certificate found invalid when it was supposed to be valid", valid);
	}	
}
