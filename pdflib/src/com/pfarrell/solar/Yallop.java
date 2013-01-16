/*
 * Copyright (C) 2010 Patrick Farrell
 * Portions of this code reproduced, with permission, from data supplied by HM Nautical
 * Almanac Office, UKHO © Crown Copyright
 */

package com.pfarrell.solar;

import com.pfarrell.utils.misc.TimeUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 * The <code>Yallop</code> class implements the algorithms desctibed in
 * <i>A simple algorithm to calculate times of sunrise and sunset</i> by
 * B.D. Yallop<br>
 * HM Nautical Almanac Office<br>
 * UK Hydrographic Office<br>
 * Admiralty Road, Taunton<br>
 * TA1 2DN England<br>
 *
 * @author pfarrell
 * Created on Nov 12, 2010, 1:02:03 AM
 */
public class Yallop {
     /** logger instance */
private static final Logger yLog = Logger.getLogger(Yallop.class);
public static final double deg2rad = Math.PI/180.0;
   /**
    * This is a simple ephemeris for calculating the Greenwich Hour Angle (GHA)
    * and declination (Dec) of the Sun between 1980 and 2050.
    * @return the calculated ephemeris
    */
public Ephemeris getEphemeris(Calendar arg) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
    yLog.debug(TimeUtils.toRfc822(arg));
    double gha = 0.0F;
   // universal time (UT)
    double UT = arg.get(Calendar.HOUR_OF_DAY) + arg.get(Calendar.MINUTE)/60.0D
            + arg.get(Calendar.SECOND)/3600.0D;
    yLog.debug("UT: " + UT);
    int m = arg.get(Calendar.MONTH)+1;    // january is 0
    int y = arg.get(Calendar.YEAR);
    int d = arg.get(Calendar.DATE);
    //If m > 2 then a = y and b = m − 3 otherwise a = y − 1 and b = m + 9
    int a,b;
    if (m > 2) {
        a = y;
        b = m - 3;
    } else {
        a = y - 1;
        b = m + 9;
    }
    yLog.debug("y: " + y + " m: " + m + " d: " + d );
    yLog.debug("a: " + a + " b: " + b );
    //t = (UT / 24 + d + [30 ⋅ 6 b + 0 ⋅ 5] + [365 ⋅ 25( a − 1976)] − 8707 ⋅ 5) / 36525
    double t = (UT/24.0 + d + (30.6*b + 0.5) + (365.25*(a-1976)) - 8707.5)/ 36525.0;
    yLog.debug(String.format("t: %f6",t));
    //  Eps = 23 ⋅ 4393 − 0 ⋅ 013t
    double epsilon = wrap360(23.4393 - 0.013 * t);
    yLog.debug("epsilon: " + epsilon);

    // G = 357 ⋅ 528 + 35999 ⋅ 050t
    double g = 357.528 + (35999.050 * t);
    yLog.debug("G: " + g );
    // C = 1 ⋅ 915 sin G + 0 ⋅ 020 sin 2 G
    double c = wrap360(1.915*Math.sin(g*deg2rad) + 0.020*Math.sin(2*g*deg2rad));
    yLog.debug("c: " + c);
    // L = 280 ⋅ 460 + 36000 ⋅ 770t + C
    double el = wrap360(280.460 + 36000.770* t + c);
    yLog.debug("el: " + el);
    // Right Ascension
    // RA = L − 2 ⋅ 466 sin 2 L + 0 ⋅ 053 sin 4 L
    double ra = el - 2.446*Math.sin(2*el*deg2rad) + 0.053*Math.sin(4*el*deg2rad);
    yLog.debug("ra: " + ra);
    // GHA = 15UT − 180 − C + L − RA
    gha = 15.0*UT - 180 - c + el - ra;
    yLog.debug("GHA: " + gha);
    //    Dec = tan^−1 (tan Eps sin RA)
    double dec = Math.tan(epsilon*Yallop.deg2rad) * Math.sin(ra*Yallop.deg2rad);
    dec /= Yallop.deg2rad;
    yLog.debug("dec: " + dec);
    Ephemeris rval = new Ephemeris((float)gha, (float) dec);
    return rval;
}
double wrap360(double arg) {
    double rval = arg % 360.0;
    if (rval < 0.0) {
        rval += 360.0;
    }
    return rval;
}
public float calculateSunset(float longitude, float lattitude, Calendar when) {
    return calculateUpDown(false, longitude, lattitude, when);
}
public float calculateSunrise(float longitude, float lattitude, Calendar when) {
    return calculateUpDown(true, longitude, lattitude, when);
}
private float calculateUpDown(boolean up, float longitude, float lattitude, Calendar when) {
    float estimate = up ? 6.0f : 18.0f;
    float nextEst = estimate;

    for (int pass = 0; pass < 10; pass++) {
        Calendar working = setEstimatedTime(when, estimate);
        yLog.debug(String.format("pass %d estimate: %f time: %tc", pass, estimate, working));
        Ephemeris eph =  getEphemeris(working);
        float gha = eph.gha;
        float dec = eph.dec;
        float lha  = doOneLhaPass(lattitude, dec);
        yLog.debug("lha " + lha);
        //T1 = (15T0 − Long + LHA − GHA) / 15, for set.
        double subAngles = (float) wrap360((double) (up ? -1 : 1)*longitude -lha -gha);
        yLog.debug("longitude -lha -gha: " + subAngles);
        nextEst = (float) ( (15.0*estimate  + subAngles) / 15.0f ) % 24.0f;
        while (nextEst < 0.0) nextEst += 24.0;
        yLog.debug("next est : " + nextEst);
        if (Math.abs(nextEst - estimate) < 0.008f) {
            break;
        } else {
            estimate = nextEst;
        }
    }
    return nextEst;
}
private float doOneLhaPass(float latitude, float dec) {
    double latr = latitude*Yallop.deg2rad;
    //sin Lat = 0 ⋅ 8403
    double sinLat = Math.sin(latr);
    //cos Lat = 0 ⋅ 5422
    double cosLat = Math.cos(latr);
    double decr = dec*Yallop.deg2rad;
    //sin Dec = 0 ⋅ 2895
    double sinDec = Math.sin(decr);
    double cosDec = Math.cos(decr);
    System.out.printf("sl: %f cl: %f,  sD %f, cD %f\n", sinLat, cosLat, sinDec, cosDec);

    //cos LHA = ( −0 ⋅ 01454 − sin Lat sin Dec ) / (cos Lat cos Dec)
    double cosLHA = (-0.01454 -sinLat *  sinDec) / (cosLat * cosDec);
    System.out.println("cosLHA " + cosLHA);
    double LHA =0.0f;
    //if cos LHA > +1 set LHA = 0 , if cos LHA < −1 set LHA =180° .
    if (cosLHA > 1.0f) {
        LHA = 0.0f;
    } else if (cosLHA < -1.0f)  {
        LHA = 180.0f;
    } else {
        LHA = Math.acos(cosLHA)/Yallop.deg2rad;
    }
    System.out.println("LHA " + LHA);
    return (float) LHA;
}
private Calendar setEstimatedTime(Calendar orig, float est) {
    Calendar rval = (Calendar) orig.clone();
    int hour = (int) Math.floor(est);
    int mins = (int) ((est - hour) *60.0);
    rval.set(Calendar.HOUR_OF_DAY, hour);
    rval.set(Calendar.MINUTE, mins);
    return orig;
}

public static class Ephemeris {
    float gha;
    float dec;
    public Ephemeris(float g, float d) {
        gha = g;
        dec = d;
    }
}
}
