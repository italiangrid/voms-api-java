package org.italiangrid.voms.credential;

/**
 * A {@link LoadCredentialsEventListener} is notified of the outcome of load credentials operations.
 * 
 * @author andreaceccanti
 *
 */
public interface LoadCredentialsEventListener {
	
	/**
	 * Informs that credentials are been looked for in the locations passed as argument.
	 * @param locations 
	 */
	public void notifyCredentialLookup(String...locations);
	
	/**
	 * Informs that credentials have been succesfully loaded from the credentials passed as argument.
	 * 
	 * @param locations
	 */
	public void notifyLoadCredentialSuccess(String...locations);
	
	/**
	 * Informs that credentials could not be loaded form the locations passed as argument.
	 * 
	 * @param error the {@link Throwable} that caused the credential load operation to fail
	 * @param locations the locations where the credentials where loaded from 
	 */
	public void notifyLoadCredentialFailure(Throwable error, String...locations);
}
