/*
 * FileUtil.java
 *
 * Created on October 30, 2005, 7:20 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package ch.poudriere.bouboutel.utils;

import java.io.*;

/**
 *
 * @author sdperret
 */
public class FileUtil {
    
    /** Creates a new instance of FileUtil */
    public FileUtil() {
    }
    
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
    
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
}
