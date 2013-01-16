/*
 * Copyright (C) 2009-2011 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

import org.apache.log4j.Logger;

/**
 * The <code>FloatDegrees</code> class implements utility functions to convert
 * degree to hour:minute:second
 * @author pfarrell
 * Created on Nov 16, 2010, 11:19:07 PM
 */
public abstract class FloatDegrees {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(FloatDegrees.class);

public static HMS getHMS(double arg) {
    double working = Math.abs(arg);
    int hour = (int) Math.floor(working);
    double minF =  (working - hour) *60.0;
    int mins = (int) minF;
    int secs = (int) ((minF % 1.0)* 60);
    HMS rval = new HMS(hour, mins, secs);
    return rval;
}
public static float getDegrees(HMS arg) {
    float rval = arg.hour*60.0f;
    rval += arg.minute;
    rval += arg.second/60.0f;
    return rval/60.0f;
}
public static class HMS {
    public final int hour;
    public final int minute;
    public final int second;
    HMS( int h, int m, int s) {
        hour = h;
        minute = m;
        second = s;
    }
}
}
