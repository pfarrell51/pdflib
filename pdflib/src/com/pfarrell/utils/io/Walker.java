/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

/**
 * The <code>Walker</code> interface defines the interface for a Walker, which will recursively search
 * through all the files and directories within the starting location.
 * @see ZipWalker
 * @see TarFileWalker
 * @see DirWalker
 * 
 * @author pfarrell
 * Created on Jun 2, 2012, 8:45:45 PM
 */
public interface Walker {

    /**
     * set abort on error flag
     * @param arg boolean value we want to set AbortOnError control
     */
    void setAbortOnError(boolean arg);

    /**
     * walk thru the directory of files, processing all the good ones
     * @throws Exception pass up any exception, usually will be runtime,
     * such as Index out of bounds, etc.
     */
    void walk() throws Exception;
    
}
