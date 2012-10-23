package org.italiangrid.voms.request;

import java.util.Set;

/**
 * A store containing the contact information for locally trusted VOMS servers.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSServerInfoStore {

	/**
	 * Returns a set of {@link VOMSServerInfo} object matching
	 * a vo name passed as argument.
	 * 
	 * @param voName a VO name
	 * @return a possibly empty set of {@link VOMSServerInfo} object matching
	 * the vo name passed as argument
	 */
	public Set<VOMSServerInfo> getVOMSServerInfo(String voName);
	
	
	/**
	 * Returns a set of all {@link VOMSServerInfo} objects in this
	 * {@link VOMSServerInfoStore}.
	 * 
	 * @return a possibly empty set of all {@link VOMSServerInfo} objects in this
	 * {@link VOMSServerInfoStore}.
	 */
	public Set<VOMSServerInfo> getVOMSServerInfo();
	
	/**
	 * Adds a {@link VOMSServerInfo} to this {@link VOMSServerInfoStore}.
	 * 
	 * @param info the {@link VOMSServerInfo} object to add.
	 */
	public void addVOMSServerInfo(VOMSServerInfo info);


}