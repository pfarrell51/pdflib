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

/**
 * The <code>Walker</code> interface defines the interface for a Walker, which will recursively search
 * through all the files and directories within the starting location.
 * @see ZipWalker
 * @see TarFileWalker
 * @see DirWalker
 * 
 * @author pfarrell
 * Created on Jun 2, 2012, 8:45:45 PM
 */
public interface Walker {

    /**
     * set abort on error flag
     * @param arg boolean value we want to set AbortOnError control
     */
    void setAbortOnError(boolean arg);

    /**
     * walk thru the directory of files, processing all the good ones
     * @throws Exception pass up any exception, usually will be runtime,
     * such as Index out of bounds, etc.
     */
    void walk() throws Exception;
    
}
