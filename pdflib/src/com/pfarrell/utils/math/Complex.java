/*
 * Complex.java
 *
 * Created on May 5, 2006, 9:49 AM
 *
 * Copyright (c) 2006, Pat Farrell All rights reserved.
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

import java.util.Formatter;

/** 
 * The <code>Complex</code> class is an object that implements 
 * an Object Oriented data type for complex numbers.
 * <p>
 * The data type is "immutable" so once you create and initialize
 * a complex object, you cannot change its value. The "final"
 * keyword when declaring re and im enforces this rule, making it
 * a compile-time error to change the .re or .im fields after
 * they've been initialized.
 */
public class Complex {
    /** the real part */
    private final double re;   
    /**  the imaginary part */
    private final double im;   

/**
 * create a new object with the given real and imaginary parts
 * @param real real part
 * @param imag imaginary part
 */
public Complex(double real, double imag) {
    this.re = real;
    this.im = imag;
}
/**
 * return a string representation  of this complex number
 * @return a string representation of this complex number
 */
public String toString()  {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb);
    double imp = (im > 1.0e-10) ? im : 0.0;
    formatter.format("%6.4f +%6.2fi", re, imp);
    return sb.toString();
}
/**
 * gets real part
 * @return real part
 */
public double getRealPart() { return re; }
/**
 * gets imaginary part
 * @return imaginary part
 */
public double getImaginaryPart() { return im;}
/**
 * return length of vector in polar coordinates [&rho; of (&rho;, &theta;) notation]. | this | 
 * @return absolute value of length.
 */
public double abs() { return Math.sqrt(re*re + im*im);  }

/**
 * implement addition
 * @param b a complex number
 * @return a new object whose value is (this + b)
 */
public Complex plus(Complex b) { 
    Complex a = this;             // invoking object
    double real = a.re + b.re;
    double imag = a.im + b.im;
    Complex sum = new Complex(real, imag);
    return sum;
}
/**
 * implements subtraction
 * @param b a complex number
 * @return  a new object whose value is (this - b)
 */
public Complex minus(Complex b) { 
    Complex a = this;   
    double real = a.re - b.re;
    double imag = a.im - b.im;
    Complex diff = new Complex(real, imag);
    return diff;
}


/**
 * calculate multiplication by a complex number
 * @param b a complex number
 * @return a new object whose value is (this * b)
 */
public Complex times(Complex b) {
    Complex a = this;
    double real = a.re * b.re - a.im * b.im;
    double imag = a.re * b.im + a.im * b.re;
    Complex prod = new Complex(real, imag);
    return prod;
}
/**
 * calculate multiplication by a real number 
 * @param alpha a real number 
 * @return a new object whose value is (this * alpha)
 */
public Complex times(double alpha) {
    return new Complex(alpha * re, alpha * im);
}
/**
 * calculate and return a new object whose value is the conjugate of this
 * @return return a new object whose value is the conjugate of this
 */
public Complex conjugate() {  return new Complex(re, -im); }
/**
 * override equals so it works the way we want
 * @param obj to compare
 * @return true if they are equal
 */
public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != getClass() ) return false;
    Complex objCpx = (Complex) obj;
    return re == objCpx.re && im == objCpx.im;
}
/**
 * override hashCode so we map identical objects
 * to the same value, calculate it from the string 
 * representation.
 * @return hashCode calculated
 */
public  int hashCode() {
    return toString().hashCode();
}
/**
 * compares two Complex, tells if they are essentially equal
 * @param r1 comparison Complex
 * @return true if nearly the same
 */
public boolean almostEquals(Complex r1) {
    if (this == r1) return true;
    if ( AlmostEquals.almostEquals(this.re, r1.re) && AlmostEquals.almostEquals(this.im, r1.im)) return true;
    return false;
}

}

