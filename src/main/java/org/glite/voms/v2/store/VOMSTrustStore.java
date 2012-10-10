package org.glite.voms.v2.store;

import java.security.cert.X509Certificate;
import java.util.List;

/**
 * The VOMS trust store takes care of parsing local trusted information (being certificates or LSC files)
 * for known VOMS servers.
 *  
 * @author andreaceccanti
 *
 */
public interface VOMSTrustStore {
	
	/**
	 * Returns the locally trusted directories where VOMS trust information (being VOMS server certificates or
	 * LSC files) are searched for.
	 * 
	 * @return a {@link List} of local paths
	 */
	public List<String> getLocalTrustedDirectories();
	
	/**
	 * Returns the list of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore}.
	 * @return the collection of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore}, an empty list
	 * if no certificate was found.
	 */
	public List<X509Certificate> getLocalAACertificates();
	
	/**
	 * Return the list of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore} for
	 * the vo passed as argument.
	 * @param voName the name of the VO 
	 * @return the collection of VOMS Attribute Authority certificates held in this {@link VOMSTrustStore} for
	 * the vo passed as argument, an empty list if no certificate was found.
	 */
	public List<X509Certificate> getAACertificateForVO(String voName);
	
	/**
	 * Returns the LSC information held in this {@link VOMSTrustStore} for the vo and hostname passed as arguments.
	 * 
	 * @param voName the name of the VO for which the LSC applies
	 * @param hostname the name of the host for which the LSC applies
	 * @return a {@link LSCInfo} object, or null if no LSC  matching the arguments was found
	 */
	public LSCInfo getLSC(String voName, String hostname);
	
	/**
	 * Loads trust information from the sources specified with the {@link #setLocalTrustedDirectories(List)} information or
	 * using sensible defaults chosen by the implementor
	 */
	public void loadTrustInformation();
	
}
