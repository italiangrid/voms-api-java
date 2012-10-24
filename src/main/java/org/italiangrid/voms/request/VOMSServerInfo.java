package org.italiangrid.voms.request;

import java.net.URI;

/**
 * This interface represents a VOMS server contact information,
 * typically provided in vomses files.
 * 
 * @see VOMSESLookupStrategy 
 * @see VOMSESParser
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSServerInfo {

	/**
	 * Returns the alias for this {@link VOMSServerInfo}.
	 * @return the alias
	 */
	public String getAlias();

	/**
	 * Returns the VO name for this {@link VOMSServerInfo}.
	 * @return the vo name
	 */
	public String getVoName();
	
	/**
	 * Returns the URL for this {@link VOMSServerInfo}.
	 * @return the contact {@link URI}
	 */
	public URI getURL();
	
	/**
	 * Returns the certificate subject as listed in the VOMSES configuration for this 
	 * {@link VOMSServerInfo}
	 * 
	 * @return a string containing the certificate subject, 
	 * enconded following the DN openssl slash-separated syntax
	 */
	public String getVOMSServerDN();

}