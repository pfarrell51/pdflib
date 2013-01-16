/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */


package com.pfarrell.gps;

import com.pfarrell.gps.enums.NmeaDataType;
import com.google.common.base.Preconditions;
import com.pfarrell.gps.statements.AmodPMBStatement;
import com.pfarrell.gps.statements.AmodVersionStatement;
import com.pfarrell.gps.statements.DummyStatement;
import com.pfarrell.gps.statements.GPSFixData;
import com.pfarrell.gps.statements.OverallSatelliteData;
import com.pfarrell.gps.statements.RecommendedMinimumNavigation;
import com.pfarrell.gps.statements.RecommendedMinimumSpecificTransit;
import com.pfarrell.gps.statements.SatellitesInView;
import com.pfarrell.gps.statements.VectorTrackGround;
import com.pfarrell.gps.statements.WaypointLocation;
import com.pfarrell.gps.statements.ZDAteTimeStamp;
import org.apache.log4j.Logger;

/**
 * The <code>StatementFactory</code> class implements a simple Factory to
 * create a suitable NmeaStatement for an input line.
 * @see NmeaStatement
 * @see <a href="http://gpsd.berlios.de/NMEA.txt>Eric's guide to NMEA</a>
 * @author pfarrell
 * Created on Dec 28, 2010, 12:56:29 AM
 */
public class StatementFactory {
     /** logger instance */
private static final Logger sfLog = Logger.getLogger(StatementFactory.class);
   /**
    * main factory function, take a line from the log, return an appropriate NMEA statement.
    * @param arg input line from NMEA file
    * @return populated {@link NmeaStatement} object
    */
public static NmeaStatement makeStatement(String arg) {
    Preconditions.checkNotNull(arg);
    NmeaStatement rval = null;
    if (arg.isEmpty() ||  arg.charAt(0) != '$') return rval;
    NmeaDataType datatype = NmeaDataType.Unknown;
    if (arg.length() < 6) return rval;
    String[] parts = arg.split(",");
    if (parts.length == 0) return rval;
    if (! parts[0].startsWith("$GP")) {
        return handlePropritaryStatement(arg);
    }
    try {
        String dataChars = parts[0].substring(3);
        datatype = NmeaDataType.valueOf(dataChars);
        switch (datatype) {
            case VTG:       //Vector track an Speed over the Ground
                rval = new VectorTrackGround(arg);
                break;
            case RMB:
                rval = new RecommendedMinimumNavigation(arg);
                break;
            case RMC:
                rval = new RecommendedMinimumSpecificTransit(arg);
                break;
            case GGA:
                rval = new GPSFixData(arg);
                break;
            case GSA:
                rval = new OverallSatelliteData(arg);
                break;
            case GSV:
                rval = new SatellitesInView(arg);
                break;
            case WPL:
                rval = new WaypointLocation(arg);
                break;
            case ZDA:
                rval = new ZDAteTimeStamp(arg);
                break;
            default:
                sfLog.warn("don't support " + datatype);
                rval = new DummyStatement(arg);
        }
    } catch (IllegalArgumentException ex) {
        sfLog.warn(ex.getMessage() + " " +   arg, ex);
    }
    return rval;
}
private static NmeaStatement handlePropritaryStatement(String arg) {
    NmeaStatement rval = new DummyStatement(arg);
    if (arg.startsWith("$AD")) {
        if (arg.startsWith("$ADVER")) {
            rval = new AmodVersionStatement(arg);
        } else if (arg.startsWith("$ADPMB")) {
            rval = new AmodPMBStatement(arg);
        } else {
            sfLog.info("skipping unsupported $AD sentence " + arg);
        }
    } else {
        sfLog.info("skipping non-GPS sentence " + arg);
    }
    return rval;
}
}
