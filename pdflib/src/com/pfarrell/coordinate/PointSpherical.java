/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

/**
 * The <code>PointSpherical</code> class implements a structure for points in a three dimensional coordinate system.
 * The conversion from the Polar corrdinate system to the Cartesian coordinate system is simple and well defined.
 * Naming of constructor arguments follows ISO standard 33-11.
 * @author pfarrell
 * Created on Dec 27, 2009, 12:33:49 AM
 */
public class PointSpherical {
private final double radius;
private final double inclination; // inclination (or elevation),
private final double azimuth;
/**
 * construct a PointSpherical, using ISO standard 33-11 notation for inclination and azimuth
 * @param rho radius
 * @param theta inclination
 * @param phi azimuth
 */
public PointSpherical(double rho, double theta, double phi) {
    radius = rho;
    inclination = theta;
    azimuth = phi;
}
    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }
    /**
     * @return the theta/inclination
     */
    public double getTheta() {
        return inclination;
    }
    public double getInclination() {
        return inclination;
    }
public double getAzimuth() {
    return azimuth;
}
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.radius) ^ (Double.doubleToLongBits(this.radius) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(getInclination()) ^ (Double.doubleToLongBits(getInclination()) >>> 32));
        hash = 43 * hash + (int) (Double.doubleToLongBits(getAzimuth()) ^ (Double.doubleToLongBits(getAzimuth()) >>> 32));
        return hash;
    }
    @Override
public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if ( ! (obj instanceof PointSpherical)) return false;
    PointSpherical otherPoint = (PointSpherical) obj;
    if (this.radius == otherPoint.radius && this.getInclination() == otherPoint.getInclination() &&
            this.getAzimuth() == otherPoint.getAzimuth())  return true;
    return false;
}
/**
 * gets the cartsian Point2D corresponding to this point
 * @return a Point2D corresponding to this point
 */
    public Point3D getCartesian() {
        double x = radius * Math.sin(getInclination()) * Math.cos(getAzimuth()) ;
        double y = radius * Math.sin(getInclination()) * Math.sin(getAzimuth());
        double z = radius * Math.cos(getInclination());
        Point3D rval = new Point3D(x, y, z);
        return rval;
    }


}