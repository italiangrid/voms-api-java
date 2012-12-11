/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms.mt;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import junit.framework.Assert;

import org.italiangrid.voms.VOMSAA;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.impl.DefaultUpdatingVOMSTrustStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.emi.security.authn.x509.NamespaceCheckingMode;
import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.OpensslCertChainValidator;
import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;

public class TestConcurrentValidation {

	static X509CertChainValidatorExt sharedCertificateValidator;
	static UpdatingVOMSTrustStore sharedVOMSTrustStore;
	
	static final String trustAnchorsDir = "src/test/resources/trust-anchors";
	static final long trustAnchorsRefreshInterval = TimeUnit.MINUTES.toMillis(5);
	
	static final String vomsTrustStoreDir = "src/test/resources/vomsdir";
	static final long trustStoreRefreshInterval = TimeUnit.SECONDS.toMillis(10);
	
	static final int numHolderCredentials = 5;
	
	static PEMCredential[] holderCerts;
	
	static VOMSAA testVO_1, testVO_2;
	
	static final String aaCert = "src/test/resources/certs/test_host_cnaf_infn_it.cert.pem";
	static final String aaKey = "src/test/resources/certs/test_host_cnaf_infn_it.key.pem";
	
	static final String aaCert2 = "src/test/resources/certs/wilco_cnaf_infn_it.cert.pem";
	static final String aaKey2 = "src/test/resources/certs/wilco_cnaf_infn_it.key.pem";
	
	static final long NUM_ITERATIONS = 20;
	static final int NUM_WORKERS = 10;
	
	static final CyclicBarrier barrier = new CyclicBarrier(NUM_WORKERS+1);
	
	static final ExecutorService pool = Executors.newCachedThreadPool();
	
	static final String[][] fqans = {{"/test.vo"},
		{"/test.vo.2"}};
	
	static VOMSACValidator sharedValidator;
	
	static void loadHolderCredentials() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		
		holderCerts = new PEMCredential[numHolderCredentials];
		
		for (int i=0; i < numHolderCredentials; i++){
			String baseFileName = String.format("src/test/resources/certs/test%d",i);
			
			holderCerts[i] = new PEMCredential(new FileInputStream(baseFileName+".key.pem"), 
					new FileInputStream(baseFileName+".cert.pem"),
					"pass".toCharArray());
		}
	}
	
	
	static void initVOs() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
		
		PEMCredential aaCred1 = new PEMCredential(new FileInputStream(aaKey),
				new FileInputStream(aaCert), (char[])null);
		
		PEMCredential aaCred2 = new PEMCredential(new FileInputStream(aaKey2),
				new FileInputStream(aaCert2), (char[])null);
		
		testVO_1 = new VOMSAA(aaCred1, "test.vo", "test-host.cnaf.infn.it", 15000);
		testVO_2 = new VOMSAA(aaCred2, "test.vo.2", "wilco.cnaf.infn.it", 15001);
		
	}
	
	@BeforeClass
	public static void setup() throws KeyStoreException, CertificateException, FileNotFoundException, IOException{
	
		sharedVOMSTrustStore = new DefaultUpdatingVOMSTrustStore(Arrays.asList(vomsTrustStoreDir), 
				trustStoreRefreshInterval);
		
		sharedCertificateValidator = new OpensslCertChainValidator(trustAnchorsDir, 
				NamespaceCheckingMode.EUGRIDPMA_AND_GLOBUS, 
				trustAnchorsRefreshInterval);
	
		loadHolderCredentials();
		initVOs();	
		sharedValidator = new DefaultVOMSValidator(sharedVOMSTrustStore, sharedCertificateValidator);
	}
	
	@AfterClass
	public static void tearDown(){
		
		
	}
	
	@Test
	public void test() throws InterruptedException, BrokenBarrierException {
		
		for (int i=0; i < NUM_WORKERS; i++)
			pool.execute(new ValidatorWorker());
		
		barrier.await();
		barrier.await();
		
		pool.shutdown();
		sharedVOMSTrustStore.cancel();
		sharedCertificateValidator.dispose();
		
		System.out.println("Done.");
	}

	
	class ValidatorWorker implements Runnable{
			
		private volatile boolean shutdownRequested = false;
		
		private long iterations = 0;
		
		public void run() {
			try {
				
				barrier.await();
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (BrokenBarrierException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			while (true){
				
				if (iterations++ > NUM_ITERATIONS)
					break;
				
				if (shutdownRequested)
					return;
				
				VOMSACValidator validator = getValidator();
				
				Random r = new Random();
				
				int credentialIndex = r.nextInt(numHolderCredentials);
				int voIndex = r.nextInt(2);
				
				try {
					
					X509Certificate[] chain = buildProxy(credentialIndex, voIndex);
					List<VOMSAttribute> attrs = validator.validate(chain);
					Assert.assertEquals(1, attrs.size());
					
				} catch (Exception e) {
					System.err.println(e.getMessage());
					
				}
			}
			
			try {
				barrier.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public synchronized void shutdown(){
			shutdownRequested = true;
		}
	}

	static X509Certificate[] buildProxy(int credentialIndex, int voIndex) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		
		VOMSAA vo = (voIndex == 0 ? testVO_1 : testVO_2);
		PEMCredential cert = holderCerts[credentialIndex];
		
		ProxyCertificate proxy = vo.createVOMSProxy(cert, Arrays.asList(fqans[voIndex]));
		return proxy.getCertificateChain();
	}
	
	static VOMSACValidator getValidator(){
		// return new DefaultVOMSValidator(sharedVOMSTrustStore, sharedCertificateValidator);
		return sharedValidator;
	}
}

