// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

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
   *          the attributes this validation result refer to
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
   *          the {@link VOMSAttribute} that will be validated
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
