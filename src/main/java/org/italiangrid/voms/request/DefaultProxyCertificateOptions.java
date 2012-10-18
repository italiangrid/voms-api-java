package org.italiangrid.voms.request;

import java.util.concurrent.TimeUnit;

import eu.emi.security.authn.x509.proxy.ProxyType;

/**
 * The default options used when creating VOMS proxy certificates.
 * 
 * @author andreaceccanti
 *
 */
public interface DefaultProxyCertificateOptions {

	/**
	 * The default proxy type.
	 */
	public static final ProxyType defaultProxyType  = ProxyType.LEGACY;
	
	/**
	 * Whether proxies should be limited by default.
	 */
	public static final boolean  defaultProxyLimitedPoilcy = false;
	
	/**
	 * The default lifetime (in seconds) for the generated proxies.
	 */
	public static final int defaultLifetimeInSeconds = (int)TimeUnit.HOURS.toSeconds(12);
	
}
