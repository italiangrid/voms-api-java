package org.italiangrid.voms.credential;

/**
 * A {@link ProxyNamingPolicy} defines the naming policy for a VOMS proxy.
 * @author andreaceccanti
 *
 */
public interface ProxyNamingPolicy {

	/**
	 * Builds the file name of a VOMS proxy
	 * 
	 * @param tmpPath the path of the temporary directory of the system
	 * @param userId the effective user id the user for which the proxy is created 
	 * @return a {@link String} representing the proxy file name
	 */
	public String buildProxyFileName(String tmpPath, int userId);
	
}
