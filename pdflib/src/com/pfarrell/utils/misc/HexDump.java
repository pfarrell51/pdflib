/*
 * HexDump.java
 * Copyright (c) 2004, Pat Farrell, All rights reserved.
 * 
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
 *
 * Created on April 18, 2004, 10:05 PM
 */

package com.pfarrell.utils.misc;

import java.io.*;
/**
 * Utilities to dump binary data structures in the usual format.
 * Dump in hexadecimal the content of a scalar. The result is returned in a
 * string. Each line of the result consists of the offset in the
 * source in the leftmost column of each line, followed by one or more
 * columns of data from the source in hexadecimal. The rightmost column
 * of each line shows the printable characters (all others are shown
 * as single dots).
 *
 * Copyright (c) 1998-1999 Fabien Tassin. All rights reserved.
 * This program is free software; you can redistribute it and/or
 * modify it under the same terms as Perl itself.

 * @author Fabien Tassin E<lt>fta@oleane.netE<gt>
 * @author  pfarrell
 */
public abstract class HexDump {
/** how many to show */    
private static final int BYTES_PER_LINE = 16;
/** how many sections */
private static final int BLOCKS_PER_LINE = 4;
    
/** convert input argument into hex string suitable for
 * debugging. Write out both the hex and characters
 * in blocks on a line.
 * @param arg byte array to dump
 * @return pretty string of hex dump and other debugging stuff
 */
public static String makeHex(byte[] arg) {
    StringBuffer sb = new StringBuffer();
    int current = 0;
    int doLines = (arg.length+BYTES_PER_LINE-1) / BYTES_PER_LINE;
    for (int line = 0; line < doLines; line++) {
        int left = java.lang.Math.min(arg.length - line * BYTES_PER_LINE, BYTES_PER_LINE);
        sb.append(padToThree(line*BYTES_PER_LINE)).append("  ");
        for (int i = 0; i < left; i++) {
            char c = (char) (0x000000FF & arg[current++]);  // lots of voodoo to make it be an unsigned byte
            if (c < 0xF) sb.append("0");
            sb.append(Integer.toHexString(c));
            if ( i % (BYTES_PER_LINE/BLOCKS_PER_LINE) == ((BYTES_PER_LINE/BLOCKS_PER_LINE)-1)) 
                sb.append(" ");
        }
        for (int j = left; j <  BYTES_PER_LINE; j++) sb.append("  ");   // fill in any short line with two blanks
        if ( left <  BYTES_PER_LINE) {
            int missingBlocks = (BYTES_PER_LINE-left)/ BLOCKS_PER_LINE;
            for (int mb = 0; mb < missingBlocks; mb++) sb.append(" ");    // add space between columns
            sb.append(" ");    // add final space
        }
        sb.append("    ");        // put some space between hex and strings
        for ( int k = 0; k < left; k++) {
            char pc = (char) (0x000000FF & arg[line*BYTES_PER_LINE+k]);
            if ( pc >= ' ' && pc < 127)
                sb.append(pc); 
            else
                sb.append('.');
        }
        sb.append("\n");
    }
    return sb.toString();
}
/** pad to three wide. effectivly the same as sprintf
 * with %03d
 * @param arg integer to print
 * @return three character long string.
 */
private static String padToThree(int arg) {
    if ( arg < 10) return "00" + arg;
    else if (arg < 100) return "0" + arg;
    else return Integer.toString(arg);
}
/** convert byte array and write resulting string of
 * hex to specified OutputStream
 * @param os Output stream to write to
 * @param arg input byte array to convert to hex
 * @throws IOException pass up any IO exceptions
 */
public static void hexToStream(OutputStream os, byte[] arg) throws IOException {
    os.write(makeHex(arg).getBytes());
}
/** write error to stderr and die. Never returns.
 * @param arg string to write to STDERR
 */
private static void die(String arg) {
    System.err.println(arg);
    System.exit(1);
}
/** Usual test driver
 * @param args file to dump, if none, do test of this code.
 */
public static void main(String args[]) {
    try {
        if ( args.length < 1) {
            byte[] a = {0,1,0x20, 0x21, 'A','B','C','a','b','c','+', (byte)190,  (byte)127,  (byte)128};
            hexToStream(System.out, a);
            System.exit(0);
        }
        //else

        File f = new File(args[0]);
        if (!f.exists())          die("No such file as " + args[0]);
        FileInputStream fis = new FileInputStream(f);
        long flen = f.length();
        if (flen > 10*1000) {
            System.err.println("file is too big to dump fully");
            flen = 10*1000;
        }
        byte[] buff = new byte[(int)flen];
        fis.read(buff);
        System.out.println(makeHex(buff));
    } catch (IOException io) {
        die(io.getMessage());
    }
}
}