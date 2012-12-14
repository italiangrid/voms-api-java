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
	VOMSACGenerator generator;
	
	private volatile long serial = 0L;
	
	public VOMSAA(PEMCredential cred, String vo, String host, int port) {
		
		credential = cred;
		voName = vo;
		this.host = host;
		this.port = port;
		
		generator = new VOMSACGenerator(credential, vo, host, port);
		
	}
	
	private synchronized BigInteger getAndIncrementSerial(){
		return BigInteger.valueOf(serial++);
	}
	
	public ProxyCertificate createVOMSProxy(PEMCredential holder, List<String> fqans) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		
		Calendar cal = Calendar.getInstance();
		
		Date now = cal.getTime();
		cal.add(Calendar.HOUR, 12);
		Date expiration = cal.getTime();
		
		X509AttributeCertificateHolder acHolder = generator.generateVOMSAttributeCertificate(fqans, 
				null, 
				null, 
				holder.getCertificate(), 
				getAndIncrementSerial(), 
				now,
				expiration);
	
		return createVOMSProxy(holder, new AttributeCertificate[]{acHolder.toASN1Structure()});
	}
	
	private ProxyCertificate createVOMSProxy(PEMCredential holder, AttributeCertificate[] acs) throws InvalidKeyException, CertificateParsingException, SignatureException, NoSuchAlgorithmException, IOException{
		ProxyCertificateOptions proxyOptions = new ProxyCertificateOptions(holder.getCertificateChain());
		
		proxyOptions.setAttributeCertificates(acs);
		ProxyCertificate proxy =  ProxyGenerator.generate(proxyOptions, holder.getKey());
		
		return proxy;
	}
}
