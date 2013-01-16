/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 */

package com.pfarrell.stat;

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * The <code>Descriptives</code> class implements functions to calculate
 * standard deviation of a collection of data.
 * 
 * @author pfarrell
 * Created on Jan 15, 2011, 8:33:13 PM
 */
public class Descriptives<T extends Number> {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(Descriptives.class);
private final Collection<T> data;
private double minimum = Double.MAX_VALUE;
private double maximum = Double.NEGATIVE_INFINITY;
private double mean;
private double stdev;
private double stdevp;

public Descriptives(Collection<T> arg) {
    data = arg;
}
public void doWork() {
    if (minimum != Double.MAX_VALUE) return;
    calcMean();
    stdev = calcStdDev();
    stdevp = calcStdDevPop();
}
/**
   * Calculates the sample standard deviation of an array
   * of numbers,  when the number are obtained by a random sampling.
   *
   * @param data Numbers to compute the standard deviation of.
   * Array must contain two or more numbers.
   * @return standard deviation estimate of population
   */
private double calcStdDev()  {
    return calcStdDev(true);
}
/**
   * Calculates the sample standard deviation of an array
   * of numbers,  when the number are the entire population.
   *
   * @param data Numbers to compute the standard deviation of.
   * Array must contain two or more numbers.
   * @return standard deviation estimate of population
   */
private double calcStdDevPop() {
    return calcStdDev(false);
}
private void calcMean() {
    double sum = 0;
    int n = 0;
    Iterator<T> it = data.iterator();
    while ( it.hasNext() ) {
        n++;
        double val = it.next().doubleValue();
        sum += val;
        if (val < minimum) minimum = val;
        if (val > maximum) maximum = val;
    }
    mean = sum/n;
}
public double getMean() {
    doWork();
    return mean;
}
    /**
     * @return the stdev
     */
public double getStdev() {
    doWork();
    return stdev;
}

    /**
     * @return the stdevp
     */
public double getStdevp() {
    doWork();
    return stdevp;
}
    /**
     * @return the minimum
     */
    public double getMinimum() {
        doWork();
        return minimum;
    }

    /**
     * @return the maximum
     */
    public double getMaximum() {
        doWork();
        return maximum;
    }

private double calcStdDev( boolean sample)  {
    final int n = data.size();
    if ( n < 2 )  {
        return Double.NaN;
    }
    double avg = getMean();
    double sumSq = 0.0;
    Iterator<T> it = data.iterator();
    while ( it.hasNext() ) {
        double dataI = it.next().doubleValue();
        double delta = ( dataI - avg );
        sumSq += delta*delta;
    }
    int divisor = sample ? n-1 : n;
    return Math.sqrt( sumSq / divisor );
}

}
