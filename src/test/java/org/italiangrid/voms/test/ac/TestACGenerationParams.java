// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.GAS;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_AFTER;
import static org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties.NOT_BEFORE;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.italiangrid.voms.request.impl.ACGenerationParams;
import org.italiangrid.voms.request.impl.FakeVOMSACServiceProperties;
import org.italiangrid.voms.util.TimeUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestACGenerationParams {

  public static final String JAN_FIRST_2020_00_00_00_S = "2020-01-01T00:00:00";
  public static final String JAN_FIRST_2020_00_00_10_S = "2020-01-01T00:00:10";

  public static final Date JAN_FIRST_2020_00_00_00 =
      Date.from(
          LocalDateTime.parse(JAN_FIRST_2020_00_00_00_S, TimeUtils.DATE_FORMATTER)
              .toInstant(ZoneOffset.UTC));

  public static final Date JAN_FIRST_2020_00_00_10 =
      Date.from(
          LocalDateTime.parse(JAN_FIRST_2020_00_00_10_S, TimeUtils.DATE_FORMATTER)
              .toInstant(ZoneOffset.UTC));

  @AfterEach
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
  public void testNotAfterNullDate() {

    Assertions.assertNull(ACGenerationParams.fromSystemProperties().getNotAfter());
  }

  @Test
  public void testNotBeforeNullDate() {

    Assertions.assertNull(ACGenerationParams.fromSystemProperties().getNotBefore());
  }

  @Test
  public void testNotAfterDateParsingError() {

    System.setProperty(NOT_AFTER.getPropertyName(), "ciccio");
    Assertions.assertThrows(
        DateTimeParseException.class, () -> ACGenerationParams.fromSystemProperties());
  }

  @Test
  public void testNotBeforeDateParsingError() {

    System.setProperty(NOT_BEFORE.getPropertyName(), "ciccio");
    Assertions.assertThrows(
        DateTimeParseException.class, () -> ACGenerationParams.fromSystemProperties());
  }

  @Test
  public void testDateParsing() {

    System.setProperty(NOT_BEFORE.getPropertyName(), JAN_FIRST_2020_00_00_00_S);
    System.setProperty(NOT_AFTER.getPropertyName(), JAN_FIRST_2020_00_00_10_S);
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    Assertions.assertEquals(JAN_FIRST_2020_00_00_00, params.getNotBefore());
    Assertions.assertEquals(JAN_FIRST_2020_00_00_10, params.getNotAfter());
  }

  @Test
  public void testGaParsing() {

    System.setProperty(GAS.getPropertyName(), "one = uno, two = due, three = tre");
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    Assertions.assertEquals(3, params.getGas().size());
    Assertions.assertEquals("one", params.getGas().get(0).getName());
    Assertions.assertEquals("uno", params.getGas().get(0).getValue());
    Assertions.assertEquals("test", params.getGas().get(0).getContext());

    Assertions.assertEquals("two", params.getGas().get(1).getName());
    Assertions.assertEquals("due", params.getGas().get(1).getValue());
    Assertions.assertEquals("test", params.getGas().get(1).getContext());

    Assertions.assertEquals("three", params.getGas().get(2).getName());
    Assertions.assertEquals("tre", params.getGas().get(2).getValue());
    Assertions.assertEquals("test", params.getGas().get(2).getContext());
  }
}
