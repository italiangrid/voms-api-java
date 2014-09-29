/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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
package org.italiangrid.voms.test.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidationStrategy;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.ac.impl.LocalHostnameResolver;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSServerInfoStore;
import org.italiangrid.voms.request.impl.DefaultVOMSACService;
import org.italiangrid.voms.request.impl.DefaultVOMSServerInfo;
import org.italiangrid.voms.store.VOMSTrustStore;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.util.CertificateValidatorBuilder;
import org.mockito.Mockito;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class Utils implements Fixture{

	private Utils() {}
	
	
	
	public static VOMSACService buildACService(VOMSProtocol main, 
			VOMSProtocol fallback) throws Exception{
		
		
		VOMSServerInfoStore store = Mockito.mock(VOMSServerInfoStore.class);
		
		Set<VOMSServerInfo> testVOEndpoints = new HashSet<VOMSServerInfo>();
		testVOEndpoints.add(getTestVOEndpoint());
		
		Mockito.when(store.getVOMSServerInfo("test.vo")).thenReturn(testVOEndpoints);
		
		DefaultVOMSACService acService = new DefaultVOMSACService.Builder(getCertificateValidator())
			.serverInfoStore(store)
			.requestListener(LogListener.INSTANCE)
			.httpProtocol(main)
			.legacyProtocol(fallback)
			.build();
		
		return acService;
		
	}

	public static VOMSACService buildACService(VOMSProtocol protocol) throws Exception{
		
		return buildACService(protocol, null);
	}
	
	
	public static VOMSServerInfo getTestVOEndpoint() throws URISyntaxException{
		DefaultVOMSServerInfo si = new DefaultVOMSServerInfo();
		si.setAlias("test.vo");
		si.setVoName("test.vo");
		si.setURL(new URI("http://localhost:15000"));
		si.setVOMSServerDN("Not checked");
		return si;
	}
	public static X509CertChainValidatorExt getCertificateValidator(){
	  return new CertificateValidatorBuilder()
	    .trustAnchorsDir(trustAnchorsDir).build();
	}
	
	public static VOMSACValidator getVOMSValidator(LocalHostnameResolver resolver){
		X509CertChainValidatorExt validator = new CertificateValidatorBuilder()
      .trustAnchorsDir(trustAnchorsDir).build();
		VOMSTrustStore ts  = new DefaultVOMSTrustStore(Arrays.asList(vomsdir)); 
		return new DefaultVOMSValidator.Builder()
			.validationStrategy(new DefaultVOMSValidationStrategy(ts, validator, resolver))
			.build();
	}
	public static VOMSACValidator getVOMSValidator(){
		X509CertChainValidatorExt validator = new CertificateValidatorBuilder()
      .trustAnchorsDir(trustAnchorsDir).build();
		return VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsdir)), validator);
		
	}
	
	public static VOMSACValidator getVOMSValidator(String vomsDir){
		X509CertChainValidatorExt validator = new CertificateValidatorBuilder()
      .trustAnchorsDir(trustAnchorsDir).build();
		return VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsDir)), validator);
		
	}

	public static PEMCredential getAACredential() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(aaKey, aaCert, keyPassword.toCharArray());	
	}
	
	public static PEMCredential getAACredential2() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(aaKey2, aaCert2, keyPassword.toCharArray());	
	}
	
	public static PEMCredential getTestUserCredential() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(holderKey, holderCert, keyPassword.toCharArray());
	}
	
	public static PEMCredential getTest1UserCredential() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(holderKey2, holderCert2, keyPassword.toCharArray());
	}
	
	public static PEMCredential getExpiredCredential() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(expiredKey, expiredCert, keyPassword.toCharArray());
	}
	
	public static VOMSAA getVOMSAA() throws KeyStoreException, CertificateException, IOException{
		return new VOMSAA(getAACredential(), defaultVO, defaultVOHost, defaultVOPort);
	}
	
	public static Date getDate(int year, int month, int day, int hour, int minute, int second){
		Calendar cal = Calendar.getInstance();
		cal.set(year,month,day, hour, minute, second);
		return cal.getTime();
	}
	
	public static Date getDate(int year, int month, int day){
		Calendar cal = Calendar.getInstance();
		cal.set(year,month,day);
		return cal.getTime();
	}
	
	
}
