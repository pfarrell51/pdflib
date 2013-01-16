/*
 * Tree.java
 *
`* Copyright (c) 2004 Pat Farrell
 * based on work of David Byrden from his site
 * <a href="http://byrden.com/java/Tree/index.shtml">http://byrden.com/java/Tree/index.shtml</a>
 *
 * Created on October 9, 2004, 11:58 AM
 */

package com.pfarrell.utils.collections;

import java.util.* ;
/**
 * This implements <code>Tree</code> class. It contains both
 * data elements and subtrees; and that the subtrees contain additional data 
 * elements. In this design, the methods of the Collection interface, 
 * which are inherited by my Tree interface,  describe all the stored 
 * elements in a Tree, including those in all subtrees. The size of the Tree, 
 * for example, will be the number of elements stored locally in the Tree plus 
 * the sum of the sizes of all its subtrees. This design decision, while 
 * making the interface easier to use, will obviously make it more difficult to implement. 
 *
 * @author David Byrden and Patrick Farrell
 */
public interface Tree<T> extends Collection<T>  {

    /**
     * This returns the parent Tree, or null if this is the root.
     * @return the parent Tree, or null if this is the root.
     */	
    Tree<T> getParent();

    /**
     * Allows you to set the parent Tree, or remove it with a null value.
     * @param parent the parent Tree, or remove it with a null value.
     */
    <T extends Tree> void setParent( T parent );


    /**
     * The subtrees of this Tree.
     * @return the subtrees of this Tree.
     */
    Collection<? extends Tree> getSubtrees();

    /**
     * This iterates the elements stored directly in this Tree but not those in its subtrees.
     * @return local iterator
     */
    Iterator<? extends Tree> localIterator();
    /** 
     * gets  an iterator over the elements in this collection. 
     * @return an Iterator over the elements in this collection
     */
    Iterator<T> iterator();
    
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
public boolean add(T o);

}
