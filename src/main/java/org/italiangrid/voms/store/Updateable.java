package org.italiangrid.voms.store;

/**
 * Represents an object which can be periodically updated according to an update frequency. 
 * 
 * @author andreaceccanti
 *
 */
public interface Updateable {
	
	/**
	 * Returns the frequency (in milliseconds) currently set for this {@link Updateable} object.  
	 * @return
	 */
	public long getUpdateFrequency();
	
	/**
	 * Updates the object.
	 */
	public void update();
	
	/**
	 * Cancels the future updates of this {@link Updateable} object.
	 */
	public void cancel();
		
}
