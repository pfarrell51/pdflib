/*
 * Copyright (C) 2012 Wayfinder Digital LLC. All Rights reserved.
 */
package com.pfarrell.utils.io;

import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractTarFileProcessor</code> class implements a base class for a tar file processor.
 * @author pfarrell
 * Created on Jun 2, 2012, 12:57:03 AM
 */
public abstract class AbstractTarFileProcessor extends AbstractArchiveFileProcessor implements TarFileProcessor{

    /** logger instance */
    private static final Logger aLog = Logger.getLogger(AbstractTarFileProcessor.class);

    /** default constructor */
    public AbstractTarFileProcessor() {
        super();
    }

    public abstract void process(InputStream input);
}
