// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.italiangrid.voms.VOMSAttribute;
import org.italiangrid.voms.asn1.VOMSACUtils;
import org.italiangrid.voms.request.VOMSACRequest;
import org.italiangrid.voms.request.impl.ACGenerationParams;
import org.italiangrid.voms.request.impl.DefaultVOMSACRequest;
import org.italiangrid.voms.request.impl.FakeVOMSACService;
import org.italiangrid.voms.util.NullListener;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestFakeVOMSACService extends TestACSupport {

  public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
  public static final Date JAN_FIRST_2010 = Date
    .from(LocalDate.parse("2010-01-01", DATE_FORMATTER).atStartOfDay().toInstant(ZoneOffset.UTC));

  public static final Date JAN_TEN_2010 = Date
    .from(LocalDate.parse("2010-01-10", DATE_FORMATTER).atStartOfDay().toInstant(ZoneOffset.UTC));

  @BeforeClass
  public static void suiteInit()
      throws KeyStoreException, CertificateException, FileNotFoundException, IOException {
    initializeCredentials();
  }

  @Test
  public void testFakeAcServiceCreation() {

    ACGenerationParams params = ACGenerationParams.builder()
      .vo("fake")
      .fqan("/fake")
      .notBefore(JAN_FIRST_2010)
      .notAfter(JAN_TEN_2010)
      .serialNo(189)
      .build();

    FakeVOMSACService acService = FakeVOMSACService.newInstance(aaCredential, params, 
        NullListener.INSTANCE);

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test").build();
    AttributeCertificate ac = acService.getVOMSAttributeCertificate(holderCredential, req);

    VOMSAttribute attrs = VOMSACUtils.deserializeVOMSAttributes(ac);

    assertThat(attrs.getVO(), equalTo("test"));
    assertThat(attrs.getNotBefore(), equalTo(JAN_FIRST_2010));
    assertThat(attrs.getNotAfter(), equalTo(JAN_TEN_2010));
    assertThat(attrs.getPrimaryFQAN(), equalTo("/fake"));
    assertThat(attrs.getVOMSAC().getSerialNumber(), equalTo(BigInteger.valueOf(189)));
  }

}
