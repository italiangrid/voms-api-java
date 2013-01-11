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
package org.italiangrid.voms.store.impl;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.store.LSCInfo;

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
 *  
 * @author Andrea Ceccanti
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

	@SuppressWarnings("deprecation")
	public boolean matches(X509Certificate[] certChain) {
		
		if (certChainDescription == null || certChainDescription.isEmpty())
			return false;
		
		if (certChain == null || certChain.length == 0)
			return false;
		
		if (certChainDescription.size() ==  certChain.length * 2 ){
			
			for (int i=0; i < certChain.length; i++){
				
				String lscSubjectRFC2253 =	OpensslNameUtils.opensslToRfc2253(certChainDescription.get(i));
				String lscIssuerRFC2253 = OpensslNameUtils.opensslToRfc2253(certChainDescription.get(i+1));
				
				boolean subjectDoesMatch = X500NameUtils.equal(certChain[i].getSubjectX500Principal(), lscSubjectRFC2253);
				boolean issuerDoesMatch = X500NameUtils.equal(certChain[i].getIssuerX500Principal(), lscIssuerRFC2253);
							
				if (!subjectDoesMatch || !issuerDoesMatch)
					return false;
				
			}
		}
		
		return true;
	}
	
}
