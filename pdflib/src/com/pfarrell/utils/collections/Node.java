/*
 * Copyright (C) 2011 Wayfinder Digital LLC. All Rights reserved.
 */
package com.pfarrell.utils.collections;

import java.util.Collection;
import java.util.Iterator;

/**
 * The <code>Node</code> interface defines one node of a tree.
 * @param <T> any object, the value of each tree node
 * 
 * @author pfarrell
 * Created on Oct 20, 2011, 5:18:04 PM
 */
public interface Node<T>  {
    /**
     * This returns the parent Tree, or null if this is the root.
     * @return the parent Tree, or null if this is the root.
     */	
    Node<? extends T> getParent();

    /**
     * Allows you to set the parent Tree, or remove it with a null value.
     * @param arg parent node.
     */
    void setParent(Node<T> arg);

    /**
     * The subtrees of this Tree.
     * @return the subtrees of this Tree.
     */
    Collection<? extends Node<T>> getSubtrees();

    /**
     * This iterates the elements stored directly in this Tree but not those in its subtrees.
     * @return local iterator
     */
    Iterator<? extends Node<T>> localIterator();
    /** 
     * gets  an iterator over the elements in this collection. 
     * @return an Iterator over the elements in this collection
     */
    Iterator<Node<T>> iterator();
    /**
     * gets an iterator to the values stored in the tree
     * @return an iterator to the values stored in the tree
     */
    Iterator<T> valueIterator();
    
    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if the collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)
     * Collections that support this operation may place limitations on what
     * elements may be added to the collection.  
     * <p>
     * In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     *
     * @return <tt>true</tt> if the collection changed as a result of the call.
     * @param o element whose presence in this collection is to be ensured. 
     */
    boolean add(Node<T> o);

}
