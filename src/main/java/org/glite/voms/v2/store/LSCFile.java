package org.glite.voms.v2.store;

import java.util.List;



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
	
}
