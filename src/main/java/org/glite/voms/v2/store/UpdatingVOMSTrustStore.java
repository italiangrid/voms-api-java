package org.glite.voms.v2.store;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.glite.voms.v2.VOMSError;
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
public class UpdatingVOMSTrustStore extends DefaultVOMSTrustStore implements
		Updateable {

	public static final Logger log = LoggerFactory.getLogger(UpdatingVOMSTrustStore.class);
	
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
	private final ScheduledExecutorService scheduler;
	
	/**
	 * 
	 * @param updateFrequency
	 */
	public UpdatingVOMSTrustStore(long updateFrequency) {
		super();
		updateFrequencySanityChecks(updateFrequency);
		this.updateFrequency = updateFrequency;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduleUpdate();
	}

	public UpdatingVOMSTrustStore(List<String> localTrustDirs, long updateFrequency) {
		super(localTrustDirs);
		updateFrequencySanityChecks(updateFrequency);
		this.updateFrequency = updateFrequency;
		scheduler = new ScheduledThreadPoolExecutor(1);
		scheduleUpdate();
	}

	public UpdatingVOMSTrustStore(){
		this(DEFAULT_UPDATE_FREQUENCY);
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
				try{
					
					update();
					
				}catch(Throwable e){
					log.error("Error updating VOMS trust store: "+e.getMessage(),e);	
				}
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
		log.debug("Starting VOMS trustore update...");
		loadTrustInformation();
		log.debug("VOMS trustore update finished.");
	}

	public synchronized void cancel() {
		log.debug("Canceling update thread");
		scheduler.shutdownNow();

	}

}
