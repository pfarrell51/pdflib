/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 *  based on code from http://www.sourcecodesworld.com/source/show.asp?ScriptID=807
 */

package com.pfarrell.stat;

import com.google.common.base.Preconditions;

import com.pfarrell.coordinate.Point2D;

/**
 * The <code>DataPoint</code> class implements a structure to hold a datapoint for Cluster Analysis
 * @see Cluster
 * @see BasicClusterAnalysis
 * @author pfarrell
 * Created on Dec 24, 2009, 11:10:26 AM
 */
public class DataPoint extends Point2D {

    private final String pointName;
    private Cluster cluster = null;

    /**
     * constuctor for one DataPoint
     * @param x x-axis coordinate
     * @param y y-axis coordinate
     * @param name name of point
     */
    public DataPoint(double x, double y, String name) {
        super(x,y);
        Preconditions.checkNotNull(name);
        pointName = name;
    }

    /**
     * sets the cluster assignment for this point
     * @param cluster the new cluster
     */
    public void setCluster(Cluster cluster) {
        Preconditions.checkNotNull(cluster);
        this.cluster = cluster;
    }
/**
 * calculates and stores the euclidean distance, sqrt of delta squared for x plus y
 * called when DP is added to a cluster or when a Centroid is recalculated.
 * @param other the other point
 * @return distance to center of cluster's centroid (always positive)
 */
    public double calcEuclideanDistance(DataPoint other) {
        return getDistance(other);
    }
    /**
     * calculates and returns Euclidean distance from this point to the center of its assigned Cluster's centroid
     * @return distance from center of Centroid
     */
    public double calcEuclideanDistance() {
        Preconditions.checkState(cluster != null);
        Centroid c = cluster.getCentroid();
        Point2D centroidPoint = new Point2D(c.getCx(), c.getCy());
        return getDistance(centroidPoint);
    }

    /**
     * calculates and returns distance from center of Centroid of the argument cluster
     * @param cl a Cluster, we will use its Centroid
     * @return distance
     */
    public double getEuclideanDistanceToClusterCentroid(Cluster cl) {
        Preconditions.checkNotNull(cl);
        Centroid c = cl.getCentroid();
        Point2D centroidPoint = new Point2D(c.getCx(), c.getCy());
        return getDistance(centroidPoint);
    }
    /**
     * gets rho (radius) in polar coordinate system
     * @param o the Other data point
     * @return the radious
     */
    public double getRho(DataPoint o) {
        return calcEuclideanDistance(o);
    }
    /**
     * gets the theta (angle) in polar coordinate system
     * @param o other Data point
     * @return value of theta (from -pi to pi
     */
    public double getTheta(DataPoint o) {
        double rval = 0;
        double deltaX = getX() - o.getX();
        double deltaY = getY() - o.getY();
        rval = Math.atan2(deltaY, deltaX);
        return rval;
    }
    /**
     * gets the cluster that this point is associated with
     * @return the Cluster
     */
    public Cluster getCluster() {
        return cluster;
    }

    /**
     * get name
     * @return name
     */
    public String getName() {
        return pointName;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(":");
        sb.append(getX()).append(",");
        sb.append(getY());
        return sb.toString();
    }
}