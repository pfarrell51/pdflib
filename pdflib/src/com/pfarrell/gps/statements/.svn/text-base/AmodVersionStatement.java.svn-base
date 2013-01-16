/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.gps.NmeaStatement;

/**
 * The <code>AmodVersionStatement</code> class implements AMOD's version statement
 * $ADVER,3080,2.2
 * @author pfarrell
 * Created on Jan 1, 2011, 1:52:42 PM
 */
public class AmodVersionStatement  extends AmodStatement implements  NmeaStatement {
private String model;
private String version;
private String minor;

    /** usual constructor argument is input string  */
public  AmodVersionStatement(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    String[] parts = getInputString().split(",");
    if (parts[0].equals("$ADVER")) {
        model = parts[1];
        String minorparts[] = parts[2].split("\\.");
        version = minorparts[0];
        minor = minorparts[1];
        consistant = true;
    } else {
        consistant = false;
    }
}

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the minor
     */
    public String getMinor() {
        return minor;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }


}
