package com.pfarrell.crypto;
/*
 * SecRandom.java	
 *
 * Copyright (c) 2004, Pat Farrell.  All rights reserved.
 * based on work Copyright (c) 2001, OneBigCD, Inc.  All rights reserved.
 * @author Pat Farrell and Brian Boesch
 */
import java.security.SecureRandom;
/**
 * The  <tt>SecRandom</tt> is a type safe version of the standard
 * {@link java.security.SecureRandom}, so we can make sure we it is a singlton.
 * It extends the base, so it inherits all of the base functions and fields.
 */
public final class SecRandom extends SecureRandom {
    /** solve double check bug */
    private static HelperSingleton theSingle;
    /** serializable version */
    private static final long serialVersionUID = 2;    

    /** unavailable constructor so we are the only person who uses it */
    private SecRandom() {};
    
    /**
     * return the singlton instance
     * @return the singlton instance
     */    
    public static SecRandom getInstance() {
        return theSingle.theOne;
    }
    /** standard helper singleton class */
static class HelperSingleton {
    /** the real static instance */
    static SecRandom theOne = new SecRandom();
}      
}
