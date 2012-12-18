package org.italiangrid.voms.ac.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * The default implementation for localhost name resolver.
 * The localhost name is resolved using the following code:
 * <pre>
 * {@code
 * 		InetAddress.getLocalHost().getCanonicalHostName();
 * }
 * </pre>
 */
public class DefaultLocalHostnameResolver implements LocalHostnameResolver {


	public String resolveLocalHostname() throws UnknownHostException{
		
		return InetAddress.getLocalHost().getCanonicalHostName();
	}

}
