/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.exceptions.PibException;
import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import org.apache.log4j.Logger;

/**
 * The <code>VectorTrackGround</code> class implements the structure and code for
 * a VTG (Vector Track over the Ground) statement.
 * Example:
 *         VTG - Track made good and ground speed
 * <pre>
 *       VTG,054.7,T,034.4,M,005.5,N,010.2,K
 *          054.7,T      True track made good
 *          034.4,M      Magnetic track made good
 *          005.5,N      Ground speed, knots
 *          010.2,K      Ground speed, Kilometers per hour
 * </pre>
 * $GPVTG,114.22,T,,M,43.44,N,80.5,K,A*03
 * @author pfarrell
 * Created on Dec 28, 2010, 12:15:56 AM
 */
public class VectorTrackGround extends GpsStatement implements NmeaStatement, HasSpeed {
     /** logger instance */
private static final Logger vtgLog = Logger.getLogger(VectorTrackGround.class);
private float trueTrackMadeGood;
private float magTrackMadeGood;
private float groundSpeedKnots;
private float groundSpeedKPH;

public VectorTrackGround(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.VTG) return;
    trueTrackMadeGood = safeParseFloat(parts[1]);
    if ( ! parts[2].equals("T")) {
        String msg = "bad, true track speed flag not T " + parts[2];
        vtgLog.error(msg);
        throw new PibException(msg);
    }
    if (parts[3]  != null && parts[3].length() > 0) {
        magTrackMadeGood = safeParseFloat(parts[3]);
    }
    if ( ! parts[4].equals("M")) {
        String msg = "bad, mag speed flag not M" + parts[4];
        vtgLog.error(msg);
        throw new PibException(msg);
    }
    groundSpeedKnots = safeParseFloat(parts[5]);
    if ( ! parts[6].equals("N")) {
        String msg = "bad, knot flag not N " + parts[6];
        vtgLog.error(msg);
        throw new PibException(msg);
    }
    groundSpeedKPH = safeParseFloat(parts[7]);
    if ( ! parts[8].equals("K")) {
        String msg = "bad, klic flag not K " + parts[8];
        vtgLog.error(msg);
        throw new PibException(msg);
    }

    if ( groundSpeedKPH > 1.0 && ! closeEnough( groundSpeedKPH, groundSpeedKnots* 1.852f)) {
        vtgLog.info(getInputString());
        vtgLog.info(String.format("gs: %f, K*1.8: %f, Knots:%f", groundSpeedKPH, groundSpeedKnots * 1.852, groundSpeedKnots));
    }
    consistant = true;
}
   /**
    * heuristically defined definition of when to speeds are close enough to accept
    * @param A one speed
    * @param B the other speed
    * @return true if they are close enough.
    */
boolean closeEnough(float A, float B) {
    if (A == 0.0f) {
        if (A == B) return true;
        else if (Math.abs(B) < 0.0001f) return true;
        else return false;
    }
    double delta = Math.abs(A - B);
    double perDelta = delta/A;
    if (A > 4.0f) {
        return perDelta < 0.015f;
    } else if (A > 1.0f) {
        return perDelta < 0.06f;
    } else {
        return perDelta < 0.1f;
    }
}
    /**
     * @return the trueTrackMadeGood
     */
    public float getTrueTrackMadeGood() {
        return trueTrackMadeGood;
    }
    /**
     * @return the magTrackMadeGood
     */
    public float getMagTrackMadeGood() {
        return magTrackMadeGood;
    }

    /**
     * @return the groundSpeedKnots
     */
    public float getGroundSpeedKnots() {
        return groundSpeedKnots;
    }
    public float getSpeedKnots() {
        return getGroundSpeedKnots();
    }

    /**
     * @return the groundSpeedKPH
     */
    public float getGroundSpeedKPH() {
        return groundSpeedKPH;
    }
public final boolean isChecksumMandatory() {
    return false;
}

}
