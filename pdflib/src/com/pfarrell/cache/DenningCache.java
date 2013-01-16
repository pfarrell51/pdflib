/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.cache;

import com.google.common.collect.ImmutableSet;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * The <code>DenningCache</code> interface defines the functions for an implementation of
 * Dr. Peter J Denning's Working Set cache.
 * A working set cache. Will manage a cache of any type V objects accessed via key K.
 * A cache maps keys to values. A cache cannot contain duplicate keys; each key can map to at most one value.
 * The cache contains a HashMap but does not expose all of the functions of a HashMap. The
 * key functions that are exported include:
 * <ul>
 * <li><a href=#get>get</a> to retreive an object
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
 *
 * 
 * @param <K> generic type of key
 * @param <V> generic type of value
 * @author pfarrell
 * Created on Nov 20, 2009, 3:05:43 PM
 */
public interface DenningCache<K, V> extends Runnable {

    /**
     * Removes all entries in this cache.
     */
    void clear();

    /**
     * slow but occasionally useful routine to slog thru the elements
     * looking for a value. Note that this locks the whole table
     * for the long time it takes.
     * @return <tt>true</tt> if this cache contains a mapping for the specified value.
     * @param value Value to search for
     */
    boolean contains(V value);

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @return <tt>true</tt> if this map contains a mapping for the specified key.
     * @param key key whose presence in this Map is to be tested.
     */
    boolean containsKey(K key);

    /**
     * Returns the value to which the specified key is mapped in this cache.
     *
     * @param   key   a key in the cache.
     * @return  the value to which the key is mapped in this cache;
     * <code>null</code> if the key is not mapped to any value in
     * this cache.
     * @see     java.util.Hashtable#get(java.lang.Object)
     */
    V get(K key);

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
    K getKeyForValue(V value);

    /**
     * handy getter for the number of full content searches done.
     * use this to make sure we are NOT doing this often.
     * @return number of full scans of content done, this has better be a small value
     */
    long getNumberSlowSearches();

    /**
     * returns a hashtable of handy-dandy statistics useful for calculating  and reporting
     * things like ratio of hits to misses, so the sweep cycle can be adjusted.
     * @return hashtable of handy-dandy statistics
     */
    HashMap<String, Object> getStatistics();
   /**
    * @return return name of this cache
    */
    String getName();
    /**
     * return a set of the values of the cache.
     * And it marks all visited elements of the cache
     * as in use -- since it sweeps thru them all until it either finds a match
     * or covers the whole table.
     * @return set of Object values from cache
     */
    ImmutableSet<V> getValues();

    /**
     * Maps the specified <code>key</code> to the specified
     * <code>value</code> in this cache. Neither the key nor the
     * value can be <code>null</code>.
     * <p>
     * The value can be retrieved by calling the <code>get</code> method
     * with a key that is equal to the original key.
     * @return the previous value of the specified key in this cache,
     * or <code>null</code> if it did not have one.
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     * @param key the cache key.
     * @param value the value.
     */
    V put(K key, V value);

    /**
     * write out a quick dump of the cache contents and status
     */
    void quickDump(PrintWriter pw);

    /**
     * Removes the key (and its corresponding value) from this
     * cache. This method does nothing (but accounting) if the key is not in the cache.
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the key had been mapped in this cache,
     * or <code>null</code> if the key did not have a mapping.
     */
    V remove(K key);

    /**
     * basic cache sweeper thread.
     * Note: envoking programs do <i>not</i> need to explicitly call this.
     */
    void run();

    /**
     * sets the thread name, handy for debugging
     * @param clz the class name, should be the same a V
     */
    void setThreadName(Class clz);

    /**
     * return number of entries in the cache
     * @return number of entries in the cache
     */
    int size();
    /**
     * Returns true if this cache contains no key-value mappings.
     * @return  true if this cache contains no key-value mappings.
     */
    boolean isEmpty();
    /**
     * Safely starts this thread.
     */
    void ensureRunning();
    /**
     * stops thread.
     */
    void shutdown();
    /**
     * @return gets naptime
     */
    long getNaptime();
}
