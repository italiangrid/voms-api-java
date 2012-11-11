package org.italiangrid.voms.store;

import java.io.File;
import java.security.cert.X509Certificate;

/**
 * 
 * This interface used to notify interested listeners in status changes of a VOMS trust store.
 * @author Andrea Ceccanti
 *
 */
public interface VOMSTrustStoreStatusListener {

	/**
	 * Informs that certificates are being looked for in the directory passed as argument 
	 * @param dir the directory where certificates are being looked for
	 */
	public void notifyCertficateLookupEvent(String dir);
	
	/**
	 * Informs that VOMS LSC file information is being looked for in the directory passed
	 * as argument.
	 * @param dir the directory where certificates are being looked for
	 */
	public void notifyLSCLookupEvent(String dir);
	
	/**
	 * Informs that a VOMS AA certificate has been loaded in the store
	 * @param cert the VOMS AA certificate loaded
	 * @param f the file from which the certificate has been loaded
	 */
	public void notifyCertificateLoadEvent(X509Certificate cert, File f);
	
	/**
	 * Informs that VOMS LSC information has been loaded in the store 
	 * @param lsc the loaded VOMS LSC information 
	 * @param f the file from which the LSC information has been loaded
	 */
	public void notifyLSCLoadEvent(LSCInfo lsc, File f);
	
}
