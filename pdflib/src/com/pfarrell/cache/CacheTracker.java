/*
 * Copyright (C) 2011 Wayfinder Digital LLC. All Rights reserved.
 */
package com.pfarrell.cache;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * The <code>CacheTracker</code> class implements a singleton to keep track of the WorkingSetCaches in use.
 * @author pfarrell
 * Created on Sep 1, 2011, 1:17:36 AM
 */
public final class CacheTracker {

    /** logger instance */
    private static final Logger ctLog = Logger.getLogger(CacheTracker.class);
    

    /** keep list of caches so we can easily kill off threads */
private  List<AbstractWSCache<?,?>> knownCaches;
private  ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

CacheTracker() {
    knownCaches = new  ArrayList<AbstractWSCache<?,?>>();
}

final List<AbstractWSCache<?,?>> getKnownCaches() {
    List<AbstractWSCache<?,?>> rval = ImmutableList.of();
    lock.readLock().lock();
    try {
        rval = ImmutableList.copyOf(knownCaches);
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
final void add(AbstractWSCache<?,?> arg) {
    lock.writeLock().lock();
    try {
        knownCaches.add(arg);    
    } finally {
        lock.writeLock().unlock();
    }
}
boolean isEmpty() {
    boolean rval = true;
    lock.readLock().lock();
    try {
        rval = knownCaches.isEmpty();
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
void clear() {
    boolean gotLock = lock.writeLock().tryLock();
    if ( ! gotLock) {
        ctLog.fatal("can't get lock in Clear");
    }
    try {
        for (AbstractWSCache<?,?> w : getKnownCaches() ) {
            w.myThread.interrupt();
        }
        knownCaches.clear();
    } finally {
        lock.writeLock().unlock();
    }
}
  /**
   * public utility to close all cache threads
   */
public void closeAllThreads() {
    boolean gotLock = lock.writeLock().tryLock();
    if ( ! gotLock) {
        ctLog.fatal("can't get lock in CloseAllThreads");
    }
    try {
        if ( this.isEmpty()) return;
        for (AbstractWSCache<?,?> w : getKnownCaches() ) {
            w.myThread.interrupt();
        }
        clear();
    } finally {
        lock.writeLock().unlock();
    }

}

}
