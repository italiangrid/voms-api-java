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
public class VOMSMessage {

  int code;
  String message;

  public int getCode() {

    return code;
  }

  public void setCode(int code) {

    this.code = code;
  }

  public String getMessage() {

    return message;
  }

  public void setMessage(String message) {

    this.message = message;
  }

  public VOMSMessage(int code, String message) {

    this.code = code;
    this.message = message;
  }

  public String toString() {

    return "voms message " + code + ": " + message;
  }
}
