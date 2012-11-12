package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;

/**
 * This interface is used to notify of events related to the lookup and succesfull
 * parsing of VOMS attribute certificates from a certificate chain.  
 * @author andreaceccanti
 *
 */
public interface ACLookupListener {
	
	/**
	 * Informs that an AC is being looked for in the cert chain passed as argument.
	 * 
	 * @param chain the chain where the AC is looked for
	 * @param chainLevel the level in the chain where the AC is being looked for
	 */
	public void notifyACLookupEvent(X509Certificate[] chain, int chainLevel);
	
	/**
	 * Informs that an AC has been succesfully parsed from the cert chain passed
	 * as argument
	 * @param chain the chain from which the AC has been parsed 
	 * @param chainLevel the level in the chain where the AC has been parsed 
	 */
	public void notifyACParseEvent(X509Certificate[] chain, int chainLevel);

}
