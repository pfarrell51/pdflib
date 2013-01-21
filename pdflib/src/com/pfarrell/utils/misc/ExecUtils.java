/*
 * ExecUtils.java 
 *
 * Created on by OneBigCD, circa 2000.
 * Copyright (C) 2000 Patrick Farrell   All Rights reserved.
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

package com.pfarrell.utils.misc;
import java.io.*;

/** 
 * Class to encapsulate processing fork'd work
 *
 * @author Pat Farrell
 */

public abstract class ExecUtils {
    
    /** execute a command in the shell of the OS
     * @param cmd command to execute
     * @throws IOException passes up any IO exception
     * @return output from STDOUT
     */    
    public static String execCmd(String cmd) throws IOException {
        Process p = Runtime.getRuntime().exec(cmd);
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        String line=" a";
        while ((line = br.readLine()) != null) {
            pw.println(line);
        }
        int rslt = 0;
        try {
            rslt = p.waitFor();
        } catch (InterruptedException e) {
            throw new IOException("Process did not complete before closing stdOut");
        }
        br.close();
        is.close();
        pw.flush();
        pw.close();
        if (rslt <0 || rslt > 1) throw new IOException("RIP Problem - result=" + rslt + "  " + sw.toString());
        return sw.toString();
    }

}

