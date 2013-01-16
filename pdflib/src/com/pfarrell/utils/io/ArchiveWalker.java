/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

/**
 * The <code>ArchiveWalker</code> interface defines key methods of an archive walker, which processes .zip or .tar files
 * 
 * @author pfarrell
 * Created on Jun 6, 2012, 3:29:15 PM
 */
public interface ArchiveWalker extends Walker {
    boolean isFileToProess(String name);
    boolean isFileToRecurseOn(String name);
}
