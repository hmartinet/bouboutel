/*
 * Preferrences.java
 *
 * Created on October 20, 2005, 10:27 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package key;

import java.util.Locale;
import java.io.*;
import java.text.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.awt.*;

/**
 *
 * @author sdperret
 */
public class Preferrences {
    
    /** Creates a new instance of Preferrences */
    public Preferrences() {
        locale = Locale.FRENCH;
    }
    
    public static Locale getLocale(){
        return locale;
    }
    public static void setLocale( Locale a ){
        locale = a;
        af = new SimpleDateFormat("EEE dd.MM.yy 'Ă ' HH:mm", getLocale() );
        saveableF = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z", getLocale() );
    }
    
    public static key.License getLicense(){
        return license;
    }
    public static void setLicense( key.License l ){
        license = l;
    }
    
    public static void setDataDirectory( File aDir ){
        dataDirectory = aDir;
    }
    public static File getDataDirectory(){
        return dataDirectory;
    }
    
    public static void setBackupDirectory( File aDir ){
        backupDirectory = aDir;
    }
    public static File getBackupDirectory(){
        return backupDirectory;
    }
    
    public static SimpleDateFormat getDateFormater(){
        return af;
    }
    public static SimpleDateFormat getSaveableDateFormater(){
        return saveableF;
    }
    
    public static boolean showOldPerformances(){
        return flagShowOldPerformances;
    }
    public static void setShowOldPerformances( boolean a ){
        flagShowOldPerformances = a;
    }
    
    public static void loadTicketImage( File aFile ) throws java.io.IOException{
        ticketImage = ImageIO.read( aFile );
    }
    public static BufferedImage getTicketImage(){
        return ticketImage;
    }
    
    public static void setPrintTicketBackground( boolean a ){
        flagPrintTicketBackground = a;
    }
    public static boolean printTicketBackground(){
        return flagPrintTicketBackground;
    }
    
    
    
    /*public static boolean isFirstPrinting(){
        if( printCounter > 1 ){
            return false;
        }else{
            return true;
        }
    }
    public static void setPrintGraphics2D( Graphics2D a ){
        System.out.println("key.Preferrences.setprintGraphics2D");
        printGraphics2D = a;
        printCounter++;
    }
    public static Graphics2D getPrintGraphics2D(){
        return printGraphics2D;
    }*/
    
    
    private static Locale locale = Locale.FRENCH;
    private static License license = null;
    private static File dataDirectory = null;
    private static File backupDirectory = null;
    
    private static SimpleDateFormat af = new SimpleDateFormat("EEE dd.MM.yy 'Ă ' HH:mm", getLocale() );
    private static SimpleDateFormat saveableF = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
    
    private static boolean flagShowOldPerformances = false;
    private static boolean flagPrintTicketBackground = true;
    
    private static BufferedImage ticketImage = null;
    
    /*private static long printCounter = 0;
    private static Graphics2D printGraphics2D = null;*/
}


