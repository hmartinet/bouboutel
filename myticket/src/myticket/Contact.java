/*
 * Contact.java
 *
 * Created on September 8, 2005, 2:52 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.io.*;
import key.Preferrences;

/**
 *
 * @author sdperret
 */
public class Contact extends AbstractColumnShowable{
    
    
    public static final long serialVersionUID = -266372994;
    
    /** Creates a new instance of Contact
        if anId is negative, a new id is automatically attributed */
    public Contact( String aName, String aCountry, String aTown, String aStreet, String aPhone, String aEmail, long anId ){
        name = aName;
        country = aCountry;
        town = aTown;
        street = aStreet;
        phone = aPhone;
        email = aEmail;
        if( anId < 0 ){
            nextid++;
            id = nextid;
        }else{
            id = anId;
        }
    }
    
    public Contact( String aName, long anId ){
        name = aName;
        id = anId;
        if( anId < 0 ){
            nextid++;
            id = nextid;
        }else{
            id = anId;
        }
    }
    
    public Contact( String aName ){
        name = aName;
        nextid++;
        id = nextid;
    }
    
    public Contact(){    
        nextid++;
        id = nextid;
    }
    
    public Contact( Long anId ){
        id = anId;
    }
    
    //public Contact(){
    //}
    
    public String getName(){
        return name;
    }
    
    public String getTown(){
        return town;
    }
    
    public String getCountry(){
        return country;
    }
    
    public String getStreet(){
        return street;
    }
    
    public String getPhone(){
        return phone;
    }
    
    public String getEmail(){
        return email;
    }
    
    public String toString(){
        return name + " " + street + " " + town + " " + country + " " + phone + " " + id;
    }
    
    public long getId(){
        return id;
    }
    
    public static Long getIdCounter(){
        return nextid;
    }
    public static void setIdCounter( long a ){
        nextid = a;
    }
    
    
    public Object getColumn( int aColumn ){
        Object rep = null;
        
        switch( aColumn ){
            case 0:
                    rep = getName();
                    //System.out.println("------------------- returning column 0: " + rep );
                    break;
            case 1:
                    rep = getStreet();
                    //System.out.println("------------------- returning column 1: " + rep );
                    break;
            case 2:
                    rep = getTown();
                    //System.out.println("------------------- returning column 2: " + rep );
                    break;
            case 3: 
                    rep = getCountry();
                    break;
            case 4:
                    rep = getPhone();
                    //System.out.println("------------------- returning column 2: " + rep );
                    break;        
            case 5:
                    rep = getEmail();
                    //System.out.println("------------------- returning column 2: " + rep );
                    break;
                    
                    
            default:
                    //System.out.println("------------------- ERROR waniting column: " + aColumn );
                    break;
            
        }
        
        return( rep );
        
    }
    
    public Class<?> getColumnClass(int aColumn ){
        return String.class;
    }
    
    public int getColumnCount(){
       return( 6 );
    }
    public String getColumnName(int aColumn) {
        String rep = "";
        
        switch( aColumn ){
            case 0:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelName" );
                    //rep = "name";
                    break;
            case 1:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelStreet" );
                    //rep = "street";
                    break;
            case 2:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTown" );
                    //rep = "town";
                    break;
            case 3:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelCountry" );
                    //rep = "country";
                    break;
            case 4:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelPhone" );
                    //rep = "phone";
                    break;        
            case 5:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelEmail" );
                    //rep = "e-mail";
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    public void setColumn( Object aValue, int aColumn ){
         switch( aColumn ){
            case 0:
                    setName( (String) aValue );
                    break;
            case 1:
                    setStreet( (String) aValue );
                    break;
            case 2:
                    setTown( (String) aValue );
                    break;
             case 3:
                    setCountry( (String) aValue);
                    break;
            case 4:
                    setPhone( (String) aValue );
                    break;        
            case 5:
                    setEmail( (String) aValue );
                    break;
            default:
                    break;
            
        }
    }
    
    public void setName( String aName ){
        name = aName;
    }
    public void setStreet( String aStreet){
        street = aStreet;
    }
    public void setTown( String aTown ){
        town = aTown;
    }
    public void setCountry( String aCountry ){
        country = aCountry;
    }
    public void setPhone( String aPhone ){
        phone = aPhone;
    }
    public void setEmail( String aEmail ){
        email = aEmail;
    }
    
    
    public boolean isCellEditable( int aColumn ){
        if( aColumn < editable.length ){
            return( editable[ aColumn ] );
        }
        return false;
    }
    public void setCellEditable( boolean[] aBool ){
        editable = aBool;
    }
    
    
    public String getFormattedName( int maxLength, String aSeparator ){
        String rep = name + " ";
        
        for( int i = 0; i < ( Math.min( 35, maxLength - ( name.length() + 1 ) ) ); i++ ){
            rep += aSeparator;
        }
        
        return rep;
    }
    
    
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Contact))
            throw new ClassCastException("in Booking:compareTo: A Booking object expected.");
      
      int rep = 0;
      
      //switch( getComparableColumn() ){
        //  case 0:
              
              String thisName = this.getName().toUpperCase();
              String otherName = ((Contact) other).getName().toUpperCase();
                      
              rep = thisName.compareTo( otherName );
          /*    break;
              
          case 1:
              
              int thisNb = getNbSeats();
              int otherNb = ((Booking) other).getNbSeats();
              
              rep = thisNb - otherNb;
              break;
              
          case 2:
              Date thisDate = this.getPerformance().getDate();
              Date otherDate = ((Booking) other).getPerformance().getDate();
              
              rep = thisDate.compareTo( otherDate );
              break;
      }*/
      
      return rep;
      
    }
    
    
    
    
    
    
    
    
    
    
    
    private static boolean[] editable = { false, false, false, false, false };
    
    private String name = "";
    private String country = "";
    private String town = "";
    private String street = "";
    private String phone = "";
    private String email = "";
    
    private static long nextid = -1;
    private long id=-1;
}
