/*
 * PerformanceFullException.java
 *
 * Created on September 8, 2005, 4:04 AM
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
public class PerformanceFullException extends Exception {
    
    /** Creates a new instance of PerformanceFullException */
    public PerformanceFullException( Performance p ) {
        super( p + " has only " + p.getNbFreeSeats() + " seats available ");
    }
    
}
