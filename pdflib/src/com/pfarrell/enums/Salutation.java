/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved
  * This program is distributed in the hope that it will be useful,
  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  *
 */

package com.pfarrell.enums;

/**
 * The <code>Salutation</code> enumerates titles for folks.
 * 
 * @author pfarrell
 */
public  enum Salutation {
    /** single or married guy    */
    Mr(Sex.male),
    /** maried woman     */
    Mrs(Sex.female),
    /** female    */
    Ms(Sex.female),
    /** medical or PhD  */
    Dr(null),
    /** reverend */
    Rev(null),
    ;

    private Sex gender;
    private Salutation(Sex g) {
        gender = g;
    }
    /**
     * gets the default gender of people with this salutation, if any
     * @return gender if generally known, null otherwise
     */
    public Sex getUsualGender() {
        return gender;
    }
}
