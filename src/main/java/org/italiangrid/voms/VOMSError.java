// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms;

/**
 * The base VOMS exception class.
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

  public VOMSError(String message, Throwable cause) {

    super(message, cause);
  }
}
