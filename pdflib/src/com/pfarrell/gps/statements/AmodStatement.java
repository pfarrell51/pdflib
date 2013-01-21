/*
 * Copyright (C) 2009-2011 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps.statements;

import org.apache.log4j.Logger;

/**
 * The <code>AmodStatement</code> class implements a common base for all proprietary
 * statements from <a href="http://www.amod.com.tw/">AMOD</a> data loggers
 * @author pfarrell
 * Created on Jan 1, 2011, 1:32:27 PM
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
public class AmodStatement  extends AbstractNmeaStatement implements ProprietaryStatement {
     /** logger instance */
private static final Logger aLog = Logger.getLogger(AmodStatement.class);

public AmodStatement(String arg) {
    super(arg);
}
    public boolean isProprietary() {
        return true;
    }

    public String getVendorName() {
        return "AMOD";
    }

    public String getVendorCode() {
        return "AD";
    }

    public boolean isChecksumMandatory() {
        return false;
    }

}
