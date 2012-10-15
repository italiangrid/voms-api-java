package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface defines the behavior of a VOMS Attribute Certificate parser.
 * 
 * 
 * @author andreaceccanti
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
	
	/**
	 * Sets the certificate chain that will be used by the {@link #parse()} method.
	 * 
	 * @param validatedChain a validated X.509 certificate chain
	 */
	public void setCertificateChain(X509Certificate[] validatedChain);
	
	/**
	 * Looks for VOMS attributes in the certificate chain set by a former call to the {@link #setCertificateChain(X509Certificate[])}
	 * method.
	 * 
	 * @return a a possibly empty list of {@link VOMSAttribute} objects providing access to the parsed VOMS attributes
	 */
	public List<VOMSAttribute> parse();
}
