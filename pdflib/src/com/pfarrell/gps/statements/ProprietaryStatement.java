/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

/**
 * The <code>ProprietaryStatement</code> interface is a marker interface indicating that
 * a NmeaStatement implementation is proprietary to a some vendor.
 * @author pfarrell
 * Created on Jan 1, 2011, 1:27:00 PM
 */
public interface ProprietaryStatement {
String getVendorName();
String getVendorCode();
}
