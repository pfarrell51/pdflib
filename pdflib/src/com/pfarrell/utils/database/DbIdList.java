/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 */

package com.pfarrell.utils.database;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>DbIdList</code> class implements an object that implements a list
 * of ID or other numbers. Utility functions deal with both the List itself, or
 * String versions, comma separated. One use of this is to denormalize a list of record IDs in a
 * DBMS record. Note: as a list, it can contain duplicate entries.
 *
 * @see ArrayList
 * @see Collection
 * @see DbIdSet
 *
 * @author pfarrell
 * Created on Sep 29, 2010, 10:30:10 PM
 */
public class DbIdList implements Collection<Long> {
     /** logger instance */
private static final Logger nlLog = Logger.getLogger(DbIdList.class);
    /** the internal list of ids */
private ArrayList<Long> theList;
    /** default constructor */
public DbIdList() {
    theList = new ArrayList<Long>();
}
    /**
     * construct a list containing the elements of the specified collection, in the order
     * they are returned by the collection's iterator.
     * @param arg  collection containing elements to be added to this list during construction
     */
public DbIdList(Collection<Long> arg) {
    Preconditions.checkNotNull(arg);
    theList = new ArrayList<Long>(arg);
}
    /**
     * construct an Id List from the argument string of comma separated long id values
     * @param arg a string representation of the list of IDs, comma separated
     */
public DbIdList(String arg) {
    if (arg == null) {
        theList = new ArrayList<Long>();
    } else
        parseString(arg);
}
    /**
     * get a string representation of the list of IDs
     * @return a string representation of the list of IDs
     */
public String getEntries() {
    if (theList == null) {
        return "";
    }
    Joiner joiner = Joiner.on(",").skipNulls();
    return joiner.join(theList);
}
    /**
     * gets a list of long values of the list
     * @return  a list of long values of the list
     */
public List<Long> getValues() {
    return ImmutableList.copyOf(theList);
}
    /**
     * set the list values to the argument list of long values of the list
     * @param arg a list of long values of the list
     */
public void setValues(List<Long> arg) {
    Preconditions.checkNotNull(arg);
    theList = new ArrayList<Long>(arg);
}
    /**
     *  set the list values to the parsed string of values
     * @param arg comma separated list of ids
     */
public void setEntries(String arg) {
    parseString(arg);
}
   /**
    * Appends the specified element to the end of this list.
    * @param e element to be appended to this list
    * @return true (as specified by Collection.add(E))
    */
public boolean add(Long e) {
    return theList.add(e);
}
   /**
    * Appends all of the elements in the specified collection to the end of this list,
    * in the order that they are returned by the specified collection's Iterator.
    * @param c collection
    * @return  if this list changed as a result of the call
    */
public boolean addAll(Collection<? extends Long> c) {
    return theList.addAll(c);
}
    /**
     * inserts the specified element at the specified position in this list. Shifts the element currently at
     * that position (if any) and any subsequent elements to the right (adds one to their indices).
     * @param index index at which the specified element is to be inserted
     * @param element element to be appended to this list
     */
public void add(int index, long element) {
    theList.add(index, element);
}
   /**
    * Removes all of the elements from this list. The list will be empty after this call returns.
    */
public void clear() {
    theList.clear();
}
    /**
     * Returns true if this list contains the specified element. More formally,
     * returns true if and only if this list contains at least one element e
     * such that (o==null ? e==null : o.equals(e)).
     * @param o  element whose presence in this list is to be tested
     * @return true if this list contains the specified element
     */
public boolean contains(Long o) {
    return theList.contains(o);
}
   /**
    * Returns true if this collection contains the specified element. More formally,
    * returns true if and only if this collection contains at least one element e
    * such that (o==null ? e==null : o.equals(e)).
    * @param o  element whose presence in this collection is to be tested
    * @return true if this collection contains the specified element
    */
public boolean contains(Object o) {
    if (o == null) return false;
    else if (o instanceof Long) {
        return theList.contains((Long) o);
    } else
        return false;
}
   /**
    * Returns true if this collection contains all of the elements in the specified collection.
    * @param c collection to test for presence of all entries in the collection x
    * @return true if this collection contains all of the elements in the specified collection
    */
public boolean containsAll(Collection<?> c) {
    return theList.containsAll(c);
}
    /**
     * Returns the element at the specified position in this list.
     * @param idx  index of the element to return
     * @return the element at the specified position in this list
     */
public long get(int idx) {
    long rval = theList.get(idx);
    return rval;
}
   /**
    * Returns true if this collection contains no elements.
    * @return true if this collection contains no elements.
    */
public boolean isEmpty() {
    return theList.isEmpty();
}
   /**
    * Returns an iterator over the elements in this collection. There are no
    * guarantees concerning the order in which the elements are returned
    * (unless this collection is an instance of some class that provides a guarantee).
    * @return an Iterator over the elements in this collection
    */
public Iterator<Long> iterator() {
    return theList.iterator();
}
    /**
     * Removes the element at the specified position in this list.
     * Shifts any subsequent elements to the left (subtracts one from their indices).
     * @param index the index of the element to be removed
     * @return the element that was removed from the list
     */
public long remove(int index)  {
    long rval = theList.remove(index);
    return rval;
}
   /**
    * Removes a single instance of the specified element from this collection, if it is present
    * (optional operation).
    * More formally, removes an element <tt>e</tt> such that <tt>(o==null ? e==null : o.equals(e))</tt>,
    * if this collection contains one or more such elements. Returns true if this collection
    * contained the specified element (or equivalently, if this collection changed as a result of the call).
    * @param o object to remove
    * @return true if an element was removed as a result of this call
    */
public boolean remove(Object o) {
    if (o == null) {
        return false;
    } else if(o instanceof Long) {
        return theList.remove((Long)o);
    } else
        return false;
}
public boolean removeAll(Collection<?> c) {
    return theList.removeAll(c);
}
public boolean retainAll(Collection<?> c) {
    return theList.retainAll(c);
}
   /**
    * Returns the number of elements in this list.
    * @return the number of elements in this list.
    */
public int size() {
    return theList.size();
}
   /**
    * Returns an array containing all of the elements in this list in proper sequence
    * (from first to last element); the runtime type of the returned array is that of the specified array.
    * @param a  the array into which the elements of the list are to be stored, if it is big enough;
    * otherwise, a new array of the same runtime type is allocated for this purpose.
    * @return an array containing the elements of the list
    */
public <Long> Long[] toArray(Long[] a) {
    return theList.toArray(a);
}
   /**
    * Returns an array containing all of the elements in this list in proper sequence (from first to last element).
    * @return an array containing all of the elements in this list in proper sequence
    */
public Object[] toArray() {
    return theList.toArray();
}

private void parseString(String arg) {
    Preconditions.checkNotNull(arg);
    if (arg.trim().length() == 0 && theList == null) {
        theList = new ArrayList<Long>();
        return;
    }
    boolean clean = false;
    ArrayList<Long> newVals = new ArrayList<Long>();
    try {
        String[] pieces = arg.split(",");
        for (String p : pieces) {
            newVals.add(Long.parseLong(p.trim()));
        }
        clean = true;
    } catch (NumberFormatException nfe) {
        nlLog.error("format error", nfe);
    }
    if (clean) {
        theList = newVals;
    }
}
}
