package org.italiangrid.voms.ac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.italiangrid.voms.error.VOMSValidationErrorMessage;

/**
 * This class represents the outcome of a VOMS validation.
 * 
 * @author andreaceccanti
 *
 */
public class VOMSValidationResult {

	/** The flag that tells whether the validation was successfull or not **/
	private final boolean valid;
	
	/** A list of {@link VOMSValidationErrorMessage}**/
	private final List<VOMSValidationErrorMessage> validationErrors;
	
	/**
	 * Default constructor.
	 * 
	 * @param valid <code>true</code> in case of validation success, <code>false</code> otherwise
	 */
	public VOMSValidationResult(boolean valid) {
		this(valid, new ArrayList<VOMSValidationErrorMessage>());
	}
	
	/**
	 * This constructor is used to pass in a list of validation errors as well.
	 * 
	 * @param valid <code>true</code> in case of validation success, <code>false</code> otherwise
	 * @param validationErrors a list of validation errors
	 */
	public VOMSValidationResult(boolean valid, List<VOMSValidationErrorMessage> validationErrors) {
		this.valid = valid;
		this.validationErrors = validationErrors;
	}

	/**
	 * Tells if validation was successful or not.
	 * 
	 * @return valid <code>true</code> in case of validation success, <code>false</code> otherwise
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @return the possibly empty list of validation errors
	 */
	public List<VOMSValidationErrorMessage> getValidationErrors() {
		return Collections.unmodifiableList(validationErrors);
	}

	@Override
	public String toString() {
		return "VOMSValidationResult [valid=" + valid + ", validationErrors="
				+ validationErrors + "]";
	}
	
}
