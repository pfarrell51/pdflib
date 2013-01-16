package  com.pfarrell.utils.database;
/*
 * DuplicateKeyException.java	0.51 2000/01/15
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 * based on work Copyright (c) 2001, OneBigCD, Inc.  All rights reserved.
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
