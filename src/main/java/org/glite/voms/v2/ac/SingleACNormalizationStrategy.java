package org.glite.voms.v2.ac;

import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.glite.voms.v2.VOMSAttributes;
import org.glite.voms.v2.asn1.VOMSACUtils;

public class SingleACNormalizationStrategy implements
		VOMSAttributesNormalizationStrategy, VOMSConstants {

	public VOMSAttributes normalizeAttributes(List<AttributeCertificate> acs) {
		
		if (acs == null || acs.isEmpty())
			return null;
		
		return VOMSACUtils.deserializeVOMSAttributes(acs.get(0));
		
	}

}
