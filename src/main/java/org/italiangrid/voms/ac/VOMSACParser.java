package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface defines the behavior of a VOMS Attribute Certificate parser.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACParser {

	/**
	 * Looks for and parses VOMS attributes in the certificate chain passed as argument (which is assumed to be already validated).
	 * 
	 * @param validatedChain a validated X.509 certificate chain  
	 * @return a possibly empty list of {@link VOMSAttribute} objects providing access to the parsed VOMS attributes
	 */
	public List<VOMSAttribute> parse(X509Certificate[] validatedChain);
	
}
