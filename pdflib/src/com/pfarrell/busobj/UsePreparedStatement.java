/*
 * UsePreparedStatement.java
 *
 * Created on December 18, 2006, 12:26 PM
 *
 * Copyright (c) 2006, Pat Farrell. All rights reserved.
 */

package com.pfarrell.busobj;

/**
 * The <code>UsePreparedStatement</code> interface declares {@link com.pfarrell.busobj.PersistentBusinessObject}s
 * that must use a PreparedStatement rather than inline strings for
 * saving and retreival.
 *
 * @author pfarrell
 */
public interface UsePreparedStatement {
      public String getSaveStatement();
      public String getSelectStatement();
}
