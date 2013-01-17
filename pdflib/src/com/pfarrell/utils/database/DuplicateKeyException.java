package  com.pfarrell.utils.database;
/*
 * DuplicateKeyException.java	0.51 2000/01/15
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 * based on work Copyright (c) 2001, OneBigCD, Inc.  All rights reserved.
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


import java.sql.SQLException;

public class DuplicateKeyException extends SQLException {

/** construct a     DuplicateKeyException
 * useful so we can identify this specific error
 */
    public DuplicateKeyException(String code, SQLException e) {
        super("SQL=" + code + "  " + e, e.getSQLState(), e.getErrorCode());
    }
    //public int getErrorCode() { return exp.getErrorCode(); };
    //public SQLException getNextException() { return exp.getNextException(); };
    //public void setNextException(SQLException e) { exp.setNextException(e); };
    //public String getSQLState() { return exp.getSQLState(); };
}
