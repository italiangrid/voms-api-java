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
package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;

/**
 * This class describes the context in which a VOMS {@link AttributeCertificate} has been
 * parsed in a certificate chain.
 * 
 * @author Andrea Ceccanti
 *
 */
public class ACParsingContext {

	/** The parsed VOMS attribute certificates **/
	private List<AttributeCertificate> ACs;
	
	/** The position in the cert chain where the VOMS attributes have been parsed **/
	private int certChainPostion;
	
	/** The certificate chain form which the VOMS attributes have been parsed. **/
	private X509Certificate[] certChain;

	/**
	 * @return the aCs
	 */
	public List<AttributeCertificate> getACs() {
		return ACs;
	}

	/**
	 * @param aCs the aCs to set
	 */
	public void setACs(List<AttributeCertificate> aCs) {
		ACs = aCs;
	}

	/**
	 * @return the certChainPostion
	 */
	public int getCertChainPostion() {
		return certChainPostion;
	}

	/**
	 * @param certChainPostion the certChainPostion to set
	 */
	public void setCertChainPostion(int certChainPostion) {
		this.certChainPostion = certChainPostion;
	}

	/**
	 * @return the certChain
	 */
	public X509Certificate[] getCertChain() {
		return certChain;
	}

	/**
	 * @param certChain the certChain to set
	 */
	public void setCertChain(X509Certificate[] certChain) {
		this.certChain = certChain;
	}

	/**
	 * @param aCs a set of parsed VOMS Attribute Certificates
	 * @param certChainPostion the position in the chain where the ACs have been parsed
	 * @param certChain the chain from where the ACs have been parsed
	 */
	public ACParsingContext(List<AttributeCertificate> aCs,
			int certChainPostion, X509Certificate[] certChain) {
		ACs = aCs;
		this.certChainPostion = certChainPostion;
		this.certChain = certChain;
	}

	
}
