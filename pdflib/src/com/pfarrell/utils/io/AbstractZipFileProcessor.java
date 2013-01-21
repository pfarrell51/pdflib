/*
 * Copyright (C) 2011-2012 Patrick Farrell. All Rights reserved.
 * Will be released with a suitable open-source liscence, problably BSD-like.
 * Contact me for a pre-release access.
 */
package com.pfarrell.utils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.apache.log4j.Logger;

/**
 * The <code>AbstractZipFileProcessor</code> class implements the commonly used function of any 
 * {@link ZipFileProcessor} so that subclasses can focus on the important stuff.
 * @author pfarrell
 * Created on Nov 15, 2011, 6:29:18 PM
 * Copyright (C) 2011 Patrick Farrell   All Rights reserved.
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
 * limitations under the License. * Licensed under the Apache License, Version 2.0 (the "License");
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
public abstract class AbstractZipFileProcessor extends AbstractArchiveFileProcessor implements ZipFileProcessor {

    /** logger instance */
    private static final Logger aLog = Logger.getLogger(AbstractZipFileProcessor.class);


    
    /** default constructor */
    public AbstractZipFileProcessor() {
        super();
    }

    @Override
    public void process(File fileToProcess) throws FileNotFoundException {
        setZipEntryName(fileToProcess.getName());
        FileInputStream fis = new FileInputStream(fileToProcess);
        process(fis);
    }

    public abstract void process(InputStream streamToProcess);

    public void setZipEntryName(String arg) {
        setEntryName(arg);
    }

    public String getZipEntryName() {
        return getEntryName();
    }

}
