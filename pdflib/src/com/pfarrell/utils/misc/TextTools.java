/*
 * TextTools.java
 *
 * Created on February 14, 2007, 6:11 PM
 *
 * Copyright (c) 2007, Pat Farrell. All rights reserved.
 * Based on code from OneBigCD and perhaps even CyberCash
 *  Licensed under the Apache License, Version 2.0 (the "License");
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

import com.google.common.base.Preconditions;
import com.pfarrell.exceptions.PibException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.language.DoubleMetaphone;

/**
 * The  <code>TextTools</code> class implements handy utilities on Strings
 *
 * @author pfarrell
 */
public abstract class TextTools {
    
    /** encoder for Metaphone replacement to soundex */
    private static DoubleMetaphone meta = new DoubleMetaphone();
 /**
  * get DoubleMetaphone algorithm encoding of argument
  * @param arg input string
  * @return DoubleMetaphone encoding
  */   
    public static String getMetaphone( String arg)  {
        meta.setMaxCodeLen(5);
        String rval = meta.encode(arg);
        return rval;
    }
/**
 * Check if the Double Metaphone values of two String values are equal.
 * @param value1 first param
 * @param value2 another param
 * @return true if the encoded Strings are equal; false otherwise.
 */
public static boolean isMetaphoneEqual(String value1, String value2) {
    return meta.isDoubleMetaphoneEqual(value1, value2);
}
/**
 * make a string with CRLF between items
 * @param arg list of strings
 * @return string with CRLF between items
 */
public static String textFromList( List<String> arg) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arg.size(); i++) {
        sb.append(arg.get(i));
        if ( i < (arg.size()-1)) {
            sb.append(Constants.CRLF);
        }
    }
    return sb.toString();
}    
/**
 * make a string with no whitespace at all
 * @param arg  string
 * @return string with no whitespace
 */
public static String noWhiteSpace( String arg) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arg.length(); i++) {
        int c =  arg.codePointAt(i);
        if ( ! Character.isWhitespace(c)) {
            sb.appendCodePoint(c);
        }
    }
    return sb.toString();
}
/**
 * make a string with no whitespace at all
 * @param arg  string
 * @return string with no whitespace
 */
public static String justLettersOrDigits( String arg) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arg.length(); i++) {
        int c =  arg.codePointAt(i);
        if ( Character.isLetterOrDigit( c )) {
            sb.appendCodePoint(c);
        }
    }
    return sb.toString();
}
/**
 * make a string with only letters
 * @param arg  string
 * @return string with only letters, no punct, space, digits, etc.
 */
public static String justLetters( String arg) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arg.length(); i++) {
        int c =  arg.codePointAt(i);
        if ( Character.isLetter( c )) {
            sb.appendCodePoint(c);
        }
    }
    return sb.toString();
}
/**
 * strip out any thing other than letters or space in the middle, 
 * @param arg any string
 * @return letters or spaces, after trim()
 */
public static String justLetterOrSpace( String arg) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < arg.length(); i++ ) {
        char c = arg.charAt(i);
        if (Character.isLetter(c) || c == ' ') {
            sb.append(c);
        }
    }
    return sb.toString().trim();
}
/**
 * make a string with only digits
 * @param arg  string
 * @return string with only digits
 */
public static String justDigits( String arg) {
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < arg.length(); i++) {
        int c =  arg.codePointAt(i);
        if ( Character.isDigit( c )) {
            sb.appendCodePoint(c);
        }
    }
    return sb.toString();
}
/**
 * ensure that we have word capitalized as American English handling of a name
 * @param arg input word
 * @return word capitalized. foo => Foo
 */
public static String capitalizeAsName(String arg ) {
    Preconditions.checkNotNull(arg);
    if (arg.length() == 0 ) return "";
    return (arg.length() == 1) ?  arg.substring(0,1).toUpperCase() : arg.substring(0,1).toUpperCase() + arg.substring(1);
}
/**
 * ensure that we have word with Capital first letter, rest lowercase
 * @param arg input word
 * @return word capitalized. foo => Foo, FOO -> Foo, fOO -> Foo
 */
public static String capitalFirstRestLower(String arg ) {
    Preconditions.checkNotNull(arg);
    if (arg.length() == 0 ) return "";
    return (arg.length() == 1) ? arg.substring(0,1).toUpperCase() : arg.substring(0,1).toUpperCase() + arg.substring(1).toLowerCase();
}
   /**
    * unquote a string surrounded by single or double quote
    * @param arg input string
    * @return unquoted string
    */
public static String unquote(String arg) {
    String rval = arg;
    if (arg != null && arg.length() > 2) {
        char c = arg.charAt(0);
        char e = arg.charAt(arg.length()-1);
        if ( c == e) {
            if ( c == '"' || c == '\'') {
                rval = arg.substring(1, arg.length()-1);
            }
        }
    }
    return rval;
}
   /**
    * unquote a string surrounded by single or double quote possible with trailing punctuation
    * @param arg input string
    * @return unquoted string
    */
public static String unquoteWithTrailing(String arg) {
    String rval = arg;
    if (arg != null && arg.length() > 2) {
        char c = arg.charAt(0);
        
        Pattern trailingPattern = Pattern.compile("(\\" + c + ")[,.]*$");
        Matcher m = trailingPattern.matcher(arg);
        if (m.find()) {
            rval = arg.substring(1, m.start()).trim();
        }
    }
    return rval;
}
/**
 * validate a credit card number with the Luhn 10 algorithm.
 * @param cardNumber a credit card number, no dashes or spaces
 * @return true if valid
 */
public static boolean validateLuhn10(String cardNumber) {
    Preconditions.checkNotNull(cardNumber);
    String working = justDigits(cardNumber);
    return calculateLuhnCheckDigit(working) == 0;
}
private static int calculateLuhnCheckDigit(String working) {
    int sum = 0;
    int  digit;
    int addend = 0;
    boolean doubled = false;
    for (int i = working.length() - 1; i >= 0; i--) {
        digit = Integer.parseInt (working.substring(i, i + 1));
        if (doubled) {
            addend = digit * 2;
            if (addend > 9) {
                addend -= 9; 
            }
        } else {
            addend = digit;
        }
        sum += addend;
        doubled = !doubled;
    }
    return  (sum % 10);
}
private static final String[] digits = {"0","1","2","3","4","5","6","7","8","9"};
/**
 * append a correct Luhn10 check digit to the argument and return new longer string
 * @param cardNumber account number, ok to have spaces or dash between groups
 * @return cardnumber with check digit
 */
public static String addLuhn10Checkdigit(String cardNumber) {
    StringBuilder sb = new StringBuilder();
    Preconditions.checkNotNull(cardNumber);
    String working = justDigits(cardNumber);
    sb.append(working);
    String appended = working + digits[0];
    int  c = calculateLuhnCheckDigit(appended);
    if (c == 0) {
        sb.append(digits[0]);   // perfect, we are done
    } else {
        c = 10 - c;
        sb.append(digits[c]);
    }
    return sb.toString();
}
    /**
     * calculate Levenshtein distance between two strings, only works for inputs that
     * are directly suitable for 'char' representation, will not work properly with extended Unicode
     * languages.
     * @see <a href=http://en.wikipedia.org/wiki/Levenshtein_distance>http://en.wikipedia.org/wiki/Levenshtein_distance</a>
     * @see <a href=http://en.literateprograms.org/Levenshtein_distance>http://en.literateprograms.org/Levenshtein_distance</a>
     * @param s1 first argument
     * @param s2 other argument
     * @return Levenshtein distance
     */
public static int levenshtein(String s1, String s2) {
    Preconditions.checkNotNull(s1);
    Preconditions.checkNotNull(s2);
    int[][] dp = new int[s1.length() + 1][s2.length() + 1];
    for (int i = 0; i < dp.length; i++) {
        for (int j = 0; j < dp[i].length; j++) {
            dp[i][j] = i == 0 ? j : j == 0 ? i : 0;
            if (i > 0 && j > 0) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
                else
                    dp[i][j] = Math.min(dp[i][j - 1] + 1, Math.min( dp[i - 1][j - 1] + 1, dp[i - 1][j] + 1));
            }
        }
    }
    return dp[s1.length()][s2.length()];
}
   /**
    * gets Levenshtein distance "probability" between two strings, map value to 0.0 to 1.0 range
    * @param s1 first argument
    * @param s2 other argument
    * @return rough probability of strings being the same.
    */
public static float levenshteinProb(String s1, String s2) {
    Preconditions.checkNotNull(s1);
    Preconditions.checkNotNull(s2);
    float rval = 0.0f;
    int raw = levenshtein( s1, s2);
    if (raw > s2.length()) {
        rval = 0.0f;
    } else  if (raw != s2.length()) {
        rval = 1.0f - (1.0f * raw / s2.length());
    }
    return rval;
}
static final Pattern doubleDotPattern = Pattern.compile("digital\\.\\.com");
/**
 * manually fix the dumb double dot that Tomcat seems to create.
 * @param url input string that may have double dot
 * @return cleaned up string.
 */
public static String fixEvilDoubleDot(String url) {
    Preconditions.checkNotNull(url);
    String rval = url;
    Matcher m = doubleDotPattern.matcher(url);
    if (m.find()) {
        rval = m.replaceAll("digital\\.com");
    }
    return rval;
}
/**
 * quickly convert a byte array into a String for the argument Charset.
 * See http://stackoverflow.com/questions/1684040/java-why-charset-names-are-not-constants
 * @param array byte array
 * @param charset Charset to use (usually UTF-8)
 * @return  converted string
 */
public static String stringFromByteArray( final byte[] array, final Charset charset) {
    String rval = null;
    try    {
        rval = new String( array, charset.name( ) );
    } catch ( UnsupportedEncodingException ex )  {
        throw new PibException("can not happen, no charset");
    }
    return rval;
}
}
