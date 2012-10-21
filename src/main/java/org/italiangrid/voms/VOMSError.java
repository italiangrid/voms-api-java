package org.italiangrid.voms;

/**
 * A VOMS runtime exception. 
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSError extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public VOMSError(String message) {
		super(message);
	}
	
	
	public VOMSError(String message, Throwable cause){
		super(message, cause);
	}
}
