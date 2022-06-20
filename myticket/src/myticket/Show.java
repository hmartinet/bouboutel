/*
 * Show.java
 *
 * Created on September 8, 2005, 2:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.util.ArrayList;
import key.Preferrences;

/**
 *
 * @author sdperret
 * this class implements the notion of a show. it is NOT associated with a time and location but can be played in different performances.
 */
public class Show extends AbstractColumnShowable {
 
    public static final long serialVersionUID = -873665221;
    
    public Show( Performer aPerformer, String aTitle, String aComment ){
        performer = aPerformer;
        title = aTitle;
        comment = aComment; 
        
        id = maxid;
        maxid++;
        
        performer.addShow( this );
    }
    
    public Show(){
        //performer = new Performer();
        performer.addShow( this );
        
        id = maxid;
        maxid++;
    }
    
    public Show( Long anId ){
        id = anId;
    }
    
    
    public Performer getPerformer(){
        return performer;
    }
    
    public String getTitle(){
        return title;
    }
    
    public String getComment(){
        return comment;
    }
    
    public void addPerformance( Performance aPerformance ){
        performances.add( aPerformance );
    }
    
    public ArrayList<Performance> getPerformances(){
        return( performances );
    }
    
    public String toString(){
        //return( title + " (" + comment +")" );
        return( title );
    }
    
    public Object getColumn( int aColumn ){
        Object rep = null;
        
        switch( aColumn ){
            case 0:
                    rep = getTitle();
                    break;
            case 1:
                    rep = getPerformer();
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    public int getColumnCount(){
        return( 2 );
    }
    
     public String getColumnName( int aColumn ){
        String rep = "";
        
        switch( aColumn ){
            case 0:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTitle" );
                    //rep = "title";
                    break;
            case 1:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelPerformer" );
                    //rep = "performer";
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
     
     
     public Class<?> getColumnClass( int aColumn ){
         return String.class;
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
                    setTitle( (String) aValue );
                    break;
            case 1:
                    setPerformer( (Performer) aValue );
                    break;
            default:
                    break;
            
        }
    }
    
    void setTitle( String aTitle ){
        title = aTitle;
    }
    void setPerformer( Performer aPerformer ){
        performer = aPerformer;
        performer.addShow( this );
    }
    
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Show))
            throw new ClassCastException("in Show:compareTo: A Show object expected.");
        
        String thisName = this.getTitle();
        String otherName = ((Show) other).getTitle();
        
      return thisName.compareTo( otherName );
    }
    
    
    public long getId(){
        return id;
    }
    
    public static long getIdCounter(){
        return( maxid );
    }
    public static void setIdCounter( long a ){
        maxid = a;
    }
    
    
    public String getSignature(){
        return signature;
    }
    
    private static long maxid = 0;
    private long id = 0;
    
    private static boolean[] editable = { false, false };
    
    private Performer performer; //new Performer();
    private String title = "";
    private String comment = "";
    
    private ArrayList<Performance> performances = new ArrayList<Performance>();
    
    /** todo add an icon and an image field */
    
    private String signature = " ";
   
    
}
