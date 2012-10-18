package org.italiangrid.voms.request;

import org.glite.voms.contact.VOMSErrorMessage;
import org.glite.voms.contact.VOMSWarningMessage;

/**
 * 
 * @author andreaceccanti
 *
 */
public interface VOMSResponse {

	/**
	 * @return <code>true</code> if this {@link VOMSResponse} has errors, <code>false</code> otherwise
	 */
	public boolean hasErrors();

	/**
	 * @return <code>true</code> if this {@link VOMSResponse} has warnings, <code>false</code> otherwise
	 */
	public boolean hasWarnings();

	/**
	 * 
	 * Extracts the AC from the VOMS response.
	 * 
	 * @return an array of bytes containing the AC.
	 */
	public byte[] getAC();

	/**
	 * Extracts the version from the VOMS response.
	 * 
	 * @return an integer containing the AC.
	 */
	public abstract int getVersion();

	/**
	 * 
	 * Extracts the error messages from the VOMS response.
	 * 
	 * @return an array of {@link VOMSErrorMessage} objects.
	 */
	public VOMSErrorMessage[] errorMessages();

	/**
	 * Extracts the warning messags from the VOMS response.
	 * @return an array of {@link VOMSWarningMessage} objects.
	 */
	public VOMSWarningMessage[] warningMessages();

}