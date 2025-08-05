// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.req;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSError;
import org.italiangrid.voms.ac.VOMSACValidator;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.VOMSACService;
import org.italiangrid.voms.request.VOMSProtocol;
import org.italiangrid.voms.request.VOMSProtocolError;
import org.italiangrid.voms.request.VOMSResponse;
import org.italiangrid.voms.request.VOMSServerInfo;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.test.utils.EchoVOMSProtocol;
import org.italiangrid.voms.test.utils.Fixture;
import org.italiangrid.voms.test.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import eu.emi.security.authn.x509.X509Credential;
import eu.emi.security.authn.x509.impl.PEMCredential;

public class TestRequests implements Fixture {

  @Test
  public void testEchoRequest() throws Exception {

    VOMSACService acService = Utils.buildACService(new EchoVOMSProtocol(Utils.getAACredential()));

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    PEMCredential holder = Utils.getTestUserCredential();

    AttributeCertificate ac = acService.getVOMSAttributeCertificate(holder, req);

    VOMSACValidator validator = Utils.getVOMSValidator();
    List<AttributeCertificate> acs = validator.validateACs(Arrays.asList(ac));

    Assert.assertFalse(acs.isEmpty());

  }

  @Test
  public void testFailureIfVOIsNotKnown() throws Exception {

    VOMSACService acService = Utils.buildACService(new EchoVOMSProtocol(Utils.getAACredential()));

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.unknown.vo").build();

    PEMCredential holder = Utils.getTestUserCredential();

    try {

      acService.getVOMSAttributeCertificate(holder, req);

    } catch (VOMSError e) {
      Assert.assertEquals(
          "VOMS server for VO test.unknown.vo is not known! Check your vomses configuration.",
          e.getMessage());
      return;
    }

    Assert.fail("No exceptions raised for unknown VO");
  }

  @Test
  public void testNullACBytesHandling() throws Exception {

    VOMSProtocol nullBytesProtocol = new VOMSProtocol() {

      public VOMSResponse doRequest(VOMSServerInfo endpoint, X509Credential credential,
          VOMSACRequest request) {

        VOMSResponse r = Mockito.mock(VOMSResponse.class);

        return r;
      }
    };

    VOMSACService acService = Utils.buildACService(nullBytesProtocol);

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    AttributeCertificate ac =
        acService.getVOMSAttributeCertificate(Utils.getTestUserCredential(), req);

    Assert.assertNull(ac);
  }

  @Test
  public void testRandomACBytesHandling() throws Exception {

    VOMSProtocol nullBytesProtocol = new VOMSProtocol() {

      public VOMSResponse doRequest(VOMSServerInfo endpoint, X509Credential credential,
          VOMSACRequest request) {

        Random r = new Random();
        byte[] acBytes = new byte[2048];

        r.nextBytes(acBytes);

        VOMSResponse response = Mockito.mock(VOMSResponse.class);
        Mockito.when(response.getAC()).thenReturn(acBytes);

        return response;
      }
    };

    VOMSACService acService = Utils.buildACService(nullBytesProtocol);

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    AttributeCertificate ac =
        acService.getVOMSAttributeCertificate(Utils.getTestUserCredential(), req);

    Assert.assertNull(ac);
  }

  @Test
  public void testProtocolFallback() throws Exception {

    VOMSProtocol exceptionProtocol = Mockito.mock(VOMSProtocol.class);

    Mockito
      .when(exceptionProtocol.doRequest(Mockito.any(VOMSServerInfo.class),
          Mockito.any(X509Credential.class), Mockito.any(VOMSACRequest.class)))
      .thenReturn(null);

    VOMSProtocol fallBackProtocol = Mockito.mock(VOMSProtocol.class);

    VOMSACService acService = Utils.buildACService(exceptionProtocol, fallBackProtocol);
    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    AttributeCertificate ac =
        acService.getVOMSAttributeCertificate(Utils.getTestUserCredential(), req);

    Mockito.verify(fallBackProtocol, Mockito.atLeastOnce())
      .doRequest(Mockito.any(VOMSServerInfo.class), Mockito.any(X509Credential.class),
          Mockito.any(VOMSACRequest.class));

    Assert.assertNull(ac);
  }

  @Test
  public void testProtocolFallbackDisabled() throws Exception {

    VOMSProtocol exceptionProtocol = Mockito.mock(VOMSProtocol.class);

    Mockito
      .when(exceptionProtocol.doRequest(Mockito.any(VOMSServerInfo.class),
          Mockito.any(X509Credential.class), Mockito.any(VOMSACRequest.class)))
      .thenReturn(null);

    VOMSProtocol fallBackProtocol = Mockito.mock(VOMSProtocol.class);

    VOMSACService acService = Utils.buildACService(exceptionProtocol, fallBackProtocol, false);
    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    AttributeCertificate ac =
        acService.getVOMSAttributeCertificate(Utils.getTestUserCredential(), req);

    Mockito.verifyNoMoreInteractions(fallBackProtocol);

    Assert.assertNull(ac);
  }

  @Test
  public void testProtocolFallback2() throws Exception {

    VOMSProtocol exceptionProtocol = Mockito.mock(VOMSProtocol.class);

    Mockito
      .when(exceptionProtocol.doRequest(Mockito.any(VOMSServerInfo.class),
          Mockito.any(X509Credential.class), Mockito.any(VOMSACRequest.class)))
      .thenThrow(new VOMSProtocolError("protocol error", null, null, null, null));

    VOMSProtocol fallBackProtocol = Mockito.mock(VOMSProtocol.class);

    VOMSACService acService = Utils.buildACService(exceptionProtocol, fallBackProtocol);
    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test.vo").build();

    AttributeCertificate ac =
        acService.getVOMSAttributeCertificate(Utils.getTestUserCredential(), req);

    Mockito.verify(fallBackProtocol, Mockito.atLeastOnce())
      .doRequest(Mockito.any(VOMSServerInfo.class), Mockito.any(X509Credential.class),
          Mockito.any(VOMSACRequest.class));

    Assert.assertNull(ac);
  }

}
