/**
 *
 * Copyright 2011 (C) Rainer Schneider,Roggenburg <schnurlei@googlemail.com>
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
package de.jdynameta.testcommon.util.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 
 *
 */
public class UtilDate {

	/**
	 * 
	 */
	public UtilDate() {
		super();
	}

	
	/**
	 * Create a Date with hour, minutes, seconds and milliseconds set to 0
	 * @param year
	 * @param month januar is 1, ...
	 * @param day 
	 * @return
	 */
	public static Date createDate(int year, int month, int day) {
		 
		Calendar calendar = GregorianCalendar.getInstance();
		
		calendar.set(year, month-1, day, 0, 0, 0);
		calendar.set( Calendar.MILLISECOND,0);
		
		return calendar.getTime();
	}
	
	/**
	 * Create a Date with hour, minutes, seconds and milliseconds set to 0
	 * @param year
	 * @param month januar is 1, ...
	 * @param day
	 * @return
	 */
	public static java.sql.Date createSqlDate(int year, int month, int day) {
		
		Calendar calendar = GregorianCalendar.getInstance();
		
		calendar.set(year, month-1, day, 0, 0, 0);
		calendar.set( Calendar.MILLISECOND,0);
		
		return new java.sql.Date(calendar.getTimeInMillis());
	}

}
