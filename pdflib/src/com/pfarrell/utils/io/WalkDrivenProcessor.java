package com.pfarrell.utils.io;
/*
 * @(#)WalkDrivenProcessor.java
 *
 * Copyright (c) 2001, Pat Farrell.  All rights reserved.
 * Copyright (c) 2004, Pat Farrell, All rights reserved.
 * This code will be released with a suitable Open Source
 * license, probably BSD-like.
 */
import java.io.*;

/**
 * interface that describes how to process each file in a directory,
 * and descend (recurse) as needed into subdirectories.
 */
public interface WalkDrivenProcessor {
    
    /** 
     * generic process function, do what is applicable to this object
     * @param fileToProcess File handle to input file
     */    
    void process(File fileToProcess) throws FileNotFoundException;
    /** 
     * generic process function, do what is applicable to this object
     * @param input stream for input file
     */  
     void process(InputStream input);
    /**
     * Decide if you want to descend down into subdirectories
     * @param dirToDecend handle to this directory 
     * @return true if process is to process subdirectory
     */    
    boolean descendDir(File dirToDecend);
    /**
     * Utility to dump internal values to standard out in a useful format
     */
    void dumpValues();
    /** tell caller if this implementation needs the dump called
     * so the user can do something, or if everything is already processed.
     * @return false if all processing is done, true if
     * dump must be called so user can tell what was done
     */
    boolean mustDumpResults();
   
}

