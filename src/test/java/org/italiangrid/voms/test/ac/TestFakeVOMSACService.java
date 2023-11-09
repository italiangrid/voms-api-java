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
package org.italiangrid.voms.test.ac;

import static org.junit.Assert.assertEquals;

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

    FakeVOMSACService acService =
        FakeVOMSACService.newInstance(aaCredential, params, NullListener.INSTANCE);

    VOMSACRequest req = new DefaultVOMSACRequest.Builder("test").build();
    AttributeCertificate ac = acService.getVOMSAttributeCertificate(holderCredential, req);

    VOMSAttribute attrs = VOMSACUtils.deserializeVOMSAttributes(ac);

    assertEquals("test", attrs.getVO());
    assertEquals(JAN_FIRST_2010, attrs.getNotBefore());
    assertEquals(JAN_TEN_2010, attrs.getNotAfter());
    assertEquals("/fake", attrs.getPrimaryFQAN());
    assertEquals(BigInteger.valueOf(189), attrs.getVOMSAC().getSerialNumber());
  }

}
