package org.italiangrid.voms.store.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.store.UpdatingVOMSTrustStore;
import org.italiangrid.voms.store.VOMSTrustStoreUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A VOMS trust store that periodically updates itself. The update frequency is set
 * once at VOMS trust store creation time.
 * 
 * 
 * @author andreaceccanti
 *
 */
public class DefaultUpdatingVOMSTrustStore extends DefaultVOMSTrustStore implements
		UpdatingVOMSTrustStore {

	public static final Logger log = LoggerFactory.getLogger(DefaultUpdatingVOMSTrustStore.class);
	
	/**
	 * Default trust store update frequency. 
	 */
	public static final long DEFAULT_UPDATE_FREQUENCY = TimeUnit.MINUTES.toMillis(10);
	
	/**
	 * This trust store update frequency.
	 */
	private long updateFrequency;
	
	/**
	 * The scheduler used to schedule the update tasks.
	 */
	private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new VOMSNamedThreadFactory());
	
	private VOMSTrustStoreUpdateListener updateListener;
	
	public DefaultUpdatingVOMSTrustStore(List<String> localTrustDirs, long updateFrequency, VOMSTrustStoreUpdateListener updateListener) {
		super(localTrustDirs);
		updateFrequencySanityChecks(updateFrequency);
		this.updateFrequency = updateFrequency;
		this.updateListener = updateListener;
		scheduleUpdate();
	}
	
	public DefaultUpdatingVOMSTrustStore(long updateFrequency) {
		this(buildDefaultTrustedDirs(), updateFrequency, new TrustStoreUpdatesLogger(false));
	}
	
	public DefaultUpdatingVOMSTrustStore(List<String> localTrustDirs, long updateFrequency) {
		this(localTrustDirs, updateFrequency, new TrustStoreUpdatesLogger(false));
	}

	
	public DefaultUpdatingVOMSTrustStore(long updateFrequency, VOMSTrustStoreUpdateListener updateListener) {
		this(buildDefaultTrustedDirs(), updateFrequency, new TrustStoreUpdatesLogger(false));
	}

	public DefaultUpdatingVOMSTrustStore(){
		this(buildDefaultTrustedDirs(), DEFAULT_UPDATE_FREQUENCY, new TrustStoreUpdatesLogger(true));
	}
	
	
	protected void updateFrequencySanityChecks(long updateFrequency){
		if (updateFrequency <= 0)
			throw new VOMSError("Please provide a positive value for this store update frequency!");
		
		if (updateFrequency > TimeUnit.DAYS.toMillis(7)){
			log.warn("Trust store update frequency set to more than one week! Please use a more sensible value (e.g., 1 DAY).");
		}
	}
	
	protected void scheduleUpdate(){
		
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
	
	public synchronized long getUpdateFrequency() {
		return updateFrequency;
	}

	public synchronized void update() {
		loadTrustInformation();
		updateListener.notifyTrustStoreUpdate(this);
	}

	public synchronized void cancel() {
		log.debug("Canceling update thread");
		scheduler.shutdownNow();

	}

	public void setTrustStoreUpdateListener(
			VOMSTrustStoreUpdateListener updateListener) {
		this.updateListener = updateListener;
		
	}

}
