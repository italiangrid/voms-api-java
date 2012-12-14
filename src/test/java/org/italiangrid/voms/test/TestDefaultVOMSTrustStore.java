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
/**
 * 
 */
package org.italiangrid.voms.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.junit.Test;

import eu.emi.security.authn.x509.impl.CertificateUtils;
import eu.emi.security.authn.x509.impl.CertificateUtils.Encoding;
/**
 * @author Andrea Ceccanti
 *
 */
public class TestDefaultVOMSTrustStore {
	
	@Test(expected=VOMSError.class)
	public void testEmptyTrustDirsFailure(){

		@SuppressWarnings({ "unused", "unchecked" })
		DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(Collections.EMPTY_LIST);
				
	}
	
	@Test(expected=VOMSError.class)
	public void testNonExistentTrustDirsFailure(){
		List<String> trustDirs = Arrays.asList(new String[]{"/etc/do/not/exist", "/etc/grid-security/vomsdir" });
		
		@SuppressWarnings("unused")
		DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);
	}
	
	// FIXME: This test assumes /etc/grid-security/vomsdir exists in the machine where the test run. Disabling it
	// for now.
	public void testDefaultTrustDir(){
		
		DefaultVOMSTrustStore store = new DefaultVOMSTrustStore();
		
		List<String> trustDirs = store.getLocalTrustedDirectories();
		
		assertEquals(1, trustDirs.size());
		assertEquals(DefaultVOMSTrustStore.DEFAULT_VOMS_DIR, trustDirs.get(0));
		
	}
	
	@Test(expected=VOMSError.class)
	public void testEmptyTrustDir(){
		
		List<String> trustDirs = Arrays.asList(new String[]{"src/test/resources/empty-voms-dir"});
		
		@SuppressWarnings("unused")
		DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);
		
	}

	@Test
	public void testCertificateParsing() throws FileNotFoundException, IOException{
		
		String vomsDir = "src/test/resources/vomsdir";
		String certFileName = "src/test/resources/vomsdir/test-host.cnaf.infn.it.pem";
		X509Certificate cert = CertificateUtils.loadCertificate(new FileInputStream(certFileName), Encoding.PEM);
		
		List<String> trustDirs = Arrays.asList(new String[]{vomsDir});
		
		DefaultVOMSTrustStore store = new DefaultVOMSTrustStore(trustDirs);
		
		assertEquals(1, store.getLocalAACertificates().size());
			
		assertTrue(cert.getSubjectX500Principal().equals(store.getLocalAACertificates().get(0).getSubjectX500Principal()));
	}
	
	
	public void testUpdatingVOMSTrustStore(){
		
		
	}
}
