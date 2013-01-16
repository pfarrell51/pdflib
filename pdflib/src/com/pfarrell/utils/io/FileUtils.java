/*
 * FileUtils.java 
 *
 * Created by OneBigCD
 * Copyright (c) 2004-2011, Pat Farrell, All rights reserved.
 * This code will be released with a suitable Open Source
 * license, probably BSD-like.
 */
package com.pfarrell.utils.io;

import java.util.*;
import java.io.*;
import java.security.*;
import com.pfarrell.utils.misc.ExecUtils;

/** 
 * Class to encapsulate all sorts of handy File utilities
 *
 * @author Pat Farrell and Brian Boesch
 */

public abstract class FileUtils {
/**
 * Recursively delete files starting with the one that is given.
 * Attempt to do this "Atomically" by moving the file/dir
 * to a new location first then deleting from there.
 * 
 * @param f File to delete.
 */
public static synchronized void recursiveDelete(File f) {
    if (f == null) return;
    if (!f.exists()) return;

    // find a parent dir to move to before delete
    File parent = f.getParentFile();
    if (parent == null) {
        throw new IllegalArgumentException("Cannot delete a root directory");
    }
    // delete "atomically" by renaming first
    // then deleting all of the parts.
    File moveTo = new File(parent, "rdelete" + System.currentTimeMillis());
    boolean moved = f.renameTo(moveTo);
    if (moved == true) f = moveTo;
    simpleRecursiveDelete(f);
}
/** delete speficied file and all subdirectories
 * @param f file to delete
 */    
private static synchronized void simpleRecursiveDelete(File f) {        
    if (!f.isDirectory()) {
        f.delete();
        return;
    }
    String [] files = f.list();
    if (files == null) return;
    for (int i=0; i<files.length; i++) {
        File theFile = new File(f, files[i]);
        simpleRecursiveDelete(theFile);
    }
    f.delete();
}
/**
     * clean up file name so that underlying OS can open file spec
     * @param arg possibly dirty name
     * @return clean name
     */
public static String cleanFileName(String arg) {
    if (arg == null) return arg;
    StringBuilder sb = new StringBuilder();
    boolean quoteNeeded = false;
    for (int i = 0; i < arg.length(); i++) {
        char c = arg.charAt(i);
        sb.append(c);
        if (c == ' ') quoteNeeded = true;
    }
    if ( quoteNeeded) return "\"" + sb.toString() + "\"";
    return sb.toString();
}
/** 
 * Copy a file from source to destination.
 * If the destination's parent directory
 * does not exist then that directory is created.
 * Attempts to do this "Atomically" by copying into
 * a temp file then moving the whole thing into final dest
 * later.
 * @param src Source file.
 * @param dst Destination file
 * @param checkCopy If true check the copy to ensure that it is correct. (Re read)
 * @return Number of bytes copied.
 * @exception java.io.IOException pass up any IO exception
 */
public static long copyFile(File src, File dst, boolean checkCopy) throws IOException {
    long copyLength = simpleCopyFile(src, dst);
    if (!checkCopy) return copyLength;

    String preSha = fileSha(src);
    String postSha = fileSha(dst);        
    if (preSha.equals(postSha)) return copyLength;
    // try again.
    copyLength = simpleCopyFile(src, dst);
    preSha  = fileSha(src);
    postSha = fileSha(dst);
    if (!preSha.equals(postSha)) {
        throw new IOException("could not accurately copy files");
    }
    return copyLength;
}
/**
 * copy a file from source to destination.
 * @param src source reader
 * @param dest destination writer
 * @return number of lines copied.
 */
public static long copyFile( BufferedReader src, BufferedWriter dest) throws IOException {
    String inline;
    long linecount = 0;
    while((inline = src.readLine()) != null)  {
        dest.write(inline);
        linecount++;
    }
    return linecount;
}

/**  Given a source and destination file parameters, this method will copy
 *  the source to destination.
 *  If last directory in the destination pathname doesn't exist, directory
 *  will be created.
 * @param src source to read
 * @param dst destination to write to
 * @throws IOException pass up any IO exception
 * @return number of bytes copied
 */
private  static long simpleCopyFile (File src, File dst) throws IOException {
    // goober checking and proofing
    if (!src.exists()) throw new IOException("File " + src.getName() + " does not exist");
    if (!src.isFile()) throw new IOException(src.getName() + " is a directory not a file");
    if (dst.exists()) {
        if (dst.isDirectory()) throw new IOException("Cannot copy a file onto a directory");
        dst.delete();
    }
    long srcLength = src.length();

    // create destination's parent directory if needed
    File dstDir = new File (dst.getParent());
    if (! dstDir.exists()) {
        dstDir.mkdirs();
    }

    FileInputStream srcStream = new FileInputStream (src);
    FileOutputStream dstStream = new FileOutputStream (dst);
    long copied =  simpleCopyFile(srcStream, dstStream);
    if (copied != srcLength) throw new IOException("Copied length does not match");
    return copied;
}
/**
 * Given a source and destination file parameters, this method will copy
 *  the source to destination.
 * @param srcStream source to read
 * @param dstStream destination to write
 * @return  number of bytes copied
 * @throws IOException pass up any IO exception
 */
public static long simpleCopyFile(InputStream srcStream, OutputStream dstStream) throws IOException {
    int maxRead = 500000;  
    int lastRead = 0;
    byte [] srcData = new byte [maxRead];
    long copied = 0;
    lastRead = srcStream.read (srcData);
    if (lastRead > 0) copied += lastRead;
    while (lastRead != -1) {
        dstStream.write (srcData, 0, lastRead);
        lastRead = srcStream.read (srcData);
        if (lastRead > 0) copied += lastRead;
    }
    dstStream.flush();
    dstStream.close();
    srcStream.close();
    return copied;
}
/** Copy an entire file or directory subtree.
 * The destination is a directory into which the
 * file or dir is copied.
 *
 * Attempts to copy "atomically" by copying then
 * moving into the final location.
 * @param src Source file or dir.
 * @param dst Destination dir.
 * @param checkCopy Reread copied file and compare with source.
 * @return number of bytes copied.
 * @exception java.io.IOException pass up any IO exception
 */
public static long recursiveCopyIntoDir(File src, File dst, boolean checkCopy) throws IOException {
    return recursiveCopyIntoDir(src, dst, checkCopy, null);
}
/** recursively copy all the files in a directory to specifed place
 * @param src source File (spec)
 * @param dst destination File
 * @param checkCopy true if you want to check the copy
 * @param filter File filter to apply
 * @throws IOException pass up any IO exception
 * @return number of copies
 */    
public static long recursiveCopyIntoDir(File src, File dst, boolean checkCopy, FileFilter filter) throws IOException {
    // goober checking and proofing
    if (!src.exists()) throw new IOException("File " + src.getName() + " does not exist");
    if (!dst.exists()) {
        dst.mkdirs();   // make dest dir and all higher dirs.
    }
    if (!dst.isDirectory()) throw new IOException("Destination is not a dir " + dst.getAbsoluteFile());
    File parent = dst.getParentFile();
    if (parent == null) parent = dst;
    File tmpDst = new File(parent, "tmp" + System.currentTimeMillis());
    String name = src.getName();        
    File dstFile = new File(dst, name);
    File tmpDstFile = new File(tmpDst, name);
    long copied = simpleRecursiveCopyIntoDir(src, tmpDst, checkCopy, filter);
    boolean b1 = tmpDstFile.renameTo(dstFile);
    boolean b2 = tmpDst.delete();
    return copied;
}
/**
 * recursive descent file copy
 * @param src source to read
 * @param dst destination to write
 * @param checkCopy true if we should verify that copy was bit exact, false for simple copy
 * @param filter filter to apply
 * @throws IOException pass up any IO exception
 * @return number of bytes copied.
 */
private static long simpleRecursiveCopyIntoDir(File src, File dst, boolean checkCopy, FileFilter filter) throws IOException {
    if (!dst.exists()) dst.mkdirs();
    String name = src.getName();        
    File dstFile = new File(dst, name);
    long copied = 0;
    if (src.isFile()) {
        System.out.println("COPY FILE src= <<" + src.getAbsolutePath() + ">>    <<" + dstFile.getAbsolutePath() + ">>");
        copied = copyFile(src, dstFile, checkCopy);
    } else {
        File [] dirFiles;
        if (filter == null) {
            dirFiles = src.listFiles();
        } else {
            dirFiles = src.listFiles(filter);
        }
        if (dirFiles == null) return 0;
        long totalCopy = 0;
        for (int i=0; i<dirFiles.length; i++) {
            totalCopy += simpleRecursiveCopyIntoDir(dirFiles[i], dstFile, checkCopy, filter);
        }
        copied =  totalCopy;
    }
    return copied;
}
/** 
 * calculate a SHA1 for a file
 * @param theFile file to look at
 * @throws IOException pass up any IO exception
 * @return string value of SHA1
 */    
public static String fileSha(File theFile) throws IOException {
    FileInputStream fis = null;
    try {
        if (!theFile.exists()) throw new IOException("File " + theFile.getAbsolutePath() + " does not exist");
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException("FileUtil could not make sha");
        }
        fis = new FileInputStream(theFile);
        byte [] buf = new byte[128000];
        int len = 1;
        while ((len = fis.read(buf)) >= 0) {
            md.update(buf, 0, len);
        }
        byte[] shaOut = md.digest();
        try { fis.close(); } catch (Exception e) {}
        return hexify(shaOut);
    } catch (IOException e) {
        try { fis.close(); } catch (Exception ee) {}
        throw e;
    }
}
/**  toHexes the given bytes array, and returns it.
 * @return printable hex string
 * @param buf binary data buffer to convert
 */ 
public static String hexify (byte [] buf) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < buf.length; i++)    {
        Byte b= new Byte(buf[i]);                       
        String s = Integer.toHexString(b.intValue());
        if (s.length() == 1)
            sb.append("0");
        if (s.length()>2)    
            s= s.substring(s.length()-2);                    
        sb.append(s);
    }
    return sb.toString();
}
/** Ask the O/S for the amount of free space on the dis.
 * @param diskLetter DOS disk letter. This is so obsolete an idea
 * @throws IOException pass up any IO exception
 * @return how much free space is on the disk
 */    
    public static long diskFree(String diskLetter) throws IOException {
    String r = ExecUtils.execCmd("df " + diskLetter);
    //System.out.println(" r= " + r);
    StringTokenizer st = new StringTokenizer(r);
    st.nextToken();
    st.nextToken();
    st.nextToken();
    String diskFree = st.nextToken().trim();
    return Long.parseLong(diskFree);
}
    /** return total size of disk
     * @param diskLetter DOS disk letter. This is so obsolete, come-on, Bill, kill this lame code
     * @throws IOException pass up any IO exception
     * @return size in bytes of disk
     */        
public static long diskTotal(String diskLetter) throws IOException {
    String r = ExecUtils.execCmd("df " + diskLetter);
    //System.out.println(" r= " + r);
    StringTokenizer st = new StringTokenizer(r);
    st.nextToken();
    String totalDisk = st.nextToken().trim();
    return Long.parseLong(totalDisk);
}
/** calculate the size of all the files in a directory tree*
 * @param file place to start searching
 * @return number of bytes
 */    
public static long treeSize(File file) {
    return treeSize(file, null);
}
/** calculate the size of all the files in a directory tree
 * @param file place to start searchign
 * @param filter filter out specified files
 * @return number of bytes
 */    
public static long treeSize(File file, FileFilter filter) {
    if (!file.exists()) return 0;
    if (file.isFile()) return file.length();
    long totalSize = 0;
    File [] dirFiles;
    if (filter == null) {
        dirFiles = file.listFiles();
    } else {
        dirFiles = file.listFiles(filter);
    }
    if (dirFiles == null) return 0;
    for (int i=0; i<dirFiles.length; i++) {
        totalSize += treeSize(dirFiles[i], filter);
    }
    return totalSize;
}
/**
     * read data, ignore file size, read until done
     * @param inReader source reader
     * @param writer output writer
     * @throws IOException passes up any exception
     */

public static void streamingReadAndWrite (Reader inReader,  Writer writer) throws java.io.IOException  {
    final  int bufferSize = 1024;
    char []buf = new char[bufferSize*2];    // allow for two reads for unknown reasons
    int totalCharsRead = 0;
    int totalCharsWritten = 0;
    int lastReadSize = 0;
    int bufferOffset = 0;

    while (lastReadSize != -1 ) {     

        try {
            lastReadSize = inReader.read(buf, bufferOffset, bufferSize-bufferOffset );
        } catch (java.net.SocketTimeoutException ste) {
            lastReadSize = -1;      // fake out end of file.
        }
        if ( lastReadSize > 0) {
            totalCharsRead += lastReadSize;
            bufferOffset += lastReadSize;
            if (bufferOffset > bufferSize) {
                if (writer != null)
                    writer.write( buf, 0, bufferOffset);
                else System.out.println(buf);
                totalCharsWritten += bufferOffset;            
                bufferOffset = 0;
            }
        }
    }
    // write out the last partial buffer.
    writer.write( buf, 0, bufferOffset);
    if (lastReadSize == -1) 
        throw new IllegalArgumentException ("Full contents not sent, only received " + (totalCharsRead+1) );
    
}
/** read data, ignore file size, read until done
 * @param inStream source stream
 * @param outStream output stream
 * @throws IOException passes up any exception
 */
public static void streamingReadAndWrite (InputStream inStream, OutputStream outStream)
            throws java.io.IOException  {
    try {
        streamingReadAndWrite ( inStream,  outStream, Integer.MAX_VALUE, 1024);
    } catch (IllegalArgumentException iae) {}
}
/** read data, ignore file size, read until done
 * @param inStream source stream
 * @param outStream output stream
 * @param bufferSize buffer size
 * @throws IOException passes up any exception
 */
public static void streamingReadAndWrite (InputStream inStream, OutputStream outStream,  int bufferSize)
            throws java.io.IOException  {
    try {
        streamingReadAndWrite ( inStream,  outStream, Integer.MAX_VALUE, bufferSize);                    
    } catch (IllegalArgumentException iae) {}
}
/** read data from input stream, complain if file length is not right.
 * @param inStream stream to read
 * @param outStream output stream
 * @param contentLength number of characters to read.
 * @param bufferSize allocate this much buffer space
 * @throws IOException pass up any exceptions
 */
public synchronized static void streamingReadAndWrite (InputStream inStream, OutputStream outStream, 
                                    int contentLength, int bufferSize) throws java.io.IOException  {
    byte []buf = new byte[bufferSize*2];    // allow for two reads for unknown reasons
    int totalBytesRead = 0;
    int totalBytesWritten = 0;
    int lastReadSize = 0;
    int bufferOffset = 0;

    while ((lastReadSize != -1) && (totalBytesRead < contentLength)) {     
        int readable = java.lang.Math.min(inStream.available(), bufferSize);
        int nextReadSize = java.lang.Math.min(readable, (contentLength - totalBytesRead));
        nextReadSize = java.lang.Math.max(nextReadSize, 1);     // always try to read at least one character
        try {
            lastReadSize = inStream.read(buf, bufferOffset, nextReadSize);
        } catch (java.net.SocketTimeoutException ste) {
            lastReadSize = -1;      // fake out end of file.
        }
        if ( lastReadSize > 0) {
            totalBytesRead += lastReadSize;
            bufferOffset += lastReadSize;
            if (bufferOffset > bufferSize) {
                if (outStream != null)
                    outStream.write( buf, 0, bufferOffset);
                else System.out.write(buf, 0, bufferOffset);
                totalBytesWritten += bufferOffset;            
                bufferOffset = 0;
            }
        }
    }
    // write out the last partial buffer.
    outStream.write( buf, 0, bufferOffset);
    if (lastReadSize == -1) 
        throw new IllegalArgumentException ("Full contents not sent, only received " + (totalBytesRead+1) + " of " + contentLength);
}
/** usual test driver
 * @param args standard array of command arguments
 */    
public static void main(String [] args) {
    try {
        long r = recursiveCopyIntoDir(new File("d:\\tmp\\foo"), new File("d:\\tmp\\bar"), true);
        System.out.println("r = " + r);
    } catch (Exception e) {
        System.out.println("Exception e= " + e);
    }
}
  

}
