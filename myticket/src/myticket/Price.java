/*
 * Price.java
 *
 * Created on September 29, 2005, 9:55 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import key.Preferrences;



/**
 *
 * @author sdperret
 */
public class Price extends AbstractColumnShowable implements Cloneable {
    
    public static final long serialVersionUID = -647388936;
    
    
    /** Creates a new instance of Price */
    public Price( String anAction, String aTitle, double aPrice, String aCurrency, String aValidityComment ) {
        action = anAction;
        price = aPrice;
        title = aTitle;
        validityComment = aValidityComment;
        currency = aCurrency;
        id = maxid;
        maxid++;
    }
    
     public Price( String anAction, String aTitle, double aPrice, String aCurrency ) {
        action = anAction;
        price = aPrice;
        title = aTitle;
        currency = aCurrency;
        id = maxid;
        maxid++;
    }
     
    public Price( String anAction, String aTitle, double aPrice, String aCurrency, String aValidityComment, long anId ) {
        action = anAction;
        price = aPrice;
        title = aTitle;
        validityComment = aValidityComment;
        currency = aCurrency;
        id = anId;
    } 
     
     
     
     public Price(){
        id = maxid;
        maxid++; 
     }
     
    public Price clone() throws CloneNotSupportedException {
        Price cloned = null;
        cloned = (Price) super.clone();
        return cloned;
    }
    
    
    public static long getIdCounter(){
        return maxid;
    }
    public static void setIdCounter( long a ){
        maxid = a;
    }
    
    public long getId(){
        return id;
    }
    
    
    public String getTitle(){
        return( title );
    }
    public void setTitle( String a ){
        title = a;
    }
    
    public double getPrice(){
        return( price );
    }
    
    public void setPrice( double a ){
        price = a;
    }
    
    public String getValidityComment(){
        return( validityComment );
    }
    public void setValidityComment( String a ){
        validityComment = a;
    }
    
    
    public void setNb( int a ){
        nb = a;
    }
    
    public int getNb(){
        return( nb );
    }
    
    public String getCurrency(){
        return currency;
    }
    
    
    public void setAction( String a ){
        action = a;
    }
    public String getAction(){
        return action;
    }
    
    
    public int getColumnCount(){
        //System.out.println( nbColumn );
        //return( nbColumn );
        return 4;
    }
    
    /*public void setNbColumn( int a ){
        nbColumn = a;
    }*/
    
    public Object getColumn( int i ){
        Object rep = "";
        switch( i ){
            case 0:
                System.out.println( getAction() );
                if( getAction().toUpperCase().equals("reservation".toUpperCase()) ){
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelBookingMAJ" );
                }else{
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTicketMAJ" );
                }
                //rep = getAction();
                break;
            case 1:
                rep = getTitle();
                break;
            case 2:
                rep = getPrice();
                break;
            case 3:
                if( editmode == false ){
                    rep = getNb();
                }else{
                    rep = getValidityComment();
                }
                break;
        }
        return rep;
    }
    
    
    public String getColumnName( int aColumn ){
        String rep = "";
        switch( aColumn ){
            case 0:
                rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelAction" );
                //rep = "action";
                break;
            case 1:
                rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelKind" );
                //rep = "tarif";
                break;
            case 2:
                rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelPricePerSeat" );
                //rep = "price per seat";
                break;
            case 3:
                if( editmode == false ){
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbSeats" );
                    //rep = "number of seats";
                }else{
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelValidityComment" );
                    //rep = "Validity comment";
                }
                break;
        }
        return rep;
    }
    
    
    public Class<?> getColumnClass( int aColumn ){
        
        //System.out.println("Now in Booking:getColumnClass");
        
        switch( aColumn ){
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return Double.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
        }
        return Object.class;
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
        
    public void setColumn( Object aValue, int aColumn ){
        switch( aColumn ){
            case 0:
                    setAction( (String)aValue );
                    break;
            case 1:
                    setTitle( (String)aValue );
                    break;
            case 2:
                    setPrice( new Double( (String)aValue ) );
                    break;
            case 3:
                
                    if( editmode == false ){
                
                        try{
                            Integer a = (Integer) aValue;
                            setNb( a );
                        }catch( Exception e ){
                            setNb( 0 );
                        }
                        
                    }else{
                        String a = (String) aValue;
                        setValidityComment( a );
                        
                    }
                    break;
            default:
                    break;
            
        }
    }
    
    public boolean isBooking(){
        return getAction().toUpperCase().equals( "RESERVATION" );
    }
    
    public boolean isTicket(){
        return getAction().toUpperCase().equals( "BILLET" );
    }
    
    public void setEditmode( boolean a ){
        editmode = a;
        
        
        if( editmode == false ){
            boolean[] e = {false, false, false, true};
            setCellEditable( e );
        }else{
            boolean[] e = {true, true, true, true};
            setCellEditable( e );
        }
    }
    
    public String toString(){
        return getTitle() + ": " + getPrice() + " " + getCurrency() + " (" + getValidityComment() + ")";
    }
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Price))
            throw new ClassCastException("in Price:compareTo: A Price object expected.");
        
        String thisName = this.getTitle();
        String otherName = ((Price) other).getTitle();
        
      return thisName.compareTo( otherName );
    }
    
    private double price = 0.0;
    private String title = "";
    private String validityComment = "";
    private int nb = 0;
    private String currency = "frs";
    private String action = "";
    
    private long id = 0;
    private static long maxid = 9;
    
    private static boolean[] editable = { false, false, false, true };
    
    //int nbColumn = 3;
    
    boolean editmode = false;

    
}
