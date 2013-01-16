/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 *  based on code from http://www.sourcecodesworld.com/source/show.asp?ScriptID=807
 */

package com.pfarrell.stat;

import com.google.common.base.Preconditions;

import com.pfarrell.coordinate.Point2D;
import com.pfarrell.utils.math.AlmostEquals;
import org.apache.log4j.Logger;

/**
 * The <code>Centroid</code> class represents the Centroid for a Cluster. The initial centroid is calculated
 * using a equation which divides the sample space for each dimension into equal parts
 * depending upon the value of k.
 * @author Shyam Sivaraman
 * @see Cluster
 * @author pfarrell
 * Created on Dec 24, 2009, 11:08:50 AM
 */
public class Centroid {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(Centroid.class);
private Point2D calcMean = new Point2D(0.0, 0.0);
private final Cluster cluster;

    /**
     * Construct a Centroid
     * @param owningCluster the Cluster that owns this
     */
    public Centroid(Cluster owningCluster) {
        Preconditions.checkNotNull(owningCluster);
        cluster = owningCluster;
    }
/**
 * calculate the coordinates of this Centoid from those of its member points.
 * only called by {@link BasicClusterAnalysis} Instance
 */
    public void calcCentroid() {
        Preconditions.checkState(cluster != null);
        int numDP = cluster.getNumDataPoints();
        double tempX = 0, tempY = 0;
        //recalc center point
        for (DataPoint dp : cluster.getDataPoints()) {
            dp.setCluster(cluster);
            tempX += dp.getX();
            tempY += dp.getY();
        }
        double newX = tempX/numDP;
        double newY = tempY/numDP;
        Point2D possNew = new Point2D(newX, newY);

        if (! calcMean.almostEquals(possNew)) {
            aLog.debug(String.format("centroid old %s will set to %s", calcMean.toString(), possNew.toString() ));
            calcMean = possNew;

            for (DataPoint dp : cluster.getDataPoints()) {
                dp.calcEuclideanDistance();
            }
        }
        //calculate the new Sum of Squares for the Cluster
        cluster.calcSumOfSquares();
    }
    /**
     * gets the X-axis coordinate of this Centroid
     * @return  the X-axis coordinate of this Centroid
     */
    public double getCx() {
        return calcMean.getX();
    }
    void setPoint(Point2D arg) {
        calcMean = arg;
    }
    public Point2D getPoint() {
        return calcMean;
    }
    /**
     * the Y-axis coordinate of this Centroid
     * @return the Y-axis coordinate of this Centroid
     */
    public double getCy() {
        return calcMean.getY();
    }
    /**
     * gets the Cluster associated with this Centroid
     * @return the Cluster associated with this Centroid
     */
    public Cluster getCluster() {
        return cluster;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (cluster == null) {
        sb.append(cluster.getName()).append(":");
        } else {
            sb.append("null cluster:");
        }
        sb.append(calcMean.toString());
        return sb.toString();
    }
}
