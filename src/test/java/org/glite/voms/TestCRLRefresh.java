package org.glite.voms;

import java.io.File;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class TestCRLRefresh extends TestCase {
	
	public static final Logger logger = Logger.getLogger(TestCRLRefresh.class); 

	public static final String trustDir = "src/test/resources/trust-anchors";
	public static final String vomsDir = "src/test/resources/vomsdir";
	public static final String testCert = "src/test/resources/certs/quasi_revoked.cert.pem";
	public static final String revokedCert = "src/test/resources/certs/revoked.cert.pem";
	public static final String validCert = "src/test/resources/certs/test0.cert.pem";
	
	private static final int NUM_ITERATIONS = 3;
	
	
	protected void executeCommandsInDir(String[] commands, String dir) throws IOException, InterruptedException{
		
		for (String c: commands){
			ProcessBuilder pb = new ProcessBuilder(c.split(" "));
			pb.directory(new File(dir));
			Process p = pb.start();
			
			int exitStatus = p.waitFor();
			if (exitStatus != 0)
				throw new IllegalStateException("Command "+c+" failed!");
			
		}
	}
	protected void updateCRL() throws IOException, InterruptedException{
		System.out.println("Updating CRL to introduce revocation of certificate...");
		String[] commands = {"ln -sf quasi-revoked-crl.pem d82942ab.r0",
				"ln -sf quasi-revoked-crl.pem 10b10516.r0"};
		
		executeCommandsInDir(commands, trustDir);
	}
	
	protected void restoreCRL() throws IOException, InterruptedException{
		System.out.println("Setting default CRL.");
		String[] commands = {"ln -sf default-crl.pem d82942ab.r0",
		"ln -sf default-crl.pem 10b10516.r0"};
		
		executeCommandsInDir(commands, trustDir);
	}
	
	protected void setupExpiredCRL() throws IOException, InterruptedException{
		System.out.println("Setting expired CRL.");
		String[] commands = {"ln -sf expired-crl.pem d82942ab.r0",
		"ln -sf expired-crl.pem 10b10516.r0"};
		
		executeCommandsInDir(commands, trustDir);
	}
	
	public void testCRLAreFunctional() throws CertificateException, CRLException, IOException, InterruptedException{
		
		restoreCRL();
		
		PKIStore caStore = PKIStoreFactory.getStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = PKIStoreFactory.getStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		caStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(5));
		vomsTrustStore.rescheduleRefresh((int)TimeUnit.SECONDS.toMillis(5));
				
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] theCert = PKIUtils.loadCertificates(testCert);
		
		for (int i=0; i < NUM_ITERATIONS; i++){
			
			boolean valid  = verifier.verify(theCert);
			
			System.out.println("Iteration #"+i+": valid -> "+valid);
			if (i < NUM_ITERATIONS - 2 )
				assertTrue("Certificate found invalid when it was supposed to be valid", valid);
			else
				assertFalse("Certificate found valid after CRL that revokes it was put in place.", valid);
			
			try {
				
				if (i == NUM_ITERATIONS - 3)
					updateCRL();
				Thread.sleep(TimeUnit.SECONDS.toMillis(10));
				
				
			} catch (InterruptedException e) {
				
			}
		}
		
		restoreCRL();
	}
	
	
	public void testExpiredCRLCertificateRejection() throws IOException, InterruptedException, CertificateException, CRLException{
		
		setupExpiredCRL();
		
		Thread.sleep(TimeUnit.SECONDS.toMillis(5));
		
		PKIStore caStore = PKIStoreFactory.getStore(trustDir, PKIStore.TYPE_CADIR, true);
		PKIStore vomsTrustStore = PKIStoreFactory.getStore(vomsDir, PKIStore.TYPE_VOMSDIR, true);
		
		PKIVerifier verifier = new PKIVerifier(vomsTrustStore,caStore);
		X509Certificate[] revokedCert = PKIUtils.loadCertificates(testCert);
		
		X509Certificate[] validCert = PKIUtils.loadCertificates(testCert);
		
		boolean valid = verifier.verify(revokedCert);
		
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
		valid = verifier.verify(validCert);
				
		assertFalse("Certificate found valid even if CRL has expired!", valid);
		
		restoreCRL();
	}
	
	
}
