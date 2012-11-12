package org.italiangrid.voms.request;

import java.util.List;

/**
 * This interface is used to notify about events related to the load operations
 * of VOMSES server endpoint information.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSServerInfoStoreListener {
	
	/**
	 * Informs that no valid VOMS information was found on the system.
	 * @param searchedPaths the list of searched paths
	 */
	public void notifyNoValidVOMSESError(List<String> searchedPaths);
	
	/**
	 *  Informs that VOMSES is being search at the path passed as argument
	 * @param vomsesPath the path where VOMSES information are being looked for
	 */
	public void notifyVOMSESlookup(String vomsesPath);
	
	/**
	 * Informs that VOMSES information was succesfully loaded from a given path
	 * @param vomsesPath the path where VOMSES information was loaded from
	 * @param info the {@link VOMSServerInfo} voms endpoint information
	 */
	public void notifyVOMSESInformationLoaded(String vomsesPath, VOMSServerInfo info);

}
