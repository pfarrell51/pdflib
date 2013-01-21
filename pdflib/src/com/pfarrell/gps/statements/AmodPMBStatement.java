/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import org.apache.log4j.Logger;

/**
 * The <code>AmodPMBStatement</code> class implements the AMOD "PMB" statement
 * $ADPMB,5,0
 * @author pfarrell
 * Created on Jan 1, 2011, 2:31:04 PM
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
public class AmodPMBStatement extends AmodStatement {
         /** logger instance */
private static final Logger asLog = Logger.getLogger(AmodPMBStatement.class);
private String first;
private String second;

public AmodPMBStatement(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    String[] parts = getInputString().split(",");
    if (parts[0].equals("$ADPMB")) {
        first = parts[1];
        second = parts[2];
        consistant = true;
        asLog.info(String.format("%s: %s, %s", getTag(), first, second));
    } else {
        consistant = false;
    }
}

    /**
     * @return the first
     */
    public String getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public String getSecond() {
        return second;
    }
}
