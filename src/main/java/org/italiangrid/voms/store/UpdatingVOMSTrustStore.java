package org.italiangrid.voms.store;

/**
 * A VOMS trust store that can be periodically refreshed.
 * 
 * @author andreaceccanti
 *
 */
public interface UpdatingVOMSTrustStore extends VOMSTrustStore, Updateable {

}
