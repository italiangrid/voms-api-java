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
package org.italiangrid.voms.ac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.italiangrid.voms.error.VOMSValidationErrorMessage;

/**
 * This class represents the outcome of a VOMS validation.
 * 
 * @author Andrea Ceccanti
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
