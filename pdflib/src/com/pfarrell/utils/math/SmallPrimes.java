/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 * Copyright (C) 2011 Patrick Farrell   All Rights reserved.
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

package com.pfarrell.utils.math;

import com.pfarrell.crypto.SecRandom;

/**
 * The <code>SmallPrimes</code> class implements convenience class to get prime numbers directly.
 * Source, www.wolframalpha, command "primes <= 300"
 * 
 * @author pfarrell
 * Created: Sep 11, 2009 12:58:33 AM
 */
public class SmallPrimes {
    /** all prime numbers below 300 */
public static final int[] valuesUnder300 = {
    2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
    79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
    167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251,
    257, 263, 269, 271, 277, 281, 283, 293,
};
/** nice primes to use, skip the tiny ones */
public static final int[] nicePrimes =  {
    7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73,
    79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163,
    167, 173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251,
    257, 263, 269, 271, 277, 281, 283, 293,
};
/**
 * gets a randomly selected small prime suitable for use calculating hash values from character/string data.
 * Selects from the first 20 values in the {@link SmallPrimes#nicePrimes} table
 * @return a small prime number
 */
public int ranCharPrime() {
    SecRandom rg = SecRandom.getInstance();
    int t = rg.nextInt(20);
    return nicePrimes[t];
}
/**
 * gets a randomly selected prime number less than 300. Uses the {@link SmallPrimes#nicePrimes} table
 * @return a small prime number
 */
public int ranSmallPrime() {
    SecRandom rg = SecRandom.getInstance();
    int t = rg.nextInt(nicePrimes.length);
    return nicePrimes[t];
}

}
