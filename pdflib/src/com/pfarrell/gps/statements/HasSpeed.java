/*
 * Copyright (C) 2009-2011 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.gps.statements;

/**
 * The <code>HasSpeed</code> interface defines NmeaStatements that have a speed in knots field.
 * @author pfarrell
 * Created on Jan 15, 2011, 11:22:45 PM
 */
public interface HasSpeed {
float getSpeedKnots();
}
