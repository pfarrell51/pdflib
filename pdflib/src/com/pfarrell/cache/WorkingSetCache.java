package com.pfarrell.cache;

/*
 * WorkingSetCache.java	
 *
 * Copyright (c) 2000-2011, Pat Farrell.  All rights reserved.
 *  This code is free for anyone to use, provided this copyright and       
 *  statement are left attached.                                           
 */

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.pfarrell.cache.AbstractWSCache.WorkingSetObject;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * A working set cache. Will manage a cache of any type V objects accessed via key K.
 * A cache maps keys to values. A cache cannot contain duplicate keys; each key can map to at most one value.
 * The cache contains a HashMap but does not expose all of the functions of a HashMap. The
 * key functions that are exported include:
 * <ul>
 * <li><a href=#get>get</a> to retrieve an object
 * <li><a href=#put>put</a> to store an object in the cache
 * <li><a href=#remove>remove</a> to explicitly remove an object from the cache (rarely used).
 * <li><a href=#clear>clear</a> to clear the cache
 * <li><a href=#contains>contains</a> which slowly searches thru the cache for a value
 * <li><a href=#containsKey>containsKey</a> tells if the specified key is in the cache
 * <li><a href=#getValues>getValues</a> returns a collection of all values V
 * <li><a href=#keySet>keySet</a> returns the set of keys of type K
 * </ul>
 * <p>
 * The constructor allows specification of
 * sweep cycle time, altho a reasonable default is automatically provided.
 * Based on the working set model by Peter J. Denning
 * See numerous citations including "The working set model for program behavior"
 * Communications of the ACM, May 1968
 * <p>
 * This class is nearly automatic and self contained. 
 * To use it, declare this and use it. But since it is 
 * multi-threaded, you do have to kill the threads to have the process
 * exit cleanly using the {@link CacheFactory#closeAllThreads} function.
 * <p>
 * Subclasses need to set the "noStart" flag in the constructor and then manually 
 * call start() when construction is finished.
 * @param <K> generic type of key
 * @param <V> generic type of value
 */
public class WorkingSetCache<K, V>  extends AbstractWSCache<K, V> {
    /** class-wide logger static for reuse   */
protected static Logger wscLogger = Logger.getLogger(WorkingSetCache.class);
/**
 * default constructor, pick a decent time for the flush cycle
 */
    public WorkingSetCache() {
        this(TimeUnit.MINUTES.toMillis(10), null);   // ten minutes seems like a good default
    }
/**
 * constructor that takes an instance of the type K for naming. A default value for the nap time is generated.
 * @param instance an instance of type K
 */
    public WorkingSetCache(V instance) {
        this(TimeUnit.MINUTES.toMillis(10), instance);   // ten minutes seems like a good default
    }
/**
 * constructor, accept time (in minutes!) for aging cycle  and instance for name
 * @param nap time to sleep between cycles (in milliseconds)
 */
    public WorkingSetCache( long nap) {
        this(nap, null);
    }
/**
 * constructor, accept time (in minutes!) for aging cycle  and instance for name
 * @param nap time to sleep between cycles (in milliseconds)
 * @param instance an instance of type K
 */
    public WorkingSetCache( long nap, V instance) {
        super(nap, instance);
    }
/**
 * sweep thru the cache, deleting any unmarked items and clearing all marks
 */
protected void processPass() {
    if (awscLog.isTraceEnabled()) {
        awscLog.trace("Doing pass for " + myThread.getName());
    }
    lock.writeLock().lock();
    try {
        Set<K> keySet = theCache.keySet();
        for (K key: keySet) {
            WorkingSetObject<V> wso =  theCache.get(key);
            if (wso == null) continue;
            if ( wso.usedThisCycle ) {
                wso.usedThisCycle = false;
            }
            else
                remove(key);
        }
    } finally {
        lock.writeLock().unlock();
    }
    checkThreadName();
}
private void checkThreadName() {
    String name = myThread.getName();
    if (defaultName.equals(name)) {
        if (breadcrumb != null && wscLogger != null) {
            wscLogger.debug("fixing thread name to " + breadcrumb);
            myThread.setName("WSCfix:" +breadcrumb);
        }
    }
}

/**
 * Returns <tt>true</tt> if this map contains a mapping for the specified
 * key.
 * 
 * @return <tt>true</tt> if this map contains a mapping for the specified key.
 * @param key key whose presence in this Map is to be tested.
 */
public boolean containsKey(K key) {
    Preconditions.checkNotNull(key);
    boolean rval = false;
    lock.readLock().lock();
    try {
       rval = theCache.containsKey(key);
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
	    
/**
 * slow but occasionally useful routine to slog thru the elements
 * looking for a value. Note that this locks the whole table
 * for the long time it takes.
 * @return <tt>true</tt> if this cache contains a mapping for the specified value.
 * @param value Value to search for
 */
public boolean contains(V value) {
    Preconditions.checkNotNull(value);
    numSlowSearches++;
    Collection<WorkingSetObject<V>> wsoColl = null;
    boolean rval = false;
    lock.readLock().lock();
    try {
        wsoColl = theCache.values();
        for (Iterator<WorkingSetObject<V>> it = wsoColl.iterator(); it.hasNext(); ) {
            WorkingSetObject<V> wso =  it.next();
            if ( wso.get() != null && wso.get().equals(value)) {
                rval =  true;
                break;
            }
        }
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
/**
 * slow but occasionally useful routine to slog thru the elements
 * looking for a value, returning its key. Note that this locks the whole table
 * for the long time it takes. And it marks all visited elements of the cache
 * as in use -- since it sweeps thru them all until it either finds a match
 * or covers the whole table.
 *
 * @param value the object to look for
 * @return Object the (first) key to the object
 */
public K getKeyForValue(V value) {
    Preconditions.checkNotNull(value);
    numSlowSearches++;
    K rval = null;
    lock.readLock().lock();
    try {
        for (K key :  theCache.keySet() ) {
            V obj = get(key);
            if ( obj.equals(value)) {
                rval =  key;
                break;
            }
        }
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
/**
 * returns an ImmutableSet copy of the keys
 * @return an ImmutableSet copy of the keys
 */
protected ImmutableSet<K> keySet() {
    return ImmutableSet.copyOf(theCache.keySet());
}
private final Function<WorkingSetObject<V>,V> getValueFromWSO = new Function<WorkingSetObject<V>,V>() {
        public V apply(WorkingSetObject<V> f) {
            return f.get();
        }
};
/**
 * return a set of the values of the cache.
 * And it marks all visited elements of the cache
 * as in use -- since it sweeps thru them all covering the whole table.
 * @return set of Object values from cache
 */
public ImmutableSet<V> getValues() {
    numSlowSearches++;
    ImmutableSet<V> rval = null;
    lock.readLock().lock();
    try {
        rval = ImmutableSet.copyOf(Iterables.transform(theCache.values(), getValueFromWSO));
    } finally {
        lock.readLock().unlock();
    }
    return rval;
}
/**
 * handy getter for the number of full content searches done.
 * use this to make sure we are NOT doing this often.
 * @return number of full scans of content done, this has better be a small value
 */
public  long getNumberSlowSearches() { return  numSlowSearches;};


} // end WorkingSetCache
