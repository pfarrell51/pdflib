/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import com.pfarrell.gps.NmeaStatement;

/**
 * The <code>AmodVersionStatement</code> class implements AMOD's version statement
 * $ADVER,3080,2.2
 * @author pfarrell
 * Created on Jan 1, 2011, 1:52:42 PM
 * Copyright (C) 2010 Patrick Farrell  
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
public class AmodVersionStatement  extends AmodStatement implements  NmeaStatement {
private String model;
private String version;
private String minor;

    /** usual constructor argument is input string  */
public  AmodVersionStatement(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    String[] parts = getInputString().split(",");
    if (parts[0].equals("$ADVER")) {
        model = parts[1];
        String minorparts[] = parts[2].split("\\.");
        version = minorparts[0];
        minor = minorparts[1];
        consistant = true;
    } else {
        consistant = false;
    }
}

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @return the minor
     */
    public String getMinor() {
        return minor;
    }

    /**
     * @return the model
     */
    public String getModel() {
        return model;
    }


}
