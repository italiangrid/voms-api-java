package org.italiangrid.voms.store.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An helper class to create a named VOMS thread.
 * This class just sets the name for the thread and set an {@link UncaughtExceptionHandler}
 * which logs the caught exception.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSThread extends Thread {

	public static final Logger log = LoggerFactory.getLogger(VOMSThread.class);
	
	/**
	 * Default constructor.
	 * 
	 * @param target the object whose <code>run</code> method is called.
	 * @param name the name of the new thread.
	 */
	public VOMSThread(Runnable target, String name) {
		super(target, name);
		setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {	
			public void uncaughtException(Thread t, Throwable e) {
				log.error("Uncaught exception in thread "+t.getName(),e);
				
			}
		});
	}	
}
