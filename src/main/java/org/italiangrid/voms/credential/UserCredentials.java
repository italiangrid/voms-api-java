// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.credential;

import eu.emi.security.authn.x509.helpers.PasswordSupplier;
import org.italiangrid.voms.credential.impl.DefaultLoadCredentialsStrategy;

import eu.emi.security.authn.x509.X509Credential;

/**
 * This class implements convenience methods to load X509 user credentials in
 * PEM or PKCS12 format.
 * 
 * @author Andrea Ceccanti
 * 
 */
public class UserCredentials {

  private static LoadCredentialsStrategy loadCredentialsStrategy = new DefaultLoadCredentialsStrategy();

  public static void setLoadCredentialsStrategy(LoadCredentialsStrategy strategy) {

    loadCredentialsStrategy = strategy;
  }

  public static X509Credential loadCredentials() {

    return loadCredentials((char[]) null);
  }

  public static X509Credential loadCredentials(final char[] keyPassword) {

    PasswordSupplier pf = new PasswordSupplier() {

      public char[] getPassword() {

        return keyPassword;
      }
    };

    return loadCredentialsStrategy.loadCredentials(pf);
  }

  public static X509Credential loadCredentials(PasswordSupplier passwordFinder) {

    return loadCredentialsStrategy.loadCredentials(passwordFinder);
  }
}
