/**
 * Copyright (c) Istituto Nazionale di Fisica Nucleare, 2006-2014.
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

import org.italiangrid.voms.VOMSAttribute;
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

  /** A list of {@link VOMSValidationErrorMessage} **/
  private final List<VOMSValidationErrorMessage> validationErrors;

  /** The attributes this validation result refer to **/
  private final VOMSAttribute attributes;

  /**
   * Default constructor.
   * 
   * @param attributes
   *          the attributes this validation result refers to
   * @param valid
   *          <code>true</code> in case of validation success,
   *          <code>false</code> otherwise
   */
  public VOMSValidationResult(VOMSAttribute attributes, boolean valid) {

    this(attributes, valid, new ArrayList<VOMSValidationErrorMessage>());
  }

  /**
   * This constructor is used to pass in a list of validation errors as well.
   * 
   * @param attributes
   *          the attributes this validation result refers to
   * @param valid
   *          <code>true</code> in case of validation success,
   *          <code>false</code> otherwise
   * @param validationErrors
   *          a list of validation errors
   */
  public VOMSValidationResult(VOMSAttribute attributes, boolean valid,
    List<VOMSValidationErrorMessage> validationErrors) {

    this.attributes = attributes;
    this.valid = valid;
    this.validationErrors = validationErrors;
  }

  /**
   * Tells if validation was successful or not.
   * 
   * @return valid <code>true</code> in case of validation success,
   *         <code>false</code> otherwise
   */
  public boolean isValid() {

    return valid;
  }

  /**
   * The attributes are to be considered valid only if the {@link #isValid()}
   * method for this {@link VOMSValidationResult} is <code>true</code>.
   * 
   * @return the attributes this validation result refer to
   */
  public VOMSAttribute getAttributes() {

    return attributes;
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
      + validationErrors + ", attributes=" + attributes + "]";
  }

}
