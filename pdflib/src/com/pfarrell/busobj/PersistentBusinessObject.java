/*
 * PersistentBusinessObject.java
 *
 * Created on May 4, 2007, 6:14 PM
 *
 * Copyright (c) 2007-2013, Pat Farrell All rights reserved.
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

package com.pfarrell.busobj;

import com.google.common.collect.ImmutableSet;
import com.pfarrell.exceptions.RequiredValueMissingException;
import com.pfarrell.utils.database.PersistentObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The <code>PersistentBusinessObject</code> interface defines real and synthetic classes
 * that represent business objects (Users, etc.) 
 *
 * @author pfarrell
 */
public interface PersistentBusinessObject extends PersistentObject {
/**
 * get name of database table associated with this object.
 * @return  name of database table associated with this object.
 */    
public abstract String getTable();    

/**
 * gets column name of primary key
 * @return column name of primary key
 */
public abstract String getIdFieldName();
    /**
     * sets date of change member variable from database
     * @param arg value from database
     */
public abstract void setDateOfChange(java.sql.Timestamp arg);
    /**
     * prototype for routine to pull data from the ResultSet and store into member variables.
     * @param rs ResultSet from SQL engine
     * @throws java.sql.SQLException pass up any complaints
     */
public abstract void populateOneRecord(ResultSet rs) throws SQLException;
    /**
     * return SQL command to retreive a single row of the table, with all fields.
     * @return SQL command to retreive a single row of the table
     */
public abstract String getSingleRecordSelect();
    /**
     * return SQL command to retreive all fields.
     * @return SQL command to retreive all fields
     */
public abstract String getStringForSelect();
/**
 * returns the list of DB fields
 * @return the list (set) of DB fields
 */
public abstract ImmutableSet<String> tableFields();
    /**
     * build up and return suitable Insert/Update command for SQL
     * @return suitable Insert/Update command for SQL
     */
public abstract String getSaveCommandString();
    /**
     * prototype for routine to validate values before saving to database
     * @throws RequiredValueMissingException  pass up missing cross-field edits
     */
public abstract void validateValues() throws RequiredValueMissingException;
    
}
