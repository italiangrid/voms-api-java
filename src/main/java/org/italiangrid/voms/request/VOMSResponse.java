package org.italiangrid.voms.request;

import org.glite.voms.contact.VOMSErrorMessage;
import org.glite.voms.contact.VOMSWarningMessage;

public interface VOMSResponse {

	public boolean hasErrors();

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

	public VOMSWarningMessage[] warningMessages();

}