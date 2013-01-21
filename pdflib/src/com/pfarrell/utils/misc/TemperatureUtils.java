/*
 * TemperatureUtils.java
 *
 * Created on March 30, 2006, 10:40 PM
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

import java.util.Formatter;

/**
 * The  <code>TemperatureUtils</code> class does
 * handy things with temperature. The most obvious being
 * converting from ancient English units to something useful
 * (i.e. Fahrenheit to Celsius and vice versa).
 *
 * @author pfarrell
 */
public abstract class TemperatureUtils {
/**
 * German born scientist Gabriel Daniel Fahrenheit. He is credited with the invention of the mercury thermometer and introduced it and his scale in 1714 in Holland.<br>
 * Tc = (5/9)*(Tf-32); Tc = temperature in degrees Celsius, Tf = temperature in degrees Fahrenheit
 * @param f degrees Fahrenheit
 * @return degrees Celsius
 */
public static double toCelsius(double f) {
    double c = (5.0f/9.0f)*(f-32.0f);
    //System.out.printf(" f: %g c: %g\n", f, c);
    return c;
}
/**
 * Swedish Astronomer Andres Celsius (1701-1744) 
 * Tf = (9/5)*Tc+32; Tc = temperature in degrees Celsius, Tf = temperature in degrees Fahrenheit
 * @param c degrees Celsius
 * @return degrees Fahrenheit
 */
public static double toFahrenheit(double c) {
    double f = ((9.0f*c)/5.0f) +32.0f;
    //System.out.printf(" f: %g c: %g\n", f, c);
    return f;
}
/**
 * William Thomson, also know as Lord Kelvin, a British scientist who made important discoveries 
 * about heat in the 1800's. Scientists have determined that the coldest it can get, in theory, 
 * is minus 273.15 degrees Celsius. This temperature has never actually been reached, though 
 * scientists have come close. The value, minus 273.15 degrees Celsius, is called absolute zero.
 * @param c degrees Celsius
 * @return Kelvin temperature (techincally not degrees)
 */
public static double toKelvin(double c) {
    return c+ 273.15f;
}
/**
 * W J M Rankine (1820-1872), a Scottish engineer, created his scale, which was 
 * merely the Kelvin scale using the Fahrenheit degree instead of the Celsius. 
 * It has also had some wide use in scientific communities but is of no practical 
 * use in other areas of measurement. It was, of course, the scale taught at VPI to
 * mechanical engineers in the 1960s.
 * @param f degrees Fahrenheight
 * @return degrees Rankine
 */
public static double toRankine(double f) {
    return toKelvin(toCelsius(f))*1.80f;
}
/**
 * gets two digit string
 * @param f number to format
 * @return string with no more than two decimal places
 */
public static String toTwoDigit(double f) {
    if (Double.isNaN(f)) return "";
    Formatter fmt = new Formatter();
    if ( Math.abs(Math.floor(f) - f) < .00001 ) {
        Double floor = new Double(Math.floor(f)); 
        long asInt = floor.longValue();
        return fmt.format("%d", asInt ).toString();
    }
    return fmt.format("%.2f", f).toString();
}
}
