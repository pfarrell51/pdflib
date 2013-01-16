/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

/**
 * The <code>PointOnRealPlane</code> interface defines common functions for points on a mathematical plane.
 *
 * @author pfarrell
 * Created on Dec 27, 2009, 3:19:59 PM
 */
public interface PointOnRealPlane<T extends PointOnRealPlane> {
/**
 * gets the distance/magnitude betwen this and the other point
 * @param other the other point
 * @return distance (Euclidian)
 */
public double getDistance(T other);
/**
 * gets the difference (or additive inverse)
 * @param other another Point
 * @return a Point2D that if added to "this" will give the other point
 */
public PointOnRealPlane subtract(T other);
/**
 * add the second point to this one, returning the sum
 * @param other another Point
 * @return the sum of the two Points
 */
public PointOnRealPlane add(T other);
/**
 * tests if this point is essentially equal to the argument point
 * @param other another Point
 * @return true if each of X and Y passes {@link com.pfarrell.utils.math.AlmostEquals} test.
 */
boolean almostEquals(T other);

    @Override
public boolean equals(Object obj);
    @Override
public int hashCode();
}
