package  com.pfarrell.exceptions;
/*
 * RequiredValueMissingException.java
 *
 * Copyright (c) 2004 Pat Farrell. All rights reserved
 * Created on October 4, 2004, 6:17 PM
 */

import java.lang.Exception;
/**
 * the <code>RequiredValueMissingException</code> class
 * implements a unique exception so we can trap this error explicitly
 * @author  Patrick Farrell
 */

public class RequiredValueMissingException  extends Exception {
    
    /**
     * Creates a new instance of RequiredValueMissingException
     * @param arg tracking string
     */
    public RequiredValueMissingException(String arg) {
        super(arg);
    }
    
}
