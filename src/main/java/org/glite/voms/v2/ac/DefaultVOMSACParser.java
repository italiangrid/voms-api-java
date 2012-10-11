package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.glite.voms.v2.VOMSAttributes;
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
	private final VOMSAttributesNormalizationStrategy acNormalizationStrategy = new SingleACNormalizationStrategy();
	
	
	private X509Certificate[] certChain;
	
	public VOMSAttributes parse(X509Certificate[] validatedChain) {
		
		setCertificateChain(validatedChain);
		return parse();
	}

	public void setCertificateChain(X509Certificate[] validatedChain) {
		this.certChain = validatedChain;
	}

	public VOMSAttributes parse() {
		
		if (certChain == null)
			throw new IllegalArgumentException("Cannot parse a null certchain!");
		
		List<AttributeCertificate> vomsACs = acLookupStrategy.lookupVOMSAttributeCertificates(certChain);
		
		return acNormalizationStrategy.normalizeAttributes(vomsACs);
	}
}
