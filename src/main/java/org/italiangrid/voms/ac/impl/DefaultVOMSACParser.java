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

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSAttributesNormalizationStrategy;

/**
 * Default implementation of the VOMS attribute certificate parsing logic.
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultVOMSACParser implements VOMSACParser {
	
	private final VOMSACLookupStrategy acLookupStrategy = new LeafACLookupStrategy();
	private final VOMSAttributesNormalizationStrategy acNormalizationStrategy = new LeafVOMSExtensionNormalizationStrategy();
	private X509Certificate[] certChain;
	
	public synchronized List<VOMSAttribute> parse(X509Certificate[] validatedChain) {
		this.certChain = validatedChain;
		return parse();
	}
	
	public synchronized List<VOMSAttribute> parse() {
		
		if (certChain == null)
			throw new IllegalArgumentException("Cannot parse a null certchain!");
		
		List<ACParsingContext> parsedACs = acLookupStrategy.lookupVOMSAttributeCertificates(certChain);
		return acNormalizationStrategy.normalizeAttributes(parsedACs);
	}

	/**
	 * @return the certChain
	 */
	protected synchronized X509Certificate[] getCertChain() {
		return certChain;
	}

	/**
	 * @param certChain the certChain to set
	 */
	protected synchronized void setCertChain(X509Certificate[] certChain) {
		this.certChain = certChain;
	}

	/**
	 * @return the acLookupStrategy
	 */
	protected synchronized VOMSACLookupStrategy getAcLookupStrategy() {
		return acLookupStrategy;
	}

	/**
	 * @return the acNormalizationStrategy
	 */
	protected synchronized VOMSAttributesNormalizationStrategy getAcNormalizationStrategy() {
		return acNormalizationStrategy;
	}
}
