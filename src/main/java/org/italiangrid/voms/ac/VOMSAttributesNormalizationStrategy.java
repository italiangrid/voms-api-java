package org.italiangrid.voms.ac;

import java.util.List;

import org.italiangrid.voms.VOMSAttribute;

/**
 * A strategy to select the set of relevant and appliable VOMS attributes
 * from a set of parsed VOMS attribute certificates.
 * 
 * This strategy is responsible of creating the {@link VOMSAttribute} objects
 * which represents the authorizative VOMS authorization information.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSAttributesNormalizationStrategy {

	/**
	 * Returns the normalized view of VOMS Authorization information starting from
	 * a list of VOMS Attribute certificates.
	 * 
	 * @param acs
	 * @return a possibly empty list {@link VOMSAttribute} object
	 */
	public List<VOMSAttribute> normalizeAttributes(List<ACParsingContext> acs);
}
