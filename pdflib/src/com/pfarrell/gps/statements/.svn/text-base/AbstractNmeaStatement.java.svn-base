/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */


package com.pfarrell.gps.statements;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Longs;
import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import com.pfarrell.gps.enums.NmeaTalkerId;
import com.pfarrell.utils.misc.CounterSingleton;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractNmeaStatement</code> class implements an abstract base class for
 * NemaStatement objects.
 * @see com.pfarrell.gps.NmeaStatement
 * @see <a href="http://gpsd.berlios.de/NMEA.txt>Eric's guide to NMEA</a>
 * @author pfarrell
 *
 * Created on Dec 27, 2010, 9:58:51 PM
 */
public abstract class AbstractNmeaStatement implements NmeaStatement {
     /** logger instance */
private static final Logger ansLog = Logger.getLogger(AbstractNmeaStatement.class);

private final String inputString;
private final String tag;         // first word of sentence, starts with $, example $GPVTG
private final boolean isValidated;
private final long counter;
protected boolean consistant = false;
protected static Pattern checksumPat = Pattern.compile("\\*(\\p{XDigit}\\p{XDigit})$");;
    /** constructor, takes a string argument that is the statement text */
protected AbstractNmeaStatement(String is) {
    inputString = is;
    isValidated = isValidatedNmeaStatement();
    int idx = is.indexOf(",");
    if (idx < 0) {
        tag = "";
    } else {
        tag = is.substring(0, idx);
    }
    counter = CounterSingleton.next();
}
   /**
    * gets true if this is a GPS statement (compared to Lorance-C or other type device)
    * @return true if this is a GPS statement.
    */
public boolean isGpsStatement() {
    if ( ! isValidated ) return false;
    String deviceCode = getInputString().substring(1,3);
    NmeaTalkerId deviceType = NmeaTalkerId.valueOf(deviceCode);
    return deviceType == NmeaTalkerId.GP;
}
   /**
    * gets first level validation, is this a possible NMEA statement.
    * @return true if this line is a well formed statement.
    */
public boolean isNmeaStatement() {
    return isValidated;
}
private boolean isValidatedNmeaStatement() {
    boolean rval = false;
    if (getInputString() == null || getInputString().isEmpty() || getInputString().length() < 3) return rval;
    if ( ! inputString.startsWith("$")) return rval;
    if ( getInputString().startsWith("$P")) return rval;
    if ( getInputString().startsWith("$AD")) return rval;
    String deviceCode = getInputString().substring(1,3);
    try {
        NmeaTalkerId deviceType = NmeaTalkerId.valueOf(deviceCode);
        switch (deviceType) {
            case GP:
                rval = true;
                break;
            case   LC:  //   Loran-C
            case   TR:  //   Transit SATNAV
            case   AP:  //   Autopilot (magnetic)
            case   HC:  //   Magnetic heading compass
            case   RA:  //   Radar
            case   SD:  //   Depth sounder
            case   VW:  //   Mechanical speed log
                rval = true;
                break;
            default:
                ansLog.debug("line doesn't start well " + getInputString());
        }
    } catch (IllegalArgumentException ex) {
        ansLog.error("line doesn't start well " + getInputString(), ex);
    }
    if (rval == true && isChecksumMandatory()) {
        rval = checksumOK();
    }
    return rval;
}
   /**
    * The checksum field consists of a '*' and two hex digits representing
    * an 8 bit exclusive OR of all characters between, but not including, the '$' and '*'.
    * @return true if the calculated checksum matches the value in the input line
    */
public boolean checksumOK() {
    boolean rval = false;
    Matcher m = checksumPat.matcher(getInputString());
    if (! m.find()) {
        return rval;
    }
    String justCheck = m.group(1);
    int lineCheck = Integer.parseInt(justCheck, 16);
    String working = getInputString().substring(1, getInputString().length()-3);
    int check = 0;
    byte[] asBytes = working.getBytes(Charset.forName("US-ASCII"));
    for (byte b : asBytes) {
        check ^= b;
    }
    return check == lineCheck;
}
   /**
    * gets the NmeaDataType of this statement
    * @return the NmeaDataType of this statement
    */
public NmeaDataType getDataType() {
    Preconditions.checkState(isValidated);
    if (getInputString().length() < 6) return  NmeaDataType.Unknown;
    String[] parts = getInputString().split(",");
    if (parts.length == 0) return  NmeaDataType.Unknown;
    String dataChars = parts[0].substring(3);
    NmeaDataType rval = NmeaDataType.valueOf(dataChars);
    return rval;
}
   /**
    * returns the relative record counter for this pass. Incremented by one
    * in the constructor of each and every Statement.
    * @return current counter value.
    */
public final long getCounter() {
    return counter;
}
protected float safeParseFloat(String arg) throws NumberFormatException {
    float rval = 0.0f;
    if (arg == null || arg.isEmpty()) {
        return rval;
    }
    String working = arg;
    Matcher m = checksumPat.matcher(working);
    if (m.find()) {
        working = working.substring(0,m.start(1)-1);
        if (working.isEmpty()) return rval;
    }
    rval =  Float.parseFloat(working);
    return rval;
}
public int safeParseInt(String arg) throws NumberFormatException {
    int rval = 0;
    if (arg == null || arg.isEmpty()) {
        return rval;
    }
    String working = arg;
    Matcher m = checksumPat.matcher(working);
    if (m.find()) {
        working = working.substring(0,m.start(1)-1);
        if (working.isEmpty()) return rval;
    }
    rval = Integer.parseInt(working);
    return rval;
}
   /**
    * gets measure of consistency/integrity of input line.
    * @return true if line parses properly
    */
public boolean isConsistent() {
    return consistant;
}
public int compareTo(NmeaStatement arg) {
    Preconditions.checkNotNull(arg);
    if (this == arg) return 0;
    return ComparisonChain.start()
         .compare(this.getCounter(), arg.getCounter())
         .compare(this.getTag(), arg.getTag())
         .result();
}

    @Override
public int hashCode() {
    int rval = Longs.hashCode(counter) << 13 + getInputString().hashCode();
    return rval;
}

    @Override
public boolean equals(Object obj) {
    if (obj == null) {
        return false;
    }
    if (getClass() != obj.getClass()) {
        return false;
    }
    final AbstractNmeaStatement other = (AbstractNmeaStatement) obj;
    if ((this.getInputString() == null) ? (other.getInputString() != null) : !this.inputString.equals(other.inputString)) {
        return false;
    }
    if (counter != other.counter) return false;
    return true;
}
    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the inputString
     */
    public String getInputString() {
        return inputString;
    }
}
