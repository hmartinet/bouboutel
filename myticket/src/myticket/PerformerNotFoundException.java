/*
 * PerformerNotFoundException.java
 *
 * Created on October 16, 2005, 12:39 PM
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
public class PerformerNotFoundException extends Exception {
    
    /** Creates a new instance of PerformanceNotFoundException */
    public PerformerNotFoundException( Performer p ) {
        super( "Performer " + p + " not found in booking system " );
    }
    
}
