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
