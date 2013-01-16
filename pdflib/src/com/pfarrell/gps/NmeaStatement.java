/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps;

import com.pfarrell.gps.enums.NmeaDataType;

/**
 * The <code>NmeaStatement</code> interface defines objects that are the
 * result of parsing a Nmea Statement
 * @author pfarrell
 * Created on Dec 27, 2010, 9:53:46 PM
 */
public interface NmeaStatement extends Comparable<NmeaStatement> {
boolean isNmeaStatement();
boolean checksumOK();
NmeaDataType getDataType();
boolean isGpsStatement();
boolean isConsistent();
boolean isChecksumMandatory();
long getCounter();
boolean isProprietary();
String getTag();
}
