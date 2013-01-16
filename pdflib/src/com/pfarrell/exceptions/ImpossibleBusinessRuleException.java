/*
 * ImpossibleBusinessRuleException.java
 *
 * Created on August 14, 2007, 3:02 PM
 *
 * Copyright (c) 2007, Pat Farrell. All rights reserved.
 */

package  com.pfarrell.exceptions;

import java.sql.SQLException;

/**
 * The <code>ImpossibleBusinessRuleException</code> class implements
 * an exception that indicates that a fundamental business rule or database
 * consitancy rule has been violated.
 *
 * @author pfarrell
 */
public class ImpossibleBusinessRuleException  extends SQLException {
    
    /** Creates a new instance of ImpossibleBusinessRuleException */
    public ImpossibleBusinessRuleException() {
    }
    /**
     * Creates a new instance of ImpossibleBusinessRuleException
     * @param message error string
     */
    public ImpossibleBusinessRuleException(String message)    {
        super(message);
    }
}
