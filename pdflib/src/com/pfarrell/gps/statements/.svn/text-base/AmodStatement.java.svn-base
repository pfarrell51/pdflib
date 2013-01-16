/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import org.apache.log4j.Logger;

/**
 * The <code>AmodStatement</code> class implements a common base for all proprietary
 * statements from <a href="http://www.amod.com.tw/">AMOD</a> data loggers
 * @author pfarrell
 * Created on Jan 1, 2011, 1:32:27 PM
 */
public class AmodStatement  extends AbstractNmeaStatement implements ProprietaryStatement {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(AmodStatement.class);

public AmodStatement(String arg) {
    super(arg);
}
    public boolean isProprietary() {
        return true;
    }

    public String getVendorName() {
        return "AMOD";
    }

    public String getVendorCode() {
        return "AD";
    }

    public boolean isChecksumMandatory() {
        return false;
    }

}
