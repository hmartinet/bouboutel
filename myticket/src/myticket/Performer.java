/*
 * Performer.java
 *
 * Created on September 8, 2005, 3:25 AM
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
public class Performer extends Company {
    
    public static final long serialVersionUID = 8365224705770786083L;
    
    /** Creates a new instance of Performer */
    public Performer( String aName, String aCountry, String aTown, String aStreet, String aPhone, String aEmail ) {
        super( aName, aCountry, aTown, aStreet, aPhone, aEmail );
    }
    public Performer() {
        super();
    }
    public Performer( Long anId ){
        super( anId );
    }
    
    
    public void addShow( Show aShow ){
        shows.add( aShow );
    }
    
    public ArrayList<Show> getShows(){
        return( shows );
    }
    
    public String getSignature(){
        return( signature );
    }
    
    
    private String signature = " ";
    
    private ArrayList<Show> shows = new ArrayList<Show>();
    
}
