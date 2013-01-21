/*
 * Copyright (C) 2012 Patrick Farrell. All Rights reserved.
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
package com.pfarrell.utils.io;

import java.io.File;
import java.io.InputStream;

/**
 * The <code>ArchiveFileProcessor</code> interface defines a WalkDrivenProcessor that can handle
 * archives of files such as ZIP or TAR (tape archive in Unix) files.
 * 
 * @author pfarrell
 * Created on May 31, 2012, 9:46:26 PM
 */
public interface ArchiveFileProcessor extends WalkDrivenProcessor {
    /** generic process function, do what is applicable to this object
     * @param input stream handle to input file
     */  
   void process(InputStream input);

    /**
     * Decide if you want to descend down into subdirectories
     * @param dirToDecend handle to this directory 
     * @return true if process is to process subdirectory
     */    
    boolean descendDir(File dirToDecend);
    void setEntryName(String arg);
    String getEntryName();
   /** gets number of raw text lines read */
   int getLinesRead();
   /** gets number of records read and processed */
   int getRecordsProcessed();
   /** gets number of records processed successfully */
   int getSuccessRecords();
   /** gets number of records rejected */
   int getRejectedRecords();
   /** gets the walker used by this processor */
   AbstractWalker getWalker();
}
