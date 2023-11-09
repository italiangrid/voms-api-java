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
package org.italiangrid.voms.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import org.italiangrid.voms.util.TimeUtils;
import org.junit.Test;

public class TestTimeUtils {

  @Test
  public void testSuccessfulCompute() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, 1);

    Date nowPlus1Minute = cal.getTime();

    assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, now, nowPlus1Minute, 1));

  }

  @Test
  public void testSameArgumentFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    assertThrows(IllegalArgumentException.class, () -> {
      TimeUtils.checkTimeInRangeWithSkew(now, now, now, 1);
    });
  }

  @Test
  public void testInvertedIntervalFailure() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();
    cal.add(Calendar.MINUTE, -5);
    Date fiveMinutesAgo = cal.getTime();

    assertThrows(IllegalArgumentException.class, () -> {
      TimeUtils.checkTimeInRangeWithSkew(now, now, fiveMinutesAgo, 1);
    });
  }

  @Test
  public void testLowerBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.MINUTE, 2);

    Date nowPlus2minute = cal.getTime();

    cal.add(Calendar.YEAR, 1);

    Date inOneYear = cal.getTime();

    assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute, inOneYear, 2));
    assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, nowPlus2minute, inOneYear, 3));

  }

  @Test
  public void testUpperBound() {

    Calendar cal = Calendar.getInstance();
    Date now = cal.getTime();

    cal.add(Calendar.YEAR, -1);

    Date oneYearAgo = cal.getTime();

    assertFalse(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now, 0));
    assertTrue(TimeUtils.checkTimeInRangeWithSkew(now, oneYearAgo, now, 1));

  }

}
