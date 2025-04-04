// SPDX-FileCopyrightText: 2006 Istituto Nazionale di Fisica Nucleare
//
// SPDX-License-Identifier: Apache-2.0

package org.italiangrid.voms.test;

import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;

import org.italiangrid.voms.util.TimeUtils;
import org.junit.Test;

public class TestTimeUtils {

  @Test
  public void testSuccessfulCompute() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, 1);

    Date nowPlus1Minute = cal.getTime();

    Assert.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, now,
      nowPlus1Minute, 1));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testSameArgumentFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    TimeUtils.checkTimeInRangeWithSkew(now, now, now, 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvertedIntervalFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, -5);
    Date fiveMinutesAgo = cal.getTime();

    TimeUtils.checkTimeInRangeWithSkew(now, now, fiveMinutesAgo, 1);
  }

  @Test
  public void testLowerBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.MINUTE, 2);

    Date nowPlus2minute = cal.getTime();

    cal.add(Calendar.YEAR, 1);

    Date inOneYear = cal.getTime();

    Assert.assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute,
      inOneYear, 2));
    Assert.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute,
      inOneYear, 3));

  }

  @Test
  public void testUpperBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.YEAR, -1);

    Date oneYearAgo = cal.getTime();

    Assert.assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now,
      0));
    Assert.assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now,
      1));

  }

}
