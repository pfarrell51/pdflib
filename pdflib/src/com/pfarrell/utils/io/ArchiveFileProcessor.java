/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

import java.io.File;
import java.io.InputStream;

/**
 * The <code>ArchiveFileProcessor</code> interface defines a WalkDrivenProcessor that can handle
 * archives of files such as ZIP or TAR (tape archive in Unix) files.
 * 
 * @author pfarrell
 * Created on May 31, 2012, 9:46:26 PM
 */
public interface ArchiveFileProcessor extends WalkDrivenProcessor {
    /** generic process function, do what is applicable to this object
     * @param input stream handle to input file
     */  
   void process(InputStream input);

    /**
     * Decide if you want to descend down into subdirectories
     * @param dirToDecend handle to this directory 
     * @return true if process is to process subdirectory
     */    
    boolean descendDir(File dirToDecend);
    void setEntryName(String arg);
    String getEntryName();
   /** gets number of raw text lines read */
   int getLinesRead();
   /** gets number of records read and processed */
   int getRecordsProcessed();
   /** gets number of records processed successfully */
   int getSuccessRecords();
   /** gets number of records rejected */
   int getRejectedRecords();
   /** gets the walker used by this processor */
   AbstractWalker getWalker();
}
