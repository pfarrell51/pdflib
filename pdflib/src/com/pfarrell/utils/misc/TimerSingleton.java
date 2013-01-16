/*
 * TimerSingleton.java
 *
 * Created on October 7, 2006, 8:59 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.misc;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * The  <code>TimerSingleton</code>  class is a simple counter, that 
 * starts at 'now' and goes up. Use it to be sure that we are thread safe.
 *
 * @author pfarrell
 */
public class TimerSingleton {
    /** one and only one */
private static HelperSinglton theHelperSingleton;

    /** place to store count */
    private long theCount;
    
    /** Creates a new instance of TimerSingleton */
    private TimerSingleton() {
        Calendar now = Calendar.getInstance(TimeUtils.utcTZ);
        theCount = now.getTimeInMillis();
    }
    /**
     * gets the next value of the counter. If you call it frequently, it will use the incremented
     * count, but if time elapses, it uses the current time
     * @return  the next value of the timer
     */
    public static synchronized Date next() {
        return next(0);
    }
    /**
     * gets the next value of the counter. If you call it frequently, it will use the incremented
     * count, but if time elapses, it uses the current time
     * @param delay millisecond increment
     * @return the next value of the timer
     */
    public static synchronized Date next(int delay) {
        Calendar now = Calendar.getInstance(TimeUtils.utcTZ);
        long tempTime = now.getTimeInMillis() + delay;
        theHelperSingleton.theOne.theCount++;
        if ( theHelperSingleton.theOne.theCount < tempTime) {
             theHelperSingleton.theOne.theCount = tempTime;
        } 
        return new Date(theHelperSingleton.theOne.theCount);
    }
/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        static TimerSingleton theOne = new TimerSingleton();
    }    
}
