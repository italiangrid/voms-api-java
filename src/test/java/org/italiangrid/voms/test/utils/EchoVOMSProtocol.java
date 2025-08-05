// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.mockito.Mockito;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class EchoVOMSProtocol implements VOMSProtocol {

  PEMCredential aaCredential;

  public EchoVOMSProtocol(PEMCredential aaCredential) {

    this.aaCredential = aaCredential;
  }

  public VOMSResponse doRequest(VOMSServerInfo endpoint,
    X509Credential credential, VOMSACRequest request) {

    VOMSAA aa = new VOMSAA(aaCredential, endpoint.getVoName(), endpoint
      .getURL().getHost(), endpoint.getURL().getPort());

    int lifetimeInSeconds = request.getLifetime();

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.SECOND, lifetimeInSeconds);
    Date endTime = cal.getTime();

    List<String> fqans;

    if (request.getRequestedFQANs().isEmpty()) {
      fqans = new ArrayList<String>();
      fqans.add("/" + request.getVoName());
    } else
      fqans = request.getRequestedFQANs();

    AttributeCertificate ac = aa.getAC(credential, fqans, null,
      request.getTargets(), now, endTime);

    VOMSResponse r = Mockito.mock(VOMSResponse.class);
    try {

      Mockito.when(r.getAC()).thenReturn(ac.getEncoded());

    } catch (IOException e) {
      throw new VOMSError(e.getMessage(), e);
    }

    return r;
  }

}
