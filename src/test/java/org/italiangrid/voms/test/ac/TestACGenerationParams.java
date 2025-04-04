// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test.ac;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
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

  @Test(expected = NullPointerException.class)
  public void testNotAfterNullDateRaisesNullPointerException() {
    System.setProperty(NOT_AFTER.getPropertyName(), null);
    ACGenerationParams.fromSystemProperties();
  }

  @Test(expected = NullPointerException.class)
  public void testNotBeforeNullDateRaisesNullPointerException() {
    System.setProperty(NOT_BEFORE.getPropertyName(), null);
    ACGenerationParams.fromSystemProperties();
  }


  @Test(expected = DateTimeParseException.class)
  public void testNotAfterDateParsingError() {

    System.setProperty(NOT_AFTER.getPropertyName(), "ciccio");
    ACGenerationParams.fromSystemProperties();

  }

  @Test(expected = DateTimeParseException.class)
  public void testNotBeforeDateParsingError() {

    System.setProperty(NOT_BEFORE.getPropertyName(), "ciccio");
    ACGenerationParams.fromSystemProperties();
  }


  @Test
  public void testDateParsing() {
    System.setProperty(NOT_BEFORE.getPropertyName(), JAN_FIRST_2020_00_00_00_S);
    System.setProperty(NOT_AFTER.getPropertyName(), JAN_FIRST_2020_00_00_10_S);
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    assertThat(params.getNotBefore(), equalTo(JAN_FIRST_2020_00_00_00));
    assertThat(params.getNotAfter(), equalTo(JAN_FIRST_2020_00_00_10));
  }

  @Test
  public void testGaParsing() {
    System.setProperty(GAS.getPropertyName(), "one = uno, two = due, three = tre");
    ACGenerationParams params = ACGenerationParams.fromSystemProperties();
    assertThat(params.getGas().size(), equalTo(3));
    assertThat(params.getGas().get(0).getName(), equalTo("one"));
    assertThat(params.getGas().get(0).getValue(), equalTo("uno"));
    assertThat(params.getGas().get(0).getContext(), equalTo("test"));
    
    assertThat(params.getGas().get(1).getName(), equalTo("two"));
    assertThat(params.getGas().get(1).getValue(), equalTo("due"));
    assertThat(params.getGas().get(1).getContext(), equalTo("test"));

    assertThat(params.getGas().get(2).getName(), equalTo("three"));
    assertThat(params.getGas().get(2).getValue(), equalTo("tre"));
    assertThat(params.getGas().get(2).getContext(), equalTo("test"));
  }



}
