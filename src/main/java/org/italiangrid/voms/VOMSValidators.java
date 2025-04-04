// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms;

import org.italiangrid.voms.ac.VOMSACLookupStrategy;
import org.italiangrid.voms.ac.VOMSACParser;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.ac.ValidationResultListener;
import org.italiangrid.voms.ac.impl.DefaultVOMSACParser;
import org.italiangrid.voms.ac.impl.DefaultVOMSValidator;
import org.italiangrid.voms.store.VOMSTrustStore;

import eu.emi.security.authn.x509.X509CertChainValidatorExt;

/**
 * A factory for VOMS attributes validators and parsers.
 * 
 * @author Andrea Ceccanti
 *
 */
public class VOMSValidators {

  private VOMSValidators() {

  }

  public static VOMSACValidator newValidator(ValidationResultListener listener) {

    return new DefaultVOMSValidator.Builder().validationListener(listener)
      .build();
  }

  public static VOMSACValidator newValidator(VOMSTrustStore trustStore,
    X509CertChainValidatorExt validator, ValidationResultListener vrListener) {

    return new DefaultVOMSValidator.Builder().trustStore(trustStore)
      .certChainValidator(validator).validationListener(vrListener).build();

  }

  public static VOMSACValidator newValidator(VOMSTrustStore store,
    X509CertChainValidatorExt validator, ValidationResultListener vrListener,
    VOMSACLookupStrategy strategy) {

    return new DefaultVOMSValidator.Builder().trustStore(store)
      .certChainValidator(validator).validationListener(vrListener)
      .acLookupStrategy(strategy).build();
  }

  public static VOMSACValidator newValidator(VOMSTrustStore trustStore,
    X509CertChainValidatorExt validator) {

    return new DefaultVOMSValidator.Builder().trustStore(trustStore)
      .certChainValidator(validator).build();
  }

  public static VOMSACValidator newValidator() {

    return new DefaultVOMSValidator.Builder().build();
  }

  public static VOMSACParser newParser() {

    return new DefaultVOMSACParser();
  }

}
