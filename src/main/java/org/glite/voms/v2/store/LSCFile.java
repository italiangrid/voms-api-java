package org.glite.voms.v2.store;

import java.security.cert.X509Certificate;
import java.util.List;

import eu.emi.security.authn.x509.helpers.CertificateHelpers;
import eu.emi.security.authn.x509.impl.OpensslNameUtils;
import eu.emi.security.authn.x509.impl.X500NameUtils;



/**
 *  A VOMS LSC file.
 * 
 *  The LSC file describes the certificate chain that a VOMS attribute authority uses to
 *  sign a VOMS attribute certificate. The LSC mechanism solves the public key distribution 
 *  problem for VOMS AA certificates and is used in the VOMS validation process to validate
 *  the signature on the AC by extracting the VOMS AA certificate included in the VOMS extension
 *  and checking that the chain conforms to the description in the LSC file.
 *  
 *  Two {@link LSCFile} object are considered to be equal if their vo and hostname fields match.
 *  See the {@link #equals()} and {@link #hashCode()} implementation. 
 *  
 *  
 * @author andreaceccanti
 *
 */
public class LSCFile implements LSCInfo{
	
	/** The  LSC filename **/ 
	String filename;

	/** The VO this LSC file is about **/
	String vo;
	
	/** The hostname this LSC file is about **/
	String hostname;
	
	/** The certificate chain description contained in this LSC file **/
	List<String> certChainDescription;
	
	
	public String getVOName() {
		
		return vo;
	}

	public String getHostname() {
		
		return hostname;
	}

	public List<String> getCertificateChainDescription() {
		
		return certChainDescription;
	}

	public String getFilename() {
		return filename;
	}

	public String getVo() {
		return vo;
	}


	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setVo(String vo) {
		this.vo = vo;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setCertificateChainDescription(List<String> certChainDesc){
		this.certChainDescription = certChainDesc;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		result = prime * result + ((vo == null) ? 0 : vo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LSCFile other = (LSCFile) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		if (vo == null) {
			if (other.vo != null)
				return false;
		} else if (!vo.equals(other.vo))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LSCFile [filename=" + filename + ", vo=" + vo + ", hostname="
				+ hostname + ", certChainDescription=" + certChainDescription
				+ "]";
	}

	public boolean matches(X509Certificate[] certChain) {
		
		if (certChainDescription == null || certChainDescription.isEmpty())
			return false;
		
		if (certChain == null || certChain.length == 0)
			return false;
		
		if (certChainDescription.size() ==  certChain.length * 2 ){
			
			for (int i=0; i < certChain.length; i++){
				
				String lscSubjectRFC2253 = CertificateHelpers.opensslToRfc2253(certChainDescription.get(i));
				String lscIssuerRFC2253 = CertificateHelpers.opensslToRfc2253(certChainDescription.get(i+1));
				
				String certChainRFC2253Subject = X500NameUtils.getReadableForm(certChain[i].getSubjectX500Principal());
				String certChainRFC2253Issuer = X500NameUtils.getReadableForm(certChain[i].getIssuerX500Principal());
				
				if 	(!lscSubjectRFC2253.equals(certChainRFC2253Subject) ||	(!lscIssuerRFC2253.equals(certChainRFC2253Issuer)))
					return false;
			}
		}
		
		return true;
	}
	
}
