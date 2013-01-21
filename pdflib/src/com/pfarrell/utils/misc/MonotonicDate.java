/*
 * MonotonicDate.java
 *
 * Created on April 17, 2007, 12:20 AM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
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

import java.util.Date;

/**
 * The <code>MonotonicDate</code> class implements a Date that
 * increments monotonically. If the current date is later than
 * the last incremented time, we use the current date. If this class
 * is called too quickly for the Date to change, we increment by one.
 *
 * @author pfarrell
 */
public class MonotonicDate {
        /** one and only one */
private static HelperSinglton theHelperSingleton;
    
    /** place to store count */
    private long theCount = 0;
    
    /** Creates a new instance of MonotonicDate */
    private MonotonicDate() {
    }
    /**
     * gets the next value of the counter
     * @return  the next value of the counter
     */
    public static synchronized Date next() {
        theHelperSingleton.theOne.theCount += 1000;
        long now = new Date().getTime();
        if ( now > theHelperSingleton.theOne.theCount) {
            theHelperSingleton.theOne.theCount = now;
        }
        return new Date(theHelperSingleton.theOne.theCount);
    }
/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        static MonotonicDate theOne = new MonotonicDate();
    }
}
