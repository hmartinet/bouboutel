/*
 * Company.java
 *
 * Created on September 8, 2005, 3:37 AM
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
public class Company extends Contact {
    
    public static final long serialVersionUID = 1319682752584367075L ;
    
    /** Creates a new instance of Company */
    public Company( String aName, String aCountry, String aTown, String aStreet, String aPhone, String aEmail ) {
        super( aName, aCountry, aTown, aStreet, aPhone, aEmail, -1 );
    }
    
    public Company() {
        super();
    }
    
    public Company( Long anId ){
        super( anId );
    }
    
    public void addContact( Contact aContact ){
        contactPersons.add( aContact );
    }
    
    public String toString(){
        return getName();
    }
    
    private ArrayList<Contact> contactPersons = new ArrayList<Contact>();
    
}
