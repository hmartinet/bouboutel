/*
 * BackupAccessException.java
 *
 * Created on October 30, 2005, 9:48 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

/**
 *
 * @author sdperret
 */
public class BackupAccessException extends Exception{
    
    /**
     * Creates a new instance of BackupAccessException 
     */
    public BackupAccessException( BookingSystem aBookingSystem ) {
        bookingSystem = aBookingSystem;
    }
    
    public BookingSystem getBookingSystem(){
        return bookingSystem;
    }
    
    BookingSystem bookingSystem;
    
}
