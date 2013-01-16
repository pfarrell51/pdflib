/*
 * Copyright (C) 2011 Wayfinder Digital LLC. All Rights reserved.
 */
package com.pfarrell.utils.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;

/**
 * The <code>NodeTree</code> class implements a base class of a Node Tree
 * @see Node
 * @author pfarrell
 * Created on Oct 20, 2011, 5:27:49 PM
 */
public class NodeTree<T> implements Node<T>  {

    /** logger instance */
    private static final Logger aLog = Logger.getLogger(NodeTree.class);
 
  /**  The parent Tree is stored here, if any. */   
  private Node<? extends T> parent;  
  private T value;
  private List<Node<T>> leaves = new ArrayList<Node<T>>();  
  
  /** default constructor */  
public NodeTree() {}

public NodeTree(T arg) {
    value = arg;
}

public Node<? extends T> getParent() {    
    return parent;    
}    


public boolean add(Node<T> o) {    
    return leaves.add(o);    
}

public void setParent(Node<T> arg) {
    parent = arg;
}

public Collection<? extends Node<T>> getSubtrees() {
    return leaves;
}

/**
 * This iterates the elements stored directly in this Tree but not those 
 * in its subtrees.
 * @return iterator for this node.
 */
public Iterator<? extends Node<T>> localIterator() {
    return new Iterator<Node<T>>() {
        private boolean done = leaves == null || leaves.isEmpty();
        public void remove() { throw new UnsupportedOperationException("no remove() in Iterator for NodeTree") ; }
        public boolean hasNext() { 
            return ! done ; 
        }
        public Node<T> next() {
            if( done ) throw new NoSuchElementException() ;
            done = true;
            Iterator<Node<T>> its = leaves.iterator();
            return its.next();
        }
    };
}
@SuppressWarnings("unchecked")
public Iterator<T> valueIterator() {
    RecursiveNodeIterator iter = new RecursiveNodeIterator(this);
    return iter.iterator();
}
/**
* Returns the number of elements in this Collection. This implementation sums the sizes of all the subtrees, but it
*  knows nothing about storage in this Tree. If your subclass stores elements, you should override with a method 
*  that calls this version and adds its own element count to the result.
 *
 * @return total size of tree and subtrees
 */
public int size() {
    int total = 0 ;
    Collection<? extends Node<T>> subtrees = getSubtrees();
    if (subtrees == null) return total;
    Iterator iter = subtrees.iterator();
    while(  iter.hasNext() ) {
        Object obj = iter.next();
        if ( obj instanceof Collection)
            total += ((Collection)obj).size() ;
    }
    return total ;
}
/**
 * count depth up to parent
 * @return depth up to parent
 */ 
public int depth() {
    Node<? extends T> pUnit = getParent();
    int count =0;
    while (pUnit != null) {
        count++;
        pUnit = ((Node<? extends T>) pUnit).getParent();
    }
    return count;
}
/**
 * Returns an Iterator over the elements contained in this Collection. All the elements locally defined
 *  in this Tree will be iterated first, using the Iterator returned from the subclass by localIterator().
 *  Then, all the elements in all subtrees will be iterated
 * @return an Iterator over the elements
 */
@SuppressWarnings(value = "unchecked")
public Iterator<Node<T>> iterator() {
    Iterator<Node<T>> rval = new RecursiveNodeIterator(this);
    return rval;
}

    /**
     * @return the value
     */
    public T getValue() {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(T value) {
        this.value = value;
    }

}
