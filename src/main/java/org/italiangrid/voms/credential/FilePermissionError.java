package org.italiangrid.voms.credential;

import org.italiangrid.voms.VOMSError;

/**
 * This error is raised when there is an attempt to load a credential
 * which has the wrong file permissions  
 * 
 */
public class FilePermissionError extends VOMSError {

	public FilePermissionError(String message) {
		super(message);
	}

	
	public FilePermissionError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
