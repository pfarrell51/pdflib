/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved
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
