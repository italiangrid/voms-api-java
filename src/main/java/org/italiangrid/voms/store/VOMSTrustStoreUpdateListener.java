package org.italiangrid.voms.store;

/**
 * The interface used to notify interested listeners in updates of the VOMS trust store.
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSTrustStoreUpdateListener {

	/**
	 * Notifies a listener about an update of a {@link VOMSTrustStore}.
	 * @param store the updated {@link VOMSTrustStore}
	 */
	public void notifyTrustStoreUpdate(VOMSTrustStore store);
}
