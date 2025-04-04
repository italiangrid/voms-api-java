// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.ac;

import java.security.cert.X509Certificate;

import org.italiangrid.voms.VOMSAttribute;

/**
 * The strategy implemented to perform the validation of a VOMS attribute
 * certificate.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACValidationStrategy {

  /**
   * Validates a VOMS Attribute Certificate
   * 
   * @param attributes
   *          the parsed VOMS attributes
   * @param theChain
   *          the certificate chain from which the attributes were parsed
   * @return a {@link VOMSValidationResult} object describing the outcome of the
   *         validation
   */
  public VOMSValidationResult validateAC(VOMSAttribute attributes,
    X509Certificate[] theChain);

  /**
   * Validates VOMS attributes not extracted from a certificate chain (e.g., as
   * returned from the VOMS server)
   * 
   * @param attributes
   *          the VOMS attributes
   * @return a {@link VOMSValidationResult} object describing the outcome of the
   *         validation
   */
  public VOMSValidationResult validateAC(VOMSAttribute attributes);
}
