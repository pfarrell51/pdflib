package com.pfarrell.utils.database;

/*
 * DBConnectionPool.java	0.51 2000/01/15
 *
 * Copyright (c) 2006-2013 Pat Farrell. All rights reserved
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  
 */
import com.google.common.base.Preconditions;
import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;
import com.pfarrell.exceptions.PibException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


/**
 * Implement trivial DB connection pooling. Read DB connection information from a properties file. File
 * can be either in the usual place (root of source tree, or WEB-INF/classes), or if not there, try to get
 * the file path from an environment variable.
 * 
 * Implemented as a singleton.
 * This will be replaced by using Tomcat or JBoss or something
 * fancier later.
 *
 * @author  Pat Farrell
 * @version September 27, 2004
 */

public class DBConnectionPool {

    /** class-wide logger static for reuse   */
protected static final Logger dbcpLogger = Logger.getLogger(DBConnectionPool.class);

/** prop file to read */
    public static final String PDFLIB_DB_PROPERTIES = "pdflibDB.properties";
/** prop file to read */
    public static final String PDFLIB_ENV_NAME = "PDFLIB_PROPS_PATH";

/** flag for initialization */
private boolean initialized = false;

/** how grody we will let something be */
private static final long MaxUnusedTime = TimeUnit.MINUTES.toMillis(4);
/** flag if we are executing inside a servlet container */
private boolean inContainer = false;

    /** JDBC class name */
private String dbClass;
/** host */
private String dbHost;
/** port */
private String dbPort;
    /** URL for JDBC  */
private String dbUrl;
/** name of default database or schema set */
private String dbDatabase;
    /** username for database connection */
private String dbUsername;
    /** password for username */
private String dbPasswd;
    /** force session times to use */
private String sessionTimeZone;
    /** the pool */
private IdentityHashMap<Connection, Date> poolConnections = new IdentityHashMap<Connection, Date>();
private IdentityHashMap<Connection, Date> openConnections = new IdentityHashMap<Connection, Date>();

/** semaphore for safety */
private  ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /** singleton connection pool */
private static HelperSinglton theHelperSingleton;
    /**
     * construct a connector
     */    
    private DBConnectionPool() {
        try {
            internalInit();
        } catch (SQLException ex) {
            dbcpLogger.error( "DBConnectionPool constructor failure", ex);
            throw new PibException("can't construct DBConnectionPool");
        }
    }
/**
 * gets the singleton instance after initializing (or re-initializing it) from the input stream
 * @param is stream to load
 * @return the singleton instance
 */
    @SuppressWarnings("static-access")
public static DBConnectionPool getInstance(InputStream is) {
    Preconditions.checkNotNull(is);
    if (! theHelperSingleton.theOne.initialized ) {
        theHelperSingleton.theOne.loadProperties(is);
        theHelperSingleton.theOne.initialized = true;
    }
    return theHelperSingleton.theOne;
}
    /**
     * initialize the pool
     * @throws SQLException pass up any Sql exceptions
     */
private void internalInit() throws SQLException {
    if ( initialized ) return;
    lock.writeLock().lock();
    try {
        if (dbUrl == null) {
            InputStream is = getPropertiesStream();
            loadProperties(is);
            Class loadedInstance = Class.forName(dbClass);
            loadedInstance.newInstance();
            try {
                Class.forName("javax.servlet.http.HttpServlet");
                inContainer = true;
            } catch (ClassNotFoundException ex) {
                inContainer = false;                
            }
            initialized = true;
        }
    } catch (ClassNotFoundException e) {
        String msg = "DBConnectionPool:init can't find class";
        System.err.println(msg);
        dbcpLogger.log(Level.ALL, msg, e);
        throw new SQLException("DB could not find driver class: " + dbClass);
    } catch (IllegalAccessException iae) {
        dbcpLogger.log(Level.ALL, "DBConnectionPool:init ", iae);
        throw new SQLException("DBConnectionPool:init caught " + iae.getMessage() + " for " + dbClass);
    } catch (InstantiationException ie) {
        dbcpLogger.log(Level.ALL, "DBConnectionPool:init ", ie);
        System.err.println("DBConnectionPool:instantiation ");
        throw new SQLException("DBConnectionPool:init caught  " + ie.getMessage() + " for " + dbClass);
    } finally {
        lock.writeLock().unlock();
    }
}

/**
 * gets an input stream for the properties file. This can be either from the file named in the the environment variable, or
 * if that does not exist, the named property file.
 * @return input stream for properties
 */
private InputStream getPropertiesStream() {
    InputStream rval = null;
    Thread myThread = Thread.currentThread();
    ClassLoader cl = myThread.getContextClassLoader();
     //ClassLoader obtained by Thread#getContextClassLoader() of the thread returned by Thread#currentThread().
    Map<String,String> mapEnv = System.getenv();
    for ( String k : mapEnv.keySet()) {
        if ( k.equalsIgnoreCase(PDFLIB_ENV_NAME)) {
            String val = mapEnv.get(k);
            if ( val != null && val.length() > 1) {
                File f = new File(val);
                try {
                    rval = new FileInputStream(f);
                    if (rval != null) {
                        dbcpLogger.trace("found property file from env");
                        break;
                    }
                } catch (FileNotFoundException ex) {
                    dbcpLogger.error( ex);
                }
            }
        }
    }
    if (rval != null) return rval;
    
    rval = cl.getResourceAsStream(PDFLIB_DB_PROPERTIES);

    dbcpLogger.trace("found property file in default location");
    if ( rval == null) {
        String err = "can not find proper file: " + PDFLIB_DB_PROPERTIES;
        dbcpLogger.fatal(err);
        System.err.println(err);
    }
    return rval;
}
/**
 * read properties, fill them in.
 * @param is stream to property file
 */
private void loadProperties(InputStream is) {
    Properties theProps = new Properties();
    try {
        if ( is  != null)  theProps.load(is);
        dbClass  = theProps.getProperty("dbClass", "com.mysql.jdbc.Driver");
        dbHost = theProps.getProperty("dbHost",  "localhost");
        dbPort = theProps.getProperty("dbPort", "3306");
        dbDatabase  = theProps.getProperty("dbDatabase");
        dbUsername = theProps.getProperty("dbUser");
        dbPasswd  = theProps.getProperty("dbPasswd");
        //"jdbc:mysql://localhost:3306/foobaz, 
        dbUrl = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbDatabase;
        dbcpLogger.trace("using JDBC url of " + dbUrl);
        sessionTimeZone = theProps.getProperty("timezone", "system");
    } catch (IOException ex) {
        dbcpLogger.error("IO error",  ex);
    }
}
private static DBConnectionPool getInstance() {
    return theHelperSingleton.theOne;
}
/**  only for testing */
public static String getUsername() {
    return getInstance().dbUsername;
}
/** only for testing */
public static String getPassword() {
    return getInstance().dbPasswd;
}
/**
 * gets JDBC url, handy for debugging
 * @return JDBC url
 */
public static String getDbUrl() {
    return getInstance().dbUrl;
}
    /**
     * gets flag if we are executing inside a servlet container 
     * @return true if running inside a servlet container
     */
public static boolean isInContainer() {
    return getInstance().inContainer;
}

    /**
     * @return the sessionTimeZone
     */
public static String getSessionTimeZone() {
    return getInstance().sessionTimeZone;
}
/**
 * @return returns the size of the connection pool
 */
public static int getPoolSize() {
    return getInstance().poolConnections.size();
}
public static int getOpenSize() {
    return getInstance().openConnections.size();
}
/**
 * get a connection from the pool
 * @throws SQLException pass up any Sql problems
 * @return a JDBC connection
 */    
public static Connection getConnection() throws SQLException  {
    DBConnectionPool dbP = getInstance();
    if (dbP == null ) return null;
    Connection rval = dbP.getConnectionInternal();
    return rval;
}
/**
 * release connection to the pool
 * @param c the Connection
 */
public static void returnCon(Connection c) {
    returnCon( c, null);
}
/**
 * release connection to the pool
 * @param c the Connection
 * @param except exception that triggered this closure.
 */    
public static void returnCon(Connection c, Exception except) {
    getInstance().returnConInternal(c, except);
}
    /**
     * return connection to the pool
     * @param c the connection
     * @param except pass up any problems
     */
    @SuppressWarnings("static-access")
private void internalReturn(Connection c, Exception except) {
    if (getInstance() == null) {
        String msg = "PIB, returning connection before using them";
        dbcpLogger.error(msg);
        throw new PibException(msg);
    }
    DBConnectionPool dbP = theHelperSingleton.theOne;
    dbP.returnConInternal(c, except);
}

 /**
 * get a connection from the pool
 * @throws SQLException pass up any Sql problems
 * @return a JDBC connection
 */    
private Connection getConnectionInternal() throws SQLException {
    Connection con = null;
    lock.writeLock().lock();
    try {
        if (poolConnections.size() > 0) {
            Iterator<Connection> iter = poolConnections.keySet().iterator();
            Connection tmpcon = iter.next();
            if (tmpcon.isClosed()) {
                dbcpLogger.error("getCon:con from pool is closed");
            }
            Date lastUsed = poolConnections.remove(tmpcon);
            Date now = new Date();
            long delta = now.getTime() - lastUsed.getTime();
            if (delta < MaxUnusedTime) {
                con = tmpcon;
            } else {
                if (! tmpcon.isClosed()) {
                    tmpcon.close();
                }
            }
        }
        if (con == null) {
            con = DriverManager.getConnection(getInstance().dbUrl,
                    getInstance().dbUsername, getInstance().dbPasswd);
        }
        Date now = new Date();
        openConnections.put(con, now);
        
    } catch (MySQLNonTransientConnectionException ex) {
        dbcpLogger.error("probably out of connections", ex);
        throw ex;
    } finally {
        lock.writeLock().unlock();
    }
    return con;
}

/**
 * release connection to the pool
 * @param c the Connection
 * @param except exception that triggered this closure.
 */
    @SuppressWarnings("empty-statement")
private void returnConInternal(Connection c, Exception except)  {
    if (c == null) return;
    lock.writeLock().lock();
    try {
        Date oldVal = openConnections.remove(c);
        if (except == null) {
            try {
                if ( c.isClosed())  {
                    dbcpLogger.error("retCon:con to pool is closed");
                } else {
                    c.clearWarnings();
                    poolConnections.put(c, new Date());
                }
            } catch (Exception e) {
                try { 
                    c.close(); 
                } catch (Exception ee) {
                    dbcpLogger.fatal("error in nexted close of returnCon", e);
                }
            }
        } else {
            // leave it removed
        }
    } finally {
        lock.writeLock().unlock();
    }
}

/** class to ensure that the connection is created without Double-checked locking problems */
    static class HelperSinglton {
        /** the one and only */
        static DBConnectionPool theOne = new DBConnectionPool();
    }
}