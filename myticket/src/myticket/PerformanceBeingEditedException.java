/*
 * PerformanceBeingEditedException.java
 *
 * Created on October 13, 2005, 11:51 AM
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
public class PerformanceBeingEditedException extends Exception {
    
    /** Creates a new instance of PerformanceFullException */
    public PerformanceBeingEditedException( Performance p ) {
        super( p + " is currently being edited try again later");
    }
    
}
