package  com.pfarrell.exceptions;
/*
 * RequiredValueMissingException.java
 *
 * Copyright (c) 2004 Pat Farrell. All rights reserved
 * Created on October 4, 2004, 6:17 PM
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
 * limitations under the License.
 */

import java.lang.Exception;
/**
 * the <code>RequiredValueMissingException</code> class
 * implements a unique exception so we can trap this error explicitly
 * @author  Patrick Farrell
 */

public class RequiredValueMissingException  extends Exception {
	private static final long serialVersionUID = 32L;
 
    /**
     * Creates a new instance of RequiredValueMissingException
     * @param arg tracking string
     */
    public RequiredValueMissingException(String arg) {
        super(arg);
    }
    
}
