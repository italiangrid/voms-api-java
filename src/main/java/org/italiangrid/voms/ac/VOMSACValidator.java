package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;

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
	 * @return a possibly empty list of {@link VOMSAttribute} objects providing access to the parsed VOMS attributes
	 */
	public List<VOMSAttribute> validate();
	
	/**
	 * Parses and validates the VOMS attributes found in the certificate chain passed 
	 * as argument (which is assumed to be validated already).
	 * 
	 * @param validatedChain a validated X.509 certificate chain
	 * @return a possibly empty list of {@link VOMSAttribute} object providing access to the validated VOMS attributes
	 */
	public List<VOMSAttribute> validate(X509Certificate[] validatedChain);
	
	/**
	 * Validates the VOMS attributes found in the attribute certificate list passed as argument.
	 * @param acs a list of {@link AttributeCertificate}
	 * @return the validated and possibly empty list of {@link AttributeCertificate} object
	 */
	public List<AttributeCertificate> validateACs(List<AttributeCertificate> acs);
	
	/**
	 * Shutdown the VOMS validator. This method should be called to perform final cleanup and stop 
	 */
	public void shutdown();
}
