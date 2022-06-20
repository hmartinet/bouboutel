/*
 * PrebookingNotFoundException.java
 *
 * Created on October 6, 2005, 8:18 AM
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
public class PrebookingNotFoundException extends Exception {
    
    /** Creates a new instance of PerformanceFullException */
    public PrebookingNotFoundException( Booking p ){
        super( "Prebooking " + p + " not found in performance " + p.getPerformance() );
    }
    
}
