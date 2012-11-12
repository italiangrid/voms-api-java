package org.italiangrid.voms.store.impl;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple thread factory to create named VOMS background threads.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSNamedThreadFactory implements ThreadFactory {

	private static final AtomicInteger created = new AtomicInteger();
	private static final String poolBaseName = "voms-thread";

	private UncaughtExceptionHandler handler;
	public VOMSNamedThreadFactory(UncaughtExceptionHandler h) {
		this.handler = h;
	}
	
	public VOMSNamedThreadFactory(){
		
	}
	public Thread newThread(Runnable r) {
		return new VOMSThread(r, poolBaseName+"-"+created.incrementAndGet(), handler);
	}
}
