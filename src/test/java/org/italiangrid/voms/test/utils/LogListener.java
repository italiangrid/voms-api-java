// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.utils;

import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSErrorMessage;
import org.italiangrid.voms.request.VOMSRequestListener;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.VOMSWarningMessage;

public enum LogListener implements VOMSRequestListener {

  INSTANCE;

  public void notifyVOMSRequestStart(VOMSACRequest request, VOMSServerInfo si) {

    System.out
      .format("Contacting %s for VO %s.\n", si.getURL(), si.getVoName());

  }

  public void notifyVOMSRequestSuccess(VOMSACRequest request,
    VOMSServerInfo endpoint) {

    System.out.format("Request for VO %s succeded.\n", endpoint.getVoName());

  }

  public void notifyVOMSRequestFailure(VOMSACRequest request,
    VOMSServerInfo endpoint, Throwable error) {

    System.out.format("Request for VO %s failed: %s.\n", request.getVoName(),
      error);

  }

  public void notifyErrorsInVOMSReponse(VOMSACRequest request,
    VOMSServerInfo si, VOMSErrorMessage[] errors) {

    System.out.format("Errors in voms response for VO %s.\n", si.getVoName());
    for (VOMSErrorMessage e : errors)
      System.out.println(e.getMessage());
  }

  public void notifyWarningsInVOMSResponse(VOMSACRequest request,
    VOMSServerInfo si, VOMSWarningMessage[] warnings) {

    System.out.format("Warnings in voms response for VO %s.\n", si.getVoName());

    for (VOMSWarningMessage m : warnings)
      System.out.println(m.getMessage());

  };

}
