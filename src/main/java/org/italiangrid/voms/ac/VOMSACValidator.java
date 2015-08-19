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

import java.security.cert.X509Certificate;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;

/**
 * This interface extends the {@link VOMSACParser} interface and provides
 * methods to perform validation on the VOMS Attribute Certificates parsed from
 * a given certificate chain.
 * 
 * @author Andrea Ceccanti
 *
 */
public interface VOMSACValidator extends VOMSACParser {

  /**
   * Parses and validates the VOMS attributes found in the certificate chain
   * passed as argument (which is assumed to be validated already).
   * 
   * This method returns the possibly empty list of the validated attributes.
   * 
   * This method should be used in conjunction with the registration of a
   * {@link ValidationResultListener} to get details about validation error and
   * warning messages.
   * 
   * Use the {@link #validateWithResult(X509Certificate[])} method in case you
   * don't want to rely on a {@link ValidationResultListener}.
   * 
   * @param validatedChain
   *          a validated X.509 certificate chain
   * @return a possibly empty list of {@link VOMSAttribute} object providing
   *         access to the validated VOMS attributes
   */
  public List<VOMSAttribute> validate(X509Certificate[] validatedChain);

  /**
   * Parses and validates the VOMS attributes found in the certificate chain
   * passed as argument (which is assumed to be validated already).
   * 
   * This method returns a possibly empty list of {@link VOMSValidationResult}
   * objects which describe the outcome of the validation for each VOMS AC found
   * in the certificate chain.
   * 
   * This method is useful when you want to use a single call to get all details
   * about validation without relying on the registration of a
   * {@link ValidationResultListener}.
   * 
   * @param validatedChain
   *          a chain of X.509 certificates   
   * @return a possibly empty list of {@link VOMSValidationResult} object
   *         providing access to validation results and related attributes
   */
  public List<VOMSValidationResult> validateWithResult(
    X509Certificate[] validatedChain);

  /**
   * Validates the VOMS attributes found in the attribute certificate list
   * passed as argument.
   * 
   * @param acs
   *          a list of {@link AttributeCertificate}
   * @return the validated and possibly empty list of
   *         {@link AttributeCertificate} object
   */
  public List<AttributeCertificate> validateACs(List<AttributeCertificate> acs);

  /**
   * Sets a listener that will received validation-related events for this
   * {@link VOMSACValidator}.
   * 
   * @param listener
   *          the listener that will receive validation-related events.
   */
  public void setValidationResultListener(ValidationResultListener listener);

  /**
   * Shutdown the VOMS validator. This method should be called to perform final
   * cleanup operations.
   */
  public void shutdown();
}
