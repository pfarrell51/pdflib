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
import java.util.regex.Matcher;
import org.apache.log4j.Logger;

/**
 * The <code>WaypointLocation</code> class implements the Nema WPL - waypoint location statement.
 * <pre>
 *      WPL,4917.16,N,12310.64,W,003*65
           4917.16,N    Latitude of waypoint
           12310.64,W   Longitude of waypoint
           003          Waypoint ID
 * </pre>
             When a route is active, this sentence is sent once for each
             waypoint in the route, in sequence. When all waypoints have
             been reported, GPR00 is sent in the next data set. In any
             group of sentences, only one WPL sentence, or an R00
             sentence, will be sent.

 * @author pfarrell
 * Created on Dec 29, 2010, 4:27:25 PM
 * Copyright (C) 2010 Patrick Farrell  
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
 * limitations under the License.
 */
public class WaypointLocation extends GpsStatement implements NmeaStatement {
     /** logger instance */
private static final Logger wpLog = Logger.getLogger(WaypointLocation.class);
private float latitude;
private LatitudeDirection latNS;
private float longitude;
private LongitudeDirection longEW;
private String waypointId;

/** standard constructor, takes input string */
public  WaypointLocation(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.WPL) return;
    latitude = safeParseFloat(parts[1])/100.0f;
    latNS = LatitudeDirection.valueOf(parts[2]);
    longitude = safeParseFloat(parts[3])/100.0f;
    longEW = LongitudeDirection.valueOf(parts[4]);
    waypointId = parts[5];
    Matcher m = AbstractNmeaStatement.checksumPat.matcher(waypointId);
    if (m.find()) {
        waypointId = waypointId.substring(0, m.start(1)-1);
    }
    consistant = true;
    wpLog.info(String.format("lat %f %s Long %f %s  wp: %s", latitude, latNS, longitude, longEW, waypointId ));
}

public boolean isChecksumMandatory() {
    return false;
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
     * @return the waypointId
     */
    public String getWaypointId() {
        return waypointId;
    }
}
