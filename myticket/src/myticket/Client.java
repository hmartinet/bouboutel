/*
 * Client.java
 *
 * Created on September 8, 2005, 3:42 AM
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
public class Client extends Contact {
    
    /** Creates a new instance of Client */
    public Client( String aName, long anId ) {
        super( aName, anId );
    }
    
    public Client( String aName ) {
        super( aName );
    }
    
    public Client(){
        super();
    }
    
    public void addBooking( Booking aBooking ){
        bookings.add( aBooking );
    }
    
    public ArrayList<Booking> getBookings(){
        return bookings;
    }
    
    public String toString(){
        return super.toString();
    }
    
    ArrayList<Booking> bookings = new ArrayList<Booking>();
    
    
}
