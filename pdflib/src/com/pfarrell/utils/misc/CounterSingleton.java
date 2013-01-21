/*
 * CounterSingleton.java
 *
 * Created on August 11, 2006, 3:33 PM
 *
 * Copyright (c) 2006-2013, Pat Farrell. All rights reserved.
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
 * The  <code>CounterSingleton</code> class is a simple counter, that 
 * starts at one and goes up. Use it to be sure that we are thread safe.
 *
 * @author pfarrell
 */
public final class CounterSingleton {
    /** one and only one */
private static HelperSinglton theHelperSingleton;
    
    /** place to store count */
    private long theCount = 0;
    
    /** Creates a new instance of CounterSingleton */
    private CounterSingleton() {
    }
    /**
     * gets the next value of the counter
     * @return  the next value of the counter
     */
    public static synchronized long next() {
        return HelperSinglton.theOne.theCount++;
    }
/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        static CounterSingleton theOne = new CounterSingleton();
    }
}
