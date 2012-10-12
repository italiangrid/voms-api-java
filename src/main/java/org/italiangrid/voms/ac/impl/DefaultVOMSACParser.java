package org.italiangrid.voms.ac.impl;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSAttributesNormalizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of the VOMS attribute certificate parsing logic.
 * 
 * @author andreaceccanti
 *
 */
public class DefaultVOMSACParser implements VOMSACParser {

	public static final Logger log = LoggerFactory.getLogger(DefaultVOMSACParser.class);
	
	private final VOMSACLookupStrategy acLookupStrategy = new LeafACLookupStrategy();
	private final VOMSAttributesNormalizationStrategy acNormalizationStrategy = new LeafVOMSExtensionNormalizationStrategy();
	
	protected DefaultVOMSACParser() {
		
	}
	
	private X509Certificate[] certChain;
	
	public List<VOMSAttribute> parse(X509Certificate[] validatedChain) {
		
		setCertificateChain(validatedChain);
		return parse();
	}

	public void setCertificateChain(X509Certificate[] validatedChain) {
		this.certChain = validatedChain;
	}

	protected X509Certificate[] getCertificateChain(){
		return certChain;
	}
	
	public List<VOMSAttribute> parse() {
		
		if (certChain == null)
			throw new IllegalArgumentException("Cannot parse a null certchain!");
		
		List<ACParsingContext> parsedACs = acLookupStrategy.lookupVOMSAttributeCertificates(certChain);
		return acNormalizationStrategy.normalizeAttributes(parsedACs);
	}

}
