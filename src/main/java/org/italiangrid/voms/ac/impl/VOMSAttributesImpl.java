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
package org.italiangrid.voms.ac.impl;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.VOMSGenericAttribute;
import org.italiangrid.voms.util.TimeUtils;

import eu.emi.security.authn.x509.impl.X500NameUtils;

/**
 * The default implementation for voms attributes
 * 
 * @author andreaceccanti
 *
 */
public class VOMSAttributesImpl implements VOMSAttribute {
	public static final int DEFAULT_CLOCK_SKEW_IN_MINUTES = 5;
	
	private String VO;
	private String host;
	private int port;
	private List<String> FQANs;
	private X500Principal issuer;
	private X500Principal holder;
	private BigInteger holderSerialNumber;
	private Date notAfter;
	private Date notBefore;
	private byte[] signature;
	private List<VOMSGenericAttribute> genericAttributes;
	private List<String> acTargets;
	private X509Certificate[] aaCerts;
	private X509AttributeCertificateHolder VOMSAC;
	
	public VOMSAttributesImpl() {
	
	}

	public X500Principal getIssuer() {
		return issuer;
	}

	public String getPrimaryFQAN() {
		return FQANs.get(0);
	}

	public String getVO() {
		return VO;
	}

	
	public void setIssuer(X500Principal issuer) {
		this.issuer = issuer;
	}

	public void setVO(String vO) {
		VO = vO;
	}
	

	public List<String> getFQANs() {
		return FQANs;
	}


	public void setFQANs(List<String> fQANs) {
		FQANs = fQANs;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		
		return port;
	}

	public X500Principal getHolder() {
		
		return holder;
	}

	public Date getNotBefore() {
		return notBefore;
	}

	public Date getNotAfter() {
		
		return notAfter;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setHolder(X500Principal holder) {
		this.holder = holder;
	}

	public void setNotAfter(Date notAfter) {
		this.notAfter = notAfter;
	}

	public void setNotBefore(Date notBefore) {
		this.notBefore = notBefore;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "VOMSAttributesImpl [VO=" + VO + ", host=" + host + ", port="
				+ port + ", FQANs=" + FQANs + ", gas=" + genericAttributes+ ", issuer='" + X500NameUtils.getReadableForm(issuer)
				+ "', holder='" + X500NameUtils.getReadableForm(holder) + "', notAfter=" + notAfter
				+ ", notBefore=" + notBefore 
				+ ", targets=" + acTargets + " ]";
	}

	public List<VOMSGenericAttribute> getGenericAttributes() {
		return genericAttributes;
	}

	public void setGenericAttributes(List<VOMSGenericAttribute> genericAttributes) {
		this.genericAttributes = genericAttributes;
	}

	public List<String> getTargets() {
		return acTargets;
	}

	public void setTargets(List<String> targets){
		acTargets = targets;
	}
	
	public X509Certificate[] getAACertificates() {
		return aaCerts;
	}

	public void setAACertificates(X509Certificate[] aaCerts) {
		this.aaCerts = aaCerts;
	}

	public boolean isValid() {
		return validAt(new Date());
	}

	public boolean validAt(Date date) {
		return TimeUtils.checkTimeInRangeWithSkew(date, 
				getNotBefore(), 
				getNotAfter(), 
				DEFAULT_CLOCK_SKEW_IN_MINUTES);
	}

	public X509AttributeCertificateHolder getVOMSAC() {
		
		return VOMSAC;
	}

	public void setVOMSAC(X509AttributeCertificateHolder ac) {
		VOMSAC = ac;
	}

	public BigInteger getHolderSerialNumber() {
		return holderSerialNumber;
	}

	public void setHolderSerialNumber(BigInteger holderSerialNumber) {
		this.holderSerialNumber = holderSerialNumber;
	}
}
