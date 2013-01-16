/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.google.common.collect.ImmutableList;
import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>OverallSatelliteData</code> class implements the Nmea GSA statement, Overall Satellite data.
 *         GSA - GPS DOP and active satellites
 * <pre>
        GSA,A,3,04,05,,09,12,,,24,,,,,2.5,1.3,2.1*39
           A            Auto selection of 2D or 3D fix (M = manual)
           3            3D fix
           04,05...     PRNs of satellites used for fix (space for 12)
           2.5          PDOP (dilution of precision)
           1.3          Horizontal dilution of precision (HDOP)
           2.1          Vertical dilution of precision (VDOP)
             DOP is an indication of the effect of satellite geometry on
             the accuracy of the fix.
 * </pre>
 * $GPGSA,A,3,05,26,29,15,,,,,,,,,6.8,2.1,6.4*33
 * @author pfarrell
 * Created on Dec 29, 2010, 1:24:28 AM
 */
public class OverallSatelliteData extends GpsStatement implements NmeaStatement {
     /** logger instance */
private static final Logger osdLog = Logger.getLogger(OverallSatelliteData.class);
private Character autoSelection;
private Character fixDimention;
private List<Integer> prns;
/**  PDOP (dilution of precision) */
private float pdop;
private float hdop;
private float vdop;

/** usual constructor */
public  OverallSatelliteData(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    boolean problemFound = false;
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.GSA) return;
    String[] parts = getInputString().split(",");
    if (parts.length > 17) {
        autoSelection = parts[1].charAt(0);
        fixDimention = parts[2].charAt(0);
        try {
            ArrayList<Integer> prnList = new ArrayList<Integer>();
            for (int i = 0; i < 12; i++) {
                int prnNum = safeParseInt(parts[i+3]);
                if (prnNum > 0) {
                    prnList.add(prnNum);
                }
            }
            prns = ImmutableList.copyOf(prnList);
            pdop = safeParseFloat(parts[15]);
            hdop = safeParseFloat(parts[16]);
            vdop = safeParseFloat(parts[17]);
        } catch (NumberFormatException ex) {
            osdLog.error(getInputString(), ex);
            problemFound = true;
        }
    }
    consistant = !problemFound;
}
public boolean isChecksumMandatory() {
    return true;
}

    /**
     * @return the autoSelection
     */
    public Character getAutoSelection() {
        return autoSelection;
    }

    /**
     * @return the fixDimention
     */
    public Character getFixDimention() {
        return fixDimention;
    }

    /**
     * @return the prns
     */
    public List<Integer> getPrns() {
        return prns;
    }
    public int getPrnSize() {
        return prns.size();
    }
    /**
     * @return the pdop
     */
    public float getPdop() {
        return pdop;
    }

    /**
     * @return the hdop
     */
    public float getHdop() {
        return hdop;
    }

    /**
     * @return the vdop
     */
    public float getVdop() {
        return vdop;
    }
}
