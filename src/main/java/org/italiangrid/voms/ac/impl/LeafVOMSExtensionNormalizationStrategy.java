package org.italiangrid.voms.ac.impl;

import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.ac.ACParsingContext;
import org.italiangrid.voms.ac.VOMSAttributesNormalizationStrategy;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.asn1.VOMSConstants;
/**
 * 
 * This strategy extracts the VOMS attributes from the top VOMS extension found 
 * in the parsing context passed as argument.
 * 
 * @author andreaceccanti
 *
 */
public class LeafVOMSExtensionNormalizationStrategy implements
		VOMSAttributesNormalizationStrategy, VOMSConstants {

	public List<VOMSAttribute> normalizeAttributes(List<ACParsingContext> acs) {
		
		if (acs == null || acs.isEmpty())
			return null;
		
		List<AttributeCertificate> attrs = acs.get(0).getACs();
		
		return VOMSACUtils.deserializeVOMSAttributes(attrs);
		
	}

}
