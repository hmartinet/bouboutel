/*
 * Room.java
 *
 * Created on September 8, 2005, 2:21 AM
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
public class Room extends AbstractColumnShowable{
    
    
    public static final long serialVersionUID = -231409674;
    
    /** Creates a new instance of Room */
    public Room( Company aCompany, String aDescription ){ //, int aNbSeatMax ){
        setCompany( aCompany );
        description = aDescription;
        //nbSeatMax = aNbSeatMax;
        
        id = maxid;
        maxid += 1;
        
    }
    
    public Room(){ //, int aNbSeatMax ){
        setCompany( new Company( "", "", "", "", "", "" ) );
        
        id = maxid;
        maxid += 1;
    }
    
    public Room( Long anId ){
        setCompany( new Company( -1L ) );
        id = anId;
    }
    
    public void setCompany( Company aCompany ){
        company = aCompany;
    }
    public Company getCompany(){
        return company;
    }
    
    /*public int getNbSeatMax(){
        return nbSeatMax;
    }*/   
    /*private void setNbSeatMax( int aNb ){
        nbSeatMax = aNb;
    }*/
    
    
    public String toString(){
        return getCompany().toString();
    }
    
   public Object getColumn( int aColumn ){
       if( aColumn < getCompany().getColumnCount() ){
           return( getCompany().getColumn( aColumn ));
       }else{
           return( getNbDefaultSeats() );
       }
        
    }
   
   public Class<?> getColumnClass( int aColumn ){
        
       if( aColumn < getCompany().getColumnCount() ){
           return( getCompany().getColumnClass( aColumn ));
       }else{
           return( Integer.class );
       }
       
    }
   
   
   
   
    public int getColumnCount(){
        return( getCompany().getColumnCount() + 1 );
    }
    public String getColumnName(int aColumn) {
        if( aColumn < getCompany().getColumnCount() ){
            return( getCompany().getColumnName( aColumn ));
        }{
            return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelDefaultNbSeats" );
            //return( "default nb of seats");
        }
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
        if( aColumn < getCompany().getColumnCount() ){
            getCompany().setColumn( aValue, aColumn );
        }else{
            setNbDefaultSeats( new Integer( (String) aValue ) );
        }
    }
    
    public int getNbDefaultSeats(){
        return nbDefaultSeats;
    }
    
    public void setNbDefaultSeats( int a ){
        nbDefaultSeats = a;
    }
    
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Room))
            throw new ClassCastException("in Room:compareTo: A Room object expected.");
        
        String thisName = this.getCompany().getName();
        String otherName = ((Room) other).getCompany().getName();
    
      return thisName.compareTo( otherName );
    }
    
    
    
    
    public long getId(){
        return id;
    }
    
    public static long getIdCounter(){
        return maxid;
    }
    public static void setIdCounter( long a ){
        maxid = a;
    }
    
    
    public String getSignature(){
        return( signature );
    }
    
    
    private static boolean[] editable = { false, false, false, false, false };
    
    
        
    //int nbSeatMax = 0;
    int nbDefaultSeats = 0;
    private String description = "";
    private Company company = null;
    
    private long id = 0;
    private static long maxid = 0;
    
    private String signature = " ";
}
