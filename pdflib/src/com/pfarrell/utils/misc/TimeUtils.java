package com.pfarrell.utils.misc;

/*
 * @(#)TimeUtils.java	0.51 1999/07/14
 *
 * Copyright (c) 2004, Pat Farrell, All rights reserved.
 * based on work Copyright (c) 2000, OneBigCD, Inc.  All rights reserved.
 * Copyright (C) 2011 Patrick Farrell   All Rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * It is abstract because all functions are static
 */

import com.google.common.base.Preconditions;
import com.pfarrell.exceptions.PibException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
 
/**
 * the <tt>TimeUtils</tt> class is a handy place to hold
 * utility functions relating to time, dates, calendars, etc.
 * It is abstract because all functions are static
 * 
 * @author  Pat Farrell
 * @version 0.51, 2000/07/07
 */
public abstract class TimeUtils {
         /** logger instance */
private static final Logger tuLog = Logger.getLogger(TimeUtils.class);
    /** static beginning of time */
public static final java.util.Date beginingOfDates = new java.util.Date(0);  //January 1, 1970 00:00:00.000 GMT.
/** static beginning of time in timestamp format */
public static final Timestamp beginingOfTime = new  Timestamp(0);
/** convert getTime to years. 1 year = 31 556 926 seconds */
public static final long SecondsPerYear = TimeUnit.DAYS.toSeconds(365);
/** constant to convert milliseconds to years */
public static final long MagicDateToAge = 1000*SecondsPerYear;
/** fixed format for RFC822 date fields */
public static final SimpleDateFormat rfc822format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
/**
 *  A  date far enough into the future that all reasonable dates are
 * less that this.
 */
public static final Timestamp FarFutureTime = makeFarFutureDate("49991221235959");
/** constant for UTC */
public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
/** standard RFC 822 date  */
private static final SimpleDateFormat RFC822DateFormat = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
/**
 * get the data that is the beginning of the Unix/linux world,
 * i.e. January 1, 1970 00:00:00.000 GMT.
 * @return a calendar initialized to long ago
 */
public static Calendar getBeginingOfCalendar() {
    Calendar rval = GregorianCalendar.getInstance(utcTZ);
    rval.setTimeInMillis(0);
    return rval;
}
private static class RegexToDateFormat {
    final Pattern pat;
    final String dateFormat;
    private RegexToDateFormat(String p, String d){
        pat = Pattern.compile(p);
        dateFormat = d;
    }
}
/**
 * array of regex and date formats to try when dealing with generic input
 */
  private static final RegexToDateFormat ansiFormat = new RegexToDateFormat("^\\d{8}$", "yyyyMMdd");

  private static RegexToDateFormat[] formats = new RegexToDateFormat[] {
        new RegexToDateFormat("\\d+/\\d+/\\d+", "MM/dd/yy"),
        new RegexToDateFormat("\\d+/\\d+/\\d+", "dd/MM/yy"),
        new RegexToDateFormat("\\p{Alpha}+\\s+\\d+\\s+\\d+", "MMM dd yy"),
        new RegexToDateFormat("\\d+\\s+\\p{Alpha}+\\s+\\d+", "dd MMM yy"),
        new RegexToDateFormat("\\d+-\\p{Alpha}+-\\d+", "dd-MMM-yy"),
        new RegexToDateFormat("\\d{4}-\\d{2}-\\d{2}", "yyyy-MM-dd"),
        new RegexToDateFormat("\\d+-\\d+-\\d+", "MM-dd-yy"),
        new RegexToDateFormat("\\d+-\\d+-\\d+ \\d+:\\d+:\\d+", "MM-dd-yy HH:mm:ss"),
        new RegexToDateFormat("\\d+-\\d+-\\d+", "dd-MM-yy"),
        new RegexToDateFormat("\\p{Alpha}+,\\s+\\d+\\s+\\p{Alpha}+\\s+\\d+\\d+\\s+\\d+:\\d+:\\d+\\s+-??\\d+", "EEE, d MMM yyyy HH:mm:ss Z"),
        ansiFormat,
    };


  /**
   * parse a string into a Date based on the given regular exp, and dateformat
   * @param datestr input string to parse
   * @param regex regular expression to match input against
   * @param dateformat SimpleDateFormat pattern to use to interpret datestr
   * @return parsed date or null if no match or parsing error
   */
  @SuppressWarnings("empty-statement")
public static Date string2Date(String datestr, Pattern regex, String dateformat) {
    Preconditions.checkNotNull(datestr);
    Date rval = null;
    try {
        Matcher m = regex.matcher(datestr.trim());
        if (m.matches()) {
            DateFormat df = new SimpleDateFormat(dateformat);
            df.setLenient(false);
            Date date = df.parse(datestr);
            rval = date;
        }
    } catch (ParseException ex) {};
    return rval;
}
  /**
   * parse a string into a valid Date when fed an ANSI format date (yyyyMMdd), if necessary, change
   * fields until date is valid
   * @param datestr an ANSI string
   * @return valid date
   */
public static Date parseAnsiString(String datestr) {
    Preconditions.checkNotNull(datestr);
    Date result = beginingOfDates;
    if (datestr.length() >= 8) {
        result = string2Date(datestr, ansiFormat.pat, ansiFormat.dateFormat);
        if (result == null || result.compareTo(beginingOfDates) == 0) {
            String working = datestr.substring(0,6) + "01";
            result =  TimeUtils.string2Date(working);
            if (result == null || result.compareTo(beginingOfDates) == 0) {
                working = datestr.substring(0,4) + "0101";
                result = TimeUtils.string2Date(working);
                if (result == null) {
                    result = beginingOfDates;
                }            
            }
        }
    }
    return result;
}
  /**
   * parse a string into a Date
   * @param datestr input string to parse
   * @return  parsed date or null if no match or parsing error
   */
  public static Date string2Date(String datestr) {
        Date result = null;
        for (RegexToDateFormat format : formats) {
            if ((result = string2Date(datestr, format.pat, format.dateFormat)) != null) {
                break;
            }
        }
        return result;
    }

/**
 * internal routine to convert timestamp to number of milliseconds rounded
 * @param arg incoming timestamp
 * @param seconds seconds to round to (1 gives seconds, 60 gives minues, etc.)
 * @return milliseconds rounded.
 */
private static long roundToSecs(Timestamp arg, int seconds) {
    long restored = 0;
    if ( arg != null) {
        Timestamp odark = startOfDay(arg);
        long delta = arg.getTime() -odark.getTime();
        long rounded = Math.round((delta)/(1000.0*seconds));
        restored =  odark.getTime() + rounded*(1000*seconds);
    }
    return restored;
}
/**
     * date tester, useful for test case delays
     * @param arg string in yymmdd format
     * @return true if today is later than arg, false otherwise
     */
public static boolean isTodayLaterThan(String arg) {
    boolean rval = false;
    if (arg == null || arg.length() != 6) return rval;
    try {
        SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyMMdd");
        Calendar gc = GregorianCalendar.getInstance(utcTZ);
        gc.setTime(utcDateFormat.parse(arg));
        Calendar rightNow = GregorianCalendar.getInstance(utcTZ);
        tuLog.debug(String.format("Str:%s  in: %tc now: %tc ? ", arg, gc, rightNow));
        rval = (gc.compareTo(rightNow) < 0 );
        tuLog.debug(rval);
    } catch (Exception ex) {
        tuLog.error(ex);
    }    
    return rval;
}
   /**
    * test argument date to see if date part (year, month, day) is not the default
    * @param arg date to test
    * @return true if date is after day 1, false if equal to the default date.
    */
public static boolean isDatePartReal(Date arg) {
    Preconditions.checkNotNull(arg);
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(arg);
    boolean rval = cal.get(Calendar.YEAR) != 1970;
    rval &= cal.get(Calendar.MONTH) != 0;
    rval &= cal.get(Calendar.DAY_OF_MONTH) != 1;
    return rval;
}
   /**
    * gets the number of milliseconds since Midnight of the current day (UTC) of the argument
    * @param arg a date
    * @return the number of milliseconds since Midnight of the current day (UTC) of the argument
    */
public static int getMilliSinceMidnight(Date arg) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(arg);
    return getMilliSinceMidnight(cal);
}
   /**
    * gets the number of milliseconds since Midnight of the current day (UTC) of the argument
    * @param arg a calendar
    * @return the number of milliseconds since Midnight of the current day (UTC) of the argument
    */
public static int getMilliSinceMidnight(Calendar arg) {
    int millis = arg.get(Calendar.HOUR_OF_DAY)*60*60*1000;
    millis = millis + arg.get(Calendar.MINUTE)*60*1000;
    millis = millis + arg.get(Calendar.SECOND)*1000;
    millis = millis + arg.get(Calendar.MILLISECOND);
    return millis;
}
/**
 * rounds the input value to the nearest whole minute
 * @param arg input Timestamp
 * @return rounds the input value to the nearest whole minute
 */
public static Timestamp roundToSecond(Timestamp arg) {
   return new Timestamp(roundToSecs(arg, 1));
}
/**
 * rounds the input value to the nearest whole minute
 * @param arg input Timestamp
 * @return the input value to the nearest whole minute
 */
public static Timestamp roundToMinute(Timestamp arg) {
   return new Timestamp(roundToSecs(arg, 60));
}
/**
 * rounds the input value to the nearest whole hour
 * @param arg input Timestamp
 * @return the input value to the nearest whole hour
 */
public static Timestamp roundToHour(Timestamp arg) {
   return new Timestamp(roundToSecs(arg, 60*60));
}
/**
 * rounds the input value to the nearest four  hour value
 * @param arg input Timestamp
 * @return the input value to the nearest four hour
 */
public static Timestamp roundToFourHour(Timestamp arg) {
   return new Timestamp(roundToSecs(arg, 60*60*4));
}
/**
 * rounds the input value to the nearest two hour
 * @param arg input Timestamp
 * @return the input value to the nearest two hour
 */
public static Timestamp roundToTwoHour(Timestamp arg) {
   return new Timestamp(roundToSecs(arg, 60*60*2));
}

/**
 * create a quoted SQL-friendly datetime string from a Java Calendar
 * @param dd the Calendar to use
 * @see java.util.Date 
 * @return a String version of the date that SQL will like
 */
    public static String toQuotedSQLDateTimeFormat(Calendar dd) {    
        return toQuotedSQLDateTimeFormat(dd.getTime());
    }
/**
 * create a quoted SQL-friendly datetime string from a Java Date
 * @param dd the java.util.Date 
 * @see java.util.Date 
 * @return a String version of the date that SQL will like
 */
    public static String toQuotedSQLDateTimeFormat(Date dd) {
    if (dd == null) return null;
    String rval = "'" + toSQLDateTimeFormat(dd) +"'";
    return rval;
}
 /**
  * create a SQL-friendly date string from a Java Date
  * @param dd the java.util.Date 
  * @see java.util.Date 
  * @return a String version of the date that SQL will like
  */
      public static String toSQLDateFormat(Date dd) {
         if (dd == null) return null;
         Calendar cal = new GregorianCalendar(utcTZ);
         cal.setTime(dd);
         return toSQLDateFormat(cal);
     }
 /**
  * create a quoted SQL-friendly date string from a Java Date
  * @param dd the java.util.Date 
  * @see java.util.Date 
  * @return a String version of the date that SQL will like
  */
      public static String toQuotedSQLDateFormat(Date dd) {
         if (dd == null) return null;
         Calendar cal = new GregorianCalendar(utcTZ);
         cal.setTime(dd);
         return "'" + toSQLDateFormat(cal) +"'";
     }

/**
 * create a SQL-friendly datetime string from a Java Calendar
 * @see java.util.Calendar
 * @param cal input calendar
 * @return a String version of the date that SQL will like
 */
 public static String toSQLDateTimeFormat(Calendar cal) {
    if (cal == null) return null;
    return toSQLDateTimeFormat(cal.getTime());
}
 /**
 * create a SQL-friendly datetime string from a Java Date
 * @param dd the java.util.Date
 * @see java.util.Date
 * @return a String version of the date that SQL will like
 */
    public static String toSQLDateTimeFormat(Date dd) {
    if (dd == null) return null;
    SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    utcDateFormat.setTimeZone(utcTZ);
    return utcDateFormat.format(dd);
}
/**
 * create a SQL-friendly date string from a Java Calendar
 * @see java.util.Calendar
 * @return a String version of the date that SQL will like
 * @param cal input calendar
 */
  public static String toSQLDateFormat(Calendar cal) {
    if (cal == null) return null;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    fmt.setTimeZone(utcTZ);
    return fmt.format(cal.getTime());
 }
  /**
   * convert Date to w3c format, 
   * See <a href="http://www.w3.org/TR/NOTE-datetime">W3C format</a>
   * i.e.  1994-11-05T13:15:30Z
   * @param dd incoming Date
   * @return w3c formatted time
   */
      public static String toW3C(Date dd) {
         if (dd == null) return null;
         Calendar cal = new GregorianCalendar(utcTZ);
         cal.setTime(dd);
         return toW3C(cal);
    }
  /**
   * convert Calendar to w3c format, 
   * See <a href="http://www.w3.org/TR/NOTE-datetime">W3C format</a>
   * i.e.  1994-11-05T13:15:30Z
   * @param cal incoming Calendar
   * @return w3c formatted time
   */
  public static String toW3C(Calendar cal) {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss Z");
    fmt.setTimeZone(utcTZ);
    return fmt.format(cal.getTime());
  }
  /**
   * convert Date to Rfc822 format, 
   * See <a href="http://www.ietf.org/rfc/rfc0822.txt">Standard for ARPA Internet Text Messages</a>
   * i.e.  27 Aug 76 0932 PDT
   * @param dd incoming Date
   * @return Rfc822 formatted time
   */
public static String toRfc822(Date dd) {
    if (dd == null) return null;
    String rval = RFC822DateFormat.format(dd);
    return rval;
}
  /**
   * convert Calendar to Rfc822 format, 
   * See <a href="http://www.ietf.org/rfc/rfc0822.txt">Standard for ARPA Internet Text Messages</a>
   * i.e. 27 Aug 76 0932 PDT
   * @param cal incoming Calendar
   * @return Rfc822 formatted time
   */
public static String toRfc822(Calendar cal) {
    Date dval = cal.getTime();
    return toRfc822(dval);
}
    /** ISO 8601 date/time format */
    private static final SimpleDateFormat ISO8601FORMAT;
    static {
        ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        ISO8601FORMAT.setTimeZone(utcTZ);
    }
     /**
     * gets creation date in ISO 8601 format
     * @return string of date in ISO 8601 format
     */
    public String toISO8601(Date theDate) {
        Preconditions.checkNotNull(theDate);
        return ISO8601FORMAT.format(theDate);
    }
/**
 * convienence routine to return current time in Sql friendly format
 * @return nice Sql (and human) friendly time.
 */
public static String sqlNow() {
    Calendar now = GregorianCalendar.getInstance(utcTZ);
    return toSQLDateTimeFormat(now);
}
/**
 * convienence routine to return current time in Sql friendly format
 * @return nice Sql (and human) friendly time.
 */
public static java.sql.Date sqlDateNow() {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    java.sql.Date sqlNow = new java.sql.Date(cal.getTime().getTime());
    return sqlNow;
}
    /**
     * calculate age from a birthdate
     * @param bdate sql Birthdate
     * @return age in years
     */
public static int getAge(java.sql.Date bdate) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    java.sql.Date dateNow = new java.sql.Date(cal.getTime().getTime()+60000);
    float delta = dateNow.getTime() - bdate.getTime();
    long age = Math.round( delta/TimeUtils.MagicDateToAge);
    return (int) age;
}
/**
 * convienence routine to return current UTC time in Sql timestamp
 * @return nice Sql friendly timestamp.
 */
public static java.sql.Timestamp timestampNow() {
    Calendar now = GregorianCalendar.getInstance(utcTZ);
    java.sql.Timestamp sqlNow = new java.sql.Timestamp(now.getTimeInMillis());
    return sqlNow;
}  
/**
 * convienence routine to return same time next year in Sql format
 * @return nice Sql (and human) friendly time.
 */
public static java.sql.Date sqlYearFromNow() {
    Calendar now = GregorianCalendar.getInstance(utcTZ);
    now.add(Calendar.YEAR , 1) ;
    java.sql.Date sqlNow = new java.sql.Date(now.getTime().getTime());
    return sqlNow;
}
/**
 * convienence routine to return same time next year in Sql format
 * @return nice Sql (and human) friendly time.
 */
public static java.sql.Timestamp timestampYearFromNow() {
    Calendar now = GregorianCalendar.getInstance(utcTZ);
    now.add(Calendar.YEAR , 1) ;
    java.sql.Timestamp sqlNow = new java.sql.Timestamp(now.getTime().getTime());
    return sqlNow;
}
/**
 * convienence routine to return current time plus argument amount
 * @param parts time unit from java.util.Calendar, should be hours, or
 * days or some unit that makes sense
 * @param value amount of time (in units of parts) to add (negative is OK)
 * @return Date representation of time before or after now.
 */
public static java.sql.Date nowPlus(int parts, int value) {
    Calendar now = GregorianCalendar.getInstance(utcTZ);
    now.add(parts, value);
    java.sql.Date sqlNow = new java.sql.Date(now.getTime().getTime());
    return sqlNow;    
}
/**
 * covert Date to end of day, 23:59:59 leaving date part
 * untouched
 * @param dd a date to use
 * @return  same date, last minute of last second
 */
public static java.sql.Date endOfDay(java.util.Date dd) {
    return endOfDay(new java.sql.Date(dd.getTime()));
}
/**
 * covert Timestamp to end of day, 23:59:59 leaving date part
 * untouched
 * @param dd a date to use
 * @return  same date, last minute of last second
 */
public static java.sql.Timestamp endOfDay(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(dd);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    //cal.set(Calendar.MILLISECOND,999);
    java.sql.Timestamp rval = new java.sql.Timestamp(cal.getTime().getTime());
    return rval;
}
/**
 * covert Timestamp to end of day in month, 23:59:59 leaving date part
 * untouched
 * @param dd a date to use
 * @return  end of last day of last month
 */
public static java.sql.Timestamp endOfMonth(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(dd);
    cal =  endOfMonth(cal);
    java.sql.Timestamp rval = new java.sql.Timestamp(cal.getTime().getTime());
    return rval;
}
/**
 * covert Timestamp to end of day in month, 23:59:59 leaving date part
 * untouched
 * @param dd a date to use
 * @return  end of last day of last month
 */
public static Calendar endOfMonth(Calendar dd) {
    Calendar cal = (Calendar) dd.clone();
    int maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    cal.set(Calendar.DAY_OF_MONTH, maxDate);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    return cal;
}
/**
 * covert Timestamp to end of last day of last month, 23:59:59 leaving year part
 * untouched
 * @param dd a date to use
 * @return  end of last day of last month
 */
public static java.sql.Timestamp endOfYear(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(dd);
    cal =  endOfYear(cal);
    java.sql.Timestamp rval = new java.sql.Timestamp(cal.getTime().getTime());
    return rval;
}
/**
 * covert Calendar to end of last day of last month, 23:59:59 leaving year part
 * untouched
 * @param dd a date to use
 * @return  end of last day of last month
 */
public static Calendar endOfYear(Calendar dd) {
    Calendar cal = (Calendar) dd.clone();
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    int maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    cal.set(Calendar.DAY_OF_MONTH, maxDate);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    return cal;
}
/**
 * covert Date to end of day, 23:59:59 leaving date part
 * untouched
 * @param dd a date to use
 * @return  same date, last minute of last second
 */
public static java.sql.Date endOfDay(java.sql.Date dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(dd);
    cal.set(Calendar.HOUR_OF_DAY,23);
    cal.set(Calendar.MINUTE,59);
    cal.set(Calendar.SECOND,59);
    //cal.set(Calendar.MILLISECOND,999);
    java.sql.Date rval = new java.sql.Date(cal.getTime().getTime());
    return rval;
}
/**
 * covert Calendar to end of day, 23:59:59 leaving date part
 * untouched
 * @param cal a Calendar to use
 * @return  same date, first minute of first second
 */
public static Calendar endOfDay(Calendar cal) {
    Calendar rval = (Calendar) cal.clone();
    rval.set(Calendar.HOUR_OF_DAY,23);
    rval.set(Calendar.MINUTE,59);
    rval.set(Calendar.SECOND,59);
    // calendars don't deal with milli
    //    rval.set(Calendar.MILLISECOND,999);
    return rval;
}
/** 
 * covert Date to start of day, 00:00:00 leaving date part
 * untouched
 * @param dd a date to use
 * @return  same date, first minute of first second
 */
public static java.sql.Date startOfDay(java.sql.Date dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    if ( dd != null ) {
        cal.setTime(dd);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
    }
    java.sql.Date rval = new java.sql.Date(cal.getTime().getTime());
    return rval;
}
/** 
 * covert Timestamp to start of day, 00:00:00 leaving date part
 * untouched
 * @param dd a date to use
 * @return  same date, first minute of first second
 */
public static java.sql.Timestamp startOfDay(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    if ( dd != null ) {
        cal.setTime(dd);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
    }
    java.sql.Timestamp rval = new java.sql.Timestamp(cal.getTime().getTime());
    return rval;
}
/** 
 * covert Timestamp to start of Month, 00:00:00 leaving year and month parts
 * untouched
 * @param dd a date to use
 * @return  time of eariest time in current month
 */
public static java.sql.Timestamp startOfMonth(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    if ( dd != null) {
        cal.setTime(dd);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
    }
    java.sql.Timestamp rval = new java.sql.Timestamp(startOfMonth(cal).getTime().getTime());
    return rval;
}
/**
 * covert Calendar to start of Month, 00:00:00 leaving year and month parts
 * untouched
 * @param cal argument Calendar
 * @return  Calendar of earliest time in current month
 */
public static Calendar startOfMonth(Calendar cal) {
    Calendar rval = (Calendar) cal.clone();
    rval.set(Calendar.DAY_OF_MONTH, 1);
    rval.set(Calendar.HOUR_OF_DAY, 0);
    rval.set(Calendar.MINUTE,0);
    rval.set(Calendar.SECOND,0);
    rval.set(Calendar.MILLISECOND,0);
    return rval;
}
/** 
 * covert Timestamp to start of Month, 00:00:00 leaving year and month parts
 * untouched
 * @param dd a date to use
 * @return  Midnight January 1, current year
 */
public static java.sql.Timestamp startOfYear(java.sql.Timestamp dd) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    if ( dd != null) {
        cal.setTime(dd);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
    }
    java.sql.Timestamp rval = new java.sql.Timestamp(cal.getTime().getTime());
    return rval;
}
/** 
 * covert calendar to start of day, 00:00:00 leaving date part
 * untouched
 * @param cal a calendar to use
 * @return  same date, last minute of last second
 */
public static Calendar startOfDay(Calendar cal) {
    Calendar rval = (Calendar) cal.clone();
    rval.set(Calendar.HOUR_OF_DAY, 0);
    rval.set(Calendar.MINUTE,0);
    rval.set(Calendar.SECOND,0);
    cal.set(Calendar.MILLISECOND,0);
    cal.getTimeInMillis();
    return rval;
}
/**
 * convienence routine to return the current local date/time as a 
 * string in format YYYYhhddhhmmss
 * @return current UTC datetime in format YYYYhhddhhmmss
 */
public static String localNow() {
    Formatter fmt = new Formatter();
    fmt.format( "%tY%<tm%<td%<tH%<tM%<tS", Calendar.getInstance());
    return fmt.toString();
}
/**
 * convienence routine to return the current date/time (UTC) as a 
 * string of YYYYMMddhhmmss
 * @return current UTC datetime in format YYYYMMddhhmmss
 */
 public static String utcNow() {
    Calendar now = Calendar.getInstance(utcTZ);
    Formatter fmt = new Formatter();
    //TODO
    fmt.format( "%tY%<tm%<td%<tH%<tM%<tS", now);
    return fmt.toString();
}
/**
 * convienence routine to return the current date/time (UTC) as a 
 * string of YYYY MM dd hh:mm:ss
 * @return current UTC datetime in format YYYY MM dd hh:mm:ss
 */
 public static String utcNiceNow() {
    Calendar now = Calendar.getInstance(utcTZ);
    Formatter fmt = new Formatter();
    fmt.format("%tY-%<2tm-%<td %<tR:%<tS UTC", now);
    return fmt.toString();
}
/** 
 * gets current time in w3c format
 * <a href="http://www.w3.org/TR/NOTE-datetime">w3c format</a>
 * such as 1994-11-05T13:15:30Z
 * @return current time in w3c format
 */ 
 public static String w3cNow() {
    Calendar now = Calendar.getInstance(utcTZ);
    return toW3C(now);
 }
 /**
 * convert Date to simple string with milliseconds
 * @param arg incoing Date
 * @return  simple string with milliseconds
 */
 public static String toSimpleString(Date arg) {
    Formatter fmt = new Formatter();
    return fmt.format("%tm-%<td %<tR:%<tS.%<tL", arg).toString();     
 }
/**
 * Takes a String object representing a SQL date and parses it
 * into a GregorianCalendar. Only the SQL format DATETIMEFORMAT
 * is supported: "YYYY-MM-DD hh:mm:ss".
 * @param sql String representation of a SQL DATETIMEFORMAT date
 * @return Calendar object representing same date as param
 * @throws java.text.ParseException pass up any parsing problems
 */
public static Calendar sqlToCalendar(String sql) throws ParseException {
    if (sql == null) return null;
    SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    utcDateFormat.setTimeZone(utcTZ);
    Calendar gc = GregorianCalendar.getInstance(utcTZ);
    gc.setTime(utcDateFormat.parse(sql));
    return gc;
}

/**
 * takes three small integers and creates a java.util.Calendar
 * @param month int from 1=January to 12=December
 * @param day int day of the month
 * @param year int CE (AD) year.
 * @return a nice Calendar
 */
public static Calendar createAndValidateCalendar (int month, int day, int year) {
    if (year < 10) year = 2000+ year;
    if (year < 100) year = 1900 + year;
    Calendar cal = Calendar.getInstance();
    cal.clear();
    cal.set(year, month-1, day);
    return cal;
}

/**
 * takes three small integers and creates a java.util.Date
 * @param month int from 1=January to 12=December
 * @param day int day of the month
 * @param year int CE (AD) year.
 * @return Date of the input
 */
public static Date createAndValidateDate(int month, int day, int year) {
    Calendar cal = createAndValidateCalendar(month, day, year);
    Date uDate = cal.getTime();
    return uDate;
}
/**
     * Translates a string of the form yyyyMMddHHmmss (UTC) into asql Date, and
     * returns that date.  If validation error occurs, returns null. 
     * Processes both 8 and 12 character string lengths.
     * @param dt a String formatted yyyyMMddHHmmss (UTC)
     * @return the parsed java.util.Date
     * @see java.sql.Date
     * @throws java.text.ParseException pass up any parsing problem
     */
public static java.sql.Timestamp parseUTCDateFromString(String dt) throws ParseException {
    if (dt == null) return null;
    SimpleDateFormat utcDateFormat =  (dt.length() > 8) ? new SimpleDateFormat("yyyyMMddHHmmss") : new SimpleDateFormat("yyyyMMdd");
    java.util.Date pdate = utcDateFormat.parse(dt);
    return new java.sql.Timestamp(pdate.getTime());
}
   /**
    * parse a string formatted in RFC822 style into a date
    * @param dt input string
    * @return the resulting Date
    * @throws ParseException pass up any parsing problems
    */
public static Date parseRfc822Date(String dt) throws ParseException {
    Date rval = rfc822format.parse(dt);
    return rval;
}

private static java.sql.Timestamp makeFarFutureDate(String dt) {
    java.sql.Timestamp rval = null;
    try {
        rval = parseUTCDateFromString(dt);
    } catch (ParseException ex) {
        throw new PibException("parse failure making constant date");
    }
    return rval;
}
 /**
  * return argument date in ANSI format (YYYYmmdd)
  * @param aD input java.util.Date
  * @return string containing current ansi format date
  */
 public static String asAnsiDate(Date aD) {
    Calendar cal = GregorianCalendar.getInstance(utcTZ);
    cal.setTime(aD);
    return (aD != null ) ? asAnsiDate(cal) : "";
 }


/**
 * return current date in ANSI format (YYYYmmdd)
 * @return string containing current ansi format date
 */
 public static String nowAsAnsiDate() {
    return asAnsiDate(GregorianCalendar.getInstance(utcTZ));
 }
 
 /**
  * return argument date in ANSI format (YYYYmmdd)
  * @param when input Calendar
  * @return string containing current ansi format date
  */
 public static String asAnsiDate(Calendar when) {
    SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyyMMdd"); // hh:mm:ss");
    return  utcDateFormat.format(when.getTime());
 }
  /**
  * return argument date in ANSI format (YYYYmmddhhmmss)
  * @param when input Calendar
  * @return string containing current ansi format date
  */
 public static String asFullAnsiDate(Calendar when) {
    return  asFullAnsiDate(when.getTime());
 }
  /**
  * return argument date in ANSI format (YYYYmmddhhmmss)
  * @param when input Date
  * @return string containing current ansi format date
  */
 public static String asFullAnsiDate(Date when) {
    SimpleDateFormat sdf =  new SimpleDateFormat("yyyyMMddHHmmss");
    return  sdf.format(when);
 }
 
 /**
  * returns the month (mm) as a string from a calendar date
  * @param date calendar date
  * @return string value of month from calendar date
  */
 public static String getCalendarDayString(Calendar date) {
    return String.valueOf(date.get( Calendar.DAY_OF_MONTH ));
 }
 
 /**
  * returns the month from a calendar date
  * @param date calendar date
  * @return string value of day from calendar date
  */
 public static String getCalendarMonthString(Calendar date) {
    return String.valueOf(date.get( Calendar.MONTH ) + 1);
 }
 private static DateFormat justYear = new SimpleDateFormat("yyyy", Locale.US);
 /**
  * returns day from a calendar date
  * @param date calendar date
  * @return string value of year from calendar date
  */
 public static String getCalendarYearString(Calendar date) {
    return justYear.format(date.getTime());
}
 /**
 * returns now as a UTC date all set up with UTC as the zone
 * @return now as a UTC date all set up with UTC as the zone
 */
public static java.util.Date getNowDateUTC() {
    return GregorianCalendar.getInstance(TimeUtils.utcTZ).getTime();
}
    /**
     * format for RFC 1123 date string -- "Sun, 06 Nov 1994 08:49:37 GMT"
     */
    public final static String RFC1123_PATTERN =
        "EEE, dd MMM yyyy HH:mm:ss z";
   /**
     * DateFormat to be used to format dates
     */
    public final static DateFormat Rfc1123Format =
        new SimpleDateFormat(RFC1123_PATTERN, Locale.US);
/**
 * @return returns the current date/time in RFC 1123 format
 */
public static String getNowRFC1123() {
    Calendar aCal = GregorianCalendar.getInstance(TimeUtils.utcTZ);
    Date aDate = aCal.getTime();
    String rval = Rfc1123Format.format(aDate);
    return rval;
}
/**
 * returns the number of months difference between the argument Calendar objects.
 * If arg2 is before arg1, count will be negative.
 * This has only been tested using Gregorian Calendars
 * @param arg1 the first Calendar
 * @param arg2 the comparison Calendar
 * @return  the number of months difference between the argument dates
 */
public static int monthsDifference(Calendar arg1, Calendar arg2) {
    Preconditions.checkNotNull(arg1);
    Preconditions.checkNotNull(arg2);
    SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd hh:mm:ss");
    sdf.setTimeZone(TimeUtils.utcTZ);
    if (arg1 == arg2 || arg1.equals(arg2)) {
        return 0;
    }
    boolean negateFlag = arg1.after(arg2);
    //System.out.printf("1: %s 2: %s n: %b\n", sdf.format(arg1.getTime()), sdf.format(arg2.getTime()), negateFlag);
    Calendar working = (Calendar) arg1.clone();
    int rval = 0;
    if (negateFlag) {
        working = (Calendar) arg2.clone();
        while ( working.compareTo(arg1) < 0) {
            rval++;
            working.add(Calendar.MONTH, 1);
            if (working.compareTo(arg1) == 0) {
                break;
            } else if (working.compareTo(arg1) < 0) {
                continue;
            } else {
                break;
            }
        }
    } else {
        while ( working.compareTo(arg2) < 0) {
            rval++;
            working.add(Calendar.MONTH, 1);
            if (working.compareTo(arg2) == 0) {
                break;
            } else if (working.compareTo(arg2) < 0) {
                continue;
            } else {
                rval--;
                break;
            }
        }
    }
    return negateFlag ? -rval : rval;
}
}