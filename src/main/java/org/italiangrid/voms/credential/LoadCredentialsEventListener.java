package org.italiangrid.voms.credential;

/**
 * A {@link LoadCredentialsEventListener} is notified of the outcome of load credentials operations.
 * 
 * @author andreaceccanti
 *
 */
public interface LoadCredentialsEventListener {
	
	public enum LoadCredentialOutcome {
		SUCCESS,
		FAILURE
	}
	
	/**
	 * Informs about the outcome of a load credential operation. 
	 * @param outcome the outcome of the load credential operation
	 * @param error the exception that caused the load credential operation to fail
	 * @param locations the paths of the credentials being loaded
	 */
	public void loadCredentialNotification(LoadCredentialOutcome outcome, Throwable error, String... locations);
}
