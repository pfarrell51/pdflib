/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.enums;

/**
 * NMEA defined "data type" keywords.
 * Source <a href="http://vancouver-webpages.com/peter/nmeafaq.txt">http://vancouver-webpages.com/peter/nmeafaq.txt</a>
 * @author pfarrell
 * Created on Dec 27, 2010, 9:16:21 PM
 */
public enum NmeaDataType {
    Unknown,
    AAM, //Waypoint Arrival Alarm
    ALM, //Almanac data
    APA, //Auto Pilot A sentence
    APB, //Auto Pilot B sentence
    BOD, //Bearing Origin to Destination
    BWC, //Bearing using Great Circle route
    DTM, //Datum being used.
    GGA, //Fix information
    GLL, //Lat/Lon data
    GRS, //GPS Range Residuals
    GSA, //Overall Satellite data
    GST, //GPS Pseudorange Noise Statistics
    GSV, //Detailed Satellite data
    MSK, //send control for a beacon receiver
    MSS, //Beacon receiver status information.
    RMA, //recommended Loran data
    RMB, //recommended navigation data for gps
    RMC, //recommended minimum data for gps
    RME,        // ** unknown
    RMM,        // ** unknown
    RTE, //route message
    TRF, //Transit Fix Data
    STN, //Multiple Data ID
    VBW, //dual Ground / Water Spped
    VTG, //Vector track an Speed over the Ground
    WCV, //Waypoint closure velocity (Velocity Made Good)
    WPL, //Waypoint Location information
    XTC, //cross track error
    XTE, //measured cross track error
    ZTG, //Zulu (UTC) time and time to go (to destination)
    ZDA, //Date and Time
    //Some gps receivers with special capabilities output these special messages.
    HCHDG, //Compass output
    PSLIB, //Remote Control for a DGPS receiver

}
