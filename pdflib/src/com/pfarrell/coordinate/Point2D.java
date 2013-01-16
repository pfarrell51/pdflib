/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

import com.pfarrell.utils.math.AlmostEquals;

/**
 * The <code>Point2D</code> class implements a structure for points in a two dimensional Cartesian coordinate system.
 * @see PointPolar
 * @author pfarrell
 * Created on Dec 27, 2009, 12:33:04 AM
 */
public class Point2D implements PointOnRealPlane<Point2D> {
private final double xVal;
private final double yVal;
/**
 * construct a point in Two-D space
 * @param x x-axis value
 * @param y y-axis value
 */
public Point2D(double x, double y) {
    xVal = x;
    yVal = y;
}
    /**
     * @return the xVal
     */
    public double getX() {
        return xVal;
    }
    /**
     * @return the yVal
     */
    public double getY() {
        return yVal;
    }
/**
 * gets the distance/magnitude betwen this and the other point
 * @param other the other point
 * @return distance (Euclidian)
 */
public double getDistance(Point2D other) {
    double rval = Math.sqrt(  Math.pow((xVal - other.getX()), 2)
                            + Math.pow((yVal - other.getY()), 2) );
    return rval;
}
/**
 * gets the difference (or additive inverse)
 * @param other another Point
 * @return a Point2D that if added to "this" will give the other point
 */
public Point2D subtract(Point2D other) {
    Point2D rval = new Point2D(xVal - other.xVal, yVal - other.yVal);
    return rval;
}
/**
 * add the second point to this one, returning the sum
 * @param other another Point
 * @return the sum of the two Points
 */
public Point2D add(Point2D other) {
    Point2D rval = new Point2D(other.xVal + xVal, other.yVal + yVal);
    return rval;
}
    @Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if ( ! (obj instanceof Point2D)) return false;
    Point2D pointObj = (Point2D) obj;
    if (this.xVal == pointObj.xVal && this.yVal == pointObj.yVal) return true;
    return false;
}

    @Override
public boolean almostEquals(Point2D other) {
    boolean xs = AlmostEquals.almostEquals(this.xVal, other.xVal);
    boolean ys = AlmostEquals.almostEquals(this.yVal, other.yVal);
    return xs && ys;
}
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.xVal) ^ (Double.doubleToLongBits(this.xVal) >>> 32));
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.yVal) ^ (Double.doubleToLongBits(this.yVal) >>> 32));
        return hash;
    }
    /**
     * gets the Polar coordinate representation of this point
     * @return a PointPolar with the same value as this point
     */
    public PointPolar getPolar() {
        PointPolar rval = null;
        if (xVal == 0.0 && yVal == 0.0) {
            rval = new PointPolar(0.0, 0.0);
        } else {
            double rho =  Math.sqrt(  xVal* xVal + yVal * yVal);
            double theta = Math.asin(yVal/ xVal);
            if (xVal < 0) {
                theta = Math.PI - theta;
            }
            rval = new PointPolar(rho, theta);
        }
        return rval;
    }
   /**
    * make a pretty string
    * @return human readible string
    */
    @Override
    public String toString() {
        return String.format("%g,%g", xVal, yVal);
    }
}