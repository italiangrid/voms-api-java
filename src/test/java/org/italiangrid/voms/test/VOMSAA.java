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
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateParsingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.asn1.VOMSACGenerator;

import eu.emi.security.authn.x509.impl.PEMCredential;
import eu.emi.security.authn.x509.proxy.ProxyCertificate;
import eu.emi.security.authn.x509.proxy.ProxyCertificateOptions;
import eu.emi.security.authn.x509.proxy.ProxyGenerator;

public class VOMSAA {

	PEMCredential credential;
	String voName;
	String host;
	int port;
	
	Date acNotBefore;
	Date acNotAfter;
	
	private volatile long serial = 0L;
	
	public VOMSAA(PEMCredential cred, String vo, String host, int port) {
		
		credential = cred;
		voName = vo;
		this.host = host;
		this.port = port;
		
	}
	
	private synchronized BigInteger getAndIncrementSerial(){
		return BigInteger.valueOf(serial++);
	}
	
	public ProxyCertificate createVOMSProxy(PEMCredential holder,
			List<String> fqans) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		return createVOMSProxy(holder, holder, fqans, null, null);
	}
	
	
	public ProxyCertificate createVOMSProxy(PEMCredential holder,PEMCredential proxyHolder, 
			List<String> fqans, 
			List<VOMSGenericAttribute> attrs,
			List<String> targets) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		
		VOMSACGenerator generator = new VOMSACGenerator(credential, voName, host, port);
		Calendar cal = Calendar.getInstance();
		
		Date startDate = acNotBefore;
		Date endDate = acNotAfter;
		
		if (startDate == null)
			startDate = cal.getTime();
		
		if (endDate == null){
			cal.add(Calendar.HOUR, 12);
			endDate = cal.getTime();
		}
		
		X509AttributeCertificateHolder acHolder = generator.generateVOMSAttributeCertificate(fqans, 
				attrs, 
				targets, 
				holder.getCertificate(), 
				getAndIncrementSerial(), 
				startDate,
				endDate);
	
		return createVOMSProxy(proxyHolder, new AttributeCertificate[]{acHolder.toASN1Structure()});
	}
	
	private ProxyCertificate createVOMSProxy(PEMCredential holder, AttributeCertificate[] acs) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(holder.getCertificateChain());
		
		proxyOptions.setAttributeCertificates(acs);
		ProxyCertificate proxy =  ProxyGenerator.generate(proxyOptions, holder.getKey());
		
		return proxy;
	}

	public VOMSAA setCredential(PEMCredential credential) {
		this.credential = credential;
		return this;
	}

	public VOMSAA setVoName(String voName) {
		this.voName = voName;
		return this;
	}

	public VOMSAA setHost(String host) {
		this.host = host;
		return this;
	}

	public VOMSAA setPort(int port) {
		this.port = port;
		return this;
	}

	public VOMSAA setAcNotBefore(Date acNotBefore) {
		this.acNotBefore = acNotBefore;
		return this;
	}

	public VOMSAA setAcNotAfter(Date acNotAfter) {
		this.acNotAfter = acNotAfter;
		return this;
	}
	
	
}
