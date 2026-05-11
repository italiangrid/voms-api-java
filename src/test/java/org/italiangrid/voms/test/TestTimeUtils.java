// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import java.util.Calendar;
import java.util.Date;
import org.italiangrid.voms.util.TimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestTimeUtils {

  @Test
  public void testSuccessfulCompute() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, 1);

    Date nowPlus1Minute = cal.getTime();

    Assertions.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, now, nowPlus1Minute, 1));
  }

  @Test
  public void testSameArgumentFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    Assertions.assertThrows(
        IllegalArgumentException.class, () -> TimeUtils.checkTimeInRangeWithSkew(now, now, now, 1));
  }

  @Test
  public void testInvertedIntervalFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, -5);
    Date fiveMinutesAgo = cal.getTime();

    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> TimeUtils.checkTimeInRangeWithSkew(now, now, fiveMinutesAgo, 1));
  }

  @Test
  public void testLowerBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.MINUTE, 2);

    Date nowPlus2minute = cal.getTime();

    cal.add(Calendar.YEAR, 1);

    Date inOneYear = cal.getTime();

    Assertions.assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute, inOneYear, 2));
    Assertions.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute, inOneYear, 3));
  }

  @Test
  public void testUpperBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.YEAR, -1);

    Date oneYearAgo = cal.getTime();

    Assertions.assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now, 0));
    Assertions.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now, 1));
  }

  @Test
  public void testParseNullDateRaisesNullPointerException() {

    Assertions.assertThrows(NullPointerException.class, () -> TimeUtils.parseDate(null));
  }
}
