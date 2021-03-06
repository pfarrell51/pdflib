/*
 * Copyright (C) 2009-2011 Patrick Farrell  and R. Scott Eisenberg. All Rights reserved.
 * Copyright (C) 2013 Patrick Farrell. All Rights reserved.
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


package com.pfarrell.gps.enums;

/**
 * the ActiveWarning enum defines the NMEA flags, which are "A" for OK/ arrived
 * and "V" for bad, warning, not here yet, etc.
 * @author pfarrell
 * Created on Dec 28, 2010, 2:41:31 PM
 */
public enum ActiveWarning {
    A,  // Active
    V;  // bad/warning
}
