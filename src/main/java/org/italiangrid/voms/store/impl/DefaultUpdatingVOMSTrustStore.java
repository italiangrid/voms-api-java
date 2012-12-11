/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2012.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.italiangrid.voms.store.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreStatusListener;
import org.italiangrid.voms.util.NullListener;

/**
 * A VOMS trust store that periodically updates itself. The update frequency is set
 * once at VOMS trust store creation time.
 * 
 * 
 * @author Andrea Ceccanti
 *
 */
public class DefaultUpdatingVOMSTrustStore extends DefaultVOMSTrustStore implements
		UpdatingVOMSTrustStore {
	
	/**
	 * Default trust store update frequency. 
	 */
	public static final long DEFAULT_UPDATE_FREQUENCY = TimeUnit.MINUTES.toMillis(10);
	
	/**
	 * This trust store update frequency in milliseconds.
	 */
	private long updateFrequency;
	
	/**
	 * The scheduler used to schedule the update tasks.
	 */
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new VOMSNamedThreadFactory());
	
	/**
	 * Builds a trust store configured as defined in the parameters.
	 * 
	 * @param localTrustDirs where VOMS trust information will be looked for
	 * @param updateFrequency the update frequency in milliseconds
	 * @param listener a listener that is notified of interesting events related to this store
	 */
	public DefaultUpdatingVOMSTrustStore(List<String> localTrustDirs, long updateFrequency, VOMSTrustStoreStatusListener listener) {
		super(localTrustDirs, listener);
		updateFrequencySanityChecks(updateFrequency);
		this.updateFrequency = updateFrequency;
		scheduleUpdate();
	}
	
	/**
	 * Builds a trust store configured as defined in the parameters.
	 * @param updateFrequency the update frequency in milliseconds
	 * 
	 */
	public DefaultUpdatingVOMSTrustStore(long updateFrequency) {
		this(buildDefaultTrustedDirs(), updateFrequency, NullListener.INSTANCE);
	}
	
	/**
	 * Builds a trust store configured as defined in the parameters.
	 * 
	 * @param localTrustDirs where VOMS trust information will be looked for
	 * @param updateFrequency the update frequency in milliseconds
	 * 
	 */
	public DefaultUpdatingVOMSTrustStore(List<String> localTrustDirs, long updateFrequency) {
		this(localTrustDirs, updateFrequency, NullListener.INSTANCE);
	}

	/**
	 * Builds a trust store configured as defined in the parameters.
	 * 
	 * @param localTrustDirs where VOMS trust information will be looked for
	 */
	public DefaultUpdatingVOMSTrustStore(List<String> localTrustDirs){
		this(localTrustDirs, DEFAULT_UPDATE_FREQUENCY, NullListener.INSTANCE);
	}
	
	/**
	 * Builds a trust store. VOMS information will be searched in {@value DefaultVOMSTrustStore#DEFAULT_VOMS_DIR}.
	 * This store will be refreshed every {@value #DEFAULT_UPDATE_FREQUENCY} milliseconds.
	 */
	public DefaultUpdatingVOMSTrustStore(){
		this(buildDefaultTrustedDirs(), DEFAULT_UPDATE_FREQUENCY, NullListener.INSTANCE);
	}
	
	
	protected void updateFrequencySanityChecks(long updateFrequency){
		if (updateFrequency <= 0)
			throw new VOMSError("Please provide a positive value for this store update frequency!");
	}
	
	protected synchronized void scheduleUpdate(){
		
		long frequency = getUpdateFrequency();
		
		scheduler.scheduleWithFixedDelay(new Runnable() {
			// Just run update on the VOMS trust store and log any error
			public void run() {
				update();
			}
		}, 
		frequency, // First execution delay 
		frequency, // Next iterations delay 
		TimeUnit.MILLISECONDS);
		
	}
	
	/**
	 * Returns the update frequency, in milliseconds, for this store.
	 */
	public synchronized long getUpdateFrequency() {
		return updateFrequency;
	}

	/**
	 * Updates the information in this store
	 */
	public synchronized void update() {
		loadTrustInformation();
	}

	/**
	 * Cancel the background tasks which updates this store.
	 */
	public synchronized void cancel() {
		scheduler.shutdownNow();

	}

}
