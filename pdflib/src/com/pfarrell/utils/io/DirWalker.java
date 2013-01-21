package com.pfarrell.utils.io;
/*
 * @(#)DirWalker.java
 *
 * Copyright (c) 2001-2013, Pat Farrell, All rights reserved.
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

import java.io.*;
import org.apache.log4j.Logger;
/**
 * This class walks through all the files defined 
 * by the arguments of the constructor. For all the 
 * "files" the WalkDrivenProcessor's process() function is called.
 * For directories, the program walk recurses.
 */
public class DirWalker implements Walker {
            /** class-wide logger static for reuse   */
protected static final Logger dwLog = Logger.getLogger(DirWalker.class);
    
    /** file handle to process */
    File base;
    /** filter to apply during processing */
    FilenameFilter filter;
    /** processor to apply to each directory and file */
    WalkDrivenProcessor processor;
    /** flag, abort/re-throw on error, or ignore exception */
    boolean abortError = false;
    /**
     * construct a new DirWalker from the parameters
     * 
     * @param base Java file spec to process
     * @param filter filter of files to ignore
     * @param processor WalkDrivenProcessor to use when processing a good file
     */
    public DirWalker(File base, FilenameFilter filter, WalkDrivenProcessor processor) {
        this(base, filter, processor, false);
    }
    /** construct a new DirWalker from the parameters
     *
     * @param base Java file spec to process
     * @param filter filter of files to ignore
     * @param processor WalkDrivenProcessor to use when processing a good file
     * @param abortErr  boolean flag, abort/rethrow on error if true, or report error and continute if false.
     */
    public DirWalker(File base, FilenameFilter filter, WalkDrivenProcessor processor, boolean abortErr) {
        this.base = base;
        this.filter = filter;
        this.processor = processor;
        this.abortError = abortErr;
    }
    /** set abort on error flag
     * @param arg boolean value we want to set AbortOnError control
     */
    @Override
    public void setAbortOnError(boolean arg) {
        abortError = arg;
    }
    /** walk thru the directory of files, processing all the good ones
     * @throws Exception pass up any exception, usually will be runtime,
     * such as Index out of bounds, etc.
     */ 
    @Override
    public void walk() throws Exception {
        if (!base.exists()) {
            return;
        }
        if (base.isFile()) {
            try {
               processor.process(base);
            } catch (Exception e) {
                if ( abortError) {
                    dwLog.error("Error, with abortError True", e);
                    throw e;
                } 
                else
                    dwLog.error("Error, with abortError false, skipping file", e);
            }
        } else {
            if (!processor.descendDir(base)) {
                dwLog.trace(" no descend for " + base.getAbsolutePath());
                return;
            }
            File[] fList;
            if (filter == null) {
                fList = base.listFiles();
            } else {
                fList = base.listFiles(filter);
            }
            if (fList == null || fList.length == 0) {
                return;
            }
            for (int i=0; i<fList.length; i++) {
                DirWalker dw = new DirWalker(fList[i], filter, processor, abortError);
                dw.walk();
            }
        }
    }
}

