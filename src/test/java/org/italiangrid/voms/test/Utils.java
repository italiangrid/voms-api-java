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
package org.italiangrid.voms.test;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.italiangrid.voms.VOMSValidators;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.store.impl.DefaultVOMSTrustStore;
import org.italiangrid.voms.util.CertificateValidatorBuilder;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class Utils implements Fixture{

	private Utils() {}
	
	public static X509CertChainValidatorExt getCertificateValidator(){
		return CertificateValidatorBuilder.buildCertificateValidator(trustAnchorsDir);
	}
	
	public static VOMSACValidator getVOMSValidator(){
		X509CertChainValidatorExt validator = CertificateValidatorBuilder.buildCertificateValidator(trustAnchorsDir);
		return VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsdir)), validator);
		
	}
	
	public static VOMSACValidator getVOMSValidator(String vomsDir){
		X509CertChainValidatorExt validator = CertificateValidatorBuilder.buildCertificateValidator(trustAnchorsDir);
		return VOMSValidators.newValidator(new DefaultVOMSTrustStore(Arrays.asList(vomsDir)), validator);
		
	}

	public static PEMCredential getAACredential() throws KeyStoreException, CertificateException, IOException{
		return new PEMCredential(aaKey, aaCert, keyPassword.toCharArray());	
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
