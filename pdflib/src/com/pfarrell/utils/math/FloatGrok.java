/*
 * Copyright (c) 2008, Pat Farrell All rights reserved.
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

package com.pfarrell.utils.math;

/**
 * The <code>FloatGrok</code> class implements sample code to show how floating point
 * numbers don't work the way you expect
 * 
 * @author pfarrell
 */
public class FloatGrok {
    /** default constructor */
public FloatGrok() {
}
/** 
 * test addition of a "penny" in float and see where the value
 * is not as expected.
 */
public void notSameValue() {
    float step = 0.01F;
    float sum = 0;
    for (int i = 0; i < 100; i++) {
        float calc = ((float) i) * step;
        if (sum != calc) {
            float delta = sum - calc;
            System.out.printf("i: %d sum: %f != calc: %f, delta: %g\n", i, sum, calc, delta);
            break;
        }
        sum += step;
    }
}
/**
 * test addition of a "penny" in float and see where the value
 * differs by at least one "penny" or 0.010.
 */
public void notSamePenny() {
    float step = 0.01F;
    float sum = 0;
    for (int i = 0; i < 1000*1000; i++) {
        float calc = ((float) i) * step;
        float delta = Math.abs( sum - calc);
        if (delta > 0.010) {
            System.out.printf("i: %d sum: %f != calc: %f, delta: %g\n", i, sum, calc, delta);
            break;
        }
        sum += step;
    }
}

    /**
     * usual shell program entry point
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FloatGrok fg = new FloatGrok();
        fg.notSameValue();
        fg.notSamePenny();
    }

}
