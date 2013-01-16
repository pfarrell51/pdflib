/*
 * Credits: Bruce Dawson,  Ernest Friedman-Hill, Pat Farrell
 */

package com.pfarrell.utils.math;

/**
 * The <code>AlmostEquals</code> class is a abstract   class  that implements functions to compare
 * floating point and double precision numbers for almost equals.
 *
 * @see <a href=http://docs.sun.com/source/806-3568/ncg_goldberg.html>
 * http://docs.sun.com/source/806-3568/ncg_goldberg.html</a>
 * @see <a href=http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm>
 * http://www.cygnus-software.com/papers/comparingfloats/comparingfloats.htm</a>
 * 
 * @author pfarrell, Ernest Friedman-Hill, Bruce Dawson 
 */
public abstract class AlmostEquals {
private static final double maxAbsoluteError = 0.1e-12;
private static final double maxRelativeError = 0.1e-4;

//AlmostEqual2sComplement
/**
 *  compare two float values to see if they are close to being the same
 * @param A first double
 * @param B second double
 * @return true if close enough to equals
 */
    public static boolean almostEquals(float A, float B) {
        return almostEquals(A, B,  600*1000);
    }
/**
 *  compare two float values to see if they are close to being the same
 * @param A first double
 * @param B second double
 * @param maxUlps the maximum error in terms of Units in the Last Place
 * or "ulp" stands for "unit of least precision". A difference of one ulp between two floats
 * indicates that they're "adjacent" floats; that there's no value in between them.
 * @return true if close enough to equals
 */    
    public static boolean almostEquals(float A, float B, int maxUlps) {
        if (Math.abs(A - B) < maxAbsoluteError)   return true;        
        // Make sure maxUlps is non-negative and small enough that the                    
        // default NAN won't compare as equal to anything.                                
        assert maxUlps < 4 * 1024 * 1024;
        assert maxUlps > 0 && maxUlps < 4 * 1024 * 1024;                                  

        // Make aInt lexicographically ordered as a twos-complement int                   
        int aInt = Float.floatToIntBits(A);                                               
        if (aInt < 0)                                                                     
            aInt = 0x80000000 - aInt;                                                     

        // Make bInt lexicographically ordered as a twos-complement int                   
        int bInt = Float.floatToIntBits(B);                                               
        if (bInt < 0)                                                                     
            bInt = 0x80000000 - bInt;                                                     

        int intDiff = Math.abs(aInt - bInt);                                              

        if (intDiff <= maxUlps)                                                           
            return true;                                                                  

        return false;                                                                     
    }                    
/**
 * compare two double values to see if they are close to being the same
 * @param A first double
 * @param B second double
 * @return true if close enough to equals
 */
    public static boolean almostEquals(double A, double B) {        
        return almostEquals(A, B, 4*1000*1000);
    }
/**
 *  compare two double values to see if they are close to being the same
 * @param A first double
 * @param B second double
 * @param maxUlps  the maximum error in terms of Units in the Last Place
 * @return true if close enough to equals
 */    
    public static boolean almostEquals(double A, double B, int maxUlps) {        
        if (Math.abs(A - B) < maxAbsoluteError)   return true;        

        // Make sure maxUlps is non-negative and small enough that the                    
        // default NAN won't compare as equal to anything.                                

        assert maxUlps < 4 * 1024 * 1024;
        assert maxUlps > 0 && maxUlps < 4 * 1024 * 1024;                                  

        // Make aInt lexicographically ordered as a twos-complement int                   
        long aInt = Double.doubleToLongBits(A);                                               
        if (aInt < 0)                                                                     
            aInt = 0x8000000000000000L - aInt;                                                     

        // Make bInt lexicographically ordered as a twos-complement int                   
        long bInt = Double.doubleToLongBits(B);                                               
        if (bInt < 0)                                                                     
            bInt = 0x8000000000000000L - bInt;                                                     

        long intDiff = Math.abs(aInt - bInt);                                              

        if (intDiff <= maxUlps)                                                           
            return true;                                                                  

        return false;                                                                     
    }                    
/**
 * compare two values to see if they are close to being the same
 * @param r1 one number
 * @param r2 another candidate
 * @return true if very close to equal
 */
static boolean almostEquals2(double r1, double r2) {
    if (r1 == Double.NaN || r2 == Double.NaN) return false;     // per IEEE spec
    if (Math.abs(r1 - r2) < maxAbsoluteError)      return true;
    double relErr = 0.0;
    if ( Math.abs(r2) > Math.abs(r1) ) {
        relErr = Math.abs(r1 - r2) / r2;
    } else {
        relErr = Math.abs(r1 - r2) / r1; 
    }
    if (relErr <= maxRelativeError)
        return true;
    return false;
}
}
