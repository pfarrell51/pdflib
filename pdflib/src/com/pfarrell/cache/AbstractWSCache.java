/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.cache;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractWSCache</code> class implements a base class for use by concrete implementations of
 * a DenningCache.
 *
 * @author pfarrell
 * Created on Nov 20, 2009, 3:07:21 PM
 */
public abstract  class AbstractWSCache<K, V>  implements DenningCache<K, V>  {
     /** logger instance */
protected static final Logger awscLog = Logger.getLogger(AbstractWSCache.class);
public static final String defaultName = "WorkingSetCache";
public static final long MINIMUM_SLEEPTIME = TimeUnit.SECONDS.toMillis(1);
        /** lock for thread safety */
protected ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /** the sweep thread */
protected Thread myThread;
    /** interval (in milliseconds) for the sweep thread to nap */
    protected long mySweepInterval;

    /** number of sweep cycles we have done */
    protected long numSweeps = 0;
    /** number of values found (hits) */
    protected long numHits = 0;
    /** number of misses */
    protected long numMiss = 0;
    /** count of terribly slow searches performed */
    protected long numSlowSearches = 0;
/** universal flag for continued running */
protected AtomicBoolean okFlag = new AtomicBoolean(true);
/** type of most recent object "put" to cache */
protected String breadcrumb;

    /** our internal data map */
protected ConcurrentHashMap<K, WorkingSetObject<V>> theCache =  new ConcurrentHashMap<K, WorkingSetObject<V>>();
    
/** default constructor is package private, only the Factory should create them */
protected AbstractWSCache() {
    this(TimeUnit.MINUTES.toMillis(2), null);
}
/**
 * constructor is package private, only the Factory should create them
 * @param nap milli second nap time
 * @param instance object that is used for naming thread.
 */
protected AbstractWSCache(long nap, V instance) {
    if (nap < MINIMUM_SLEEPTIME) {
        awscLog.fatal("AWC nap too short");
        Preconditions.checkArgument(nap > MINIMUM_SLEEPTIME, "AWC nap too short");
    }
    myThread = new Thread(this);
    if (instance == null) {
        myThread.setName(defaultName);
    } else {
        setThreadName(instance.getClass());
    }
    myThread.setDaemon(true);
    mySweepInterval = nap;
    CacheFactory.addToTracker(this);
    start();
}
public abstract ImmutableSet<V> getValues();
protected abstract void processPass();
/**
 * sets the thread name, handy for debugging
 * @param clz the class name, should be the same a V
 */
    public void setThreadName(Class clz) {
        Preconditions.checkNotNull(clz);
        myThread.setName("aWSC:" + clz.getSimpleName());
    }
/**
 * {@inheritDoc}
 * @return {@inheritDoc}
 */
    public String getName() {
        String rval = myThread.getName();
        if (rval.equals(defaultName) && breadcrumb != null) {
            rval = "WSC:d:" + breadcrumb;
            myThread.setName(rval);
        }
        return rval;
    }
/**
 * start sweeping, protected so only subclasses can call it
 */
    protected final void start() {
        myThread.start();
    }
/**
 * basic cache sweeper thread.
 * Note: envoking programs do <i>not</i> need to explicitly call this.
 */
    public void run() {
        while (okFlag.get()) {
            try {
                Thread.sleep(mySweepInterval);
                numSweeps++;
                processPass();
            } catch (InterruptedException e) {
                okFlag.set(false);
            }
        }
    }
    /**
     * stops thread.
     */
    public void shutdown() {
        okFlag.set(false);
        myThread.interrupt();
    }
/**
 * Safely starts this thread.
 */
public void ensureRunning() {
    if (myThread.isAlive()) return;
    start();
}

/**
 * @return gets naptime
 */
public long getNaptime() {
    return mySweepInterval;
}
/**
 * return number of entries in the cache
 * @return number of entries in the cache
 */
public  int size() { return theCache.size();}
/**
 * Returns true if this cache contains no key-value mappings.
 * @return  true if this cache contains no key-value mappings.
 */
public boolean isEmpty() { return theCache.isEmpty();}

/**
 * Removes all entries in this cache.
 */
public void clear() {
    theCache.clear();
}
/**
 * returns a hashtable of handy-dandy statistics useful for calculating  and reporting
 * things like ratio of hits to misses, so the sweep cycle can be adjusted.
 * @return hashtable of handy-dandy statistics
 */
public  HashMap<String, Object> getStatistics() {
    HashMap<String, Object> rval = new HashMap<String, Object>(10);
    rval.put("interval", new Long(mySweepInterval));
    rval.put("sweeps", new Long(numSweeps));
    rval.put("hits", new Long(numHits));
    rval.put("misses", new Long(numMiss));
    rval.put("size", new Long(theCache.size()));
    rval.put("slowsrch", new Long (numSlowSearches ));
    rval.put("approxActiveCount", new Long(size()));
    return rval;
}
/**
 * write out a quick dump of the cache contents and status
 */
public  void quickDump(PrintWriter pw) {
    pw.println("quick dump");
    lock.readLock().lock();
    try {
        Collection wsoColl = theCache.values();
        for (Iterator it = wsoColl.iterator(); it.hasNext(); ) {
            WorkingSetObject wso =  (WorkingSetObject) it.next();
            pw.println(( wso.obj == null ) ? "null" :  wso.obj.toString() + " used: " + wso.usedThisCycle);
        }
    } finally {
        lock.readLock().unlock();
    }
    pw.println("stats: " + getStatistics().toString() +"\n");
}
    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this cache. Neither the key nor the
     * value can be <code>null</code>.
     * <p>
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     * @return the previous value of the specified key in this cache,
     *             or <code>null</code> if it did not have one.
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     * @param key the cache key.
     * @param value the value.
     */
    public V put(K key, V value) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        WorkingSetObject<V> rval = null;
        lock.writeLock().lock();
        try {
            if (value != null && breadcrumb == null) breadcrumb = value.getClass().getSimpleName();
            WorkingSetObject<V> wso = new WorkingSetObject<V>( value );
            rval = theCache.put(key, wso);
        } finally {
            lock.writeLock().unlock();
        }
        return (rval != null) ? rval.get() : null;
    }
    /**
     * Removes the key (and its corresponding value) from this
     * cache. This method does nothing (but accounting) if the key is not in the cache.
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the key had been mapped in this cache,
     *          or <code>null</code> if the key did not have a mapping.
     */
    public V remove(K key) {
        Preconditions.checkNotNull(key);
        WorkingSetObject<V> wso = null;
        lock.writeLock().lock();
        try {
            wso = theCache.remove(key);
            if (wso == null ) {
                numMiss++;
            } else {
                numHits++;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return  wso == null ? null :  wso.get();
    }
    /**
     * Returns the value to which the specified key is mapped in this cache.
     *
     * @param   key   a key in the cache.
     * @return  the value to which the key is mapped in this cache;
     *          <code>null</code> if the key is not mapped to any value in
     *          this cache.
     * @see     java.util.Hashtable#get(java.lang.Object)
     */
    public V get(K key) {
        Preconditions.checkNotNull(key);
        WorkingSetObject<V> wso = null;
        lock.readLock().lock();
        try {
            wso = theCache.get(key);
            if (wso == null )  {
                numMiss++;
            } else  {
                numHits++;
                wso.usedThisCycle = true;
            }
        } finally {
            lock.readLock().unlock();
        }
        return wso == null ? null :  wso.get();
    }


/**
 * local class that contains the real object and the inUse flag
 */
static public class WorkingSetObject<V> {
    /** flag that this one was touched */
    boolean usedThisCycle;
    /** cached object */
    private final SoftReference<V> obj;
    /**
     * construct a WorkingSetObject
     * @param rhs object to cache
     */
    WorkingSetObject(V rhs) {
        usedThisCycle = true;
        obj = new SoftReference<V>(rhs);
    }
    V get() {
        return obj.get();
    }
}

}