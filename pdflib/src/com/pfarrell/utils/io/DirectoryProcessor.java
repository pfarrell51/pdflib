/*
 * @(#)DirectoryProcessor.java
 *
 * Copyright (c) 2001-2011, Pat Farrell.  All rights reserved.
 */
package com.pfarrell.utils.io;

import java.io.*;
import org.apache.log4j.Logger;

/**
 * Object to walk a directory tree, processing each file 
 * in the tree, recursing into subdirectories
 * 
 * @see WalkDrivenProcessor
 */
public abstract class DirectoryProcessor implements WalkDrivenProcessor {
        /** class-wide logger static for reuse   */
protected static final Logger dpLog = Logger.getLogger(DirectoryProcessor.class);
    
/** initial starting place for expansion */ 
private String startdir;
    /**
     * allocate a <tt>DirectoryProcessor</tt> on the heap
     * 
     * @param filespec string containing file specification of directory to scan
     * @param skipExt String containing extension to skip
     */
public DirectoryProcessor(String filespec, String skipExt){
    try {
        File temp  = new File(filespec);
        startdir =  temp.getCanonicalPath();
    } catch (IOException ioe ) {
        dpLog.error("IO Error", ioe);
    }
}
/** 
 * start the process of reading and processing files
 * @return true if all is happy
 */
public boolean scan() {
    System.out.println("scan called for " + startdir  );
    File theStart = new File(startdir);
    DirWalker walk = new DirWalker(theStart, null, this);
    try {
        walk.walk();          
    } catch (Exception e) {
        dpLog.error("scan Error", e);
        return false;
    }
    return true;
}
/** 
 * called by Diskwalker for each file to process
 * @param fileToProcess file handle to process
 */
public abstract void process(File fileToProcess);
/**
    * Diskwalker calls this method to see if we
    * have reached a stop point.
    * 
    * @param dirToDecend a java.io.File specifying the 
    * directory we are about to descend into.
    * @return true if it is ok, false if we should not decend.
    */
public boolean descendDir(File dirToDecend) {
    return true;
}
/**
 * dummy 
 */
public void dumpValues() {
    System.out.println("I have no idea what to do here");
}
    /**
     * returns our dump value
     * @return  our dump value
     */
public boolean mustDumpResults() { 
    return false;
}


}

