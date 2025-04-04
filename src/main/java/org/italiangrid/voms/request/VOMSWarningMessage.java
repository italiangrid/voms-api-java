// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.request;

/**
 * 
 * This class is used to decode VOMS error messages contained in a VOMS
 * response.
 * 
 * @author Andrea CEccanti
 * 
 */
public class VOMSWarningMessage extends VOMSMessage {

  public VOMSWarningMessage(int code, String message) {

    super(code, message);
  }

  public String toString() {

    return "voms warning " + code + ": " + message;

  }
}
