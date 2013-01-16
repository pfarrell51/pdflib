/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

import com.google.common.base.Preconditions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * The <code>TarFileWalker</code> class implements a class that recursively walks through a tar (unix tape archive) file,
 * expanding internally contained zip files into directories, and then processing any below that it finds.
 * The is typically used having a DirWalker find the tar file in a tree, and then having this TarFileWalker handle
 * recursively processing the tar file's contents.
 * 
 * @author pfarrell
 * Created on Jun 1, 2012, 1:01:26 AM
 */
public class TarFileWalker extends AbstractWalker implements ArchiveWalker {

    /** logger instance */
    private static final Logger tfwLog = Logger.getLogger(TarFileWalker.class);
    public static final String TAR_FILE_EXTENSION = ".tar";
 
    public TarFileWalker(String aName,  ArchiveFileProcessor processorArg, FilenameFilter processFilter) {
        this(aName,  processorArg, processFilter, false);
    }
    public TarFileWalker(String aName,  ArchiveFileProcessor processorArg, FilenameFilter processFilter, boolean abortErr) {
        super(aName,  processorArg, processFilter, abortErr);
    }

    @Override
public void walk() throws IOException {
    String currentName = getFileName();
    System.out.printf("TfW:start: %s %d", currentName, getInternalFileNames().size());
    if (currentName.startsWith(".")) {
        tfwLog.info("ignoring . hidden file");
        return;
    } else if (currentName.endsWith(".tar")) {
        tfwLog.info("Good, its a tar file");
    } else {
        tfwLog.warn("Bad, not a tar file");        
    }
    File file = new File(currentName);
    InputStream is = new FileInputStream(file);
    TarInputStream tis = new TarInputStream(is);
    TarEntry te = null;
    while ( (te = tis.getNextEntry()) != null ) {
        currentName = te.getName();
        if (currentName.startsWith(".")) {
            tfwLog.info("ignoring . hidden file");
            continue;
        } else if (currentName.endsWith(".tar")) {
            tfwLog.info("Good, its a tar file, need to recurse");
        } else {
            tfwLog.warn("regular, not a tar file");
        }
        getProcessor().setEntryName(currentName);
        String currentPath = currentName;

        BufferedInputStream bis = null;
        try {
            if (te.isDirectory()) {
                // is a directory
                System.out.println(te);
            } else {
                // not directory
                if (getProcessor().getEntryName().toLowerCase().endsWith(".tar"))  {
                    tfwLog.trace("recursing to " + currentName);
                    getInternalFileNames().add(currentName);
                    //simpleCopy(tis, destFile);
                  
                    TarFileWalker tfw   = new TarFileWalker(currentName, getProcessor(), getProcessFilter());
                    tfw.walk();
                    getInternalFileNames().addAll(tfw.getInternalFileNames());               
                } else {
                    bis = new BufferedInputStream(tis);
                    File pathFile = new File(currentPath);
                    if (getProcessFilter().accept(pathFile, getProcessor().getEntryName())) {
                            getProcessor().setEntryName(currentName);
                            getProcessor().process(bis);
                    }
                }
            }
        } catch (IOException ex) {
            tfwLog.error("tw:IO " + getFileName(), ex);
        } finally {
            try {
                if (bis != null) bis.close();
            } catch (IOException ex) {
                tfwLog.error("tw:closing IO " + getFileName(), ex);
            }
        }
    }
    
}

public boolean isFileToProess(String name) {
    Preconditions.checkNotNull(name);
    return ! name.toLowerCase().endsWith(TAR_FILE_EXTENSION);
}

public boolean isFileToRecurseOn(String name) {
    Preconditions.checkNotNull(name);
    return name.toLowerCase().endsWith(TAR_FILE_EXTENSION);
}
}
