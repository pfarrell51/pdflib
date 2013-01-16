/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>SatellitesInView</code> class implements the Nmea GSV statement -- Satellites in view.
 * <pre>        GSV,2,1,08,01,40,083,46,02,17,308,41,12,07,344,39,14,22,228,45*75
           2            Number of sentences for full data
           1            sentence 1 of 2
           08           Number of satellites in view
           01           Satellite PRN number
           40           Elevation, degrees
           083          Azimuth, degrees
           46           Signal strength - higher is better
           <repeat for up to 4 satellites per sentence>
                There my be up to three GSV sentences in a data packet
 * </pre>
 *
 * @author pfarrell
 * Created on Dec 29, 2010, 3:43:50 PM
 */
public class SatellitesInView extends GpsStatement implements NmeaStatement {
     /** logger instance */
private static final Logger sivLog = Logger.getLogger(SatellitesInView.class);
private int numSents;
private int numSats;
private int statementNumber;
private List<SatInfo> satInfo = new ArrayList<SatInfo>();
/** standard constructor, takes input string */
public  SatellitesInView(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.GSV) return;
    numSents = safeParseInt(parts[1]);
    statementNumber = safeParseInt(parts[2]);
    numSats = safeParseInt(parts[3]);

    int satBlkStart = 4;
    for (int i = 0; i < Math.min( numSats, 4) && satBlkStart+3 < parts.length; i++) {
        int prn = safeParseInt(parts[satBlkStart]);
        if (prn > 0) {
            int elev = safeParseInt(parts[satBlkStart+1]);
            int az = safeParseInt(parts[satBlkStart+2]);
            int snr = safeParseInt(parts[satBlkStart+3]);
            SatInfo info = new SatInfo(prn, elev, az, snr);
            satInfo.add(info);
        }
        satBlkStart += 4;
    }

    consistant = true;
}
public boolean isChecksumMandatory() {
    return false;
}

    /**
     * @return the numSents
     */
    public int getNumSentences() {
        return numSents;
    }

    /**
     * @return the numSats
     */
    public int getNumSatellites() {
        return numSats;
    }
public int getStatementNumber() {
    return  statementNumber;
}
    /**
     * @return the satInfo
     */
    public List<SatInfo> getSatInfo() {
        return satInfo;
    }

   /**
    * simple struct to hold four values of information for each satellite
    */
public static class SatInfo {
    public final int prn;    // satellite id prn
    public final int elevation;
    public final int azimuth;
    public final int snr;    // signal strength
/** constructor */
private SatInfo(int p, int e, int a, int s) {
        prn = p;
        elevation = e;
        azimuth = a;
        snr = s;
    }
}
}
