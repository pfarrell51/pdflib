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
 * Copyright (C) 2010 Patrick Farrell. All rights reserved
 * 
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

public class UnacceptablePassphraseException  extends IllegalStateException {
	private static final long serialVersionUID = 32L;

public UnacceptablePassphraseException(String m) {
        super(m);
    }
}
