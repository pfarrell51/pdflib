/*
 * AbstractPersistentObject.java
 *
 * Created on July 25, 2006, 7:07 PM
 *
 * Copyright (c) 2006, Pat Farrell All rights reserved.
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

package com.pfarrell.utils.database;

import com.google.common.base.Preconditions;
import com.pfarrell.busobj.Cacheable;
import com.pfarrell.busobj.AbstractPersistentBusinessObject;
import com.pfarrell.busobj.UsePreparedStatement;
import com.pfarrell.exceptions.PibException;
import com.pfarrell.exceptions.RequiredValueMissingException;
import com.pfarrell.utils.misc.TimeUtils;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.log4j.Logger;


/**
 * The  <code>AbstractPersistentObject</code> class is an abstract base
 * class implementing the {@link PersistentObject} interface.
 * Concrete classes extending this may talk to separate databases,
 * use pooled connections or other enhancements.
 *
 * @author pfarrell
 */
public abstract class AbstractPersistentObject<T extends AbstractPersistentObject> implements PersistentObject, Serializable, Comparable<T> {
    /** class-wide logger static for reuse   */
protected static final Logger apoLogger = Logger.getLogger(AbstractPersistentObject.class);
/** need to make sure we are thread safe */
protected final ReentrantLock persistentInitializationLock = new ReentrantLock();    
    /** boolean indicator if loaded or created */
protected boolean fromDatabase = false;
        /** generated id, primary key */
protected long apo_id;
/** need to write to the DB? */
protected boolean dirtyFlag = false;
    /**
     * gets whether this is from database
     * 
     * @return true if from database, false if generated from ether
     */
    public boolean isFromDatabase() {
        return fromDatabase;
    }
    /**
     * sets flag saying this is from database
     */
    public void setFromDatabase() {
        Preconditions.checkState(getId() > 0, "APO:setFDB, id not positive");
        fromDatabase = true;
    }
   /**
    * sets the dirty flag
    */
protected void setDirtyFlag() {
    dirtyFlag = true;
}
   /**
    * clear the dirty flag
    */
protected void clearDirtyFlag() {
    dirtyFlag = false;
}
    /**
     * gets dirty flag, indicates something has changed
     * @return true if something not in DB record
     */
public boolean isDirty() {
    return dirtyFlag;
}

public int compareTo(AbstractPersistentObject o) {
    Preconditions.checkNotNull(o);
    if (this == o) return 0;
    int rval = Long.valueOf(apo_id).compareTo(o.apo_id);
    if (rval != 0) return rval;
    rval = Boolean.valueOf(dirtyFlag).compareTo(o.dirtyFlag);
    if (rval != 0) return rval;
    rval = Boolean.valueOf(fromDatabase).compareTo(o.fromDatabase);
    return rval;
}
    /**
     * deletes object from database
     * @return true if success
     */
    @SuppressWarnings("unchecked")
    public boolean delete() throws SQLException  {
        int numRows = 0;
        if (this instanceof Cacheable) {
            Cacheable asCacheable = (Cacheable) this;            
            asCacheable.invalidate(asCacheable.getCacheKey());
        }
        Connection myCon = DBConnectionPool.getConnection();
        String command = "delete from " + getTable() + " where " +  getIdFieldName()  + " = " + getId(); 
        Statement stmt = myCon.createStatement();
        apoLogger.debug(command);
        numRows = stmt.executeUpdate(command);
        stmt.close();
        DBConnectionPool.returnCon(myCon, null);
        stmt.close();
        fromDatabase = false;
        return numRows == 1;
    }

    /**
     * get the  Id, the primary key. Will change database
     * name for each class.
     * 
     * @return the  Id
     */
    public long getId() { return apo_id;};
    /**
     * set ID (the primary key)
     * @param arg ney key
     */
    public void setId(long arg) {
        if (! this.fromDatabase)  apo_id = arg;
        else throw new IllegalArgumentException("can't change primary key when non-zero");
    }
   /**
    * clear out DB indicators, clear ID and fromDatabase
    */
    public void clearFromDB() {
        fromDatabase = false;
        apo_id = 0;
        setDirtyFlag();
    }
    /**
     * gets column name of primary key
     * 
     * @return column name of primary key
     */
    public abstract String getIdFieldName();

    /**
     * build up and return suitable Insert/Update command for SQL
     * 
     * @return suitable Insert/Update command for SQL
     */
    public abstract String getSaveCommandString();

    /**
     * return SQL command to retreive a single row of the table, with all fields.
     * 
     * @return SQL command to retreive a single row of the table
     */
    public abstract String getSingleRecordSelect();

    /**
     * get name of database table associated with this object.
     * 
     * @return name of database table associated with this object.
     */
    public abstract String getTable();
    
    /**
     * gets save() status
     * @return true if saved to database, false if needs to be saved.
     */
    public boolean isSaved() { 
        return fromDatabase;
    }
    
    /**
     * load object from database
     * @throws SQLException pass up any Sql errors
     */
    protected void load()  throws SQLException {
        if (this instanceof UsePreparedStatement) {
            throw new PibException("can not call load() on UsePreparedStatement classes");
        }
        Connection myCon = null;
        String queryStr = null;
        try {
            myCon = DBConnectionPool.getConnection();
            Statement  s = myCon.createStatement();
            queryStr =  getSingleRecordSelect();
            apoLogger.info(queryStr);
      
            ResultSet rs = s.executeQuery(queryStr);
            
            boolean next = rs.next();
            while (next) {
                long id = rs.getLong(getIdFieldName());
                if (id != getId()) {
                    apoLogger.error("APO:load Database error, returned value does not match select criteria");
                }
                populateOneRecord(rs);
                setFromDatabase();
                clearDirtyFlag();
                if (this instanceof Cacheable) {
                    Cacheable asCacheable = (Cacheable) this;
                    asCacheable.storeThisInCache();
                }
                if (next = rs.next()) {
                    apoLogger.error("APO:load : Load data integrity problem, too many records ");
                }
            }
            s.close();
        }  catch (SQLException sqe) {
            apoLogger.error("APO:load : Load caught ",sqe);
            apoLogger.info(queryStr);
        }
         
        DBConnectionPool.returnCon(myCon, null);
    }

    /**
     * prototype for routine to pull data from the ResultSet and store into member variables.
     * 
     * @param rs ResultSet from SQL engine
     * @throws java.sql.SQLException pass up any complaints
     */
    public abstract void populateOneRecord(ResultSet rs) throws SQLException;
    
    /**
     * save object contents to database
     * @return primary key if insert
     * @throws RequiredValueMissingException when critical key data is missing
     */
    public long save() throws RequiredValueMissingException, SQLException {
        long autoIncKeyFromApi = -1;
        if (this instanceof UsePreparedStatement) {
            throw new PibException("can not call save() on UsePreparedStatement classes");
        }
        String command = null;
        Connection myCon = null;
        try {
            validateValues();
        } catch (RequiredValueMissingException ea) {
            apoLogger.error("Missing critical values ***** in " + this.getClass().getName(), ea);
            apoLogger.error(command);
            throw ea;
        } catch (Exception ex) {
            String errMsg = "validation failure";
            System.err.println(errMsg);
            apoLogger.error(errMsg, ex);
            apoLogger.error(command);
        }
        Statement stmt = null;
        try {
            myCon = DBConnectionPool.getConnection();
            command = getSaveCommandString();
            stmt = myCon.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                java.sql.ResultSet.CONCUR_UPDATABLE);
            apoLogger.trace( command);
            int numRows = stmt.executeUpdate(command, Statement.RETURN_GENERATED_KEYS);

            if (numRows != 1) {
                apoLogger.error("Save did not change one rec, numRows=" + numRows);
            }
            if (!fromDatabase) {
                ResultSet rs = null;
                try {
                    rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        autoIncKeyFromApi = rs.getLong(1);
                    } else {
                        throw new PibException("PIB, can't find most recent insert we just entered");
                    }
                    rs.close();
                    rs = null;
                }  finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (SQLException ex) {} // ignore
                    }
                    try {
                        stmt.close();
                    } catch (SQLException ex) {}  // ignore
                }
                if (  autoIncKeyFromApi != -1) {
                    setId( autoIncKeyFromApi );
                }
                if ( this instanceof AbstractPersistentBusinessObject) {
                    AbstractPersistentBusinessObject asPBO = (AbstractPersistentBusinessObject) this;
                    asPBO.setDateOfChange( TimeUtils.timestampNow());
                }
                fromDatabase = true;
                clearDirtyFlag();
            }
            if (this instanceof Cacheable) {
                Cacheable asCacheable = (Cacheable) this;
                asCacheable.storeThisInCache();
            }
        } catch (com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException msEx) {
            String msg = "Save Integrity Exception in AbstractPersistentObject save, check logs ";
            if (apoLogger.isDebugEnabled()) {
                apoLogger.debug( msg + msEx.getMessage());
                apoLogger.debug(command);
            }
            throw new DuplicateKeyException(msg, msEx);
        } catch (SQLException sqe) {
            apoLogger.error( "APO: Save caught",sqe);
            apoLogger.error(command);
            System.err.println("Exception in AbstractPersistentObject save, check logs " + sqe.getMessage());
            System.err.println(sqe.getClass().getName());
            throw sqe;
        } finally {
            if (stmt != null) stmt.close();
            DBConnectionPool.returnCon(myCon, null);
        }
        return autoIncKeyFromApi;
    }


    /**
     * prototype for routine to validate values before saving to database
     */
    public abstract void validateValues() throws RequiredValueMissingException;
    
}
