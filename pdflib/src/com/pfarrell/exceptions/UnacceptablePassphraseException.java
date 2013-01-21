/*
 * Copyright (C) 2009 Patrick Farrell.  All Rights reserved.
 */

package com.pfarrell.exceptions;

/**
 * The <code>UnacceptablePassphraseException</code> class is a runtime exception
 * that does not have to be declared, but can be readily trapped.
 * 
 * @author pfarrell
 * Created on Mar 23, 2010, 12:25:40 AM
 */
public class UnacceptablePassphraseException  extends IllegalStateException {
	private static final long serialVersionUID = 32L;

public UnacceptablePassphraseException(String m) {
        super(m);
    }
}
