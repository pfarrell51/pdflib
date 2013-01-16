package com.pfarrell.utils.io;
/**
 * Copyright (c) 2001, Pat Farrell Genealogy Research
 */

import java.io.*;
/**
 * the <tt>OnlyDirectoryFilter</tt> class implements a filter
 * that only accepts directories 
 */
public class OnlyDirectoryFilter implements FilenameFilter {
 
    /**
     * returns our policy for a file.
     * @param dir Java File of the directory to accept
     * @param name of file
     * @return true if it is a directory
     */
    public boolean accept(File dir,  String name) {
        System.out.println("dir is "  + dir.toString());
        System.out.println("name is " + name);
        return dir.isDirectory();
    }
 
}

