/*
 * Booking.java
 *
 * Created on September 8, 2005, 3:44 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.io.*;
import javax.swing.*;
import java.awt.print.*;
import java.util.*;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.text.*;
import key.Preferrences;

/**
 *
 * @author sdperret
 */
public class Booking extends AbstractColumnShowable {
    
    public static final long serialVersionUID = -1102973538;
    
    /** Creates a new instance of Booking */
    public Booking( Client aClient, int aNbSeats, Performance aPerformance ) {
        client = aClient;
        nbSeats = aNbSeats;
        performance = aPerformance;
        
        
        id = maxid;
        maxid++;
        
        
        for( Price p : performance.getPrices() ){
            try{ 
              tarifs.add( p.clone() );
            }catch( CloneNotSupportedException e ){
                System.out.println("ATTRAPPE UNE EXCEPTION CLONAGE: PAS NORMAL");
            }
        }
        
        client.addBooking( this );
    }
    
    /*public Booking(){
        performance = new Performance();
        client = new Client();
    }*/
    
    public Booking( Performance aPerformance ) {
        client = new Client();
        performance = aPerformance;
        
        
        id = maxid;
        maxid++;
        
        for( Price p : performance.getPrices() ){
            try{ 
              tarifs.add( p.clone() );
            }catch( CloneNotSupportedException e ){
                System.out.println("ATTRAPPE UNE EXCEPTION CLONAGE: PAS NORMAL");
            }
        }
        
    }
    
    public void setClient( Client aClient ){
        client = aClient;
    }
       
    public Client getClient(){
        return client;
    }
    
    public int getNbSeats(){
        return nbSeats;
    }
    
    public int getNbTickets(){
        return tickets.size();
    }
    
    public void setNbSeats( int aNb ) throws PerformanceFullException, PerformanceBeingEditedException{
        System.out.println("SETTING NB SEATS TO " + aNb );
        
        try{
            getPerformance().getSeats( aNb - getNbSeats() );
            nbSeats = aNb;
        }catch( PerformanceFullException e ){
            throw e;
        }
        
    }   
    
    public void restoreNbSeats( int aNb ) throws PerformanceFullException, PerformanceBeingEditedException{
        System.out.println("RESTORING NB SEATS TO " + aNb );
        nbSeats = aNb;
    }   
    
    public Performance getPerformance(){
        return( performance );
    }
    private void setPerformance( Performance aPerformance ){
        performance = aPerformance;
    }
    
    
    public String toString(){
        //return client + "|" + nbSeats + "|" + performance.getShow() + "|" + performance.getRoom() + "|" + performance.getDate();
        
        
        return client.getFormattedName( 35, "_") + " " + nbSeats + "   " + comment;
        
    }
    
    public void cancelPrebooking(){
        getPerformance().cancelPreBooking( this );
    }
    
    
    
    public void confirmPreBooking() throws IOException, PrebookingNotFoundException, java.awt.print.PrinterException {
        getPerformance().confirmPrebooking( this );
    }
    
    public void updateBooking( String newName, int newNbSeats, String newComment, String newStreet, String newTown, String newPhone, String newEmail ) throws IOException, PerformanceFullException {

        getPerformance().updateBooking( this, newName, newNbSeats, newComment, newStreet, newTown, newPhone, newEmail );
        client.setName( newName );
        client.setStreet( newStreet );
        client.setTown( newTown );
        client.setPhone( newPhone);
        client.setEmail( newEmail );
        setComment( newComment );
        
        nbSeats = newNbSeats;
            
    }
    
    /*public void writeTickets( String command ) throws java.io.IOException {
        // create bookings file if it does not exist
        //getPerformance().writePreBooking( this, command );
        getPerformance().sellPrebooking( this );
    }*/
    
    /*public void sellPrebooking() throws IOException {
        
        createTickets();
        
        try{
            try{
                getPerformance().sellPrebooking( this );
            }catch( PrebookingNotFoundException p ){
                JOptionPane.showMessageDialog( null, p.toString(), "System error", JOptionPane.OK_OPTION );
            }
        
            // lancer l'impression des tickets
            try{
                printTickets();
            }catch( PrinterException e ){
                getPerformance().restorePrebooking( this );
                JOptionPane.showMessageDialog( null, "Could not print tickets, please try again", "Booking error", JOptionPane.OK_OPTION );    
            }
            
        }catch( IOException e2 ){
            destroyTickets();
            throw e2;
        }
    }*/
    
    
    public void unsellTickets() throws IOException {
            getPerformance().unsellPrebooking( this );
    }
    
    public int getSumPlaces(){
                int totalPlaces = 0;

                for( Price p : getTarifs() ){
                    totalPlaces += p.getNb();
                }
                return totalPlaces;
    }
    
    public double getSumPrice(){
        
        double total = 0.0;

        for( Price p : getTarifs() ){
            total += p.getNb()*p.getPrice();
        }
        return( total );
    }
    
    /*public void createTickets(){
        for( Price a : tarifs ){
            for( int i=0; i < a.getNb(); i++ ){
                Ticket t = new Ticket( a, getPerformance() );
                tickets.add( t );
                System.out.println( "created ticket " + t );
            }
        }
    }*/
    
    /*public void destroyTickets(){
        tickets.clear();
    }*/
    
    /*public void printTickets() throws PrinterException {
        //try{
            for( Ticket t : tickets){
                t.printTicket();
            }
            
        //}catch( IOException e ){
            
        //}
    }*/
    
    
    public void convertBookingToPrebooking() throws IOException {
        nbSeatsSaved = getNbSeats();
        commentSaved = getComment();
                   
        // find tarif which is not a booking and has the appropriate title
            
        boolean found = false;
        for( Price p:tarifs){
            if( p.getTitle().toUpperCase().equals( getComment().toUpperCase() ) && !p.isBooking() ){
                p.setNb( getNbSeats() );
                found = true;
            }
        }    
        if( found == false ){
            
            // find most expensive ticket price
            double maxPrice = 0;
            Price maxTarif = tarifs.get( 0 );
            for( Price p:tarifs){
                if( p.getPrice() >= maxPrice && !p.isBooking() ){
                    maxTarif = p;
                    maxPrice = p.getPrice();
                }
            }    
            maxTarif.setNb( getNbSeats() );
            
        }
        
        getPerformance().convertBookingToPrebooking( this );
    }
       
    public void restoreBookingInFile() throws java.io.IOException, PrebookingNotFoundException, PerformanceFullException, PerformanceBeingEditedException {
        setNbSeats( nbSeatsSaved );
        nbSeatsSaved = 0;
        setComment( commentSaved );
        
        getPerformance().restoreBookingInFile( this );
        //getPerformance().confirmPrebooking( this );
    }
    
    
    
    public Object getColumn( int aColumn ){
        Object rep = null;
        
        switch( aColumn ){
            
            case 0:
                    rep = getClient().getName();
                    break;
            case 1:
                    rep = getNbSeats();
                    break;
            case 2:
                    rep = getComment();
                    break;
            case 3:
                    rep = key.Preferrences.getDateFormater().format( getDate() );
                    break;
            case 4:
                    rep = getPerformance().getShow().getTitle();
                    break;
            case 5:
                    rep = getPerformance().getSimpleFormatedDate();
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    public Class<?> getColumnClass( int aColumn ){
        
        switch( aColumn ){
            case 0:
                return String.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            case 3:
                return SimpleDateFormat.class;
            case 4:
                return String.class;
            case 5:
                return SimpleDateFormat.class;
        }
        return Object.class;
    }
    
    
    
    public int getColumnCount(){
        return nbColumns; // set to 3 if the performance should be shown in table
    }
    
    public void setColumnCount( int a ){
        nbColumns = a;
    }
    
    public String getColumnName( int aColumn ){
        String rep = "";
        
        switch( aColumn ){
            
            case 0:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNameM" );
                    break;
            case 1:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbSeats" );
                    break;
            case 2:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelComment" );
                    break;
            case 3:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelBookingDate" );
                    break;
            case 4:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelShowM" );
                    break;
            case 5:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelDateM" );
                    break;
            default:
                    break;
            
        }
        
        return( rep );
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
                    getClient().setName( (String) aValue );
                    break;
            case 1:
                    try{
                        try{
                            setNbSeats( new Integer( (String) aValue ) );
                        }catch( PerformanceBeingEditedException e2 ){
                            
                        }
                    }catch( PerformanceFullException e ){
                        
                    }
                    break;
            case 2:
                    setComment( (String) aValue );
                    break;
            case 3:
                    setDate( (Date) aValue );
                    break;
            default:
                    break;
            
        }
    }
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Booking))
            throw new ClassCastException("in Booking:compareTo: A Booking object expected.");
      
      int rep = 0;
      
      switch( getComparableColumn() ){
          case 0:
              
              String thisName = this.getClient().getName().toUpperCase();
              String otherName = ((Booking) other).getClient().getName().toUpperCase();
                      
              rep = thisName.compareTo( otherName );
              break;
              
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
      }
      
      return rep;
      
    }
    
    
    
    public long getId(){
        return id;
    }
    /* restores the booking's id. Should only be used when reading from a bookings file! */
    public void setId( long anId ){
        id = anId;
    }
    
    public static long getIdCounter(){
        return maxid;
    }
    public static void setIdCounter( long a ){
        maxid = a;
    }
    
    
    public Date getDate(){
        return date;
    }
    
    public String getFormatedDate(){
        return( key.Preferrences.getSaveableDateFormater().format( getDate() ) );
    }
    
    public void setDate( Date aDate ){
        date = aDate;
    }
    
    
    public void setComment( String aComment ){
        comment = aComment;
    }
    public String getComment(){
        return comment;
    }
    
    public void setStreet( String aStreet ){
        getClient().setStreet( aStreet );
    }
    public void setTown( String aTown ){
        getClient().setTown( aTown );
    }
    public void setPhone( String aPhone ){
        getClient().setPhone( aPhone );
    }
    public void setEmail( String aEmail ){
        getClient().setEmail( aEmail );
    }
    
    
    public void journalBook( int aNb, String aComment ) throws IOException{ 
        
        File dataFile = performance.getDataFile();
        File backupFile = performance.getBackupFile();
        
        if( !dataFile.exists() ){
            performance.createDataFile( aNb );
        }
        
        
        System.out.println( "writing booking to " + dataFile.getPath()  );
        
        BackedUpBufferedWriter writer;
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) )
                    );
        }
        
        /*BufferedWriter backup;
        try{
            backup = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ), "UTF-8"));
        }catch( java.io.UnsupportedEncodingException ee ){
            backup = new BufferedWriter( new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) ));
        }*/
        
         
        writer.write( "BOOK" + "|" + getId() + "|" + key.Preferrences.getSaveableDateFormater().format( new Date() ) + "|" + aNb + "|" + getClient().getName() + "|" + aComment + "|" + ( performance.getNbFreeSeats() ) + "|" + getClient().getStreet() + "|" + getClient().getTown() + "|" + getClient().getPhone() + "|" + getClient().getEmail() + "\n" );
        writer.close();
    }
    
    public void journalUpdateBooking( int aNb, String aName, String aComment, String aStreet, String aTown, String aPhone, String aEmail ) throws IOException{ 
        
        File dataFile = performance.getDataFile();
        File backupFile = performance.getBackupFile();
        
        if( !dataFile.exists() ){
            performance.createDataFile( aNb );
        }
        
        BackedUpBufferedWriter writer;
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) )
                    );
        }
         
        writer.write( "UPDATEBOOKING" + "|" + getId() + "|" + key.Preferrences.getSaveableDateFormater().format( new Date() ) + "|" + aNb + "|" + aName + "|" + aComment + "|" + ( performance.getNbFreeSeats() - ( aNb - getNbSeats() ) ) + "|" + aStreet + "|" + aTown + "|" + aEmail + "\n" );
        writer.close();
    }
    
    /*public void journalBook() throws IOException{
       //journalBook( getNbSeats(), getComment() );
        journalBook( 0, getComment() );
    }*/
    
    
    public void journalUnbook() throws IOException{
        
        File dataFile = performance.getDataFile();
        File backupFile = performance.getBackupFile();
        
        if( !dataFile.exists() ){
            performance.createDataFile( 0 );
        }
        
        
        BackedUpBufferedWriter writer;
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) )
                    );
        }
        
        writer.write( "UNBOOK" + "|" + getId() + "|" + key.Preferrences.getSaveableDateFormater().format( new Date() ) + "|" + getNbSeats() + "|" + getClient().getName() + "|" + ( performance.getNbFreeSeats() + getNbSeats()  ) + "\n");    
        writer.close();
            
    }
    
    
    public boolean hasBookings(){
        boolean hasBookings = false;
        for( Price p:getTarifs() ){
            if( p.getAction().toUpperCase().equals( "reservation".toUpperCase() ) && p.getNb() > 0 ){
                hasBookings = true;
                return hasBookings;
            }
        }
        return hasBookings;
    }
    
    
 
    
    public ShowableArrayList<Price> getTarifs(){
        return( tarifs );
    }
    
    public ShowableArrayList<Ticket> getTickets(){
        return( tickets );
    }
    
    ShowableArrayList<Price> tarifs = new ShowableArrayList<Price>();
    ShowableArrayList<Ticket> tickets = new ShowableArrayList<Ticket>();
    
    
    private static boolean[] editable = { false, false, false, false };
    
    
    private int nbSeats = 0;
    private int nbSeatsSaved = 0;
    private Client client = null;
    private Performance performance = null;
    private Date date = new Date();
    
    private String comment = "";
    private String commentSaved = "";
    
    private long id = 0;
    private static long maxid = 0;
    
    //private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
    
    private Locale locale = null;
    
    private int nbColumns = 4;
    
    
    
}
