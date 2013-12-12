package org.italiangrid.voms.test.canl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.junit.Test;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.KeystoreCredential;

public class TestCANLValidation {

	static final long NUM_ITERATIONS = 100;
	static final int NUM_WORKERS = 250;

	static final CyclicBarrier barrier = new CyclicBarrier(NUM_WORKERS + 1);
	static final ExecutorService pool = Executors.newCachedThreadPool();
	
	@Test
	public void test() throws InterruptedException, BrokenBarrierException, KeyStoreException, CertificateException, FileNotFoundException, IOException {

		

		final X509CertChainValidatorExt sharedCertificateValidator = 
			CertificateValidatorBuilder
			.buildCertificateValidator("/etc/grid-security/certificates", 
				null,
				TimeUnit.MINUTES.toMillis(5), 
				false);
		
		String pkcsFilePath = "/Users/andreaceccanti/.globus/usercred.p12";
		String keyPassword = "0v0s0d0";

		KeystoreCredential cred = new 
			KeystoreCredential(pkcsFilePath, keyPassword.toCharArray(), 
				keyPassword.toCharArray(), null, "PKCS12");
		
		
		System.out.format("Workers: %d. Iterations: %d\n", NUM_WORKERS,
			NUM_ITERATIONS);

		long start = System.currentTimeMillis();

		for (int i = 0; i < NUM_WORKERS; i++)
			pool.execute(new Worker(sharedCertificateValidator, 
				cred.getCertificateChain()));

		// Start test
		barrier.await();

		// Wait for termination
		barrier.await();

		pool.shutdown();

		long duration = System.currentTimeMillis() - start;

		System.out
			.format(
				"Done. Test duration: %d milliseconds. Avg validation duration: " +
				"%d milliseconds.\n",
				duration, duration / (NUM_WORKERS * NUM_ITERATIONS));

	}
	
	class Worker implements Runnable {	
		
		private final X509CertChainValidatorExt val;
		private final X509Certificate[] cert;
		
		public Worker(X509CertChainValidatorExt validator, X509Certificate[] chain) {
			val = validator;
			cert = chain;
		}

		public void run() {
			long iterations = 0;
			
			try {
				
				barrier.await();
				
				while(true){
					
					if (iterations++ > NUM_ITERATIONS)
						break;
					
					val.validate(cert);
					
				}
				
				barrier.await();
				
			} catch (Throwable t) {
				t.printStackTrace(System.err);
				System.exit(1);
			}
				
			
		}
		
	}

}
