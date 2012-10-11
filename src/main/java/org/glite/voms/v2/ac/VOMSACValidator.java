package org.glite.voms.v2.ac;

import java.security.cert.X509Certificate;

import org.glite.voms.v2.VOMSAttributes;

/**
 * This interface extends the {@link VOMSACParser} interface and provides methods
 * to perform validation on the VOMS Attribute Certificates parsed from a given
 * certificate chain.  
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSACValidator extends VOMSACParser {

	/**
	 * Parses and validates VOMS attributes in the certificate chain set by a former call to the {@link #setCertificateChain(X509Certificate[])}
	 * method.
	 * 
	 * @return a {@link VOMSAttributes} object providing access to the parsed VOMS attributes
	 */
	public VOMSAttributes validate();
	
	/**
	 * Parses and validates the VOMS attributes found in the certificate chain passed 
	 * as argument (which is assumed to be validated already).
	 * 
	 * @param validatedChain a validated X.509 certificate chain
	 * @return a {@link VOMSAttributes} object providing access to the validated VOMS attributes
	 */
	public VOMSAttributes validate(X509Certificate[] validatedChain);
}
