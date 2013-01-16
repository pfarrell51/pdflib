/*
 * TextTree.java
 *
 * Copyright (c) 2004, Pat Farrell, All rights reserved.
 * This code will be released with a suitable Open Source
 * license, probably BSD-like.
 * Created on October 9, 2004, 11:31 AM
 */

package com.pfarrell.utils.collections;

import java.util.*;
/**
 * The <code>TextTree</code> represents a tree; a data structure where each 
 * node may store elements and additional nodes. The precise term for this 
 * is a <i>directed acyclic graph</i>. Don't know who has to enforce
 * the acyclic part.
 *
 * @author  Patrick Farrell
 */
public class TextTree extends AbstractTree<TextTree> {

    /** our local tag  */
private String theTag;
/** any value for this node */
private String theValue;
/** any properties associated with this node */
private Properties theProps;
/** sub trees */
private ArrayList<TextTree> subtrees = new ArrayList<TextTree>();

/**
 * Creates a new instance of TextTree
 * @param targ tag name
 */
public TextTree(String targ) {
    this(targ, null, null, ( ArrayList<TextTree>) null);
}
/**
 * Creates a new instance of TextTree
 * @param targ tag name
 * @param varg value
 */
public TextTree(String targ, String varg) {
    this(targ, varg, null, ( ArrayList<TextTree>) null);
}
/**
 * Creates a new instance of TextTree
 * @param targ tag name
 * @param varg value
 * @param p Properties
 */
public TextTree(String targ, String varg,  Properties p) {
    this(targ, varg, p, ( ArrayList<TextTree>) null);
}
/**
 * Creates a new instance of TextTree
 * @param targ tag name
 * @param varg value value for this node
 * @param p Properties for this node
 * @param aTree a single TextTree
 */
public TextTree(String targ, String varg,  Properties p, TextTree aTree) {
    super();
    ArrayList<TextTree> aList = new ArrayList<TextTree>();
    aList.add(aTree);
    commonConstructor(targ, varg, p, aList);
}
/**
 * Creates a new instance of TextTree
 * @param targ tag name
 * @param varg value value for this node
 * @param p Properties for this node
 * @param subsArg array of subtrees
 */
public TextTree(String targ, String varg,  Properties p, ArrayList<TextTree> subsArg) {
    super();
    commonConstructor(targ, varg, p, subsArg);
}
/**
 * do the real work of the constructor
 * @param targ tag name
 * @param varg value value for this node
 * @param p Properties for this node
 * @param subsArg array of subtrees
 */
private void commonConstructor(String targ, String varg,  Properties p, ArrayList<TextTree> subsArg) {
    if (subsArg != null) {
        subtrees.addAll(subsArg);
    }
    for (TextTree tt : subtrees) {
        tt.setParent(this);
    }
    theTag = targ;
    theValue = varg;
    theProps = p;
    
}
/**
 * gets the size, including any subtrees
 * @return the size
 */    
    @Override
public int size() {
    return super.size() + 1;
}
/**
 * gets the tag for this node
 * @return the tag for this node
 */
public String getTag() { return theTag;}
/**
 * gets the Properties of this node
 * @return the Properties of this node
 */
public Properties getProperties() { return theProps;}
/**
 * setter for properties
 * @param p Properties to set
 */
public void setProperties(Properties p) {theProps = p;}
/**
 * gets the first node of this tree whose tag matches the argument
 * @param t string tag to match
 * @return subtree with matching tag, or null if none.
 */
public TextTree getSubTreeMatching(String t) {
    TextTree rval = null;
    for (Iterator<TextTree> it = iterator(); it.hasNext(); ) {
        TextTree aT =  it.next();
        String tg = aT.getTag();
        if (tg != null && tg.equalsIgnoreCase(t)) {
            rval = aT;
            break;
        }
    }
    return rval;
}
/**
 * Search tree for the first node matching argument
 * return that node's value as a string
 * @param arg tag to search for
 * @return string value of tag of node matching arg, or null if none
 */
public String getValueMatching(String arg) {
	TextTree tmp = getSubTreeMatching(arg);
	if (tmp == null) return null;
	return tmp.getFirstValue();
}
/**
 * Search tree for the first node matching argument
 * return that node's value as a string
 * @param arg tag to search for
 * @return string value of tag of node matching arg, or null if none
 */
public int getIntValueMatching(String arg) {
	TextTree tmp = getSubTreeMatching(arg);
	if (tmp == null) return 0;
	return tmp.getFirstAsInt();
}
/**
 * gets the value of this node itself if any
 * @return the value of this node itself if any
 */
public String getValue() {
    return theValue;
}
/**
 * gets The subtrees of this Tree.
 * @return  subtrees of this Tree.
 */
public Collection<TextTree> getSubtrees() {
    return subtrees;
}
/**
 * return a string representation of the first value in this node's
 * chain of values
 * @return a string representation of the first value
 */
public String getFirstValue() {
    Iterator<TextTree> it = iterator();
    if (it.hasNext()) {
        TextTree t = it.next();
        return t.getValue();
    }
    else return null;
}
/**
 * return a boolean representation of the first value in this node's
 * chain of values
 * @return a boolean representation of the first value
 */
public boolean getFirstValueAsBoolean() {
    Iterator<TextTree> it = iterator();
    if (it.hasNext()) {
        TextTree tt = it.next();
        String v =  tt.getValue();
        if ( v == null || v.length() < 1) return false;
        char c = v.charAt(0);
        if ((c == 'T') || (c == 't') || (c == 'Y') || (c == 'y')  ) return true;
    }
    return false;
}
/**
 * return an integer representation of the first value in this node's
 * chain of values. returns zero for any and all errors
 * @return an integer representation of the first value
 */
public int getFirstAsInt() {
    Iterator<TextTree> it = iterator();
    if (it.hasNext()) {
        TextTree tt =  it.next();
        String v =  tt.getValue();
        if ( v == null || v.length() < 1) return 0;
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException ne) {};
    }
    return 0;
}
/**
 * This iterates the elements stored directly in this Tree but not those 
 * in its subtrees.
 * @return iterator for this node.
 */
public Iterator<TextTree> localIterator() {
    return new Iterator<TextTree>() {
        private boolean done = subtrees == null || subtrees.size() == 0;
        public void remove() { throw new UnsupportedOperationException("no remove() in Iterator for Text Tree") ; }
        public boolean hasNext() { 
            return ! done ; 
        }
        public TextTree next() {
            if( done ) throw new NoSuchElementException() ;
            done = true;
            Iterator<TextTree> its = subtrees.iterator();
            return its.next();
        }
    };
}
/**
 * add an object to the collection
 * @param obj object to add, usually just a node
 * @return <tt>true</tt> if collection was changed by this call.
 */
    @Override
public boolean add(TextTree obj ) {
    if (obj == null) return false;
    boolean t = subtrees.add(obj);
    obj.setParent(this);
    return t;
}
/**
 * generates human readable string of tree, with recursions.
 * @return human readable string of tree
 */
public String getTreeAsString() {
    StringBuilder sb = new StringBuilder();
    Iterator<TextTree> it = iterator();
    while ( it.hasNext() ) {
        TextTree theTree = it.next();
        for (int i =0; i < theTree.depth(); i++)
            sb.append("  ");
        sb.append("t/v: ").append(theTree.getTag());
        if ( theTree.getProperties() != null) sb.append(" ").append(theTree.getProperties());
        sb.append(" = ").append(theTree.getValue()).append("\n");
    }
    return sb.toString();
}
/**
 * close approximation of toString()
 * @return human readable string of tree
 */
public String toStringAsTree() {
    return getTreeAsString();
}
/**
 * gets standard human readable string for debugging
 * @return standard human readable string for debugging
 */
    @Override
public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("tag:").append( theTag);
    sb.append(", val:").append(theValue);
    sb.append(", props").append(theProps);
    sb.append(", subtree:");
    if (subtrees == null) sb.append("null");
    else sb.append(" size:").append(subtrees.size());
    return sb.toString();
}

}

