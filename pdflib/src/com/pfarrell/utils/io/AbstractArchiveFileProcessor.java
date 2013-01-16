/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractArchiveFileProcessor</code> class implements the base class methods
 * for an ArchiveFileProcessor
 * @see ArchiveFileProcessor
 * 
 * @author pfarrell
 * Created on Jun 1, 2012, 1:16:04 PM
 */
public abstract class AbstractArchiveFileProcessor implements ArchiveFileProcessor {

    /** logger instance */
    private static final Logger aLog = Logger.getLogger(AbstractArchiveFileProcessor.class);
    protected int linesRead;
    protected int recordsProcessed;
    protected int successRecords;
    protected int rejectRecords;
    private String entryName;
    private AbstractWalker absWalker;
    
    /** default constructor */
    public AbstractArchiveFileProcessor() {
    }


    public boolean descendDir(File dirToDecend) {
        return true;
    }

    public void setEntryName(String arg) {
        entryName = arg;
    }

    public String getEntryName() {
        return entryName;
    }


    public AbstractWalker getWalker() {
        return absWalker;
    }
    protected void setWalker(AbstractWalker arg) {
        absWalker =  arg;
    }

    public void process(File fileToProcess) throws FileNotFoundException {
        setEntryName(fileToProcess.getPath());
        FileInputStream fis = new FileInputStream(fileToProcess);
        process(fis);
    }

    public void dumpValues() {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean mustDumpResults() {
        return false; //throw new UnsupportedOperationException("Not supported yet.");
    }
    public int getLinesRead() {
        return linesRead;
    }

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

    public int getSuccessRecords() {
        return successRecords;
    }

    public int getRejectedRecords() {
        return rejectRecords;
    }    
}
