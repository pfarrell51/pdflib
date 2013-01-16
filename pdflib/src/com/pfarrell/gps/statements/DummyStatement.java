/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.gps.NmeaStatement;

/**
 * The <code>DummyStatement</code> class implements a dummy  NMEA statement that we can
 * use as a placeholder.
 * @author pfarrell
 * Created on Dec 29, 2010, 12:13:06 AM
 */
public class DummyStatement extends AbstractNmeaStatement implements NmeaStatement {

public DummyStatement(String arg) {
    super(arg);
}
public boolean isChecksumMandatory() {
    return false;
}
public boolean isProprietary() {
    return false;
}
}
