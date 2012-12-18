package org.italiangrid.voms.ac.impl;

import java.net.UnknownHostException;

/**
 * A {@link LocalHostnameResolver} resolves the localhost host name.
 *
 */
public interface LocalHostnameResolver {
	
	/**
	 * Resolves the hostname for localhost
	 * @return a String containing the localhost hostname
	 * @throws UnknownHostException when there is an error resolving the hostname
	 */
	public String resolveLocalHostname() throws UnknownHostException;

}
