package org.glite.voms.v2.asn1;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.VOMSGenericAttribute;

import eu.emi.security.authn.x509.impl.X500NameUtils;

public class VOMSAttributesImpl implements VOMSAttributes {

	private String VO;
	private String host;
	private int port;
	private List<String> FQANs;
	private X500Principal issuer;
	private X500Principal holder;
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
				+ port + ", FQANs=" + FQANs + ", issuer='" + X500NameUtils.getReadableForm(issuer)
				+ "', holder='" + X500NameUtils.getReadableForm(holder) + "', notAfter=" + notAfter
				+ ", notBefore=" + notBefore + "]";
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
		return (getNotAfter().after(date) && getNotBefore().before(date));
	}

	public X509AttributeCertificateHolder getVOMSAC() {
		
		return VOMSAC;
	}

	public void setVOMSAC(X509AttributeCertificateHolder ac) {
		VOMSAC = ac;
	}
}
