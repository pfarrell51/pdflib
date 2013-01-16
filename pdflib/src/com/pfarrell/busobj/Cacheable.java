/*
 * Cachable.java
 *
 * Created on December 7, 2006, 11:19 PM
 *
 * Copyright (c) 2006, Pat Farrell All rights reserved.
 */

package com.pfarrell.busobj;

import java.util.concurrent.ExecutionException;

/**
 * The <code>Cacheable</code> interface declares that the {@link AbstractPersistentBusinessObject} instance
 * implements a backing {@link com.pfarrell.cache.DatabaseCache}
 *
 * @param <K> Key (nearly always Long, but sometimes String) to use to access the cache
 * @param <V> Value type, the explicit {@link AbstractPersistentBusinessObject} being cached.
 *
 * @author pfarrell
 */
public interface Cacheable<K, V extends AbstractPersistentBusinessObject & Cacheable<K, V> >  {
/**
 * Maps the specified <code>key</code> to the specified
 * <code>value</code> in this cache. Neither the key nor the
 * value can be <code>null</code>.
 * <p>
 * The value can be retrieved by calling the <code>get</code> method
 * with a key that is equal to the original key.
 * @see java.lang.Object#equals(java.lang.Object)
 * @see java.util.Hashtable#get(java.lang.Object)
 * @param key the cache key.
 * @param value the value.
 */
public void put(K key, V value);
/**
 * Removes the key (and its corresponding value) from this
 * cache. This method does nothing (but accounting) if the key is not in the cache.
 *
 * @param   key   the key that needs to be removed.
 */
public void invalidate(K key);
/**
 * Returns the value to which the specified key is mapped in this cache.
 *
 * @param   key   a key in the cache.
 * @return  the value to which the key is mapped in this cache;
 *          <code>null</code> if the key is not mapped to any value in
 *          this cache.
 * @see     java.util.Hashtable#put(java.lang.Object, java.lang.Object)
 */
public V get(K key) throws ExecutionException;
/**
 * gets the key value used to hash this value
 *
 * @return  the key value used to hash this value
 */
public K getCacheKey();
/**
 * store 'this' in cache 
 */
public void storeThisInCache();        

}
