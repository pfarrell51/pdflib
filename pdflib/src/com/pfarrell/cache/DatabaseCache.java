/*
 * DatabaseCache.java
 *
 * Created on August 29, 2006, 2:44 PM
 *
 * Copyright (c) 2006, Pat Farrell All rights reserved.
 */

package com.pfarrell.cache;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.pfarrell.busobj.Cacheable;
import com.pfarrell.busobj.AbstractPersistentBusinessObject;
import com.pfarrell.utils.database.DBUtil;
import com.pfarrell.utils.misc.TimeUtils;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;


/**
 * The  <code>DatabaseCache</code> class contains a {@link WorkingSetCache working set cache}. 
 * It operates exactly as the WorkingSetCache does, except that it knows about the database.
 * Currently does *not* implement write-back, perhaps it should.
 * <p>
 * Will manage a cache of any type V objects accessed via key K.
 * A cache maps keys to values. A cache cannot contain duplicate keys; each key can map to at most one value.
 * <p>
 * Note: V  extends {@link AbstractPersistentBusinessObject}, so
 * this class only works with classes that are extensions of AbstractPersistentBusinessObject.
 * <p>
 *
 * @param <K> Key (nearly always Long) to use to access the cache
 * @param <V> Value type, the explicit {@link AbstractPersistentBusinessObject} being cached.
 * @author pfarrell
 */
public class DatabaseCache<K extends Comparable<? super K>, V extends AbstractPersistentBusinessObject & Cacheable<K, V>> 
                           extends WorkingSetCache<K, V>  {
     /** logger instance */
protected static final Logger dbcLog = Logger.getLogger(DatabaseCache.class);
/** how often to force refresh from DB */
static final int CLANKmod = 3;
/** nap time for a pass */
public static final long napTime = CacheFactory.getCommonSleepTime();
/** only check the database periodically, or it won't be a cache */
private int dbClankCounter = 0;
 
    /** Creates a new instance of DatabaseCache */
    public DatabaseCache() {
        this(napTime);
    }

/**
 * standard constructor, accept time (in minutes!) for aging cycle
 * @param nap  the time (in millis) to sleep between cycles
 */
    public DatabaseCache(long nap) {
        this(nap, null);
    }
/**
 * standard constructor, accept time (in minutes!) for aging cycle and instance for naming.
 * @param nap  the time (in millis) to sleep between cycles
 * @param instance an instance of the type of PBO that we are caching, used to set the name of the thread.
 */
    public DatabaseCache(long nap, V instance) {
        super(nap, instance);
        if (instance == null) {
            myThread.setName("DatabaseCache");
        } else {
            setThreadName(instance.getClass());
        }
    }
    @Override
    public void setThreadName(Class clz) {
        Preconditions.checkNotNull(clz);
        myThread.setName("DbC:" + clz.getSimpleName());
    }
/**
 * {@inheritDoc}
 * @return {@inheritDoc}
 */
    @Override
    public String getName() {
        String rval = myThread.getName();
        if (rval.equals(defaultName) && breadcrumb != null) {
            rval = "DbC:d:" + breadcrumb;
            myThread.setName(rval);
        }
        return rval;
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
    @Override
 @SuppressWarnings("unchecked")
public  V put(K key, V value) {
    Preconditions.checkNotNull(key);
    Preconditions.checkNotNull(value);
    Preconditions.checkArgument(value instanceof Cacheable && value instanceof AbstractPersistentBusinessObject);
    return super.put(key, value);
}
    
/**
 * sweep thru the cache, deleting any unmarked items and clearing all marks, 
 * and clanking database as needed
 */
 @SuppressWarnings("unchecked")
 @Override
protected void processPass() {
    if (dbcLog.isTraceEnabled()) awscLog.trace("DBC: Doing pass for " + myThread.getName());
    Set<K> keySet = null;
    lock.writeLock().lock();
    try {
        keySet = theCache.keySet();
        for (K key: keySet) {
            WorkingSetObject<V> wso = theCache.get(key);
            if (wso == null) continue;
            if ( wso.usedThisCycle ) {
                wso.usedThisCycle = false;
            }
            else {
                remove(key);
            }
        }
    } finally {
        lock.writeLock().unlock();
    }
    dbClankCounter++;
    if ( (dbClankCounter % CLANKmod) == 0 &&  super.size() > 0) {
        refreshValues();
    }
    if (dbcLog.isDebugEnabled()) dbcLog.debug("DBC:postPass " + myThread.getName() + " in use " + super.size());
}
    /**
     * gets a query string for the fetch
     * @param firstKey first key to access a cache value
     * @return a query string for the fetch
     */
private String makeRefreshString(V firstVal) {
    Preconditions.checkNotNull(firstVal);
    String query = null;
    lock.readLock().lock();
    try {
        if ( ! super.isEmpty()) {
            ImmutableSet<K> keys = super.keySet();
                Iterable<K> iter = Iterables.filter(keys, new Predicate<K>() {
                        public boolean apply(K arg) {
                            WorkingSetObject<V> wso = theCache.get(arg);
                            return wso != null ? wso.usedThisCycle : false;
                        }
                    });
            ArrayList<K> ids = Lists.newArrayList(iter);
            Collections.sort(ids);
            query = firstVal.getStringForSelect() +
                        " where " + firstVal.getIdFieldName() + "  in " +   DBUtil.makeInClause(ids);
            assert query != null : "Query string is null with non-null key & value";
            if (dbcLog.isTraceEnabled()) dbcLog.trace(myThread.getName() + " " + query);
        } else 
            if (dbcLog.isTraceEnabled()) dbcLog.trace(myThread.getName() + " nothing in cache to refersh");
    } finally {
        lock.readLock().unlock();
    }
    return query;
}
    /**
     * periodically, loop through the values in this cache, and go clank the DB to get them again, so that
     * we don't reflect really stale data
     */
@SuppressWarnings("unchecked")
protected  void refreshValues() {
    if (super.isEmpty()) return;
    ImmutableSet<K> keys = super.keySet();
    if (dbcLog.isDebugEnabled()) {
        dbcLog.debug(String.format("refreshing for %d", keys.size()));
    }
    V firstVal = null;
    K firstKey = null;
    String query = null;
    lock.readLock().lock();
    try {
        if (! keys.isEmpty()) {
            firstKey = super.keySet().iterator().next();
            firstVal = super.get(firstKey);             // this sets in use 
            if (firstVal == null) {
                dbcLog.error("Null first val with real key " + firstKey);
                return;
            }
            query = makeRefreshString(firstVal);
            if (query == null) return;
        }
    } finally {
        lock.readLock().unlock();
    }
    
    if (dbcLog.isTraceEnabled()) {
        dbcLog.trace(String.format("Will freshen database for %s at %s ", firstVal.getClass().getName(), TimeUtils.getNowDateUTC()));
    }
    Class firstClass = firstVal.getClass();
    assert firstClass != null;
    List<? extends AbstractPersistentBusinessObject> dbvalues = null;
    lock.writeLock().lock();
    try {
        try {
            dbvalues = AbstractPersistentBusinessObject.factoryFromQueryString(query, firstClass);
            assert dbvalues != null;
        } catch (SQLException ex) {
            dbcLog.error(ex);
            if (dbvalues == null) {
                return;
            }
        }
        for ( AbstractPersistentBusinessObject dbv : dbvalues) {
            Cacheable<K, V> asCache = null;
            if ( dbv instanceof Cacheable) {
                asCache = (Cacheable<K, V> ) dbv;
                K key = asCache.getCacheKey();
                super.put(key, ((V) dbv));
                theCache.get(key).usedThisCycle = false;        // don't mark as used
            }
        }
    } finally {
        lock.writeLock().unlock();
    }
}

}
