/*
 * Copyright (C) 2011 Wayfinder Digital LLC. All Rights reserved.
 */
package com.pfarrell.utils.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * The <code>RecursiveNodeIterator</code> class  is an Iterator class. The Iterator will iterate over the elements in 
 * this Node at first. Then it will work its way through all the subtrees.
 * @see Node
 * @see NodeTree
 * 
 * @author pfarrell
 * Created on Oct 20, 2011, 8:32:46 PM
 */
public class RecursiveNodeIterator<E extends Node<E>> implements Iterable<E>, Iterator<E> {

    /** logger instance */
    private static final Logger aLog = Logger.getLogger(RecursiveNodeIterator.class);
/** The pointer to the current element of a tree  */
private Iterator<E> currentElement;
/** Iterator for walking thru subtrees. */
private Iterator<E> currentTree;
/** the thing to iterate over */
private E col;
/** copy of the subtree */
private Collection<? extends Node<E>> subtree;
/** dummy so we can tell we are just starting  */
private static final Iterator unused = Collections.EMPTY_LIST.iterator();
/** dummy indicating we've gotten the base  */
private static final Iterator usedSelf = Collections.EMPTY_SET.iterator();

/**
 * Creates a new instance of RecursiveTreeIterator
 * @param e Element to iterate over
 */
@SuppressWarnings(value = "unchecked")
public RecursiveNodeIterator(E e) {
    col = e;
    currentElement = unused;
    subtree = col.getSubtrees();
    currentTree =  null;
    if (subtree != null) {
        currentTree = (Iterator<E>) subtree.iterator();
    }
}

public Iterator<E> iterator() {
    return currentElement;
}
/**
 * Removes from the underlying collection the last element returned by the iterator (optional operation). 
 * This method can be called only once per call to next. The behavior of an iterator is unspecified if 
 * the underlying collection is modified while the iteration is in progress in any way other than 
 * by calling this method.
 */
public void remove(){
    currentElement.remove();
}
/**
 * Returns true if the iteration has more elements. (In other words, returns true if 
 * next would return an element rather than throwing an exception.)
 * @return true if the iterator has more elements.
 */
public boolean hasNext() {
    if( currentElement.hasNext() ) return true;
    return anyMoreElements();
}
/**
 * Returns the next element in the iteration. Calling this method repeatedly until the 
 * hasNext() method returns false will return each element in the underlying collection exactly once.
 * @return the next element in the iteration.
 */
@SuppressWarnings(value = "unchecked")
public E next() {
    E rval = null;
    if ( currentElement == unused) {
       rval = col;
       currentElement = usedSelf;
       anyMoreElements();
    } else {
        if( ! currentElement.hasNext() ) {
            anyMoreElements();
        }
        rval = currentElement.next();
    }
    return rval;
}
/**
 * This is called when we exhaust the elements of the current subtree. It will search for elements
 * in subsequent subtrees. The value it returns is the same as the value of 
 * currentElement.hasNext().
 * If it returns false, this Iterator is finished.
 * @return true if there are more elements.
 */
private boolean anyMoreElements()  {
    if ( currentElement == unused) {
       return true;
    } else if (currentElement != null && currentElement.hasNext()) {
        return true;
    }
    // else
    while( currentTree != null && currentTree.hasNext() )  {
        E aTree = currentTree.next();
        if ( aTree == null) return false;
        currentElement = aTree.valueIterator();
        if( currentElement.hasNext() ) return true ;
    }
    return false;
}


}
