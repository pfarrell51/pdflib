/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps;

import com.google.common.base.Function;
import com.pfarrell.gps.enums.NmeaDataType;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pfarrell.exceptions.PibException;
import com.pfarrell.gps.statements.GPSFixData;
import com.pfarrell.gps.statements.HasSpeed;
import com.pfarrell.gps.statements.OverallSatelliteData;
import com.pfarrell.gps.statements.RecommendedMinimumNavigation;
import com.pfarrell.gps.statements.RecommendedMinimumSpecificTransit;
import com.pfarrell.gps.statements.SatellitesInView;
import com.pfarrell.gps.statements.VectorTrackGround;
import com.pfarrell.gps.statements.WaypointLocation;
import com.pfarrell.stat.Descriptives;
import com.pfarrell.utils.math.AlmostEquals;
import com.pfarrell.utils.misc.TimeUtils;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>LogAnalyzer</code> class implements code to look at a list of
 * Nmea statements and calculate useful information.
 * @author pfarrell
 * Created on Jan 1, 2011, 11:52:08 PM
 * Copyright (c) 2011, Pat Farrell. All rights reserved
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
public class LogAnalyzer {
     /** logger instance */
private static final Logger laLog = Logger.getLogger(LogAnalyzer.class);
    /** current time in stream */
private Date logTime = TimeUtils.beginingOfDates;
private final int OLD_SIZE = 3;
private Date[] oldTimes = new Date[OLD_SIZE];
private float[] oldKnots = new float[OLD_SIZE];
float currentKnots;
    /** default constructor */
public LogAnalyzer() {
}
public void analyze( List<NmeaStatement> statements) {
    Preconditions.checkNotNull(statements);
    Collections.sort(statements);
    // expected order GPRMC, followed by a GGA, possibly followed by a GPGLL and/or GPVTG.
    for (NmeaStatement statement:  statements) {
        long index = statement.getCounter();
        String tag = statement.getTag();
        NmeaDataType type = statement.getDataType();
        switch (type) {
            case GGA:
                GPSFixData gfd = (GPSFixData) statement;
                updateLogTime(gfd.getTimeOfFix());
                break;
            case GSA:
                OverallSatelliteData osd = (OverallSatelliteData) statement;
                break;
            case GSV:
                SatellitesInView siv = (SatellitesInView) statement;
                break;
            case RMB:
                RecommendedMinimumNavigation rmb = (RecommendedMinimumNavigation) statement;
                currentKnots = analyzeSpeed(rmb.getSpeedKnots());
                break;
            case RMC:
                RecommendedMinimumSpecificTransit rmc = (RecommendedMinimumSpecificTransit) statement;
                currentKnots = 0.0f;
                updateLogTime(rmc.getTimeOfFix());
                currentKnots =  analyzeSpeed(rmc.getSpeedKnots());
                break;
            case VTG:
                VectorTrackGround vtg = (VectorTrackGround) statement;
                currentKnots =  analyzeSpeed(vtg.getGroundSpeedKnots());
                break;
            case WPL:
                WaypointLocation wl = (WaypointLocation) statement;
                break;
            default:
                laLog.warn("unknown/unsupported type " + type);
        }
    }
    Predicate<NmeaStatement> filterSpeedRecords = new Predicate<NmeaStatement>() {
            public boolean apply(NmeaStatement t) {
                return (t instanceof HasSpeed);
            }
        };
    Iterable<NmeaStatement> speedyIt = Iterables.filter(statements, filterSpeedRecords);
    Function<NmeaStatement,Double> getKnots = new Function<NmeaStatement,Double>() {
            public Double apply(NmeaStatement f) {
                if (f instanceof HasSpeed) {
                    return (double) ((HasSpeed) f).getSpeedKnots();
                }
                throw new PibException("all records must be HasSpeed");
            }
        };

    Iterable<Double> speedsIt = Iterables.transform( speedyIt, getKnots);
    Collection<Double> speeds = Lists.newArrayList(speedsIt);
    Descriptives<Double> stats = new Descriptives<Double>(speeds);
    double stDev = stats.getStdev();
    System.out.printf("min: %g, max: %g, mean: %g, stdev %g \n", stats.getMinimum(), stats.getMaximum(), stats.getMean(),  stats.getStdev());
}
void updateLogTime(Date arg) {
    if (TimeUtils.getMilliSinceMidnight(arg) > TimeUtils.getMilliSinceMidnight(getLogTime()))  {
        System.arraycopy(oldTimes, 0, oldTimes, 1, OLD_SIZE - 1);
        if (TimeUtils.isDatePartReal(arg)) {
            oldTimes[0] = getLogTime();
            logTime = arg;
        } else {
            Calendar cal = new GregorianCalendar(TimeUtils.utcTZ);
            cal.setTime(arg);
            Calendar oldCal = new GregorianCalendar(TimeUtils.utcTZ);
            oldCal.setTime(getLogTime());
            cal.set(Calendar.DAY_OF_MONTH, oldCal.get(Calendar.DAY_OF_MONTH));
            cal.set(Calendar.YEAR, oldCal.get(Calendar.YEAR));
            cal.set(Calendar.MONTH, oldCal.get(Calendar.MONTH));
            logTime = cal.getTime();
        }
    }
}
float analyzeSpeed(float arg) {
    if (AlmostEquals.almostEquals(currentKnots, arg)) {
        return arg;
    } else if ( ! AlmostEquals.almostEquals(0.0f, arg)) {
        System.arraycopy(oldKnots, 0, oldKnots, 1, OLD_SIZE - 1);
        oldKnots[0] = arg;
    }
    return arg;
}
    /**
     * @return the logTime
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * @return the oldTimes
     */
    public Date[] getOldTimes() {
        return oldTimes;
    }
    public float[] getOldKnots() {
        return oldKnots;
    }
}
