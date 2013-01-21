/*
 * Sex.java  
 *   
 * Copyright (C) 2008 Patrick Farrell  
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

package com.pfarrell.enums;

/**
 * The <code>Sex</code>  enum holds gender. Not a lot of options here.
 * 
 * @author pfarrell
 */
public enum Sex {
    /** boy */
    male,
    /** girl */ 
    female,
    /** mind your own business */
    myob,
    ;
    
/**
 * get single upper case character
 * @return enum as String of one character 
 */
public String first() {
    char c1 = toString().charAt(0);
    return String.valueOf(c1).toUpperCase();
}
/**
 * convert a character to the matching enum (does not support MYOB)
 * @param c char, either M or F
 * @return matching enum
 */
public static Sex valueOf(char c) {
    switch (c) {
        case 'm':
        case 'M':
            return male;
        case 'f':
        case 'F':
            return female;
    }
    throw new IllegalArgumentException("Illegal sex, must be M or F");
}
/**
 * gets third person singular pronoun for this sex
 * @return third person singular pronoun for this sex
 */
public String getPronoun() {
    if ( this == female) return "she";
    return "he";
}
/**
 * gets posessive pronoun for this sex
 * @return  posessive pronoun for this sex
 */
public String getPossessive() {
    if ( this == female) return "her";
    return "his";
}    
}
