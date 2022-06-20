/*
 * PerformancesBackupErrorException.java
 *
 * Created on October 30, 2005, 11:18 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.util.ArrayList;

/**
 *
 * @author sdperret
 */
public class PerformancesBackupErrorException extends Exception {
    
    /** Creates a new instance of PerformanceNotFoundException */
    public PerformancesBackupErrorException( BookingSystem aBS, ArrayList<Performance> p ) {
        super( "PerformancesBackupErrorException" );
        bookingSystem = aBS;
        performances = p;
    }
    
    public BookingSystem getBookingSystem(){
        return bookingSystem;
    }
    public ArrayList<Performance> getPerformances(){
        return performances;
    }
    
    
    private BookingSystem bookingSystem = null;
    private ArrayList<Performance> performances = null;
}
