/*
 * PibException.java
 *
 * Created on April 24, 2006, 6:37 PM
 *
 * Copyright (c) 2006, Pat Farrell, Inc. All rights reserved.
 */

package com.pfarrell.exceptions;

/**
 * The  <code>PibException</code> class is a runtime exception
 * that does not have to be declared, but can be readily trapped.
 *
 * @author pfarrell
 */
public class PibException extends IllegalStateException {
    
    /**
     * construct a Bozo with given message
     * @param m message
     */
    public PibException(String m) {
        super(m);
    }
    
}
