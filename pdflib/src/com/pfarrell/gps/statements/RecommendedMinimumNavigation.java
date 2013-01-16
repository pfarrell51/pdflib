/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import com.pfarrell.gps.enums.ActiveWarning;
import com.pfarrell.gps.enums.LatitudeDirection;
import com.pfarrell.gps.enums.LongitudeDirection;
import org.apache.log4j.Logger;

/**
 * The <code>RecommendedMinimumNavigation</code> class implements the RMB statement,
 * Recommended minimum navigation information (sent by nav.
                receiver when a destination waypoint is active)
 * <pre>
        RMB,A,0.66,L,003,004,4917.24,N,12309.57,W,001.3,052.5,000.5,V*0B
           A            Data status A = OK, V = warning
           0.66,L       Cross-track error (nautical miles, 9.9 max.),
                                steer Left to correct (or R = right)
           003          Origin waypoint ID
           004          Destination waypoint ID
           4917.24,N    Destination waypoint latitude 49 deg. 17.24 min. N
           12309.57,W   Destination waypoint longitude 123 deg. 09.57 min. W
           001.3        Range to destination, nautical miles
           052.5        True bearing to destination
           000.5        Velocity towards destination, knots
           V            Arrival alarm  A = arrived, V = not arrived
           *0B          mandatory checksum
 * </pre>
 * @author pfarrell
 * Created on Dec 28, 2010, 12:46:09 AM
 */
public class RecommendedMinimumNavigation extends GpsStatement implements NmeaStatement, HasSpeed {
     /** logger instance */
private static final Logger rmnLog = Logger.getLogger(RecommendedMinimumNavigation.class);
private ActiveWarning dataStatus;
private float crossTrackError;
private String crossTrackDirection;
private String originWaypointId;
private String destinationWaypointId;
private float latitude;
private LatitudeDirection latNS;
private float longitude;
private LongitudeDirection longEW;
private float range;
private float trueBearing;
private float velocityKnots;
private boolean arrived;

/**
 * standard constructor, takes argument of the input line to parse.
 * @param arg the line to parse
 */
public RecommendedMinimumNavigation(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.RMB) return;
    dataStatus = ActiveWarning.valueOf(parts[1]);
    crossTrackError = safeParseFloat(parts[2]);
    crossTrackDirection = parts[3];
    originWaypointId = parts[4];
    destinationWaypointId = parts[5];
    latitude =  safeParseFloat(parts[6])/100.0f;
    latNS = LatitudeDirection.valueOf(parts[7]);
    longitude = safeParseFloat(parts[8])/100.0f;
    longEW = LongitudeDirection.valueOf(parts[9]);
    range = safeParseFloat(parts[10]);
    trueBearing = safeParseFloat(parts[11]);
    velocityKnots = safeParseFloat(parts[12]);
    arrived = ActiveWarning.valueOf(parts[13].substring(0,1)) == ActiveWarning.A;
    consistant = true;
}
    /**
     * @return the dataStatus
     */
    public ActiveWarning getDataStatus() {
        return dataStatus;
    }
    /**
     * gets Data status A = OK, V = warning
     * @return true if Data status A = OK
     */
    public boolean isDataOK() {
        return dataStatus == ActiveWarning.A;
    }

    /**
     * @param dataStatus the dataStatus to set
     */
    public void setDataStatus(ActiveWarning dataStatus) {
        this.dataStatus = dataStatus;
    }

    /**
     * @return the crossTrackError
     */
    public float getCrossTrackError() {
        return crossTrackError;
    }

    /**
     * @param crossTrackError the crossTrackError to set
     */
    public void setCrossTrackError(float crossTrackError) {
        this.crossTrackError = crossTrackError;
    }

    /**
     * @return the crossTrackDirection
     */
    public String getCrossTrackDirection() {
        return crossTrackDirection;
    }
    /**
     * @return the originWaypointId
     */
    public String getOriginWaypointId() {
        return originWaypointId;
    }
    /**
     * @return the destinationWaypointId
     */
    public String getDestinationWaypointId() {
        return destinationWaypointId;
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
     * @return the range
     */
    public float getRange() {
        return range;
    }
    /**
     * @return the trueBearing
     */
    public float getTrueBearing() {
        return trueBearing;
    }
    public float getSpeedKnots() {
        return velocityKnots;
    }
    /**
     * @return the arrived
     */
public boolean isArrived() {
    return arrived;
}
/**
 * tells if Checksum is Mandatory()
 * @return true if checksum is mandatory
 */
public final boolean isChecksumMandatory() {
    return true;
}

}
