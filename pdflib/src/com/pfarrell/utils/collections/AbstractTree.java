/*
 * AbstractTree.java
 *
 * Copyright (c) 2004 Pat Farrell. 
 * based on work of David Byrden from his site
 * <a href="http://byrden.com/java/Tree/index.shtml">http://byrden.com/java/Tree/index.shtml</a>.
 * Original versions of  <code>AbstractTree</code> and <code>Tree</code>  are both freeware, 
 * provided "as is" without warranties of any kind.
 *
 * Created on October 9, 2004, 11:57 AM
 */

package com.pfarrell.utils.collections;

import java.util.* ;

/**
 * The <code>AbstractTree</code> interface an extension of the framework,
 * and to make subtrees in a Tree less 
 * important than the stored data elements. The interface Tree will extend the 
 * standard interface Collection; all the methods of that interface will refer 
 * to the data elements stored in the Tree. The subtrees of a Tree will be stored 
 * in a separate Collection object within it, accessible through a method called getSubtrees.
 * <P>
 * Now, I will take another design decision. Remember that a Tree contains both
 * data elements and subtrees; and that the subtrees contain additional data 
 * elements. I now decide that the methods of the Collection interface, 
 * which are inherited by my Tree interface, shall describe all the stored 
 * elements in a Tree, including those in all subtrees. The size of the Tree, 
 * for example, will be the number of elements stored locally in the Tree plus 
 * the sum of the sizes of all its subtrees. This design decision, while 
 * making the interface easier to use, will obviously make it more difficult to implement. 
 * <p>
 * @see <a href="http://byrden.com/java/Tree/index.shtml">http://byrden.com/java/Tree/index.shtml</a>
 *
 * @author David Byrden and Patrick Farrell
 */
public abstract class AbstractTree<T extends Tree> extends AbstractCollection<T> implements Tree<T> {

/**  The parent Tree is stored here, if any. */
private Tree<T> parent ;

/**
 * This returns the parent Tree, or null.
 * @return parent node for this tree.
 */	
public Tree<T> getParent() {
    return parent;
}

/**
 * Allows you to set the parent Tree, or remove it with a null value.
 * @param parent  parent node for this tree.
 */
   @SuppressWarnings(value = "unchecked")
public final <T extends Tree> void setParent( T parent ) {
    this.parent = parent;
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
    Collection<? extends Tree> subtrees = getSubtrees();
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
    Object pUnit = getParent();
    int count =0;
    while (pUnit != null) {
        count++;
        pUnit = ((Tree) pUnit).getParent();
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
public Iterator<T> iterator() {
    Iterator<T> rval = new RecursiveTreeIterator(this);
    return rval;
}

}
