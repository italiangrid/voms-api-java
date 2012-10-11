package org.glite.voms.v2.ac;

import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.glite.voms.v2.VOMSAttributes;

/**
 * A strategy to select the set of relevant and appliable VOMS attributes
 * from a set of VOMS attribute certificates.
 * 
 * This strategy is responsible of creating the {@link VOMSAttributes} object
 * which represents the authorizative VOMS authorization information.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSAttributesNormalizationStrategy {

	/**
	 * Returns the normalized view of VOMS Authorization information starting from
	 * a list of VOMS Attribute certificates.
	 * 
	 * @param acs
	 * @return a {@link VOMSAttributes} object, or null if no VOMS attributes certificate were
	 * found in the list passed as argument
	 */
	public VOMSAttributes normalizeAttributes(List<AttributeCertificate> acs);
}
