package org.italiangrid.voms.store;

/**
 * A VOMS trust store that can be periodically refreshed.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface UpdatingVOMSTrustStore extends VOMSTrustStore, Updateable {
	
	public void setTrustStoreUpdateListener(VOMSTrustStoreUpdateListener updateListener);

}
