/*
 * HmacUtil.java
 *
 * Created on July 27, 2006, 1:36 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
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

package com.pfarrell.crypto;

import com.google.common.base.Preconditions;
import com.pfarrell.utils.misc.WebUtils;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;

/**
 * The  <code>HmacUtil</code> abstract class holds
 * handy utilities related to HMAC. All functions are static.
 * We have moved to SHA-256 from the older SHA1, because the serious
 * crypto guys are concerned that SHA1 may be too weak. Since it is
 * trivial to change now, we have.
 *
 * @see <a href="http://www.faqs.org/rfcs/rfc2104.html">RFC 2104 - HMAC: Keyed-Hashing for Message Authentication</a>
 *  
 * @author pfarrell
 */
public abstract class HmacUtil {
    /**
     * toHexes the given bytes array, and returns it.
     * @param buf input byte buffer
     * @return  hexified result
     */ 
    public static String hexify (byte [] buf)  {
        Preconditions.checkNotNull(buf);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < buf.length; i++)    {
            Byte b= new Byte(buf[i]);                        
            String s = Integer.toHexString(b.intValue());
            if (s.length() == 1)
                s = "0" + s;
            if (s.length()>2)    
                s= s.substring(s.length()-2);                    
            sb.append(s);
        }
        return sb.toString();
    }
   /**
    * calculate the sha of the argument, return hex encoded value
    * @param message to hash
    * @return hexified result
    */
public static String sha(String message) {
    Preconditions.checkNotNull(message);
    return sha(message.getBytes());
}
   /**
    * calculate the sha of the argument, return hex encoded value
    * @param message to hash
    * @return hexified result
    */
public static String sha(byte[] message) {
    Preconditions.checkNotNull(message);
    byte[] gas = null;
    try      {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update (message);
        gas = digest.digest();

    } catch (Exception e) {
        System.out.println ("WebUtils.sha256 - caught exception: " + e.toString());
    }
    return hexify(gas);
}
    /**
     * calculate an HMAC using the SHA256 algorithm 
     * Given a secret and message, returns a hmac using sha256 hash of the message.
     * (no longer uses SHA1)
     * @param secret string known to both parties
     * @param message to sign
     * @return hexified result
     */ 
    public static String hmac (String secret, String message) {
        Preconditions.checkNotNull(secret);
        Preconditions.checkNotNull(message);
        return hmac(secret, message.getBytes());
    }
    
    /**
     * calculate an HMAC using the SHA256 algorithm.
     * Given a secret and message, returns a sha256 hash of the message.
     * (no longer uses SHA1)
     * @param secret string known to both parties
     * @param message to sign
     * @return hexified result
     */ 
    public static String hmac (String secret, byte[] message)   {
        Preconditions.checkNotNull(secret);
        Preconditions.checkNotNull(message);
        byte[] gas = null;
        try      {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            byte[]sbytes = secret.getBytes();
            digest.update (sbytes);
            digest.update (message);
            digest.update (sbytes);
            gas = digest.digest();
            
        } catch (Exception e) {
            System.out.println ("WebUtils.hamac - caught exception: " + e.toString());
        }
        return hexify(gas);        
    } 
    /**
     * a string nonce of the length specified in len
     * binary value of bytes is converted to a HEX
     * so each byte gives two characters in the string.
     * @param len length of output desired.
     * @return a string nonce
     */
public static String getNonceAsHex(int len) {
    int blen = len;
    byte[] buf = new byte[blen];
    SecRandom rg = SecRandom.getInstance();
    rg.nextBytes(buf);
    return hexify( buf);
}    
    /**
     * gets a string nonce of the length specified in len, encoded in Base64 "safe mode"/
     * The url-safe variation emits - and _ instead of + and / characters.
     * @param len length of output desired.
     * @return a string nonce
     */
public static final String getNonce(int len) {
    byte[] buf = new byte[len];
    SecRandom rg = SecRandom.getInstance();
    rg.nextBytes(buf);
    StringBuilder rval = new StringBuilder();
    String encoded = Base64.encodeBase64URLSafeString(buf);
    rval.append(encoded);
    rval.setLength(len);
    return rval.toString();
}
/**
 * generate a string nonce of a nice length
 * @return a string nonce
 */
public static String getNonce() {
    return getNonce(16);
}
/**
 * generate a string challenge of a nice length (6 chars)
 * @return a string challenge
 */
public static String getChallenge() {
    return getChallenge(6);
}
/**
 * generate a string challenge of  specified length
 * @param len length to generate
 * @return a string challenge
 */
public static String getChallenge(int len) {
    int blen = (len)/3;
    byte[] buf = new byte[blen];
    SecRandom rg = SecRandom.getInstance();
    rg.nextBytes(buf);
    String r = WebUtils.base32encode( buf);
    return r;
}

}
