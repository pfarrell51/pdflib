/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.enums.LatitudeDirection;
import com.pfarrell.gps.enums.LongitudeDirection;
import com.pfarrell.gps.NmeaStatement;
import com.pfarrell.gps.enums.ActiveWarning;
import com.pfarrell.utils.misc.TimeUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * The <code>RecommendedMinimumSpecificTransit</code> class implements the RMC statement.
 *         RMC - Recommended minimum specific GPS/Transit data
   <pre>     RMC,225446,A,4916.45,N,12311.12,W,000.5,054.7,191194,020.3,E*68
           225446       Time of fix 22:54:46 UTC
           A            Navigation receiver warning A = OK, V = warning
           4916.45,N    Latitude 49 deg. 16.45 min North
           12311.12,W   Longitude 123 deg. 11.12 min West
           000.5        Speed over ground, Knots
           054.7        Course Made Good, True
           191194       Date of fix  19 November 1994
           020.3,E      Magnetic variation 20.3 deg East
           *68          mandatory checksum
 * </pre>
 * $GPRMC,200157.000,A,3853.8706,N,07710.6801,W,41.47,113.95,271210,,,E*4D
 * @author pfarrell
 * Created on Dec 28, 2010, 12:20:57 AM
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
public class RecommendedMinimumSpecificTransit extends GpsStatement implements NmeaStatement, HasSpeed {
     /** logger instance */
private static final Logger rmcLog = Logger.getLogger(RecommendedMinimumSpecificTransit.class);
private Date timeOfFix;
private ActiveWarning navigationWarning;
private float latitude;
private LatitudeDirection latNS;
private float longitude;
private LongitudeDirection longEW;
private float speedKnots;
private float course;
private float magVariation;
private LongitudeDirection magVarEW;

public RecommendedMinimumSpecificTransit (String arg) {
    super(arg);
    parseParts();
}
private static SimpleDateFormat fractions = new SimpleDateFormat("ddMMyy HHmmss.SS Z");
private static SimpleDateFormat whole     = new SimpleDateFormat("ddMMyy HHmmss Z");
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.RMC) return;
    //timeOfFix is [1]
    navigationWarning = ActiveWarning.valueOf(parts[2]);
    latitude = safeParseFloat(parts[3])/100.0f;
    latNS = LatitudeDirection.valueOf(parts[4]);
    longitude = safeParseFloat(parts[5])/100.0f;
    longEW = LongitudeDirection.valueOf(parts[6]);
    speedKnots = safeParseFloat(parts[7]);
    course = safeParseFloat(parts[8]);
    try {
        int idx = parts[1].indexOf(".");
        String dateTime = String.format("%s %s UTC", parts[9], parts[1]);
        SimpleDateFormat dfmt = idx < 0 ? whole : fractions;
        dfmt.setTimeZone(TimeUtils.utcTZ);
        timeOfFix = dfmt.parse(dateTime);
    } catch (ParseException ex) {
        rmcLog.error(ex);
    }
    magVariation = parts[10].length() > 0 ? safeParseFloat(parts[10]) : 0.0f;
    String magV = parts[11].length() >= 4 ? parts[11].substring(0,1) : "W";
    magVarEW = LongitudeDirection.valueOf(magV);
    consistant = true;
}
    /**
     * @return the timeOfFix
     */
    public Date getTimeOfFix() {
        return timeOfFix;
    }
    /**
     * @return the navigationWarning
     */
    public ActiveWarning getNavigationWarning() {
        return navigationWarning;
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
     * @return the speedKnots
     */
    public float getSpeedKnots() {
        return speedKnots;
    }
    /**
     * @return the course
     */
    public float getCourse() {
        return course;
    }
    /**
     * @return the magVariation
     */
    public float getMagVariation() {
        return magVariation;
    }
    /**
     * @return the magVarEW
     */
public LongitudeDirection getMagVarEW() {
    return magVarEW;
}
public final boolean isChecksumMandatory() {
    return true;
}
    @Override
public int compareTo(NmeaStatement arg) {
    Preconditions.checkNotNull(arg);
    if ( ! (arg instanceof RecommendedMinimumSpecificTransit)) return super.compareTo(arg);
    RecommendedMinimumSpecificTransit that = (RecommendedMinimumSpecificTransit) arg;
    return ComparisonChain.start()
         .compare(this.getCounter(), arg.getCounter())
         .compare(this.getTag(), arg.getTag())
         .compare(this.getTimeOfFix(), that.getTimeOfFix())
         .result();
}
}
