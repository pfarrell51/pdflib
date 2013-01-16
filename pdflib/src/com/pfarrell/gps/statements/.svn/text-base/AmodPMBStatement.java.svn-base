/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import org.apache.log4j.Logger;

/**
 * The <code>AmodPMBStatement</code> class implements the AMOD "PMB" statement
 * $ADPMB,5,0
 * @author pfarrell
 * Created on Jan 1, 2011, 2:31:04 PM
 */
public class AmodPMBStatement extends AmodStatement {
         /** logger instance */
private static final Logger asLog = Logger.getLogger(AmodPMBStatement.class);
private String first;
private String second;

public AmodPMBStatement(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    String[] parts = getInputString().split(",");
    if (parts[0].equals("$ADPMB")) {
        first = parts[1];
        second = parts[2];
        consistant = true;
        asLog.info(String.format("%s: %s, %s", getTag(), first, second));
    } else {
        consistant = false;
    }
}

    /**
     * @return the first
     */
    public String getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public String getSecond() {
        return second;
    }
}
