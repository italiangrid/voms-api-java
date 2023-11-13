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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.GAS;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_AFTER;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_BEFORE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;

import org.italiangrid.voms.request.impl.ACGenerationParams;
import org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties;
import org.italiangrid.voms.util.TimeUtils;
import org.junit.After;
import org.junit.Test;

public class TestACGenerationParams {

  public static final String JAN_FIRST_2020_00_00_00_S = "2020-01-01T00:00:00";
  public static final String JAN_FIRST_2020_00_00_10_S = "2020-01-01T00:00:10";

  public static final Date JAN_FIRST_2020_00_00_00 =
      Date.from(LocalDateTime.parse(JAN_FIRST_2020_00_00_00_S, TimeUtils.DATE_FORMATTER)
        .toInstant(ZoneOffset.UTC));

  public static final Date JAN_FIRST_2020_00_00_10 =
      Date.from(LocalDateTime.parse(JAN_FIRST_2020_00_00_10_S, TimeUtils.DATE_FORMATTER)
        .toInstant(ZoneOffset.UTC));

  @After
  public void after() {
    // Cleanup system properties
    for (FakeVOMSACServiceProperties p : FakeVOMSACServiceProperties.values()) {
      System.getProperties().remove(p.getPropertyName());
    }
  }


  @Test
  public void testNoPropertySetsSucceeds() {

    ACGenerationParams.fromSystemProperties();

  }

  @Test
  public void testNotAfterNullDateRaisesNullPointerException() {
    assertThrows(NullPointerException.class, () -> {
      System.setProperty(NOT_AFTER.getPropertyName(), null);
      ACGenerationParams.fromSystemProperties();
    });
  }

  @Test
  public void testNotBeforeNullDateRaisesNullPointerException() {
    assertThrows(NullPointerException.class, () -> {
      System.setProperty(NOT_BEFORE.getPropertyName(), null);
      ACGenerationParams.fromSystemProperties();
    });
  }


  @Test
  public void testNotAfterDateParsingError() {

    assertThrows(DateTimeParseException.class, () -> {
      System.setProperty(NOT_AFTER.getPropertyName(), "ciccio");
      ACGenerationParams.fromSystemProperties();
    });

  }

  @Test
  public void testNotBeforeDateParsingError() {

    assertThrows(DateTimeParseException.class, () -> {
      System.setProperty(NOT_BEFORE.getPropertyName(), "ciccio");
      ACGenerationParams.fromSystemProperties();
    });
  }


  @Test
  public void testDateParsing() {
    System.setProperty(NOT_BEFORE.getPropertyName(), JAN_FIRST_2020_00_00_00_S);
    System.setProperty(NOT_AFTER.getPropertyName(), JAN_FIRST_2020_00_00_10_S);
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    assertEquals(JAN_FIRST_2020_00_00_00, params.getNotBefore());
    assertEquals(JAN_FIRST_2020_00_00_10, params.getNotAfter());
  }

  @Test
  public void testGaParsing() {
    System.setProperty(GAS.getPropertyName(), "one = uno, two = due, three = tre");
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    assertEquals(3, params.getGas().size());
    assertEquals("one", params.getGas().get(0).getName());
    assertEquals("uno", params.getGas().get(0).getValue());
    assertEquals("test", params.getGas().get(0).getContext());
    
    assertEquals("two", params.getGas().get(1).getName());
    assertEquals("due", params.getGas().get(1).getValue());
    assertEquals("test", params.getGas().get(1).getContext());

    assertEquals("three", params.getGas().get(2).getName());
    assertEquals("tre", params.getGas().get(2).getValue());
    assertEquals("test", params.getGas().get(2).getContext());
  }



}
