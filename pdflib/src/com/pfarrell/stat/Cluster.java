/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 * based on code by Shyam Sivaraman from http://www.sourcecodesworld.com/source/show.asp?ScriptID=807
 */

package com.pfarrell.stat;


import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * The <code>Cluster</code> class implements a Cluster in a Cluster Analysis Instance. A Cluster is associated
 * with one and only one JCA Instance. A Cluster is related to more than one DataPoints and
 * one centroid.
 * based on code from http://www.sourcecodesworld.com/source/show.asp?ScriptID=807
 * @see DataPoint
 * @see Centroid
 * @author pfarrell
 * Created on Dec 24, 2009, 11:08:00 AM
 */
public class Cluster {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(Cluster.class);
    private final String name;
    private Centroid aCentroid;
    private Set<DataPoint> dataPoints = new HashSet<DataPoint>();
    private boolean needCalc = false;
    private double calcedSumSquare;

    /**
     * constructor for Cluster
     * @param narg name to use
     */
    public Cluster(String narg) {
        name = narg;
        aCentroid = new Centroid(this);
    }
    /**
     * gets the Centroid for this cluster
     * @return the Centroid
     */
    Centroid getCentroid() {
        return aCentroid;
    }
    public void calcCentroid() {
        Preconditions.checkState(aCentroid != null);
        aCentroid.calcCentroid();
    }
    /**
     * add/associate a datapoint with this Cluster
     * @param dp the DataPoint
     */
    public void addDataPoint(DataPoint dp) {
        dp.setCluster(this); //initiates a inner call to calcEuclideanDistance() in DP.
        dataPoints.add(dp);
        needCalc = true;
    }
   /**
    *  add/associate all the datapoints in argument list with this Cluster
    * @param argList list of DataPoints
    */
    public void addAll(List<DataPoint> argList) {
        for (DataPoint dp :  argList) {
            dp.setCluster(this); //initiates a inner call to calcEuclideanDistance() in DP.
            dataPoints.add(dp);
        }
        needCalc = true;
    }
    /**
     * remove the argument datapoint from this Cluster
     * @param dp the Datapoint
     */
    public void removeDataPoint(DataPoint dp) {
        if (dataPoints.size() > 1) {
            dataPoints.remove(dp);
            needCalc = true;
        } else {
            aLog.debug("will not remove last point");
        }
    }

    /**
     * gets number of points associated here
     * @return number of points
     */
    public int getNumDataPoints() {
        return dataPoints.size();
    }
    /**
     * calcuate the sum of the squares of the distance metric
     */
    public double calcSumOfSquares() {
        calcedSumSquare = 0;
        for ( DataPoint dp : dataPoints) {
            calcedSumSquare += dp.calcEuclideanDistance();
        }
        needCalc = false;
        return calcedSumSquare;
    }
   /**
    * gets the sum of the squares, and calcualtes it fresh if needed
    * @return the sum of the squares
    */
 public double getSumOfSquares() {
    if (needCalc) {
        calcedSumSquare = calcSumOfSquares();
    }
    return calcedSumSquare;
 }
    /**
     * gets the name
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * gets the List of points
     * @return List of points
     */
    public ImmutableList<DataPoint> getDataPoints() {
        return ImmutableList.copyOf(dataPoints);
    }

}
