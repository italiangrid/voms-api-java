package org.glite.voms.v2.store;

import java.util.List;

/**
 * 
 * @author andreaceccanti
 *
 */
public interface LSCInfo {
	
	
	/**
	 * Sets the name of the file from where this LSC info was parsed from.
	 * 
	 * @param filename the name of the file from where this LSC info was parsed from.
	 */
	public void setFilename(String filename);
	
	
	/** 
	 * Returns the name of file from where this LSC info was parsed from.
	 * 
	 * @return the name of the file from where this LSC info was parsed from.
	 */
	public String getFilename();
	
	/**
	 * Returns the VO name this LSC info applies to.
	 * 
	 * @return the VO name this LSC info applies to
	 */
	public String getVOName();
	
	/**
	 * Returns the host name of the VOMS AA this LSC info applies to.
	 * 
	 * @return the host name of the VOMS AA this LSC info applies to
	 */
	public String getHostname();
	
	/**
	 * Returns the certificate chain description of the VOMS AA for the
	 * given VO and hostname.
	 * 
	 * The certificate chain description is a list of X.500 distinguished names encoded as strings 
	 * according to the OpenSSL slash-separated format, as in:
	 * <verbatim>
	 * /C=IT/O=INFN/CN=INFN CA
	 * </verbatim>
	 * 
	 * The first element in the description is the leaf certificate, while the last is the CA certificate.
	 * @return
	 */
	public List<String> getCertificateChainDescription();
	
}
