/*
 * SystemLicenceExpiredException.java
 *
 * Created on October 17, 2005, 7:58 AM
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
public class SystemLicenseException extends Exception {
    
    /** Creates a new instance of SystemAlreadyLoadedException */
    public SystemLicenseException( String a ) {
        super( a );
    }
    
}
