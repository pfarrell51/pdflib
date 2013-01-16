/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

import com.pfarrell.utils.math.AlmostEquals;

/**
 * The <code>PointPolar</code> class implements a structure for points in a two dimensional coordinate system.
 * The conversion from the Polar corrdinate system to the Cartesian coordinate system is simple and well defined.
 * @see Point2D
 * @author pfarrell
 * Created on Dec 27, 2009, 12:33:36 AM
 */
public class PointPolar implements PointOnRealPlane<PointPolar> {

private final double radius;
private final double theta;

/**
 * construct a PolarPoint
 * @param rho radius
 * @param th angle/theta
 */
public PointPolar(double rho, double th) {
    radius = rho;
    double tmpTheta = th % (2*Math.PI);
    if (tmpTheta > Math.PI)
        tmpTheta -= Math.PI;
    theta = tmpTheta;
}
    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return the theta
     */
    public double getTheta() {
        return theta;
    }
    public double getDistance(PointPolar other) {
        return this.getCartesian().getDistance(other.getCartesian());
    }

    public PointOnRealPlane subtract(PointPolar other) {
        return this.getCartesian().subtract(other.getCartesian());
    }

    public PointOnRealPlane add(PointPolar other) {
        return this.getCartesian().add(other.getCartesian());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.radius) ^ (Double.doubleToLongBits(this.radius) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.theta) ^ (Double.doubleToLongBits(this.theta) >>> 32));
        return hash;
    }
    @Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if ( ! (obj instanceof PointPolar)) return false;
    PointPolar otherPoint = (PointPolar) obj;
    if (this.radius == otherPoint.radius && this.theta == otherPoint.theta)  return true;
    return false;
}
/**
 * gets the cartsian Point2D corresponding to this point
 * @return a Point2D corresponding to this point
 */
    public Point2D getCartesian() {
        double x = radius * Math.cos(theta);
        double y = radius * Math.sin(theta);
        Point2D rval = new Point2D(x, y);
        return rval;
    }
   /**
    * make a pretty string
    * @return human readible string
    */
    @Override
    public String toString() {
        return String.format("r: %g t: %g", radius, theta);
    }
    
    @Override
    public boolean almostEquals(PointPolar other) {
        boolean rs = AlmostEquals.almostEquals(this.radius, other.radius);
        boolean ts = AlmostEquals.almostEquals(this.theta, other.theta);
        return rs && ts;
    }
}