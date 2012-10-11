package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;

import org.glite.voms.v2.VOMSAttributes;

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
	 * @return a {@link VOMSAttributes} object providing access to the parsed VOMS attributes
	 */
	public VOMSAttributes parse(X509Certificate[] validatedChain);
	
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
	 * @return a {@link VOMSAttributes} object providing access to the parsed VOMS attributes
	 */
	public VOMSAttributes parse();
	
}
