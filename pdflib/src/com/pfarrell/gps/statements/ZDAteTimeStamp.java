/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */


package com.pfarrell.gps.statements;

import com.pfarrell.gps.enums.NmeaDataType;
import com.pfarrell.gps.NmeaStatement;
import com.pfarrell.utils.misc.TimeUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * The <code>ZDAteTimeStamp</code> class implements the ZDA statement.
 *
 *   $GPZDA,hhmmss.ss,dd,mm,yyyy,xx,yy*CC<br>
  $GPZDA,201530.00,04,07,2002,00,00*60
<pre>
where:
	hhmmss    HrMinSec(UTC)
        dd,mm,yyy Day,Month,Year
        xx        local zone hours -13..13
        yy        local zone minutes 0..59
        *CC       checksum
</pre>
 * @author pfarrell
 * Created on Jan 2, 2011, 1:11:16 AM
 * Copyright (C) 2011 Patrick Farrell  
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
public class ZDAteTimeStamp  extends GpsStatement implements NmeaStatement {
     /** logger instance */
private static final Logger zLog = Logger.getLogger(ZDAteTimeStamp.class);
private Date timeOfFix;

public ZDAteTimeStamp(String arg) {
    super(arg);
    parseParts();
}
private void parseParts() {
    if ( ! isNmeaStatement()) return;
    boolean problemFound = false;
    String[] parts = getInputString().split(",");
    NmeaDataType type =  getDataType();
    if (type != NmeaDataType.ZDA) return;
    try {
        SimpleDateFormat dfmt = new SimpleDateFormat("HHmmss.SS ddMMyyyy Z");
        dfmt.setTimeZone(TimeUtils.utcTZ);
        String dateTime = String.format("%s %2s%2s%2s UTC", parts[1], parts[2], parts[3], parts[4]);
        System.out.println(dateTime);
        timeOfFix = dfmt.parse(dateTime);
    } catch (ParseException ex) {
        zLog.error(ex);
    }
}

    public boolean isChecksumMandatory() {
        return true;
    }

    /**
     * @return the timeOfFix
     */
    public Date getTimeOfFix() {
        return timeOfFix;
    }
}
