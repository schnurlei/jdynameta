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
package de.jdynameta.testcommon.util;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Util Class to create a Date 
 * @author Rainer
 * Copyright 2004 Rainer Schneider, Schiessen All rights reserved.
 */
public class DateCreator
{

	
	 public static Date createDate(int year, int month, int day)
	 {
	 	
	 	Calendar calendar = GregorianCalendar.getInstance();
	 	calendar.set( year,  month,  day);
		return new  Date(calendar.getTimeInMillis()); 	
	 }
	
}
