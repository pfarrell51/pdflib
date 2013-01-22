/*
 * @(#)WebUtils.java	0.51 1999/07/14
 *
 * Copyright (c) 2004, Pat Farrell, All rights reserved.
 * based on work Copyright (c) 2001, OneBigCD, Inc.  All rights reserved.
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


package com.pfarrell.utils.misc;


/**
 * handy dandy web/jsp utilities. Abstract since all functions are static.
 * @author  Scott Eisenberg, Brian Boesch, and others
 * @version 0.51, 1999/07/14
 */
public abstract class WebUtils {
    
    
    /**
     *  Given a string, this method remove all the non digits and return that
     *  string.  For example "123-456-789a" would return "123456789".  This
     *  will return a null if null arg passed in.
     * @param arg - String to parse digits from
     * @return string without non-digits.
     */
    public static String parseOnlyDigits (String arg)
    {
        if (arg == null) return null;
        StringBuffer rval = new StringBuffer (arg.length());
        for (int i = 0; i < arg.length(); i++)
        {
            char c = arg.charAt(i);
            if (Character.isDigit(c))
                rval.append(c);
        }
        return rval.toString();
    }

    
/** simple table, no letters Eye or OH to confuse users. */
private static char b34enctab[] = { '0','1','2','3','4','5','6','7','8','9', 
            'A','B','C','D','E','F','G','H','J','K','L','M','N','P',
              'Q','R','S','T','U','V','W','X','Y','Z'};
/**
 * convert byte buffer to easy to type alphanumeric values.
 * Uses just upper case letters and digits. Put last nibble
 * in the middle because it will always be a digit.
 * @param buf incomming buffer
 * @return encoded string
 */
public static String base32encode(byte[] buf) {
    StringBuffer sb = new StringBuffer();
    if (buf == null || buf.length == 0) return null;
    for (int i = 0; i < buf.length; i++) {
        byte b0 = buf[i];
        sb.append(b34enctab[(b0>>6)&0x1f]);
        sb.append(b34enctab[(b0)&0x3]);
        sb.append(b34enctab[(b0>>3)&0x1f]);

    }
    return sb.toString();
}
 
}

