/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

import com.google.common.base.Preconditions;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import org.apache.log4j.Logger;

/**
 * The <code>ZipWalker</code> class implements a class that recursively walks through a zip file, expanding
 * internally contained zip files into directories, and then processing any below that it finds.
 * The is typically used having a DirWalker find the zip file in a tree, and then having this ZipWalker handle
 * recursively processing the zip file's contents.
 * @see DirWalker
 * @see ZipFileProcessor
 * 
 * @author pfarrell
 * Created on Nov 15, 2011, 11:13:31 AM
 */
public class ZipWalker extends AbstractWalker implements ArchiveWalker {

    /** logger instance */
    private static final Logger zdLog = Logger.getLogger(ZipWalker.class);
    public static final String ZIP_FILE_EXTENSION = ".zip";
    
    /** 
     * construct a new ZipWalker from the parameters
     *
     * @param zipName name of the zip file to open and process
     * @param processFilter filter to apply, only do processing on those files that pass this filter
     * @param processor FileProcessor to use when processing a good file
     */
    public ZipWalker(String zipName,  ArchiveFileProcessor processor, FilenameFilter processFilter) {
        this(zipName, processor, processFilter, false);
    }
    /** 
     * construct a new ZipWalker from the parameters
     *
     * @param zipName name of the zip file to open and process
     * @param processFilter filter to apply, only do processing on those files that pass this filter
     * @param processorArg ZipFileProcessor to use when processing a good file
     * @param abortErr  boolean flag, abort/re-throw on error if true, or report error and continue if false.
     */
    public ZipWalker(String zipName,  ArchiveFileProcessor processorArg, FilenameFilter processFilter, boolean abortErr) {
        super(zipName,  processorArg, processFilter, abortErr);
    }


private static Pattern dupDirPat = Pattern.compile("(.*)\\./(\\1)");
private static Pattern pathDupDirPat = Pattern.compile("(.+)(\\1)/\\1");
    /** 
     * walk thru the directory of files, processing all the good ones
     * @throws ZipException pass up any Zip file problems
     * @throws IOException  pass up an IO traps
     */
public void walk() throws IOException {
    zdLog.error(String.format("ZW:start: %s %d", getFileName(), getInternalFileNames().size()));
    File file = new File(getFileName());

    ZipFile zip = null;
    try {
        zip = new ZipFile(file);
    } catch (ZipException ex) {
        zdLog.error("Zip exception " + getFileName(), ex);
    } catch (IOException ex) {
        zdLog.error("Zip IOexception " + getFileName(), ex);
        throw ex;
    }
    String newPathName = getFileName().substring(0, getFileName().length() - 4);
    
    File newPath = new File(newPathName);
    newPath.mkdirs();
    Enumeration zipFileEntries = zip.entries();

    // Process each entry
    while (zipFileEntries.hasMoreElements()) {
        // grab a zip file entry
        ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
        getProcessor().setEntryName(entry.getName());
        String possibleNewName = newPathName + getProcessor().getEntryName();
        // for PTO data, which is probably too much of a special to belong here, its useful to remove silly 
        // duplicate entries in the path
        Matcher m = pathDupDirPat.matcher(possibleNewName);
        if (m.find()) {
            possibleNewName = m.replaceFirst(m.group(1));
        }
        m = dupDirPat.matcher(possibleNewName);
        if (m.find()) {
            possibleNewName =  m.replaceAll(m.group(1));
        }
        File destFile = new File(possibleNewName);
        
        // create the parent directory structure if needed
        File destinationParent = destFile.getParentFile();
        destinationParent.mkdirs();
        BufferedInputStream bis = null;
        try {
            if (!entry.isDirectory()) {
                bis = new BufferedInputStream(zip.getInputStream(entry));
                if (getProcessFilter().accept(newPath, getProcessor().getEntryName())) {
                        getProcessor().setEntryName(destFile.getName());
                        getProcessor().process(bis);
                }
            }

            if (getProcessor().getEntryName().toLowerCase().endsWith(".zip"))  {
                String recurseName = destFile.getAbsolutePath();    // found a zip file, try to open
                zdLog.trace("recursing to " + recurseName);
                getInternalFileNames().add(recurseName);
                simpleCopy(zip.getInputStream(entry), destFile);
                ZipWalker zw   = new ZipWalker(recurseName, getProcessor(), getProcessFilter());
                zw.walk();
                getInternalFileNames().addAll(zw.getInternalFileNames());               
            }
        } catch (IOException ex) {
            zdLog.error("zw:IO " + getFileName(), ex);
        } finally {
            try {
                if (bis != null) bis.close();
            } catch (IOException ex) {
                zdLog.error("zw:closing IO " + getFileName(), ex);
            }
        }
    }
    if (zdLog.isDebugEnabled()) {
        String msg = String.format("ZW:end: %s %d  %d", getFileName(), getInternalFileNames().size(), getFileName().length());
        zdLog.debug(msg);
    }
}
public boolean isFileToProess(String name) {
    Preconditions.checkNotNull(name);
    return ! name.toLowerCase().endsWith(ZIP_FILE_EXTENSION);
}

public boolean isFileToRecurseOn(String name) {
    Preconditions.checkNotNull(name);
    return name.toLowerCase().endsWith(ZIP_FILE_EXTENSION);
}

}
