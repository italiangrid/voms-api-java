package org.italiangrid.voms.store.impl;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A simple thread factory to create named VOMS background threads.
 * 
 * @author andreaceccanti
 *
 */
public class VOMSNamedThreadFactory implements ThreadFactory {

	private static final AtomicInteger created = new AtomicInteger();
	private static final String poolBaseName = "voms-thread";

	public Thread newThread(Runnable r) {
		return new VOMSThread(r, poolBaseName+"-"+created.incrementAndGet());
	}
}
