/*
 * CounterSingleton.java
 *
 * Created on August 11, 2006, 3:33 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.misc;

/**
 * The  <code>CounterSingleton</code> class is a simple counter, that 
 * starts at one and goes up. Use it to be sure that we are thread safe.
 *
 * @author pfarrell
 */
public final class CounterSingleton {
    /** one and only one */
private static HelperSinglton theHelperSingleton;
    
    /** place to store count */
    private long theCount = 0;
    
    /** Creates a new instance of CounterSingleton */
    private CounterSingleton() {
    }
    /**
     * gets the next value of the counter
     * @return  the next value of the counter
     */
    public static synchronized long next() {
        return HelperSinglton.theOne.theCount++;
    }
/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        static CounterSingleton theOne = new CounterSingleton();
    }
}
