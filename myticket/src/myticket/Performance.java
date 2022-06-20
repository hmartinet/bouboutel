/*
 * Performance.java
 *
 * Created on September 8, 2005, 2:17 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.*;
import java.text.*;
import java.io.*;
import key.Preferrences;


/**
 *
 * @author sdperret
 * this class implements the notion of a performance, a show presented at a given time in a given place
 */
public class Performance extends AbstractColumnShowable {
    
    public static final long serialVersionUID = -743689839;
    
    /** Creates a new instance of Performance */
    public Performance( int aDay, int aMonth, int aYear, int anHour, int aMinute, Room aRoom, Show aShow, File aDataDirectory, File aBackupDirectory, int aNbTotalSeats, BookingSystem aBS ) {
        GregorianCalendar calendar = new GregorianCalendar( aYear, aMonth, aDay, anHour, aMinute );
        date = calendar.getTime();
        
        
        bookingSystem = aBS;
        id = maxid;
        maxid += 1;
        initPerformance( date, aRoom, aShow, aDataDirectory, aBackupDirectory, aNbTotalSeats );
    }
    
    
    public Performance( Date aDate, Room aRoom, Show aShow, File aDataDirectory, File aBackupDirectory, int aNbTotalSeats, BookingSystem aBS ) {   
        bookingSystem = aBS;
        id = maxid;
        maxid += 1;
        initPerformance( aDate, aRoom, aShow, aDataDirectory, aBackupDirectory, aNbTotalSeats );
    }
    
    public Performance( Long anId, BookingSystem aBS ){
        bookingSystem = aBS;
        id = anId;
    }
    
    
    
    
    /*public Performance( File aDataDirectory, BookingSystem aBS ) {
        initPerformance( new Date(), new Room(), new Show(), aDataDirectory, 0 );
        bookingSystem = aBS;
    }*/
    
    public void initPerformance( Date aDate, Room aRoom, Show aShow, File aDataDirectory, File aBackupDirectory, int aNbTotalSeats ) {
        
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime( aDate );
        date = calendar.getTime();
        
        room = aRoom;
        show = aShow;
        
        
        // check if bookings file already exists. If yes, adapt the id and maxid so that existing files are not overwritten!
        File aFile;
        /*do{
            id = maxid;
            maxid += 1;
            aFile = new File( aDataDirectory, Long.toString(id) + ".bookings" );
        }while( aFile.exists() );*/
        
        System.out.println( "created performance " + getId() );
        System.out.println( "  max id now = " + maxid );
        
        dataFile = new File( aDataDirectory.getPath() + File.separator + getId() + ".bookings" );
        backupFile = new File( aBackupDirectory.getPath() + File.separator + getId() + ".bookings" );
        
        ticketImageFile = new File( aDataDirectory.getPath() + File.separator + getId() + ".jpg" );
        if( !ticketImageFile.exists() ){
            ticketImageFile = new File( aDataDirectory.getPath() + "defaultTicket.jpg" );
        }
        
        //nbFreeSeats = aNbFreeSeats;
        //nbFreeSeats = room.getNbSeatMax();
        nbTotalSeats = aNbTotalSeats;
        
        /*restoreDefaultPrices();
        try{
            journalAddPrices();
        }catch( IOException e ){
            
        }*/
        
        
        show.addPerformance( this );
       
    }
    
    public void restoreDefaultPrices(){
        prices.clear();
        try{
            addPrice( new Price( "Reservation", "(payable au th??atre)", 0.0, getCurrency(), "", 0L ));
            addPrice( new Price( "Billet", "Adulte", 25.0, getCurrency(), "", 1L ));
            addPrice( new Price( "Billet", "Membre/Adh??rant/Membre UNIMA/Label Bleu/Club Express", 16.0, getCurrency(), "VALABLE SEULEMENT AVEC PIECE DE LEGITIMATION", 2L ));
            addPrice( new Price( "Billet", "AVS/Etudiant/Apprenti", 18.0, getCurrency(), "VALABLE SEULEMENT AVEC PIECE DE LEGITIMATION", 3L ));
            addPrice( new Price( "Billet", "Professionel du spectacle/Ch??meur", 12.0, getCurrency(), "VALABLE SEULEMENT AVEC PIECE DE LEGITIMATION", 4L ));
            addPrice( new Price( "Billet", "Enfant en matin??e", 12.0, getCurrency(), "VALABLE SEULEMENT EN MATINEE", 5L ));
            addPrice( new Price( "Billet", "Adulte acompagnant en matin??e", 15.0, getCurrency(), "VALABLE SEULEMENT EN MATINEE ACCOMPAGNE D'UN BILLET ENFANT", 6L ));
            addPrice( new Price( "Billet", "Abonnement 6 spectacles", 0.0, getCurrency(), "VALABLE SEULEMENT AVEC LE PASS DU FESTIVAL", 7L ));
            addPrice( new Price( "Billet", "Invitation", 0.0, getCurrency(), "", 8L ));
            addPrice( new Price( "Reservation", "Invitation", 0.0, getCurrency(), "", 9L ));
        }catch( IOException e ){
            
        }
    }
    
    
    public BookingSystem getBookingSystem(){
        return( bookingSystem );
    }
    
    
    /*protected void finalize() throws Throwable {
        
        System.out.println("NOW FINALIZING");
        
        nbPrebookedSeats = 0;
        preBookings.clear();
        try {
            //close();        // close open files
        } finally {
            super.finalize();
        }
    }*/

    
    public File getDataFile(){
        return( dataFile );
    }
    public File getBackupFile(){
        return( backupFile );
    }
    
    
    public File getTicketImageFile(){
        return( ticketImageFile );
    }
     
    public long getId(){
        return( id );
    }
    
    public static long getIdCounter(){
        return( maxid );
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
    
    public String getSimpleFormatedDate(){
        
        SimpleDateFormat bf = key.Preferrences.getDateFormater(); //new SimpleDateFormat("EEE dd.MM.yy '??' HH'h'mm", Preferrences.getLocale());

        return( bf.format( getDate() ) );
    }

    public Room getRoom(){
        return room;
    }
    
    public Show getShow(){
        return show;
    }
    
    
    public void addPrice( Price aPrice ) throws IOException{   
        
        if( dataFile.exists() ){     
        }else{
            createDataFile( getNbFreeSeats() );
        }
        
        prices.add( aPrice );
        prices.fireTableDataChanged();
    }
    
    
    public void deletePrice( int i ) throws IOException{
        
        Price aPrice = prices.get( i );
        
        
        if( dataFile.exists() ){       
        }else{
            createDataFile( getNbFreeSeats() );
        }
        
        
        getPrices().remove( i );
        getPrices().fireTableDataChanged();
    }
    
    
    public void deletePrices() throws IOException{
             
        
        if( dataFile.exists() ){
        }else{
            createDataFile( getNbFreeSeats() );
        }
        
        
        
        getPrices().clear();
        getPrices().fireTableDataChanged();
    }
    
    public void journalDeleteOldPrices() throws IOException{
        
        if( dataFile.exists() ){
            
            BackedUpBufferedWriter writer;
            try{
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"), 
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                        );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( dataFile.getPath(), true ) ),
                        new OutputStreamWriter(new FileOutputStream( backupFile.getPath(), true ) )
                        );
            }
            
            for( Price aPrice:oldPrices ){     
                writer.write( "DELETEPRICE" + "|" + aPrice.getId() + "|" + aPrice.getAction() + "|" + aPrice.getTitle() + "|" + aPrice.getPrice() + "|" + aPrice.getValidityComment() +"\n" );
            }
            writer.close();
        
        }
    }
    
    public void journalDeletePrices() throws IOException{
        
        
        if( dataFile.exists() ){
            
            BackedUpBufferedWriter writer;
            try{
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                        );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter( new FileOutputStream( getDataFile().getPath(), true ) ),
                        new OutputStreamWriter( new FileOutputStream( getBackupFile().getPath(), true ) )
                        );
            }
            
            for( Price aPrice:prices ){     
                writer.write( "DELETEPRICE" + "|" + aPrice.getId() + "|" + aPrice.getAction() + "|" + aPrice.getTitle() + "|" + aPrice.getPrice() + "|" + aPrice.getValidityComment() +"\n" );
            }
            writer.close();
        
        }
    }
    
    
    
    public ShowableArrayList<Price> getPrices(){
        return( prices );
    }
    
    public Price getPriceWithName( String aTarifName ) throws UnknownTarifException{
        System.out.println("Performance: getPriceWithName( " + aTarifName +" )");
        for( Price p : prices ){
            System.out.println("   found: " + p.getTitle() );
        
            if( p.getTitle().equals( aTarifName ) ){
                System.out.println(    "OK" );
                return p;
            }
        }
        System.out.println(    aTarifName + " not found" );
        throw new UnknownTarifException( aTarifName, this );
    }
    
    public String getCurrency(){
        return( currency );
    }
    
    
    /*public void addPreBooking( Client aClient, int aNbSeats ) throws PerformanceFullException {
        if( getNbFreeSeats() - aNbSeats >= 0 ){
            Booking aBooking = new Booking( aClient, aNbSeats, this );
            
            // write booking to file
            //bookings.add( aBooking );
            
            preBookings.add( aBooking );
            
            nbPrebookedSeats += aNbSeats;
            //nbFreeSeats -= aNbSeats;
        }else{
            throw new PerformanceFullException( this );
        }
    }*/
    
    public void addPreBooking( Booking aBooking ) throws PerformanceFullException{
        System.out.println("*** Performance:addPrebooking() ");
        
        
        int aNbSeats = aBooking.getNbSeats();
        if( getNbFreeSeats() - aNbSeats >= 0 ){
            System.out.println("*** Performance:addPrebooking() " + aBooking);
            //Booking aBooking = new Booking( aClient, aNbSeats, this );
            
            /* write booking to file */
            //bookings.add( aBooking );
            
            preBookings.add( aBooking );
            
            nbPrebookedSeats += aNbSeats;
            //nbFreeSeats -= aNbSeats;
        }else{
            throw new PerformanceFullException( this );
        }
    }
    
    public void getSeats( int aNbSeats ) throws PerformanceFullException, PerformanceBeingEditedException{
        if( isBeingEdited() ){
            throw new PerformanceBeingEditedException( this );
        }else{
            if( getNbFreeSeats() - aNbSeats >= 0 ){
                nbPrebookedSeats += aNbSeats;
                //nbFreeSeats -= aNbSeats;
            }else{
                throw new PerformanceFullException( this );
            }
        }
    }
    
    public void cancelPreBooking( Booking aBooking ){
        // TODO write to file that booking was canceled
        
        // then update the number of free seats
        nbPrebookedSeats -= aBooking.getNbSeats();
        //nbFreeSeats += aBooking.getNbSeats();
        preBookings.remove( aBooking );
    }
    
    public void cancelAllPrebookings(){
        preBookings.clear();
        nbPrebookedSeats = 0;
    }
    
    public void updatePreBooking( Booking aBooking ) throws PerformanceFullException{
        
        System.out.println(" updating prebooking " + aBooking);
        System.out.println("   -> " + aBooking.getNbSeats());
        
        int aNbSeats = aBooking.getNbSeats();
        if( ( getNbFreeSeats() + aBooking.getNbSeats() ) - aNbSeats >= 0 ){         
       
            setNbPrebookedSeats( aNbSeats - aBooking.getNbSeats() );
            //nbPrebookedSeats += ( aNbSeats - aBooking.getNbSeats() );
            //nbFreeSeats -= ( aNbSeats - aBooking.getNbSeats() );
        }else{
            System.out.println("   not possible, throwing exception");
            throw new PerformanceFullException( this );
        }
    }
    
    
    
    
    public void restoreBookingInFile( Booking aBooking ) throws java.io.IOException {
        if( preBookings.contains( aBooking )){
            /*for( Price p:aBooking.getTarifs() ){
                if( p.getNb() > 0 ){
                    if( p.isBooking() ){
                        aBooking.journalBook( p.getNb(), p.getTitle() );                      
                        
                        // update seat counts    
                        nbBookedSeats += p.getNb();
                        nbPrebookedSeats -= p.getNb();
                    }
                }
            }*/
            aBooking.journalBook( aBooking.getNbSeats(), aBooking.getComment() );         
            
            nbBookedSeats += aBooking.getNbSeats();
            nbPrebookedSeats -= aBooking.getNbSeats();
            
            preBookings.remove( aBooking );
        }
    }
    
    public void confirmPrebooking( Booking aBooking ) throws java.io.IOException, PrebookingNotFoundException, java.awt.print.PrinterException {
        
        System.out.println("Writing " + aBooking + " in performance " + getDataFile().getPath() );
        
        if( preBookings.contains( aBooking )){
        
            for( Price p:aBooking.getTarifs() ){
                if( p.getNb() > 0 ){
                
                    if( p.isBooking() ){
                        System.out.println("booking " + p.getNb() + " (" + p.getTitle() + ")" );
                            
                         
                        // write preBooking to file
                        //aBooking.setComment( p.getTitle() );
                        aBooking.journalBook( p.getNb(), p.getTitle() );                      
                        
                        // update seat counts    
                        nbBookedSeats += p.getNb();
                        nbPrebookedSeats -= p.getNb();
 
                    }else{
                        System.out.println("printing " + p.getNb() + " tickets with tarif " + p.getTitle() );
                    
        
                        // create bookings file if it does not exist
                        if( dataFile.exists() ){
                        }else{
                            createDataFile( aBooking.getNbSeats() );
                        }
                        
                        
                        // write the preBooking to file
                        
                       
                        for( int i = 0 ; i < p.getNb(); i++ ){
                            // issue ticket
                            Ticket t = new Ticket( p, aBooking.getPerformance() );
                            aBooking.getTickets().add( t );
                            
                            // register ticket
                            //aBooking.journalSell( t );
                            t.journalSell( t.getNb() );
                            
                            // print Ticket
                            t.printTicket( 1 );
                        
                            
                        
                        
                            // register income
                            totalIncome += t.getPrice().getPrice();
                
                            // actualise seat counts
                            nbSoldSeats += 1; //p.getNb();
                            nbPrebookedSeats -= 1; //p.getNb();
                        }     
                        //writer.close(); 
        
                    
                    }
                
                }
            
            }
            preBookings.remove( aBooking );
            
            bookingSystem.saveStatics();
            
        }else{
            throw new PrebookingNotFoundException( aBooking );
        }
    
        
    }
    
    
    public void unsellPrebooking( Booking aBooking ) throws java.io.IOException {
        System.out.println("Writing unsell " + aBooking + " in performance " + getDataFile().getPath() );
        
        // create bookings file if it does not exist
        if( dataFile.exists() ){
        }else{
            createDataFile( aBooking.getNbSeats() );
        }
        // write the preBooking to file
        //                                             for( Booking b : preBookings ){
        for( Ticket t : aBooking.getTickets() ){
            
            t.journalUnsell( 1 );
            
            nbSoldSeats -= 1;
            totalIncome -= t.getPrice().getPrice();
        }
        // restore prebookings       
        restorePrebooking( aBooking );
        
    }
    
    
    public void unsellTicket( Ticket aTicket ) throws IOException {
        System.out.println("Writing unsell " + aTicket + " in performance " + getDataFile().getPath() );
        
        aTicket.journalUnsell( 0 );
        
        try{
            tickets.remove( aTicket );
        }catch( NoSuchElementException e2 ){
                
        }
        
        totalIncome -= aTicket.getPrice().getPrice();
        nbSoldSeats -= 1;
        
    }
    
    
    
    
    public void restorePrebooking( Booking aBooking ){
        
        System.out.println("*** Performance:restorePrebooking() " + aBooking);
        
        if( preBookings.contains( aBooking ) ){
            
        }else{
            nbPrebookedSeats += aBooking.getNbSeats();
            preBookings.add( aBooking );
        }
    }
    
    public void cancelBooking( Booking aBooking ) throws java.io.IOException {
        System.out.println("Canceling " + aBooking + " in performance " + getDataFile().getPath() );
        
        try{   
            try{
                bookings.remove( aBooking );
            }catch( NoSuchElementException e2 ){
                
            }
            aBooking.journalUnbook();
            nbBookedSeats -= aBooking.getNbSeats();   //aBooking.getNbSeats();
        }catch( IOException e ){
            throw e;
        }
    }
    
    
    public void convertBookingToPrebooking( Booking aBooking ) throws IOException{
        System.out.println("*** Performance:convertBookingToPrebooking() " + aBooking);
        
        /*nbBookedSeats -= aBooking.getNbSeats();
        nbPrebookedSeats += aBooking.getNbSeats();*/
        
        //nbBookedSeats -= aBooking.getNbSeats();
        
        nbPrebookedSeats += aBooking.getNbSeats();
        cancelBooking( aBooking );
        preBookings.add( aBooking );
    }
    
   
    
    public void journalAddPrices() throws IOException{
        for( Price p:getPrices() ){
            
            if( dataFile.exists() ){
            }else{
                createDataFile( getNbFreeSeats() );
            }
            
            BackedUpBufferedWriter writer;
            try{
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                        );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                        );
            }
            
            writer.write( "ADDPRICE" + "|" + p.getId() + "|" + p.getAction() + "|" + p.getTitle() + "|" + p.getPrice() + "|" + p.getValidityComment() +"\n" );
            writer.close();
        }
    }
    
    
   
    
    
    
    /** updates a booking already stored on file
     */
    public void updateBooking( Booking oldBooking, String newName, int newNbSeats, String newComment, String newStreet, String newTown, String newPhone, String newEmail ) throws java.io.IOException, PerformanceFullException{
        System.out.println("Updating " + oldBooking + " in performance " + getDataFile().getPath() );
        System.out.println("  -> " + newNbSeats + " for " + newName );
        
        // update free seat number
        int deltaSeats = ( oldBooking.getNbSeats() - newNbSeats  );
        System.out.println("Performance:updateBooking: " + ( getNbFreeSeats() + deltaSeats) + " seats remaining after modification (" + deltaSeats + ")");
        if( ( getNbFreeSeats() + deltaSeats) >= 0 ){
            //nbFreeSeats -= deltaSeats;
        
            // unbook in file
            
            //oldBooking.journalUnbook();

            //Booking newBooking = new Booking( new Client( newName ), newNbSeats, this );
            //newBooking.setComment( newComment );
            
            //try{         
                oldBooking.journalUpdateBooking( newNbSeats, newName, newComment, newStreet, newTown, newPhone, newEmail);
                nbBookedSeats -= deltaSeats;
            //}catch( IOException e ){
                //nbFreeSeats -= oldBooking.getNbSeats();
            //    throw e;
            //}
            
        }else{
            System.out.println("throwing PerformanceFullException ");
            throw new PerformanceFullException( this );
        }
        
    }
    
    public void createDataFile( int nbPrebooked ) throws java.io.IOException {
        
        System.out.print("Performance:createDataFile " + dataFile );
              
        BackedUpBufferedWriter writer;
      
        if( dataFile.exists() ){
            try{
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"), 
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                        );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                        );
            }
            
        }else{
            dataFile.createNewFile();
            
            try{
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8") 
                        );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BackedUpBufferedWriter( 
                        new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                        new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                        );
            }
            
            writer.write("bookingsV1\n");
            writer.write("Journal for " + getShow().getTitle() + " " + getFormatedDate() + " " + getRoom().getCompany().getName() + " " + getRoom().getCompany().getTown() + "\n");
            
            writer.write( 
                    getId() + "|" + 
                    this.getNbTotalSeats () + "|" + 
                    (getNbFreeSeats() + nbPrebooked ) + "|" + 
                    getShow().getId() + "|" + 
                    getRoom().getId() + "\n");
        }
        writer.write( "SETSHOW" + "|" + getShow().getId() + "|" + getShow().getTitle() + "\n");
        writer.write( "SETROOM" + "|" + getRoom().getId() + "|" + getRoom().getCompany().getName() + "\n");
        writer.write( "SETDATE" + "|" + getFormatedDate() + "\n" ); 
        writer.write( "SETNBSEATS" + "|" + getNbTotalSeats() + "|" + getNbFreeSeats() + "|" + getNbBookedSeats() + "|" + getNbSoldSeats() + "|" + getNbPrebookedSeats() + "\n" );
        writer.close();
    }
    
    public int getNbFreeSeats(){
        //return nbFreeSeats;
        return( getNbTotalSeats() - getNbSoldSeats() - getNbBookedSeats() - getNbPrebookedSeats() );
    }
    
    public int getNbSoldSeats(){
        return nbSoldSeats;
    }
    
     public int getNbBookedSeats(){
        return nbBookedSeats;
    }
     
    public int getNbPrebookedSeats(){
        return nbPrebookedSeats;
    }
    
    public String toString(){
        //return date + "|" + room + "|" + show + "|" + getNbFreeSeats() + " free seats of " + getNbTotalSeats() +" seats";
        return date + room.getCompany().getName() + show.getTitle() + show.getPerformer().getName();
    }
    
    public Object getColumn( int aColumn ){
        Object rep = null;
        
        switch( aColumn ){
            case 0:
                    rep = getShow();
                    break;
            //case 1:
            //        rep = getShow().getPerformer().getName();
            //        break;
            case 1:
                    //DateFormat df = DateFormat.getDateInstance(DateFormat.FULL);
                    
                    rep = getFormatedDate();
                    break;
            //case 3:
            //        rep = getRoom().getCompany().getTown();
            //        break;
            case 2:
                    rep = getRoom().getCompany();
                    break;
            case 3:
                    rep = getNbTotalSeats();
                    break;
            case 4:
                    rep = getId();
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    public int getColumnCount(){
        return 5;
    }
    
    public Class<?> getColumnClass( int aColumn ){
        
        switch( aColumn ){
            case 0:
                return String.class;
            case 1:
                //return Date.class;
                return SimpleDateFormat.class;
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            case 4:
                return Integer.class;
        }
        return Object.class;
    }
    
    
    public String getColumnName( int aColumn ){
        String rep = "";
        
        switch( aColumn ){
            case 0:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTitle" );
                    //rep = "title";
                    break;
            //case 1:
            //        rep = "performer's name";
            //        break;
            case 1:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelDate" );
                    //rep = "date";
                    break;
            //case 3:
            //        rep = "town";
            //        break;
            case 2:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelRoom" );
                    //rep = "room";
                    break;
            case 3:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTotalNbSeats" );
                    //rep = "total nb seats";
                    break;
            case 4:
                    rep = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelId" );
                    //rep = "id";
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
    
    public void setEditmode( boolean a ){
        editmode = a;
        if( editmode == true ){
            for( Price i : getPrices() ){
                i.setEditmode( true );            
                
            }
        }else{
            for( Price i : getPrices() ){
                i.setEditmode( false );            
            }
        }
    }
    
    public boolean isBeingEdited(){
        return editmode;
    }
    
     public void setColumn( Object aValue, int aColumn ){
        switch( aColumn ){
            case 0:
                    try{
                        setShow( (Show) aValue );
                    }catch( java.io.IOException e ){
                        // this should not be here, throw the exception instead
                        // this correction needs a change in the ShowableArrayList class !!!
                        javax.swing.JOptionPane.showMessageDialog( null, "COULD NOT WRITE CHANGE TO file, this value will not be taken into account, try again.", "File error", javax.swing.JOptionPane.OK_OPTION );
                    }
                    break;
            //case 1:
            //        break;
            case 1:
                    //System.out.println("Performance: setColumn " + aColumn + ":" + aValue );
                    //DateFormat df = new DateFormat();
                    //df.parse( aValue );
                    //df = DateFormat.getDateInstance(DateFormat.LONG);
                    DateFormat df = DateFormat.getDateInstance();
                    try{
                        Date myDate = key.Preferrences.getSaveableDateFormater().parse( (String) aValue );
                        System.out.println("Performance: setColumn date transformed to " + (String) aValue );
                        try{
                            setDate( myDate );
                        }catch( java.io.IOException e ){
                            // this should not be here, throw the exception instead
                            // this correction needs a change in the ShowableArrayList class !!!
                            javax.swing.JOptionPane.showMessageDialog( null, "COULD NOT WRITE CHANGE TO file, this value will not be taken into account, try again.", "File error", javax.swing.JOptionPane.OK_OPTION );
                        }
                    } catch( ParseException exception ){
                      System.out.println("Performance: setColumn caught exeption" );
                    }
                    break;
            //case 3:
            //        break;
            case 2:
                    try{
                        setRoom( (Room) aValue );
                    }catch( java.io.IOException e ){
                        // this should not be here, throw the exception instead
                        // this correction needs a change in the ShowableArrayList class !!!
                        javax.swing.JOptionPane.showMessageDialog( null, "COULD NOT WRITE CHANGE TO file, this value will not be taken into account, try again.", "File error", javax.swing.JOptionPane.OK_OPTION );
                    }
                    break;
            case 3:
                    try{
                        setNbTotalSeats( new Integer( (String) aValue) );
                    }catch( java.io.IOException e ){
                        // this should not be here, throw the exception instead
                        // this correction needs a change in the ShowableArrayList class !!!
                        javax.swing.JOptionPane.showMessageDialog( null, "COULD NOT WRITE CHANGE TO file, this value will not be taken into account, try again.", "File error", javax.swing.JOptionPane.OK_OPTION );
                    }
                    break;
            default:
                    break;
            
        }
    }
    
    public void setShow( Show aShow ) throws IOException {
        
        BackedUpBufferedWriter writer = null;
        if( dataFile.exists() ){
        }else{
            createDataFile( getNbFreeSeats() );
        }
        
        
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                    );
        }
        

        writer.write( "SETSHOW" + "|" + aShow.getId() + "|" + aShow.getTitle() +"\n" );
        writer.close();
        
        
        show = aShow;
    }
    public void setDate( Date aDate ) throws IOException {
        System.out.println("Performance: setting date to " + aDate );
        
        BackedUpBufferedWriter writer = null;
        if( dataFile.exists() ){
        }else{
            createDataFile( getNbFreeSeats() );
        }
        
        
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                    );
        }
        writer.write( "SETDATE" + "|" + key.Preferrences.getSaveableDateFormater().format( aDate ) + "\n" );
        writer.close();
        
        
        date = aDate;
    }
    public void setRoom( Room aRoom ) throws IOException{
        
        BackedUpBufferedWriter writer = null;
        if( dataFile.exists() ){
        }else{
            createDataFile( getNbFreeSeats() );
        }
           
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                    );
        }
        writer.write( "SETROOM" + "|" + aRoom.getId() + "|" + aRoom.getCompany().getName() +"\n" );
        writer.close();
        
        room = aRoom;
    }
    
    
    
    public void setNbTotalSeats( int aNb ) throws IOException {
        
        System.out.print("Performance:setNbTotalSeats " + aNb);
        
        /*FileWriter writer = null;
        if( dataFile.exists() ){
            writer = new FileWriter( getDataFile(), true );
        }else{
            //createDataFile( getNbFreeSeats() );
            
            createDataFile( aNb );
        }
        writer = new FileWriter( getDataFile(), true ); */
        
        
        
        
        BackedUpBufferedWriter writer = null;
        if( dataFile.exists() ){
        }else{
            createDataFile( aNb );
        }
        
        
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( getBackupFile().getPath(), true ) )
                    );
        }
        
        
        writer.write( "SETNBSEATS" + "|" + aNb + "|" + (getNbFreeSeats() + (aNb - getNbTotalSeats() ) ) + "|" + getNbBookedSeats() + "|" + getNbSoldSeats() + "|" + getNbPrebookedSeats() + "\n" );       
        writer.close();
          
        nbTotalSeats = aNb;
        
    }
    
    public void restoreNbTotalSeats( int aNb ){
        nbTotalSeats = aNb;
    }
    
    public double getTotalIncome(){
        return totalIncome;
    }
    
    public int getNbTotalSeats(){
        return nbTotalSeats;
    }
    
    private void setNbPrebookedSeats( int aNb ){
        nbPrebookedSeats = aNb;
    }
    
    
    
    public int compareTo( Object other ){
      if (!(other instanceof Performance))
            throw new ClassCastException("in Performance:compareTo: A Performance object expected.");
      
      int rep = 0;
      
      String thisName = this.getShow().getTitle().toUpperCase();
      String otherName = ((Performance) other).getShow().getTitle().toUpperCase();
      
      rep = thisName.compareTo( otherName );
              
      
      return rep;
      
    }
    
    
    public ShowableArrayList<Booking> getBookings(){
        return bookings;
    }
    public ShowableArrayList<Ticket> getTickets(){
        return tickets;
    }
    
    public void clearBookingsFromMemory(){
        if( bookings.size() > 0 ){
            bookings.clear();
        }
    }
    public void clearTicketsFromMemory(){
        if( tickets.size() > 0 ){
            tickets.clear();
        }
    }
    
    
    public Booking getBookingWithId ( long anId, String aString, int aNb) throws BookingNotFoundException{
        for( Booking b : bookings ){
            if( b.getId() == anId && b.getClient().getName().equals( aString ) && b.getNbSeats() == aNb ){
                return( b );
            }
        }
        throw new BookingNotFoundException();
        //return( null );
    }
    
    public Booking getBookingWithId ( long anId ) throws BookingNotFoundException{
        for( Booking b : bookings ){
            //System.out.println("Booking:getBookingWithId ")
            if( b.getId() == anId ){
                return( b );
            }
        }
        throw new BookingNotFoundException();
        //return( null );
    }
    
    public Price getPriceWithId ( long anId ) throws PriceNotFoundException{
        for( Price b : prices ){
            //System.out.println("Booking:getBookingWithId ")
            if( b.getId() == anId ){
                return( b );
            }
        }
        throw new PriceNotFoundException();
        //return( null );
    }
    
    
    public Ticket getTicketWithId ( long anId ) throws TicketNotFoundException {
        for( Ticket t : tickets ){
            if( t.getId() == anId ){
                return( t );
            }
        }
        throw new TicketNotFoundException();
        //return( null );
    }
    
    
    public void loadBookingsTicketsInMemory() throws IOException{
        
        
        System.out.println("Performance:loadBookingsTicketsInMemory");
        
        
        resetPerformance();
        
        
        // read dataFile
        
        File dataFile = getDataFile();
        if( dataFile.exists() ){
            //BufferedReader in = new BufferedReader( new FileReader( dataFile ) );
            BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream( dataFile.getPath() ), "UTF-8"));
            
            //FileReader in = new FileReader( dataFile );
            String s;
            long lineCounter = 0;
            
            while( (s = in.readLine()) != null ){
                
                StringTokenizer st = new key.StringTokenizerStrict( s, "|" );
                
                if( lineCounter == 1 ){
                }else if( lineCounter == 2 ){
                    
                   
                    
 
                }else if( lineCounter > 2){
                    System.out.println("---------------");
                    System.out.println( s );
                    System.out.println( st.countTokens() + " tokens" );
                    
                
                    if( st.countTokens() > 1 ){
                        
                        String command = st.nextToken().trim();
                        System.out.println( " found command:" + command + ":" );
                        
                        if( command.equals( "BOOK" ) ){
                            //System.out.println( " executing command " + command );
                            
                            long bookId = new Integer( st.nextToken() );
                            String dateString = st.nextToken();
                            
                            int nbSeats = new Integer( st.nextToken() );
                            String clientName = st.nextToken().trim().toUpperCase( key.Preferrences.getLocale() );
                            String comment = "";
                            if( st.hasMoreTokens() ){
                                comment = st.nextToken();
                            }
                            
                            int nbFreeAfterAction = new Integer( st.nextToken() );
                            
                            String aStreet = "";
                            String aTown = "";
                            String aPhone = "";
                            String aEmail = "";
                            
                            if( st.hasMoreTokens() ){
                                aStreet = st.nextToken();
                                aTown = st.nextToken();
                                aPhone = st.nextToken();
                                aEmail = st.nextToken();
                            } 
                            
                            
                                
                            Booking toto = new Booking( new Client( clientName ) ,nbSeats ,this );
                            toto.setComment( comment );
                            toto.setId( bookId );
                            toto.setStreet( aStreet );
                            toto.setTown( aTown );
                            toto.setPhone( aPhone );
                            toto.setEmail( aEmail );
                            
                            try{
                                //SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
                                Date aDate = key.Preferrences.getDateFormater().parse( dateString );    
                                toto.setDate( aDate );
                            }catch( java.text.ParseException e ){
                                /*try{
                                    Date aDate = key.Preferrences.getSaveableDateFormater().parse( dateString );    
                                    toto.setDate( aDate );
                                }catch( java.text.ParseException e2 ){
                                    
                                }*/
                            }
                            
                            boolean[] editable = {false, false, false};
                            toto.setCellEditable( editable );
                            //bookings.add( toto );
                            restoreBook( toto );
                                
                                // System.out.println( "added booking " + toto );
                                
                        }else if( command.equals( "UNBOOK" ) ){
                            long bookId = new Integer( st.nextToken() );
                            String dateString = st.nextToken();
                            int nbSeats = new Integer( st.nextToken() );
                            String clientName = st.nextToken().trim().toUpperCase( key.Preferrences.getLocale());
                            //bookings.remove( getBooking( clientName, nbSeats ) );
                            
                            try{
                                Booking r = getBookingWithId( bookId, clientName, nbSeats );
                                //bookings.remove( r );
                                restoreUnbook( r );
                            }catch( BookingNotFoundException e ){
                            }
                            
                        }else if( command.equals( "SELL" ) ){
                            System.out.println( "tickets has " + tickets.size() + " tickets");
                            System.out.println( "  SELL:" );
                            
                            long ticketId = new Integer( st.nextToken() );
                            String dateString = st.nextToken();
                            String tarifName = st.nextToken();
                            Double pricePaid = new Double( st.nextToken() );
                            //int nbSeats = new Integer( st.nextToken() );
                            Ticket toto;
                            try{
                                toto = new Ticket( getPriceWithName( tarifName ), ticketId, this );
                            }catch( UnknownTarifException e ){
                                toto = new Ticket( new Price( "Billet", tarifName +" (Deprecated)", pricePaid, ""), ticketId, this );
                            }
                            boolean[] editable = {false, false};
                            toto.setCellEditable( editable );
                            
                            //tickets.add( toto );
                            restoreSell( toto );
                            
                            System.out.println( "tickets has " + tickets.size() + " tickets");
                                
                        }else if( command.equals( "UNSELL" ) ){
                            System.out.println( "tickets has " + tickets.size() + " tickets");
                            
                            long ticketId = new Integer( st.nextToken() );
                            String dateString = st.nextToken();
                            System.out.println( "  UNSELL:" + ticketId );
                            //String tarifName = st.nextToken();
                            try{
                                Ticket toto = getTicketWithId( ticketId );
                                restoreUnsell( toto );
                                //tickets.remove( toto );
                            }catch( TicketNotFoundException e ){
                                
                            }
                            
                            System.out.println( "tickets has " + tickets.size() + " tickets");    
                        }else if( command.equals( "UPDATEBOOKING" ) ){
                            long bookingId = new Long( st.nextToken() );
                            String dateString = st.nextToken();
                            int aNb = new Integer( st.nextToken() );
                            String aName = st.nextToken().trim().toUpperCase( key.Preferrences.getLocale());
                            String aComment = st.nextToken();
                            
                            int nbFreeAfterAction = new Integer( st.nextToken() );
                            
                            String aStreet = "";
                            String aTown = "";
                            String aPhone = "";
                            String aEmail = "";
                            
                            if( st.hasMoreTokens() ){
                                aStreet = st.nextToken();
                                aTown = st.nextToken();
                                aPhone = st.nextToken();
                                aEmail = st.nextToken();
                            } 
                           
                            
                            System.out.println("UPDATEBOOKING for " + bookingId );
                            try{
                                //Booking toto = getBookingWithId( bookingId );
                                Booking toto = getBookingWithId( bookingId );
                                restoreUpdateBooking( toto, aNb, aName, aComment, aStreet, aTown, aPhone, aEmail );
                                /*System.out.println("UPDATEBOOKING: found " + toto );
                                try{
                                    try{
                                        toto.setNbSeats( aNb );
                                    }catch( PerformanceBeingEditedException e2 ){
                                        
                                    }
                                }catch( PerformanceFullException e ){
                                    
                                }
                                toto.setComment( aComment );
                                toto.setId( bookingId );                              
                                toto.getClient().setName( aName );
                            
                                boolean[] editable = {false, false, false};
                                toto.setCellEditable( editable );
                                System.out.println("CHANGED TO: " + toto);*/
                            }catch( BookingNotFoundException e ){
                                System.out.println("UPDATEBOOKING: booking with id " + bookingId + " not found");
                            }
                            
                        }else if( command.equals( "SETNBSEATS" ) ){
      
                            int aTotal = new Integer( st.nextToken() );
                            restoreNbTotalSeats( aTotal );
                            
                        }else if( command.equals("ADDPRICE") ){
                            //ADDPRICE|3|Billet|AVS/Etudiant/Apprenti|18.0|VALABLE SEULEMENT AVEC PIECE DE LEGITIMATION
                            long anId = new Long( st.nextToken() );
                            String anAction = st.nextToken();
                            String aTitle = st.nextToken();
                            double aPrice = new Double( st.nextToken() );
                            String aValidityComment = st.nextToken();
                            //addPrice( new Price( anAction, aTitle, aPrice, "frs", aValidityComment, anId ) );
                            prices.add( new Price( anAction, aTitle, aPrice, "frs", aValidityComment, anId ) );
                            
                        }else if( command.equals("DELETEPRICE") ){
                            long anId = new Long( st.nextToken() );
                            try{
                                prices.remove( getPriceWithId( anId ) );
                            }catch( Exception ee ){
                                
                            }
                            
                        }else{
                            System.out.println( " " + command + " is unknown" );
                        }
                        
                        
                   // }else{
                        
                   }
                    
                }
                lineCounter++;
            }
            in.close();
            
            
            
        }else{
            throw new IOException();
        }
        
        recomputeSeats();
        
    }
    
    private void restoreBook( Booking booking ){
        bookings.add( booking );
        if( booking.getId() >= Booking.getIdCounter() ){
            Booking.setIdCounter( ( booking.getId() + 1 ) );
        }
    }
    
    private void restoreUnbook( Booking booking ){
        bookings.remove( booking );
    }
    private void restoreUpdateBooking( Booking booking, int aNb, String aName, String aComment, String aStreet, String aTown, String aPhone, String aEmail ){
        System.out.println("UPDATEBOOKING: found " + booking );
        try{
            try{
                booking.restoreNbSeats( aNb );
            }catch( PerformanceBeingEditedException e2 ){
                              
            }
        }catch( PerformanceFullException e ){
                                    
        }
        booking.setComment( aComment );
        booking.getClient().setName( aName );
        booking.getClient().setStreet( aStreet );
        booking.getClient().setTown( aTown );
        booking.getClient().setPhone( aPhone );
        booking.getClient().setEmail( aEmail );
                            
        boolean[] editable = {false, false, false};
        booking.setCellEditable( editable );
        System.out.println("CHANGED TO: " + booking);
    }
    
    private void restoreSell( Ticket ticket ){
        tickets.add( ticket );
        /*if( ticket.getId() >= Ticket.getIdCounter() ){
           Ticket.setIdCounter( ( ticket.getId() + 1 ) );
        }*/
        totalIncome += ticket.getPrice().getPrice();
    }
    private void restoreUnsell( Ticket ticket ){
        totalIncome -= ticket.getPrice().getPrice();
        tickets.remove( ticket );
    }
    
    
    public void savePrices(){
        oldPrices.clear();
        for( Price p:prices ){
            oldPrices.add( p );
        }
    }
    public void restoreOldPrices(){
        prices.clear();
        for( Price p:oldPrices ){
            prices.add( p );
        }
    }
    
    
    
    
    
    private void recomputeSeats() {
        // compute bookings
        for( Booking b:bookings ){
            nbBookedSeats += b.getNbSeats();
        }
        for( Ticket t:tickets ){
            nbSoldSeats += t.getNb();
        }
    }
    
    
    public void resetPerformance(){
        totalIncome = 0;
        nbSoldSeats = 0;
        nbBookedSeats = 0;
        bookings.clear();
        tickets.clear();
        prices.clear();
        oldPrices.clear();
          
    }
    
    
    public String getSignature(){
        return( signature );
    }
       
    
    public void setReadOnly( boolean a ){
        readOnly = a;
    }
    public boolean isReadOnly(){
        return readOnly;
    }
    
    private static boolean[] editable = { false, false, false, false };
    private static long maxid = 0;
    
    private long id = 0;
    
    
    private Date date = null;
    private Room room = null;
    private Show show = null;
    //private int nbFreeSeats = 0;
    private int nbSoldSeats = 0;
    //private int nbReservedSeats = 0;
    private int nbPrebookedSeats = 0;
    private int nbBookedSeats = 0;
    private int nbTotalSeats = 0;
    //private ArrayList<Booking> bookings = new ArrayList<Booking>();
    private ArrayList<Booking> preBookings = new ArrayList<Booking>();
    private ShowableArrayList<Booking> bookings = new ShowableArrayList<Booking>();
    private ShowableArrayList<Ticket> tickets = new ShowableArrayList<Ticket>();
    
    
    private File dataFile = null;
    private File backupFile = null;
    private File ticketImageFile = null;
    private ShowableArrayList<Price> prices = new ShowableArrayList<Price>();
    private ShowableArrayList<Price> oldPrices = new ShowableArrayList<Price>();
    private String currency = "frs";
    
    private double totalIncome = 0;
    
    private boolean editmode = false;
    
    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
    
    
    
    //private SimpleDateFormat af = new SimpleDateFormat("EEE dd.MMM.yyyy', 'HH'h'mm");
    
    
    //private SimpleDateFormat af = new SimpleDateFormat("EEE dd.MM.yy '??' HH:mm", Preferrences.getLocale() );

    private BookingSystem bookingSystem = null;
    
    private String signature = " ";
    
    private boolean readOnly = true;
    
    
}
