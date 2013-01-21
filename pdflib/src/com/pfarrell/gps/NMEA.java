/*
 * Copyright (C) 2010 Patrick Farrell. All Rights reserved.
 * This will be released under the Apache License. Open source, use
 * with attribution.
 */

package com.pfarrell.gps;

import com.google.common.base.Preconditions;
import com.pfarrell.gps.statements.DummyStatement;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * The <code>NMEA</code> class implements a parser for data in the NMEA standard 2.3 for GPS devices.
 * @see <a href="http://gpsd.berlios.de/NMEA.txt">Eric's NMEA revealed</a>
 * @author pfarrell
 * Created on Dec 27, 2010, 9:14:09 PM
 *
 * Copyright (c) 2011, Pat Farrell. All rights reserved
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
public class NMEA {
     /** logger instance */
private static final Logger nLog = Logger.getLogger(NMEA.class);

public NmeaStatement parseLine(String is) {
    Preconditions.checkNotNull(is);
    NmeaStatement rval = StatementFactory.makeStatement(is);
    return rval;
}
    /**
     * standard shell driver
     * @param args the command line arguments
     */
public static void main(String[] args) throws FileNotFoundException, IOException {
    if (args.length < 1) {
        System.out.println("Usage: NMEA <filespec>");
    } else {
        NMEA aNmea = new NMEA();
        FileInputStream fis = new FileInputStream(args[0]);
        BufferedInputStream bis = new BufferedInputStream(fis);
        BufferedReader br = new BufferedReader(new InputStreamReader(bis));
        List<NmeaStatement> statements = new ArrayList<NmeaStatement>();
        int numSkipped = 0;
        String line = null;
        while ((line = br.readLine()) != null) {
            NmeaStatement statmt = aNmea.parseLine(line);
            if (statmt == null || statmt instanceof DummyStatement) {
                numSkipped++;
            } else {
                statements.add(statmt);
            }
        }
        System.out.printf("processed %d and skipped %d\n", statements.size(), numSkipped);
        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for (NmeaStatement n : statements) {
            String name = n.getClass().getSimpleName();
            Integer oldCount = counts.get(name);
            if (oldCount == null) {
                counts.put(name, 1);
            } else {
                oldCount++;
                counts.put(name, oldCount);
            }
        }
        System.out.println(counts);
        LogAnalyzer la = new LogAnalyzer();
        la.analyze(statements);

    }
}

}
