/*
 * BookingsFilenameFilter.java
 *
 * Created on October 13, 2005, 7:02 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.io.*;

/**
 *
 * @author sdperret
 */
public class BookingsFilenameFilter implements FilenameFilter{
    
    /** Creates a new instance of BookingsFilenameFilter */
    public BookingsFilenameFilter() {
    }
    
    public boolean accept(File dir, String name){
    
        //File aFile = new File( dir, name );
        //if( aFile.)
        
        //System.out.println( "checking "+ name );
        
        String[] nameList = null;
        nameList = name.split("\\.");
        
        //System.out.println( "found "+ nameList.length + " parts in filename" );
        
        if( nameList.length > 0){
            String extension = nameList[(nameList.length - 1)];
            //System.out.println( "extension: "+ extension );
        
            //System.out.println(" found extension " + extension);
        
            if( extension.equals( "bookings" ) ){
                System.out.println(" added file   " + name);
                return true;
            }else{
                System.out.println(" skipped file " + name);
                return false;
            }
             
        }else{
            return false;
        }
    }
    
}
