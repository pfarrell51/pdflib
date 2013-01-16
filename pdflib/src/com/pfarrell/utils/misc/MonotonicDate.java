/*
 * MonotonicDate.java
 *
 * Created on April 17, 2007, 12:20 AM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.misc;

import java.util.Date;

/**
 * The <code>MonotonicDate</code> class implements a Date that
 * increments monotonically. If the current date is later than
 * the last incremented time, we use the current date. If this class
 * is called too quickly for the Date to change, we increment by one.
 *
 * @author pfarrell
 */
public class MonotonicDate {
        /** one and only one */
private static HelperSinglton theHelperSingleton;
    
    /** place to store count */
    private long theCount = 0;
    
    /** Creates a new instance of MonotonicDate */
    private MonotonicDate() {
    }
    /**
     * gets the next value of the counter
     * @return  the next value of the counter
     */
    public static synchronized Date next() {
        theHelperSingleton.theOne.theCount += 1000;
        long now = new Date().getTime();
        if ( now > theHelperSingleton.theOne.theCount) {
            theHelperSingleton.theOne.theCount = now;
        }
        return new Date(theHelperSingleton.theOne.theCount);
    }
/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        static MonotonicDate theOne = new MonotonicDate();
    }
}
