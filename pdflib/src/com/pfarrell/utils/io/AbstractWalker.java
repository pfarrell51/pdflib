/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
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
 * limitations under the License. * Licensed under the Apache License, Version 2.0 (the "License");
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
package com.pfarrell.utils.io;

import com.google.common.base.Preconditions;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractWalker</code> class implements a base class with methods
 * to make it easy to build a proper Directory Walker
 * @author pfarrell
 * Created on Jun 1, 2012, 1:46:14 PM
 */
public abstract class AbstractWalker implements Walker {

    /** logger instance */
    private static final Logger awLog = Logger.getLogger(AbstractWalker.class);
    
    public static final String PATH_SEPARATOR = System.getProperty("file.separator");
    /** name of base file */
    private String fileName;
/** file handle to process */
    private  File base;

    /** filter to apply for processing */
    private  FilenameFilter processFilter;
    /** processor to apply to each directory and file */
    private  ArchiveFileProcessor processor;
    /** list of file names within the zip file */
    private List<String> internalFileNames = new ArrayList<String>();
        /** flag, abort/re-throw on error, or ignore exception */
    private boolean abortError = false;

    /** 
     * construct a new AbstractWalker from the parameters
     *
     * @param zipName name of the zip file to open and process
     * @param processFilter filter to apply, only do processing on those files that pass this filter
     * @param processorArg ZipFileProcessor to use when processing a good file
     * @param abortErr  boolean flag, abort/re-throw on error if true, or report error and continue if false.
     */
    public AbstractWalker(String zipName,  ArchiveFileProcessor processorArg, FilenameFilter processFilter, boolean abortErr) {
        Preconditions.checkNotNull(zipName);
        Preconditions.checkNotNull(processFilter);
        Preconditions.checkArgument(processorArg instanceof ArchiveFileProcessor);
        this.fileName = zipName;
        this.base = new File(zipName);
        this.processor =  processorArg;
        this.processFilter = processFilter;
        this.abortError = abortErr;
    }
    
    /** set abort on error flag
     * @param arg boolean value we want to set AbortOnError control
     */
    public void setAbortOnError(boolean arg) {
        setAbortError(arg);
    }
    
    /**
     * @return the internalFileNames
     */
public List<String> getInternalFileNames() {
    return internalFileNames;
}


protected void simpleCopy(InputStream src, File destFile) throws FileNotFoundException, IOException {
    Preconditions.checkNotNull(src);
    int currentByte;
    int BUFFER = 8*1024;

    // establish buffer for writing file
    byte data[] = new byte[BUFFER];
    BufferedInputStream is = new BufferedInputStream(src);
    // write the current file to disk
    FileOutputStream fos = new FileOutputStream(destFile);
    BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

    // read and write until last byte is encountered
    while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
        dest.write(data, 0, currentByte);
    }
    dest.flush();
    dest.close();
    is.close();
}

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the base
     */
    public File getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(File base) {
        this.base = base;
    }

    /**
     * @return the processFilter
     */
    public FilenameFilter getProcessFilter() {
        return processFilter;
    }

    /**
     * @param processFilter the processFilter to set
     */
    public void setProcessFilter(FilenameFilter processFilter) {
        this.processFilter = processFilter;
    }

    /**
     * @return the processor
     */
    public ArchiveFileProcessor getProcessor() {
        return processor;
    }

    /**
     * @param processor the processor to set
     */
    public void setProcessor(ArchiveFileProcessor processor) {
        this.processor = processor;
    }

    /**
     * @return the abortError
     */
    public boolean isAbortError() {
        return abortError;
    }

    /**
     * @param abortError the abortError to set
     */
    public void setAbortError(boolean abortError) {
        this.abortError = abortError;
    }
/**
 * filter class, returns true only for .zip files
 */
public static class JustZips implements FilenameFilter {
    public boolean accept(File dir, String filename) {
        boolean rval = false;
        String working = filename.toLowerCase();
        if (working.endsWith(".zip"))
            rval = true;
        else if (dir.isDirectory())
            rval = true;
        else {
            rval = false;
            awLog.error(String.format("JZ: defaulting to no, dir: %s, %s", dir.getName(), filename));
        }
        return rval;
    }    
}
static class JustTars implements FilenameFilter {
    public boolean accept(File dir, String filename) {
        boolean rval = false;
        String working = filename.toLowerCase();
        if (working.endsWith(".tar"))
            rval = true;
        return rval;
    }    
}
}
