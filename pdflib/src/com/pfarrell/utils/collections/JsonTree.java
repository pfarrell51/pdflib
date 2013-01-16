/*
 * Copyright (C) 2009 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 */

package com.pfarrell.utils.collections;

import com.sdicons.json.model.JSONInteger;
import com.sdicons.json.model.JSONValue;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;

/**
 * The <code>JsonTree</code> class implements a tree parsed from a JSON object
 * @author pfarrell
 * Created on Oct 28, 2009, 8:57:54 PM
 */
public class JsonTree extends AbstractTree<JsonTree> {

     /** logger instance */
private static final Logger jtLog = Logger.getLogger(JsonTree.class);
private final String tag;
private final JSONValue value;
private  ArrayList<JsonTree> subTree;

public JsonTree(String tag) {
    this(tag, null, null);
}
public JsonTree(String tag, JSONValue val) {
    this(tag, val, null);
    
}
public JsonTree(String tag, JSONValue val, JsonTree subT) {
        this.tag = tag;
        value = val;
        subTree = new ArrayList<JsonTree>();
        if ( subT != null) {
            subTree.add(subT);
            subT.setParent(this);
        }
}
/**
 * add an object to the collection
 * @param obj object to add, usually just a node
 * @return <tt>true</tt> if collection was changed by this call.
 */
    @Override
public boolean add(JsonTree obj ) {
    if (obj == null) return false;
    boolean t = subTree.add(obj);
    obj.setParent(this);
    return t;
}
public String getTag() {
    return tag;
}
public JSONValue getValue() {
     return value;
}
public Collection<? extends Tree> getSubtrees() {
    return  subTree;
}
/**
 * gets the first node of this tree whose tag matches the argument
 * @param t string tag to match
 * @return subtree with matching tag, or null if none.
 */
public JsonTree getSubTreeMatching(String t) {
    JsonTree rval = null;
    for (Iterator<JsonTree> it = iterator(); it.hasNext(); ) {
        JsonTree aT =  it.next();
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
    JsonTree tmp = getSubTreeMatching(arg);
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
    JsonTree tmp = getSubTreeMatching(arg);
    if (tmp == null) return 0;
    return tmp.getFirstAsInt();
}
/**
 * return a string representation of the first value in this node's
 * chain of values
 * @return a string representation of the first value
 */
public String getFirstValue() {
    Iterator<JsonTree> it = iterator();
    if (it.hasNext()) {
        JsonTree t = it.next();
        JSONValue tv = t.value;
        if (tv.isString())
            return (String) tv.strip();
        else {
            jtLog.debug(String.format("T: %s v:%s", tv.getClass().getName(), tv.render(true)));
            return tv.render(true);
        }
    }
    else return null;
}
/**
 * return a boolean representation of the first value in this node's
 * chain of values
 * @return a boolean representation of the first value
 */
public boolean getFirstValueAsBoolean() {
    boolean rval = false;
    Iterator<JsonTree> it = iterator();
    if (it.hasNext()) {
        JsonTree tt = it.next();
        JSONValue v =  tt.getValue();
        if (v == null)
            if ( v.isBoolean() ) {
                Boolean val = (Boolean) v.strip();
                jtLog.trace(val.toString());
                rval = val.booleanValue();
        }
    }
    return rval;
}
/**
 * return an integer representation of the first value in this node's
 * chain of values. returns zero for any and all errors
 * @return an integer representation of the first value
 */
public int getFirstAsInt() {
    int rval = 0;
    Iterator<JsonTree> it = iterator();
    if (it.hasNext()) {
        JsonTree tt =  it.next();
        JSONValue v =  tt.getValue();
        if (v.isInteger()) {
            BigInteger val = ((JSONInteger)v).getValue();
            jtLog.trace(val.toString());
            rval = val.intValue();
        }
    }
    return rval;
}

    /**
     * This iterates the elements stored directly in this Tree but not those in its subtrees.
     * @return local iterator
     */
    public Iterator<? extends Tree> localIterator() {
        return new Iterator<JsonTree>() {
            private boolean done = subTree == null || subTree.size() == 0;
            public void remove() { throw new UnsupportedOperationException("remove not implemented for Json iterator") ; }
            public boolean hasNext() {
                return ! done ;
            }
            public JsonTree next() {
                if( done ) throw new NoSuchElementException() ;
                done = true;
                Iterator<JsonTree> its = subTree.iterator();
                return its.next();
            }
        };
    }
/**
 * generates human readable string of tree, with recursions.
 * @return human readable string of tree
 */
public String getTreeAsString() {
    StringBuffer sb = new StringBuffer();
    Iterator<JsonTree> it = iterator();
    while ( it.hasNext() ) {
        JsonTree theTree = it.next();
        for (int i =0; i < theTree.depth(); i++)
            sb.append("  ");
        sb.append("t/v: ").append(theTree.getTag());
        sb.append(" = ").append(theTree.getValue()).append("\n");
    }
    return sb.toString();
}
}
