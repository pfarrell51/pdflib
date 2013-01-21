package com.pfarrell.utils.misc;
/**
 * static byte array utilities
 *
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
 */

/** 
 * ByteArrayUtils
 * static class of handly byte array utilities
 */
 public class ByteArrayUtils {
    /** 
     * utility to build a long from values in an array of bytes in littleEndian order
     * @param buf byte array source
     * @param offset offset into array
     * @param len number to read (must be  8 or less
     * @return a long
     */
    public static long longFromArray(byte []buf, int offset, int len) {
        long val = 0;
        for (int i=len-1; i>=0; i--) {
            val = val << 8;
            val += 0xff & ((long)buf[offset+i]);
        }
        return val;
    }
    /** 
     * utility to build a long from values in an array of bytes in bigEndian order
     * @param buf byte array source
     * @param offset offset into array
     * @param len number to read (must be  8 or less
     * @return a long
     */
    public static long longFromBigEndainArray(byte []buf, int offset, int len) {
        long val = 0;
        for (int i=0; i < len; i++) {
            val = val << 8;
            int d = (0xff & buf[i+offset]); 
            val += d;
        }
        return val;
    }
    /** utility to generate an int from an array of bytes in littleEndian order
     * @param buf byte array source
     * @param offset into the array
     * @param len number of bytes to use, must be 4 or less
     * @return an int
     */
    public static int intFromByteArray(byte []buf, int offset, int len) {
        int val = 0;
        for (int i=len-1; i>=0; i--) {
            int d = (0xff & ((int)buf[offset+i]));
            val = val << 8;
            val += d;
        }
        return val;
    }
    /** utility to generate an int from an array of bytes in BigEndain order
     * @param buf byte array source
     * @param offset into the array
     * @param len number of bytes to use, must be 4 or less
     * @return an int
     */
    public static int intFromBigEndainByteArray(byte []buf, int offset, int len) {
        return (int)longFromBigEndainArray(buf, offset, len);
    }
    /** utility to generate an int from an array of bytes in syncsafe format. 
     * This format uses only 7 bits per byte
     * @param buf byte array source
     * @param offset into the array
     * @param len number of bytes to use, must be 4 or less
     * @return an int
     */
    public static int intFromSyncSafeByteArray(byte []buf, int offset, int len) {
        int val = 0;
        int v2 = 0;
        for (int i=0; i < len; i++) {
            val = val << 7;
            v2 = v2 << 7;
            int d =   (char) (0x0000007F & buf[i+offset]); // use only low bits
            int d2 =   buf[i+offset]; 
            if ( d2 < 0) d2 = 256+d2;
            val += d;
            v2 += d2;
        }
        if ( val != v2)
            System.out.println(val + " != " + v2);
        if (val > 64000) {
            System.err.println("Val is way too big: " + val);
            System.err.println("off: " + offset + " and len: " + len);
            for (int i = 0; i < len; i++) {
                byte b = buf[i];
                System.err.println(i + "]=" + Integer.toHexString( b) + " as char " + (new Character((char) b).toString()) );
            }
        }
        return val;
    }
    /**
     * compare byte arrays
     * @return true if equal
     * @param haystack byte array to search thru looking for the needle
     * @param needle what we want to find in the haystack
     */
public static boolean startsWith(byte[] haystack, byte[] needle) {
    int i;
    for (i = 0; i < needle.length; i++)
        if (needle[i] != haystack[i])
            break;
    return i == needle.length;
}    
    /** compare byte arrays
     * @return true if equal
     * @param offset offset into haystack
     * @param haystack byte array to search thru looking for the needle
     * @param needle what we want to find in the haystack
     */
public static boolean startsWith(byte[] haystack, int offset, byte[] needle) {
    int i;
    for (i = 0; i < needle.length; i++)
        if (needle[i] != haystack[i + offset])
            break;
    return i == needle.length;
}    

}

