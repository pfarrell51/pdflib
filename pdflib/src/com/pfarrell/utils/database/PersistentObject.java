/*
 * PersistentObject.java
 *
 * Created on July 25, 2006, 7:00 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.utils.database;

import com.pfarrell.exceptions.RequiredValueMissingException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <tt> PersistentObject</tt> interface defines how all PersistentObjects
 * bind to the database.
 *
 * @author pfarrell
 */
public interface PersistentObject {
    /**
     * deletes object from database
     * @return true if success
     * @throws SQLException pass up any complaints
     */
    boolean delete() throws SQLException;
    /**
     * gets dirty flag, indicates something has changed
     * @return true if something not in DB record
     */
    boolean isDirty();
    /**
     * get the  Id, the primary key.
     * @return the  Id
     */
    long getId();
    /**
     * set the  Id, the primary key.
     * @param v primary key
     */
    void setId(long v);
    /**
     * gets column name of primary key
     * 
     * @return column name of primary key
     */
    String getIdFieldName();

    /**
     * build up and return suitable Insert/Update command for SQL
     * @return suitable Insert/Update command for SQL
     */
    String getSaveCommandString();

    /**
     * return SQL command to retreive a single row of the table, with all fields.
     * @return SQL command to retreive a single row of the table
     */
    String getSingleRecordSelect();
    /**
     * return SQL command to retreive all fields.
     * @return SQL command to retreive all fields
     */
    String getStringForSelect();

    /**
     * get name of database table associated with this object.
     * @return name of database table associated with this object.
     */
    String getTable();

    /**
     * gets save() status
     * @return true if saved to database, false if needs to be saved.
     */
    boolean isSaved();
    /**
     * gets whether this is from database
     * @return true if from database, false if generated from ether
     */
    boolean isFromDatabase();
    /**
     * sets this is from database
     */
    void setFromDatabase();
    /**
     * prototype for routine to pull data from the ResultSet and store into member variables.
     * @param rs ResultSet from SQL engine
     * @throws java.sql.SQLException pass up any complaints
     */
    void populateOneRecord(ResultSet rs) throws SQLException;

    /**
     * save object contents to database
     * 
     * @return primary key if insert
     * @throws RequiredValueMissingException when critical key data is missing
     * @throws SQLException pass up any complaints
     */
   long save() throws RequiredValueMissingException, SQLException;

    /**
     * prototype for routine to validate values before saving to database
     * @throws RequiredValueMissingException thrown to indicate missing value or other problem
     */
    void validateValues() throws RequiredValueMissingException;
        
}
