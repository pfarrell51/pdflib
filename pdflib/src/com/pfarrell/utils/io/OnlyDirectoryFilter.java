package com.pfarrell.utils.io;
/**
 * Copyright (C) 2001-2011 Patrick Farrell   All Rights reserved.
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

import java.io.*;
/**
 * the <tt>OnlyDirectoryFilter</tt> class implements a filter
 * that only accepts directories 
 */
public class OnlyDirectoryFilter implements FilenameFilter {
 
    /**
     * returns our policy for a file.
     * @param dir Java File of the directory to accept
     * @param name of file
     * @return true if it is a directory
     */
    public boolean accept(File dir,  String name) {
        System.out.println("dir is "  + dir.toString());
        System.out.println("name is " + name);
        return dir.isDirectory();
    }
 
}

