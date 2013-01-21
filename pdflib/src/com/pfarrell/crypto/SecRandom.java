package com.pfarrell.crypto;
/*
 * SecRandom.java	
 *
 * Copyright (c) 2004, Pat Farrell.  All rights reserved.
 * based on work Copyright (c) 2001, OneBigCD, Inc.  All rights reserved.
 * @author Pat Farrell and Brian Boesch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.    
 */


import java.security.SecureRandom;
/**
 * The  <tt>SecRandom</tt> is a type safe version of the standard
 * {@link java.security.SecureRandom}, so we can make sure we it is a singleton.
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
     * return the singleton instance
     * @return the singleton instance
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
