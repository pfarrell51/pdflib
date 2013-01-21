/*
 * Constants.java
 *
 * Created on March 3, 2007, 11:40 AM
 * 
 * License: public domain
 */

package com.pfarrell.utils.misc;

import java.util.TimeZone;

/**
 * The <code>Constants</code> class is abstract, it hold global constants for general use. 
 * These are not parameters, they are constant.
 *
 * @author pfarrell
 */
public abstract class Constants {
    /** constant for UTC */
public static final TimeZone utcTZ = TimeZone.getTimeZone("UTC");
/** system specific \n */
public static final String LineSeparator = System.getProperty("line.separator");
/** system specific \n */
public static final String DoubleLineSeparator = LineSeparator + LineSeparator;
/** http insists of CRLF  so this is CR */
public static final char CR = '\r';
/** http insists of CRLF  so this is LF */
public static final char LF = '\n';
/** http insists of CRLF  so this is CRLF */
public static final String CRLF = "\r\n";
/** http ends with two  CRLF  */
public static final String TwoCRLF = "\r\n\r\n";
}
