/*
 * Sex.java  
 *   
 * Copyright (C) 2008 Patrick Farrell  
 *   
 * This program is distributed in the hope that it will be useful,  
 * but WITHOUT ANY WARRANTY; without even the implied warranty of  
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
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
