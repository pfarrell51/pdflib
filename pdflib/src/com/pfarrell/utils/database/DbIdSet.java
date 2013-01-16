/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 */

package com.pfarrell.utils.database;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import org.apache.log4j.Logger;

/**
 * The <code>DbIdSet</code> class implements an object that implements a set
 * of ID or other numbers. Utility functions deal with both the Set itself, or
 * String versions, comma separated. One use of this is to denormalize a list of record IDs in a
 * DBMS record.
 *
 * @see DbIdList
 * @author pfarrell
 * Created on Nov 7, 2010, 12:59:37 AM
 */
public class DbIdSet implements Set<Long> {
     /** logger instance */
private static final Logger disLog = Logger.getLogger(DbIdSet.class);
/** the internal set we use for everything */
private TreeSet<Long> theSet;
/** default constructor */
public DbIdSet() {
    theSet = new TreeSet<Long>();
}
   /**
    * construct a <code>DbIdSet</code> containing the argument collection's data
    * @param arg a collection to load into this object
    */
public DbIdSet(Collection<Long> arg) {
    theSet = new TreeSet<Long>();
    theSet.addAll(arg);
}
    /**
     * construct an Id Set from the argument string of comma separated long id values
     * @param arg a string representation of the list of IDs, comma separated
     */
public DbIdSet(String arg) {
    if (arg == null) {
        theSet = new TreeSet<Long>();
    } else
        parseString(arg);
}
private void parseString(String arg) {
    Preconditions.checkNotNull(arg);
    theSet = new TreeSet<Long>();
    boolean clean = false;
    TreeSet<Long> newVals = new TreeSet<Long>();
    try {
        String[] pieces = arg.split(",");
        for (String p : pieces) {
            newVals.add(Long.parseLong(p.trim()));
        }
        clean = true;
    } catch (NumberFormatException nfe) {
        disLog.error("format error", nfe);
    }
    if (clean) {
        theSet.addAll(newVals);
    }
}
    /**
     * get a string representation of the list of IDs
     * @return a string representation of the list of IDs
     */
public String getEntries() {
    if (theSet == null) {
        return "";
    }
    Joiner joiner = Joiner.on(",").skipNulls();
    return joiner.join(theSet);
}
    /**
     * gets a list of long values of the list
     * @return  a list of long values of the list
     */
public Set<Long> getValues() {
    return ImmutableSet.copyOf(theSet);
}
    /**
     * set the list values to the argument list of long values of the list
     * @param arg a list of long values of the list
     */
public void setValues(Set<Long> arg) {
    Preconditions.checkNotNull(arg);
    theSet = new TreeSet<Long>(arg);
}
    public int size() {
        return theSet.size();
    }

    public boolean isEmpty() {
        return theSet.isEmpty();
    }

    public boolean contains(Object o) {
        return theSet.contains(o);
    }

    public Iterator<Long> iterator() {
        return theSet.iterator();
    }

    public Object[] toArray() {
        return theSet.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return theSet.toArray(a);
    }

    public boolean add(Long e) {
        return theSet.add(e);
    }

    public boolean remove(Object o) {
        return theSet.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return theSet.containsAll(c);
    }

    public boolean addAll(Collection<? extends Long> c) {
        return theSet.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return theSet.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return theSet.removeAll(c);
    }

    public void clear() {
        theSet.clear();
    }

}
