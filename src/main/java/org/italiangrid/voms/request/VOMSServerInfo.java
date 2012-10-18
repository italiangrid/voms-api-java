package org.italiangrid.voms.request;

import java.net.URL;

/**
 * This interface represents a VOMS server contact information,
 * typically provided in vomses files.
 * 
 * @see VOMSESLookupStrategy VOMSESParser
 * @author cecco
 *
 */
public interface VOMSServerInfo {

	public String getAlias();

	public String getVoName();
	
	public URL getURL();
	
	public String getVOMSServerDN();

}