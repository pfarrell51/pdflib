/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.coordinate;

/**
 * The <code>Point3D</code> class implements a structure for points in a three dimensional Cartesian coordinate system.
 * @author pfarrell
 * Created on Dec 27, 2009, 12:33:22 AM
 */
public class Point3D {
private final double xVal;
private final double yVal;
private final double zVal;

public Point3D(double x, double y, double z) {
    xVal = x;
    yVal = y;
    zVal = z;
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
     * @return the yVal
     */
    public double getZ() {
        return zVal;
    }
}