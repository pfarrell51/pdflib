/*
 * AbstractPersistentBusinessObject.java
 *
 * Created on March 19, 2006, 2:04 AM
 *
 * Copyright (c) 2006-2011, Pat Farrell, Inc. All rights reserved.
 */

package com.pfarrell.busobj;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pfarrell.utils.database.AbstractPersistentObject;
import com.pfarrell.utils.database.DBConnectionPool;
import com.pfarrell.utils.misc.TimeUtils;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Common abstract base for Business Objects that bind to a database, i.e. they are Persistant
 * @param <T> the generic type of any subclass
 * @see PersistentBusinessObject
 *
 * @author pfarrell
 */
public abstract class AbstractPersistentBusinessObject<T extends AbstractPersistentBusinessObject> extends AbstractPersistentObject<T> 
            implements  Comparable<T>, PersistentBusinessObject, Serializable {

    /** class-wide logger static for reuse   */
    protected static final Logger pboLogger = Logger.getLogger(AbstractPersistentBusinessObject.class);


    /** Creates a new instance of AbstractPersistentBusinessObject */
    protected AbstractPersistentBusinessObject() {
    }
    /**
     * Creates a new instance of AbstractPersistentBusinessObject, loading it from database
     * @param id primary key
     * @throws SQLException  pass up any Sql complaints
     */
    protected AbstractPersistentBusinessObject(long id) throws SQLException {
        Preconditions.checkArgument(id > 0, "Primary key must be positive");
        setId(id);
        load();
    }
    /** helper function, Google Guava function */
private static  Function<AbstractPersistentBusinessObject, Long> PboIdConvertor = new Function<AbstractPersistentBusinessObject, Long>() {
                @Override
        public Long apply(AbstractPersistentBusinessObject arg) {
                return  arg.getId();
            }
    };
    /**
     * gets (converts) a list of PBO to a list of their primary keys
     * @param <T> type of the PBO
     * @param arg the list of PBO
     * @return List of primary keys
     */
public static <T extends AbstractPersistentBusinessObject>  List<Long> getIdList(List<T> arg ) {
    List<Long> rval = null;
    rval = Lists.transform(arg, PboIdConvertor);
    return rval;
}
    /**
     * creates and loads list of all objects from database 
     * @param <T> generic type of PBO
     * @param caller class/type of data to be returned, must not be null
     * @return array list of <T> loaded with data
     * @throws SQLException  pass up any Sql complaints
     */
public static <T extends AbstractPersistentBusinessObject> List<T> findAll( Class<T> caller) throws SQLException {
    Preconditions.checkNotNull(caller);
    List<T> rval = new ArrayList<T>();
    AbstractPersistentBusinessObject pbo = null;
    try {
        pbo = caller.newInstance();
        String query = pbo.getStringForSelect();
        rval = factoryFromQueryString( query,  caller);
    } catch (InstantiationException ex) {
        pboLogger.fatal("find all InstantiationException", ex);
    } catch (IllegalAccessException ex) {
        pboLogger.fatal("find all IllegalAccessException", ex);
    } catch (IllegalStateException ex) {
        pboLogger.error("find all error", ex);
        throw ex;
    }
    return rval;
}

/**
 * creates and returns a {@link PboIterable} for all records in the calling argument's table
 * @param <T> the generic type desired
 * @param caller the class of the type
 * @return a PboItarable into the result set
 */
public static <T extends AbstractPersistentBusinessObject> PboIterable<T>
                                            findIterableForAll( Class<T> caller)  {
        Preconditions.checkNotNull(caller);
        String query = null;
        Connection myCon = null;
        Statement  s = null;
        PboIterable<T> rval = null;
        T anObj = null;
        try {
            anObj = (T) caller.newInstance();
            assert anObj != null;
            
            query = anObj.getStringForSelect();
            myCon = DBConnectionPool.getConnection();
            s = myCon.createStatement();
            if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
            ResultSet rs = s.executeQuery(query);
            rval = new PboIterable<T>(myCon, rs, caller);
        } catch (SQLException ex) {
            String msg = String.format("APBO:findIterableForAll for %s caught Sql doing %s", 
                            caller.getName(), query);
            pboLogger.error(msg, ex);
        }  catch (InstantiationException ex) {
            pboLogger.error("PBO factory",  ex);
        } catch (IllegalAccessException ex) {
            pboLogger.error("PBO IAE factory",  ex);
        }
    
        return rval;
}


    /**
     * creates and loads list of  objects from database retreived according to the query string
     * @param <T> generic type of PBO
     * @param query SQL select statement, must not be null
     * @param caller class/type of data to be returned, must not be null
     * @return array list of <T> loaded with data
     * @throws SQLException  pass up any Sql complaints
     */
public static <T extends AbstractPersistentBusinessObject> PboIterable<T>
        iterableFromQueryString(String query, Class<T> caller) throws SQLException {
    Preconditions.checkNotNull(query);
    Preconditions.checkNotNull(caller);
    Connection myCon = null;
    Statement  s = null;
    PboIterable<T> rval = null;
    try {
        myCon = DBConnectionPool.getConnection();
        s = myCon.createStatement();
        if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
        ResultSet rs = s.executeQuery(query);
        rval =  new PboIterable<T>( myCon, rs, caller);
    }  catch (SQLException sqe) {
        String msg = String.format("APBO: iterable FromQueryString for %s caught Sql doing %s", 
                        caller.getName(), query);
        pboLogger.error(msg, sqe);
        throw sqe;
    } catch (RuntimeException e) {
        if (pboLogger != null && e != null) pboLogger.error("factory error", e);
        throw e;
    }
    return rval;
}
public static <T extends AbstractPersistentBusinessObject> PboIterable<T>
        iterableFromQueryString(String query, Class<T> caller, String... args) throws SQLException {
    Preconditions.checkNotNull(query);
    Preconditions.checkNotNull(caller);
    Connection myCon = null;
    PboIterable<T> rval = null;
    PreparedStatement ps = null;
    try {
        myCon = DBConnectionPool.getConnection();
        if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
        ps = myCon.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            ps.setString(i+1, args[i]);
        }
        if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
        ResultSet rs = ps.executeQuery(query);
        rval =  new PboIterable<T>( myCon, rs, caller);
    }  catch (SQLException sqe) {
        String msg = String.format("APBO: iterable FromQueryStringVarArg for %s caught Sql doing %s", 
                        caller.getName(), query);
        pboLogger.error(msg, sqe);
        throw sqe;
    } catch (RuntimeException e) {
        if (pboLogger != null && e != null) pboLogger.error("factory error", e);
        throw e;
    }
    return rval;
}
/**
 * gets a list of populated records for the query string argume
 * @param <T> type of the returned objects
 * @param query a SQL query ready to fire
 * @param caller the class of the PBO to return
 * @return the List resulting from executing the query
 * @throws SQLException
 */
public static <T extends AbstractPersistentBusinessObject> List<T>
        factoryFromQueryString(String query, Class<T> caller) throws SQLException {
    Preconditions.checkNotNull(query);
    Preconditions.checkNotNull(caller);
    ArrayList<T> rval = null;
    Connection myCon = null;
    Statement  s = null;
    try {
        myCon = DBConnectionPool.getConnection();
        s = myCon.createStatement();
        if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
        ResultSet rs = s.executeQuery(query);
        rval = returnResultList(rs, caller);
    }  catch (SQLException sqe) {
        String msg = String.format("APBO: factoryFromQueryString for %s caught Sql doing %s", 
                        caller.getName(), query);
        pboLogger.error(msg, sqe);
        throw sqe;
    } catch (RuntimeException e) {
        if (pboLogger != null && e != null) pboLogger.error("factory error", e);
        throw e;
    } finally {
        if ( s != null) s.close();
    }
    DBConnectionPool.returnCon(myCon, null);
    return rval;
}
    /**
     * creates and loads list of  objects from database retrieved according to the query string (with PreparedStatement
     * style binding).
     * @param <T> generic type of PBO
     * @param query SQL select statement, must not be null
     * @param caller class/type of data to be returned, must not be null
     * @param args variable list of parameters to fill in
     * @return array list of <T> loaded with data
     * @throws SQLException  pass up any Sql complaints
     */
public static <T extends AbstractPersistentBusinessObject> ArrayList<T> 
        factoryFromQueryString(String query, Class<T> caller, String... args)
                    throws SQLException {
    Preconditions.checkNotNull(query);
    Preconditions.checkNotNull(caller);
    Preconditions.checkNotNull(args);
    ArrayList<T> rval = null;
    Connection myCon = null;
    PreparedStatement ps = null;
    try {
        myCon = DBConnectionPool.getConnection();
        if ( pboLogger.isInfoEnabled()) pboLogger.info(query);
        ps = myCon.prepareStatement(query);
        for (int i = 0; i < args.length; i++) {
            ps.setString(i+1, args[i]);
        }
        ResultSet rs = ps.executeQuery();
        rval = returnResultList(rs, caller);
    }  catch (SQLException sqe) {
        String msg = String.format("APBO:factoryFromQueryString with args for %s caught Sql doing %s", 
                        caller.getName(), query);
        pboLogger.error(msg, sqe);
        throw sqe;
    } catch (RuntimeException e) {
        if (pboLogger != null && e != null) pboLogger.error("factory error", e);
        throw e;
    } finally {
        if ( ps != null) ps.close();
    }
    DBConnectionPool.returnCon(myCon, null);
    return rval;
}
   /**
    * rolls through the ResultSet and creates filled in objects of the argument type
    * @param <T> the type of the 'caller' parameter
    * @param rs SQL result set
    * @param caller the class of the caller, used to create new instance
    * @return array list of <T> loaded with data
    * @throws SQLException  pass up any Sql complaints
    */
private static <T extends AbstractPersistentBusinessObject> ArrayList<T> returnResultList(ResultSet rs, Class<T> caller) throws SQLException {
    Preconditions.checkNotNull(rs);
    Preconditions.checkNotNull(caller);
    ArrayList<T> rval = new ArrayList<T>();
    try {
        boolean next = rs.next();
        while (next) {
            T anObj = null;
            try {
                anObj = (T) caller.newInstance();
            }  catch (InstantiationException ex) {
                pboLogger.error("PBO factory",  ex);
                break;
            } catch (IllegalAccessException ex) {
                pboLogger.error("PBO IAE factory",  ex);
                break;
            }
            assert anObj != null;
            anObj.populateOneRecord(rs);
            anObj.setFromDatabase();

            if (anObj instanceof Cacheable) {
                Cacheable asCacheable = (Cacheable) anObj;
                asCacheable.storeThisInCache();
            }
            rval.add(anObj);
            next = rs.next();
        }
    }  catch (SQLException sqe) {
        pboLogger.error("PersistentBusinessObject:returnResultList for " + caller.getName(), sqe);
        throw sqe;
    } catch (RuntimeException e) {
        if (pboLogger != null && e != null) pboLogger.error("factory error", e);
        throw e;
    }
    return rval;
}

static final SimpleDateFormat dbgDtFormat;
static {
    dbgDtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
    dbgDtFormat.setTimeZone(TimeUtils.utcTZ);
}
   /**
    * dump out contents of a Date/Timestamp field to the debug log
    * @param arg the Date to write
    * @return the formatted string.
    */ 
protected String debugDate(Date arg) {
    String rval = dbgDtFormat.format(arg);
    pboLogger.debug(rval);
    return rval;
}
   /**
    * gets a timestamp from the current row of the ResultSet, identified by the
    * fieldName argument.
    * @param rs an open, JDBC ResultSet
    * @param fieldName the column within the current row
    * @return the field's value, converted to a Timestamp
    * @throws SQLException pass up any Sql problems.
    */
public Timestamp getTimestamp(ResultSet rs, String fieldName) throws SQLException {
    Preconditions.checkNotNull(rs);
    Preconditions.checkNotNull(fieldName);
    String working = rs.getString(fieldName);
    Timestamp rval = getTimestamp(working);
    return rval;
}
   /**
    * gets a timestamp from the current row of the ResultSet, identified by the
    * fieldName argument.
    * @param rs an open, JDBC ResultSet
    * @param colNum the column number within the current row
    * @return the field's value, converted to a Timestamp
    * @throws SQLException pass up any Sql problems.
    */
public Timestamp getTimestamp(ResultSet rs, int colNum) throws SQLException {
    Preconditions.checkNotNull(rs);
    Preconditions.checkArgument(colNum > 0);
    String working = rs.getString(colNum);
    Timestamp rval = getTimestamp(working);
    return rval;
}
static final SimpleDateFormat sqlFormat;
static {
    sqlFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    sqlFormat.setTimeZone(TimeUtils.utcTZ);
}
private Timestamp getTimestamp(String arg) {
    Timestamp rval = TimeUtils.beginingOfTime;
    if (arg == null || arg.length() < 3 || arg.startsWith("0000-00-00 00:00:00")) {
        return rval;
    }
    try {
        Date tmp = sqlFormat.parse(arg);
        rval = new Timestamp(tmp.getTime());
    } catch (ParseException ex) {
         pboLogger.error(ex);
    }
    return rval;
}
}
