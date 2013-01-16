/*
 * Copyright (C) 2009-2011 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.utils.collections;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import com.google.common.primitives.Ints;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import com.pfarrell.utils.misc.TimeUtils;

/**
 * The <code>TrackedHashMap</code> class extends HashMap<K,V> to include a timestamp of the most recent
 * update (put/remove/clear)
 * @param <K> type of Key
 * @param <V>  value
 * @see HashMap
 *
 * @author pfarrell
 * Created on Jul 12, 2010, 10:21:41 AM
 */
public class TrackedHashMap<K,V extends Comparable<V> >
                                extends HashMap<K,V>
                                implements Cloneable, Comparable<TrackedHashMap<K,V>> {

private Calendar calNow;
/** default constructor */
public TrackedHashMap() {
    super();
    calNow = new GregorianCalendar(TimeUtils.utcTZ);
}
   /**
    * put the key/value into the map, updating the change time. This class does not allow null keys, so we check that
    * @param key the key
    * @param value the value
    * @return the previous value associated with key, or null if there was no mapping for key
    */
    @Override
public V put(K key, V value) {
    Preconditions.checkNotNull(key);
    calNow = new GregorianCalendar(TimeUtils.utcTZ);
    return super.put(key, value);
}
   /**
    * remove object and return old value. This class does not allow null keys, so we check that
    * @param key the key
    * @return old value
    */
    @Override
    @SuppressWarnings("unchecked")
public V remove(Object key) {
    Preconditions.checkNotNull(key);
    calNow = new GregorianCalendar(TimeUtils.utcTZ);
    V rval = super.remove((K)key);
    return rval;
}
    @Override
public void clear() {
    calNow = new GregorianCalendar(TimeUtils.utcTZ);
    super.clear();
}
    /**
     * gets the Date of the most recent change
     * @return  the Date of the most recent change
     */
public Calendar getChangedDate() {
    return calNow;
}
    @Override
public Object clone()  {
    return super.clone();
}
   /**
    * standard compareTo, compare size of map first, size() is first check, then
    * check each entry
    * @param o other Map
    * @return -1, 0 or 1
    */
public int compareTo(TrackedHashMap<K, V> o) {
    if (o == null) return 1;
    if (o == this) return 0;
    int rval = getChangedDate().compareTo(o.getChangedDate());
    if (rval != 0) return rval;
    rval = Ints.compare(this.size(), o.size());
    if (rval != 0) return rval;
    for (K k : this.keySet()) {
        if (o.containsKey(k)) {
            V val = o.get(k);
            if (val == null) return 1;
            rval = ComparisonChain.start().compare(this.get(k), val).result();
            if (rval != 0) return rval;
        } else return 1;
    }
    return rval;
}
}
