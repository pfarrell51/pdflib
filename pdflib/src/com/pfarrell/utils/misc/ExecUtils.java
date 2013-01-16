/*
 * ExecUtils.java 
 *
 * Created on by OneBigCD, circa 2000.
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

