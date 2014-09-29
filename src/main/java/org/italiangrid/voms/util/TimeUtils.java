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
package org.italiangrid.voms.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Time utilities.
 * 
 * @author cecco
 *
 */
public class TimeUtils {
	
	private TimeUtils(){}
	
	/**
	 * Checks that a date falls in the interval allowing for a certain clock skew expressed in minutes.
	 * The interval defined by (startDate, endDate) is modified to be (startDate - skewInMinutes, endDate + skewInMinutes).
	 * 
	 * @param timeToCheck the time to be checked
	 * @param startDate the start date of the time range
	 * @param endDate the end date of the time range
	 * @param skewInMinutes the clock skew in minutes to take into account
	 * 
	 * @throws IllegalArgumentException if passed an illegal time range
	 * @return <code>true</code>, if the time is in the given range, <code>false</code> otherwise
	 */
	public static boolean checkTimeInRangeWithSkew(Date timeToCheck, Date startDate, Date endDate, int skewInMinutes){
	
		if (startDate.after(endDate) || startDate.equals(endDate)){
			String msg = String.format("Illegal time interval: start date must be before end date. [start date: %s, end date: %s]",startDate, endDate);
			throw new IllegalArgumentException(msg);
		}
			
			
		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.add(Calendar.MINUTE, -skewInMinutes);
		
		Date skewedStartDate = cal.getTime();
		
		cal.clear();
		cal.setTime(endDate);
		cal.add(Calendar.MINUTE, skewInMinutes);
		
		Date skewedEndDate = cal.getTime();
		
		return skewedEndDate.after(timeToCheck) && skewedStartDate.before(timeToCheck);
		
	}

}
