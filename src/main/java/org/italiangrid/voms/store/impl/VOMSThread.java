package org.italiangrid.voms.store.impl;


/**
 * An helper class to create a named VOMS thread.
 * This class just sets the name for the thread and set an {@link UncaughtExceptionHandler}
 * which logs the caught exception.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSThread extends Thread {
	
	/**
	 * Default constructor.
	 * 
	 * @param target the object whose <code>run</code> method is called.
	 * @param name the name of the new thread.
	 */
	public VOMSThread(Runnable target, String name, UncaughtExceptionHandler handler) {
		super(target, name);
		setUncaughtExceptionHandler(handler);
	}	
}
