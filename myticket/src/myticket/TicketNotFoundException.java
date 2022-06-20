/*
 * TicketNotFoundException.java
 *
 * Created on October 21, 2005, 8:12 AM
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
public class TicketNotFoundException extends Exception {
    
    /** Creates a new instance of PerformanceFullException */
    public TicketNotFoundException( Ticket t ){
        super( "Ticket " + t + " not found in performance " + t.getPerformance() );
    }
    public TicketNotFoundException(){
        super( "Ticket not found in performance " );
    }
    
}
