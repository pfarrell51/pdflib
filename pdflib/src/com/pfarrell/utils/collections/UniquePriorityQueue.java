/*
 * UniquePriorityQueue.java
 *
 * Created on May 23, 2006, 10:49 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.collections;


import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The  <code>UniquePriorityQueue</code> class implements a generic PriorityQueue that
 * does not allow duplicates. 
 * <p>
 * Note that this implementation is not synchronized.   If multiple threads access this queue concurrently,
 * and at least one of the threads modifies the map structurally, it must be synchronized externally. (A
 * structural modification is any operation that adds or deletes one or more mappings; merely changing 
 * the value associated with a key that an instance already contains is not a structural modification.)
 * This is typically accomplished by synchronizing on some object that naturally encapsulates the map.
 * If no such object exists, the map should be "wrapped" using the Collections.synchronizedMap method.
 * This is best done at creation time, to prevent accidental unsynchronized access to the map:
 * <p>
 * <code>Collection c = Collections.synchronizedCollection(new UniquePriorityQueue(...));
 * </code>
 * 
 * @author pfarrell
 * See {@link java.util.PriorityQueue}
 */
public class UniquePriorityQueue<E> implements Iterable<E>, Collection<E>, Queue<E>, Cloneable {
/** our backing  fifo queue  */
private PriorityQueue<E> requests;
/** keep things thread safe */
private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    /**
     * Creates a new instance of UniquePriorityQueue
     */
    public UniquePriorityQueue() {
        requests = new PriorityQueue<E> ();
    }
    /**
     * Creates a new instance of UniquePriorityQueue
     * @param comparator comparator - the comparator used to order this priority queue. If <tt>null</tt>
     * then the order depends on the elements' natural ordering.
     */
    public UniquePriorityQueue( Comparator<? super E> comparator) {
        requests = new PriorityQueue<E> (11, comparator);
    }
    /**
     * Returns an iterator over the elements in this queue. The iterator does not return the elements in any particular order.
     * @return an iterator over the elements in this queue.
     */
    public Iterator<E> iterator() {
        return requests.iterator();
    }
    /**
     * Returns the comparator used to order this collection, or <tt>null</tt>
     * if this collection is sorted according to its elements natural ordering (using <tt>Comparable</tt>).
     * @return he comparator used to order this collection, or <tt>null</tt>
     * if this collection is sorted according to its elements natural ordering.
     */
public Comparator<? super E> comparator() {
    return requests.comparator();
}
    /**
     * Returns the number of elements in this collection. If the collection contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * @return the number of elements in this collection.
     */
    public int size() {
        return requests.size();
    }

    /**
     * Returns true if this collection contains no elements.
     * @return true if this collection contains no elements
     */
    public boolean isEmpty() {
        return requests.isEmpty();
    }

    /**
     * Returns true if this collection contains the specified element. More formally, 
     * returns true if and only if this collection contains at least one element e such that (o==null ? e==null : o.equals(e)).
     * @param o element whose presence in this collection is to be tested.
     * @return true if this collection contains the specified element
     */
    public boolean contains(Object o) {
        return requests.contains(o);
    }

    /**
     * Returns an array containing all of the elements in this collection. This method must return 
     * the elements in the same order specified at constructor.
     * <p>
     * The returned array will be "safe" in that no references to it are maintained by this collection. 
     * (In other words, this method must allocate a new array even if this collection is backed by an 
     * array). The caller is thus free to modify the returned array.
     * <p>
     * This method acts as bridge between array-based and collection-based APIs. 
     * @return an array containing all of the elements in this queue
     */
    public Object[] toArray() {
        return requests.toArray();
    }

    /**
     * Returns an array containing all of the elements in this collection; the runtime type of the 
     * returned array is that of the specified array. If the collection fits in the specified array,
     * it is returned therein. Otherwise, a new array is allocated with the runtime type of the 
     * specified array and the size of this collection. This method must return 
     * the elements in the same order specified at constructor.
     * @param a the array into which the elements of this collection are to be stored, if it is big enough;
     * otherwise, a new array of the same runtime type is allocated for this purpose.
     * @return an array containing the elements of this queue
     */
    public <T> T[] toArray(T[] a) {
        return requests.toArray(a);
    }

    /**
     * Adds the specified element to this queue.
     * This implementation returns true if offer succeeds, else throws an IllegalStateException.
     * @param o the element to insert.
     * @return true (as per the general contract of Collection.add).
     */
    public boolean add(E o) {
        boolean rval = false;
        lock.readLock().lock();
        try {
	        if ( !requests.contains(o)) {
	            rval = requests.add(o);
	        }
        } finally {
        	lock.readLock().unlock();
        }
        return rval;
    }

    /**
     * Removes a single instance of the specified element from this collection, if it is 
     * present (optional operation). More formally, removes an element e such 
     * that (o==null ? e==null : o.equals(e)), if the collection contains one or more 
     * such elements. Returns true if the collection contained the specified element 
     * (or equivalently, if the collection changed as a result of the call).
     * <p>
     * This implementation iterates over the collection looking for the specified element. 
     * If it finds the element, it removes the element from the collection using the iterator's remove method.
     * <p>
     * Note that this implementation throws an UnsupportedOperationException if the iterator returned 
     * by this collection's iterator method does not implement the remove method and this collection 
     * contains the specified object.
     * @param o element to be removed from this collection, if present.
     * @return true if the collection contained the specified element.
     */
    public boolean remove(Object o) {
        boolean rval = false;
        try {
        	rval =  requests.remove(o);    	
	    } finally {
	    	lock.readLock().unlock();
	    }
        return rval;
    }

    /**
     * Returns true if this collection contains all of the elements in the specified collection.
     * <p>
     * This implementation iterates over the specified collection, checking each 
     * element returned by the iterator in turn to see if it's contained in this
     * collection. If all elements are so contained true is returned, otherwise false.
     * @param c collection to be checked for containment in this collection.
     * @return true if this collection contains all of the elements in the specified collection.
     */
    public boolean containsAll(Collection<?> c) {
    	lock.readLock().lock();
        boolean rval = false;
        try {
        	rval = requests.containsAll(c);
	    } finally {
	    	lock.readLock().unlock();
	    }
        return rval;
    }

    /**
     * Adds all of the elements in the specified collection to this queue. Attempts to addAll 
     * of a queue to itself result in IllegalArgumentException. Further, the behavior of this
     * operation is undefined if the specified collection is modified while the operation is 
     * in progress.
     * This implementation iterates over the specified collection, and adds each element 
     * returned by the iterator to this collection, in turn. A runtime exception encountered 
     * while trying to add an element (including, in particular, a null element) may result 
     * in only some of the elements having been successfully added when the associated 
     * exception is thrown.      
     * @param c collection whose elements are to be added to this collection.
     * @return true if this collection changed as a result of the call.
     */
    public boolean addAll(Collection<? extends E> c) {
        boolean rval = false;
        lock.readLock().lock();
        try {
	        if ( c == null ) return rval;
	        for ( E one : c) {
	            rval = rval | add(one);
	        }
	    } finally {
	    	lock.readLock().unlock();
	    }
        return rval;
    }

    /**
     * Removes from this collection all of its elements that are contained in the specified collection (optional operation).
     * <p>
     * This implementation iterates over this collection, checking each element returned by 
     * the iterator in turn to see if it's contained in the specified collection. If it's so
     * contained, it's removed from this collection with the iterator's remove method.
     * <p>
     * Note that this implementation will throw an UnsupportedOperationException if the 
     * iterator returned by the iterator method does not implement the remove method and 
     * this collection contains one or more elements in common with the specified collection.
     * @param c elements to be removed from this collection.
     * @return true if this collection changed as a result of the call.
     */
    public boolean removeAll(Collection<?> c) {
        boolean rval = false;
    	lock.readLock().lock();
    	try {
    		rval = requests.removeAll(c);
	    } finally {
	    	lock.readLock().unlock();
	    }        
    	return rval;
    }
/**
     * Retains only the elements in this collection that are contained in the specified 
     * collection (optional operation). In other words, removes from this collection all
     * of its elements that are not contained in the specified collection.
     * <p>
     * This implementation iterates over this collection, checking each element returned 
     * by the iterator in turn to see if it's contained in the specified collection. If it's 
     * not so contained, it's removed from this collection with the iterator's remove method.
     * <p>
     * Note that this implementation will throw an UnsupportedOperationException if the 
     * iterator returned by the iterator method does not implement the remove method and this 
     * collection contains one or more elements not present in the specified collection.
     * @param c elements to be retained in this collection.
     * @return true if this collection changed as a result of the call.
     */
    public boolean retainAll(Collection<?> c) {
        return requests.retainAll(c);
    }
    
/**
 *    Removes all elements from the smart priority queue. The queue will be empty after this call returns.
 */
    public void clear() {
        lock.readLock().lock();
        try {
        	requests.clear();
        } finally {
        	lock.readLock().unlock();
        }
    }

    /**
     * Inserts the specified element into this priority queue.
     * @param o the element to insert.
     * @return true if accepted
     */
    public boolean offer(E o) {
        if ( requests.contains(o))
            return false;
        return requests.offer(o);
    }

    /**
     * Retrieves and removes the head of this queue, or null  if this queue is empty.
     * @return the head of this queue, or null if this queue is empty.
     */
    public E poll() {
         return requests.poll();
    }

    /**
     * Retrieves and removes the head of this queue. This implementation returns the result of poll  unless the queue is empty.
     * @return element to be removed from this collection, if present.
     */
    public E remove() {
        E rval = null;
        lock.readLock().lock();
        try {
        	rval = requests.remove();
        } finally {
        	lock.readLock().unlock();
        }
        return rval;
    }

    /**
     * Retrieves, but does not remove, the head of this queue, returning null if this queue is empty.
     * @return the head of this queue, or null if this queue is empty.
     */
    public E peek() {
         return requests.peek();
    }

    /**
     * Retrieves, but does not remove, the head of this queue. This implementation returns the result of peek  unless the queue is empty.
     * @return the head of this queue.
     */
    public E element() {
         return requests.element();
    }
/**
 * Creates and returns a copy of this queue.
 * @return a copy of this queue.
 * @throws java.lang.CloneNotSupportedException unlikely, but part of the standard prototype
 */
public UniquePriorityQueue<E> clone() throws CloneNotSupportedException {
    Comparator<? super E> clonecomparator = requests.comparator();
    UniquePriorityQueue<E>  rval = null;
    lock.readLock().lock();
    try {
	    rval = new UniquePriorityQueue<E>(clonecomparator);
	    for ( E tmp : requests ) {
	        rval.add(tmp);
	    }
    } finally {
    	lock.readLock().unlock();
    }
    return rval;
}
}
