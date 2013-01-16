/*
 * Copyright (C) 2011 Patrick Farrell. All Rights reserved.
 */
package com.pfarrell.busobj;

import com.google.common.base.Preconditions;
import com.pfarrell.utils.database.DBConnectionPool;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.apache.log4j.Logger;

/**
 * The <code>PboIterable</code> class implements the minimum needed to return an object that 
 * implements Iterable for a SQL results set.
 * @param <T> type, must extend AbstractPersistentBusinessObject
 * @see Iterable
 * @see Iterator
 * @see ResultSet
 * @see AbstractPersistentBusinessObject
 * 
 * @author pfarrell
 * Created on Aug 1, 2011, 12:58:17 PM
 */
public class PboIterable<T extends AbstractPersistentBusinessObject> implements Iterable<T> {
    /** logger instance */
    private static final Logger piLog = Logger.getLogger(PboIterable.class);
    private ResultSet saveRs;
    private Class<T> saveClass;
    private Connection saveCon;
    private boolean hasNext;
    /**  
     * constructor
     * @param aCon Connection to use, keep so we call close().
     * @param rs Sql result set
     * @param caller class of caller PBO
     */
public PboIterable(Connection aCon, ResultSet rs, Class<T> caller) {
    Preconditions.checkNotNull(aCon);
    Preconditions.checkNotNull(rs);
    Preconditions.checkNotNull(caller);
    saveCon = aCon;
    saveRs = rs;
    saveClass = caller;
}
/**
 * returns the connection
 * @throws SQLException passes up any Sql 
 */
public void returnCon() throws SQLException {
    saveRs.close();
    saveRs = null;
    saveClass = null;
    hasNext = false;
    if ( ! saveCon.getAutoCommit() ) {
        saveCon.commit();
    }
    DBConnectionPool.returnCon(saveCon);
    saveCon = null;
}
/**
 * gets the iterator
 * @return the iterator
 */
public Iterator<T> iterator() {
    return new LocalIterator<T>();
}

    class LocalIterator<T> implements Iterator<T> {
        private NextStatus status = NextStatus.untested;
        /**
         * Returns true if the iteration has more elements. (In other words, 
         * returns true if next() would return an element rather than throwing an exception.)
         * @return true if the iteration has more elements.
         */ 
        public boolean hasNext() {
            boolean rval = false;
            if (status == NextStatus.yes)
                rval = true;
            else if (status == NextStatus.no) {
                rval = false;
            } else if (status == NextStatus.untested) {
                try {
                    rval = saveRs.next();       // side effect, moves cursor to next
                    status = rval ? NextStatus.yes : NextStatus.no;
                } catch (SQLException ex) {
                    piLog.fatal("hasNext failed, bad", ex);                    
                }
                
            } else {
                throw new IllegalStateException("impossible NextStatus state");
            }
            return rval;
        }
        /**
         * Returns the next element in the iteration.
         * @return the next element in the iteration.
         */
        
            @SuppressWarnings("unchecked")
        public T next() {
            if (status == NextStatus.no) {
              throw new  NoSuchElementException();// - iteration has no more elements.
            } if (status == NextStatus.yes) {
                // cool, go on
            } else if (status == NextStatus.untested) {
                hasNext();
            } else {
                throw new IllegalStateException("impossible NextStatus state2");
            }
            T anObj = null;
            try {
                anObj = (T) saveClass.newInstance();
            }  catch (InstantiationException ex) {
                piLog.error("PBOIterable factory",  ex);
                //break;
            } catch (IllegalAccessException ex) {
                piLog.error("PBOIerable IAE factory",  ex);
                //break;
            }
            assert anObj != null;
            AbstractPersistentBusinessObject asPBO = (AbstractPersistentBusinessObject) anObj;
            try {
                asPBO.populateOneRecord(saveRs);
            } catch (SQLException ex) {
                piLog.error("Sql probablem populating record",  ex);
            }
            asPBO.setFromDatabase();

            if (anObj instanceof Cacheable) {
                Cacheable asCacheable = (Cacheable) anObj;
                asCacheable.storeThisInCache();
            }
            status = NextStatus.untested;
            return anObj;
        }
            /**
             * always throws UnsupportedOperationException
             */
        public void remove() {
            throw new UnsupportedOperationException("Not supported ever.");
        }
        
    }
    private enum NextStatus {
        yes,
        no,
        untested;
    }
}
