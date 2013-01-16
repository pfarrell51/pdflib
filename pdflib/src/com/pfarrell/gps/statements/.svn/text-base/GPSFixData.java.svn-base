/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */
package com.pfarrell.gps.statements;

import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import com.pfarrell.gps.enums.LatitudeDirection;
import com.pfarrell.gps.enums.LongitudeDirection;
import com.pfarrell.utils.misc.TimeUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * The <code>GPSFixData</code> class implements the GGA statment, with information
 * on the fix (position fix)
 *
 *         GGA - Global Positioning System Fix Data
 * <pre>
        GGA,123519,4807.038,N,01131.324,E,1,08,0.9,545.4,M,46.9,M, , *42
           123519       Fix taken at 12:35:19 UTC
           4807.038,N   Latitude 48 deg 07.038' N
           01131.324,E  Longitude 11 deg 31.324' E
           1            Fix quality: 0 = invalid
                                     1 = GPS fix
                                     2 = DGPS fix
                                     3=PPS (Precise Position Service),
                                     4=RTK (Real Time Kinematic) with fixed integers,
                                     5=Float RTK, 6=Estimated, 7=Manual, 8=Simulator
           08           Number of satellites being tracked
           0.9          Horizontal dilution of position
           545.4,M      Altitude, Metres, above mean sea level
           46.9,M       Height of geoid (mean sea level) above WGS84
                        ellipsoid
           (empty field)  Age of differential GPS data, time in seconds since last DGPS update
           (empty field) Differential reference station ID, 0000-1023
 * </pre>
 * @author pfarrell
 * Created on Dec 29, 2010, 12:32:31 AM
 */
public class GPSFixData extends GpsStatement implements NmeaStatement {
     /** logger instance */
private static final Logger gfdLog = Logger.getLogger(GPSFixData.class);
private Date timeOfFix;
private float latitude;
private LatitudeDirection latNS;
private float longitude;
private LongitudeDirection longEW;
private int fixQuality;
private int numSats;
private float dilution;
private float altitude;
private Character meterAlt;
private float heightGeoid;
private Character meterHeight;

/** standard constructor, takes one line of input
 * @param arg input line
 */
public GPSFixData(String arg) {
    super(arg);
    parseParts();
}
private static SimpleDateFormat whole      = new SimpleDateFormat("HHmmss");
private static SimpleDateFormat fractions  = new SimpleDateFormat("HHmmss.SS");
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    boolean problemFound = false;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.GGA) return;
    int idx = parts[1].indexOf(".");
    SimpleDateFormat dfmt = idx < 0 ? whole : fractions;
    dfmt.setTimeZone(TimeUtils.utcTZ);
    try {
        timeOfFix = dfmt.parse(parts[1]);
        latitude = safeParseFloat(parts[2])/100.0f;
        latNS = LatitudeDirection.valueOf(parts[3]);
        longitude = safeParseFloat(parts[4])/100.0f;
        longEW = LongitudeDirection.valueOf(parts[5]);
        fixQuality = safeParseInt(parts[6]);
        numSats = safeParseInt(parts[7]);
        dilution = safeParseFloat(parts[8]);
        altitude = safeParseFloat(parts[9]);
        meterAlt = parts[10].charAt(0);
        heightGeoid = safeParseFloat(parts[11]);
        meterHeight = parts[12].charAt(0);
    } catch (ParseException ex) {
        gfdLog.error(getInputString(), ex);
        problemFound = true;
    }
    consistant = !problemFound;
}
/**
 * gets if the checksum is mandatory for this sentence
 * @return true if checksum is mandatory
 */
public boolean isChecksumMandatory() {
    return true;
}

    /**
     * @return the timeOfFix
     */
    public Date getTimeOfFix() {
        return timeOfFix;
    }

    /**
     * @return the latitude
     */
    public float getLatitude() {
        return latitude;
    }

    /**
     * @return the latNS
     */
    public LatitudeDirection getLatNS() {
        return latNS;
    }

    /**
     * @return the longitude
     */
    public float getLongitude() {
        return longitude;
    }

    /**
     * @return the longEW
     */
    public LongitudeDirection getLongEW() {
        return longEW;
    }

    /**
     * @return the fixQuality
     */
    public int getFixQuality() {
        return fixQuality;
    }

    /**
     * @return the numSats
     */
    public int getNumSats() {
        return numSats;
    }

    /**
     * @return the dilution
     */
    public float getDilution() {
        return dilution;
    }

    /**
     * @return the altitude
     */
    public float getAltitude() {
        return altitude;
    }

    /**
     * @return the meterAlt
     */
    public Character getMeterAlt() {
        return meterAlt;
    }

    /**
     * @return the heightGeoid
     */
    public float getHeightGeoid() {
        return heightGeoid;
    }

    /**
     * @return the meterHeight
     */
    public Character getMeterHeight() {
        return meterHeight;
    }

}
