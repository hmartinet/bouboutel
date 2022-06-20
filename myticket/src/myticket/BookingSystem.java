/*
 * BookingSystem.java
 *
 * Created on September 8, 2005, 4:14 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.util.*;
import javax.swing.table.*;
import java.text.*;
import java.io.*;
import key.Preferrences;

/**
 *
 * @author sdperret
 */
public class BookingSystem extends AbstractListTableModel {
    
    public static final long serialVersionUID = -663728923;
    
    /** Creates a new instance of BookingSystem */
    public BookingSystem( File aDataDirectory, File aBackupDirectory ) {
        setDataDirectory( new File( aDataDirectory.getPath() ) );
        setBackupDirectory( new File( aBackupDirectory.getPath() ) );
        
        System.out.println("BookingSystem constructor: dataDirectory = " + key.Preferrences.getDataDirectory() );
    
        Preferrences.setLocale( Locale.FRENCH );
        
        // check for already existing booking files so that they will not be overwritten by the system
        /*boolean ok = false;
        while( ok == false ){
            long anIdCounter = Performance.getIdCounter();
            File aFile = new File( aDataDirectory, anIdCounter + ".bookings" );
            if( aFile.exists() ){
                Performance.setIdCounter( anIdCounter + 1 );
            }else{
                System.out.println("Created bookingSystem with IdCounter of Performance = " + anIdCounter );
                ok = true;
            }
        }*/
        
        
        /*File[] bookingFiles = aDataDirectory.listFiles( new BookingsFilenameFilter() );
        if( bookingFiles.length > 0 ){
            bookingFiles.sort();
            String toto = bookingFiles[ bookingFiles.length-1 ].getName();
            String[] toto2 = toto.split("\\.");
            int anId = new Integer( toto2[0] );
            Performance.setIdCounter( anId + 1 );
        }
        
        System.out.println("Created bookingSystem with IdCounter of Performance = " + Performance.getIdCounter() );
        */
        
    }
    
    public float getVersion(){
        return( 1.0F );
    }
    
    public File getDataDirectory(){
        return key.Preferrences.getDataDirectory();
    }
    public void setDataDirectory( File aDataDirectory ){
        key.Preferrences.setDataDirectory( aDataDirectory );
    }
    
    public File getBackupDirectory(){
        return key.Preferrences.getBackupDirectory();
    }
    public void setBackupDirectory( File aDataDirectory ){
        key.Preferrences.setBackupDirectory( aDataDirectory );
    }
    
    public File getLicensePath(){
        return licencePath;
    }
    public void setLicensePath( File aDataFile ){
        licencePath = aDataFile;
    }
    
    public Date getLicenseValidUntil(){
        return licenceLimit;
    }
    public void setLicenseValidUntil( Date aDate ){
        licenceLimit = aDate;
    }
    
    
    public void addPerformance( Performance aPerformance ){
        performances.add( aPerformance );
    }
    
    public void deletePerformance( int a ){
        performances.remove( getPerformances().get( a ) );
    }
    public void deletePerformance( Performance aPerformance ) throws PerformanceNotFoundException{
        try{
            performances.remove( aPerformance );
        }catch( Exception e ){
            throw new PerformanceNotFoundException( aPerformance );
        }
    }
    
    public void deletePerformer( int a ){
        
        Performer p = getPerformers().get( a );
        
        //first delete all shows of this performer
        for( Show s : p.getShows() ){
            try{
                deleteShow( s );
            }catch( ShowNotFoundException e ){
                
            }
        }
        performers.remove( p );
    }
    
    public void deletePerformer( Performer aPerformer ) throws PerformerNotFoundException{
        try{
            
            //first delete all shows of this performer
            for( Show s : aPerformer.getShows() ){
                try{
                    deleteShow( s );
                }catch( ShowNotFoundException e ){
                
                }
            }         
            performers.remove( aPerformer );
            
        }catch( Exception e ){
            throw new PerformerNotFoundException( aPerformer );
        }
    }
    
    public void deleteShow( int a ){
        
        Show s = getShows().get( a );
        //first delete all performances for this show
        for( Performance p : s.getPerformances() ){
            try{
                deletePerformance( p );
            }catch( PerformanceNotFoundException e ){
                
            }
        }
        
        shows.remove( s );
    }
    public void deleteShow( Show s ) throws ShowNotFoundException{
        //first delete all performances for this show
        for( Performance p : s.getPerformances() ){
            try{
                deletePerformance( p );
            }catch( PerformanceNotFoundException e ){
                
            }
        }
        shows.remove( s );
        
    }
    
    public void deleteRoom( int a ){
        rooms.remove( getRooms().get( a ) );
    }
    public void deleteRoom( Room aRoom ) throws RoomNotFoundException{
        try{
            rooms.remove( aRoom );
        }catch( Exception e ){
            throw new RoomNotFoundException( aRoom );
        }
    }
    
    
    
    
    public void addRoom( Room aRoom ){
        rooms.add( aRoom );
    }
    
    public void addShow( Show aShow ){
        shows.add( aShow );
    }
    
    public void addPerformer( Performer aPerformer ){
        performers.add( aPerformer );
    }
    
    /*public void addPreBooking( Performance aPerformance, Client aClient, int aNbSeats ) throws PerformanceFullException{
        //setLastClient( aClient );
        try{
          aPerformance.addPreBooking( aClient, aNbSeats );
        }catch( PerformanceFullException e ){
            throw( e );
        }
    }*/
    
    public void addPreBooking( Booking aBooking ) throws PerformanceFullException {
        //setLastClient( aBooking.getClient() );
        try{
          aBooking.getPerformance().addPreBooking( aBooking );
        }catch( PerformanceFullException e ){
            throw( e );
        }
    }

    public void cancelPreBooking( Booking aBooking ){
          aBooking.getPerformance().cancelPreBooking( aBooking );
    }
    
    public ShowableArrayList<Performer> getPerformers(){
        return( performers );
    }
    public ShowableArrayList<Performance> getPerformances(){
        return( performances );
    }
    
    /** returns an ArrayList of bookings for aPerformance */
    public ArrayList<Booking> getBookings( Performance aPerformance ){
        return( aPerformance.getBookings() );
    }
    
    public ShowableArrayList<Show> getShows(){
        return( shows );
    }
    public ShowableArrayList<Room> getRooms(){
        return( rooms );
    }
    
    
    public int getRowCount(){
        //System.out.println("------------------- returning row number: " + performances.size() );
        return( performances.getSize() );
        //return( 0 );
    }
    public int getColumnCount(){
        return( 8 );
    }
    
    
    public Object getValueAt( int aRow, int aColumn ){
        
        Object rep = null;
        
        switch( aColumn ){
            case 0:
                    rep = performances.get( aRow ).getShow().getTitle();
                    //System.out.println("------------------- returning column 0: " + rep );
                    break;
            case 1:
                
                    /*System.out.println( "performance: "+ performances.get( aRow ) );
                    System.out.println( "show       : "+ performances.get( aRow ).getShow() );
                    System.out.println( "performer  : "+ performances.get( aRow ).getShow().getPerformer() );
                    System.out.println( "name       : "+ performances.get( aRow ).getShow().getPerformer().getName() );*/
                
                    rep = performances.get( aRow ).getShow().getPerformer().getName();
                    //System.out.println("------------------- returning column 1: " + rep );
                    break;
            case 2:
                    //rep = performances.get( aRow ).getDate();
                    //SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
                    SimpleDateFormat sf = new SimpleDateFormat("EEE dd.MM.yy 'Ă ' HH:mm", Preferrences.getLocale());
                    rep = sf.format( performances.get( aRow ).getDate() );
                    //System.out.println("------------------- returning column 2: " + rep );
                    break;
            /*case 3:
                    rep = performances.get( aRow ).getRoom().getCompany().getTown();
                    //System.out.println("------------------- returning column 3: " + rep );
                    break;*/
            case 3:
                    rep = performances.get( aRow ).getRoom().getCompany().getName();
                    //System.out.println("------------------- returning column 4: " + rep );
                    break;
            case 4:
                    rep = performances.get( aRow ).getNbFreeSeats();
                    //System.out.println("------------------- returning column 5: " + rep );
                    break;
            case 5:
                    rep = performances.get( aRow ).getNbBookedSeats();
                    break;
            case 6:
                    rep = performances.get( aRow ).getNbSoldSeats();
                    break;
            case 7:
                    rep = performances.get( aRow ).getNbPrebookedSeats();
                    break;
            default:
                    //System.out.println("------------------- ERROR waniting column: " + aColumn );
                    break;
            
        }
        
        return( rep );
        //return(1);
    }
    
    public Class<?> getColumnClass( int aColumn ){
        switch( aColumn ){
            case 0:
                return String.class;
            case 1:
                return String.class;
            case 2:
                return SimpleDateFormat.class;
            case 3:
                return String.class;
            case 4:
                return Integer.class;
            case 5:
                return Integer.class;
            case 6:
                return Integer.class;
            case 7:
                return Integer.class;
        }
        return Object.class;
    }
    
    
    public String getColumnName( int c ){
        switch( c ){
            case 0:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelTitle" );
            case 1:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelCompany" );
            case 2:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelDate" );
                //return( "date" );
            /*case 3:
                return( "town" );*/
            case 3:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelRoom" );
                //return( "room" );
            case 4:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbFreeSeats" );
                //return( "nb free seats" );
            case 5:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbBookedSeats" );
                //return( "nb booked seats" );
            case 6:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbSoldSeats" );
                //return( "nb sold seats" );
            case 7:
                return java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbBookingSeats" );
                //return( "now booking" );
        }
        return("");
    }
    
    
    /*public void addClient( Client aClient ){
        clients.put( aClient.getId(), aClient );
    }
    public Client getClient( long anId ){
        return clients.get( anId );
    }*/
    
    public void setLastClient( Client aClient ){
        
        System.out.println( "setting last client to " + aClient );
        
        lastClient = aClient;
    }
    public Client getLastClient(){
        return( lastClient );
    }
    
    
    
    
    public String toString(){
        
        String r = "Booking System:\n";
        r += "  Registered Performances:\n";
 
        for( Performance p : performances ){
            r += "    " + p;
        }
        
        return r;
                
    }
    
    
    public void exportStatistics() throws IOException {
        // open statistics file for writing
        File aFile = new File( getDataDirectory()+File.separator+"bouboutelStatistics.csv" );
        if( !aFile.exists() ){
            aFile.createNewFile();   
        }
        if( aFile.canWrite() ){
            
            BufferedWriter writer = null;
        
            try{
                writer = new BufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( aFile, false ), "UTF-8")
                );
            }catch( java.io.UnsupportedEncodingException ee ){
                writer = new BufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( aFile, false ) )
                );
            }
            
            // write date and time on the first line
            SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy ';' HH:mm:ss z");
            writer.write( sf.format( new Date() ) + "\n" );
            
            // write column labels on the second line
            writer.write( "titre;troupe;date;salle;nb libres;nb reserves;nb vendu;encaisse\n" );
            
            // add a line for each performance
            for( Performance p : performances ){
                 System.out.println("saving statistics for " + p + "\n");
                 writer.write( p.getShow().getTitle() + ";" );
                 writer.write( p.getShow().getPerformer().getName() + ";" );
                 writer.write( p.getDate() + ";" );
                 writer.write( p.getRoom().getCompany().getName() + ";" );
                 writer.write( p.getNbFreeSeats() + ";" );
                 writer.write( p.getNbBookedSeats() + ";" );
                 writer.write( p.getNbSoldSeats() + ";" );
                 writer.write( p.getTotalIncome() + ";" );
                 writer.write("\n");
            }
            
            // close statistics file
            writer.close();
            
        }else{
            throw new IOException();
        }
        
        
    }
    
    
    public Performance getPerformanceWithId( int anId ){
        for( Performance p : performances ){
            if( p.getId() == anId ){
                return( p );
            }
        }
        return( null );
    }
    
    public Performer getPerformerWithId( Long anId ){
        for( Performer p : performers ){
            if( p.getId() == anId ){
                return( p );
            }
        }
        return( null );
    }
    
    public Room getRoomWithId( Long anId ){
        for( Room p : rooms ){
            if( p.getId() == anId ){
                return( p );
            }
        }
        return( null );
    }
    
    public Show getShowWithId( Long anId ){
        for( Show p : shows ){
            if( p.getId() == anId ){
                return( p );
            }
        }
        return( null );
    }
    
    public void savePreferrences() throws IOException{
        File aFile = new File( getDataDirectory()+File.separator+"bouboutel.prefs" );
        File bFile = new File( getBackupDirectory()+File.separator+"bouboutel.prefs" );
        

        if( aFile.canWrite() && bFile.canWrite() ){
            
            System.out.println("CASE 1");
            
            if( !aFile.exists() ){
                aFile.createNewFile();   
            }
            if( !bFile.exists() ){
                bFile.createNewFile();
            }
            
                    BackedUpBufferedWriter writer = null;
        
                    try{
                                writer = new BackedUpBufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ), "UTF-8"),
                                new OutputStreamWriter(new FileOutputStream( bFile, false ), "UTF-8")
                                );
                    }catch( java.io.UnsupportedEncodingException ee ){
                        writer = new BackedUpBufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ) ),
                                new OutputStreamWriter(new FileOutputStream( bFile, false ) )
                                );
                    }
                
                    // dataDirectory
                    writer.write( getDataDirectory().getPath() + "\n" );
                    
                    // backupDirectory
                    //System.out.println("SAVING BACKUP FILE " + getBackupDirectory().getPath() );
                    writer.write( getBackupDirectory().getPath() + "\n" );
                    
                    // Locale
                    writer.write( key.Preferrences.getLocale().getLanguage() + "\n" );
                    
                    // normal exit
                    boolean e = true;
                    writer.write( e + "\n" );
                    
                    // printTicketBackground
                    writer.write( key.Preferrences.printTicketBackground() + "\n" );
                    
                    writer.close(); 
                    
        }else{
            if( aFile.exists() && aFile.canWrite() ){
                
                System.out.println("CASE 2");
                
                    BufferedWriter writer = null;
        
                    try{
                                writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ), "UTF-8")
                                );
                    }catch( java.io.UnsupportedEncodingException ee ){
                        writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ) )
                                );
                    }
                
                    // dataDirectory
                    writer.write( getDataDirectory().getPath() + "\n" );
                    
                    // backupDirectory
                    //System.out.println("SAVING BACKUP FILE " + getBackupDirectory().getPath() );
                    writer.write( getBackupDirectory().getPath() + "\n" );
                    
                    // Locale
                    writer.write( key.Preferrences.getLocale().getLanguage() + "\n" );
                    
                    // normal exit
                    boolean e = false;
                    writer.write( e + "\n" );
                    
                    // printTicketBackground
                    writer.write( key.Preferrences.printTicketBackground() + "\n" );
                    
                    writer.close();

            }else{
                if( !aFile.exists() ){
                    
                    System.out.println("CASE 3");
                    
                    aFile.createNewFile();  
                    
                    BufferedWriter writer = null;
        
                    try{
                                writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ), "UTF-8")
                                );
                    }catch( java.io.UnsupportedEncodingException ee ){
                        writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( aFile, false ) )
                                );
                    }
                
                    // dataDirectory
                    writer.write( getDataDirectory().getPath() + "\n" );
                    
                    // backupDirectory
                    //System.out.println("SAVING BACKUP FILE " + getBackupDirectory().getPath() );
                    writer.write( getBackupDirectory().getPath() + "\n" );
                    
                    // Locale
                    writer.write( key.Preferrences.getLocale().getLanguage() + "\n" );
                    
                    // normal exit
                    boolean e = false;
                    writer.write( e + "\n" );
                    
                    // printTicketBackground
                    writer.write( key.Preferrences.printTicketBackground() + "\n" );
                    
                    writer.close();
                    
                }else{
                    System.out.println("CASE 4");
                    throw new IOException();
                }
            }
        }
        
    }
    
    public void saveStatics() throws IOException{
        // write static variables
        File staticsFile = new File( getDataDirectory()+File.separator+"bouboutel.statics" );
        File backupStaticsFile = new File( getBackupDirectory()+File.separator+"bouboutel.statics" );
        
   
        if( staticsFile.exists() &&  backupStaticsFile.exists() && staticsFile.canWrite() && backupStaticsFile.canWrite() ){
            
            if( !staticsFile.exists() ){
                staticsFile.createNewFile();   
            }
            if( !backupStaticsFile.exists() ){
                backupStaticsFile.createNewFile();
            }
            
                //try{
                    //FileWriter writer = new FileWriter( staticsFile, false );  
                    
                    
                    BackedUpBufferedWriter writer = null;
                    try{
                        writer = new BackedUpBufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( getDataDirectory() + File.separator + staticsFile.getPath(), false ), "UTF-8"),
                                new OutputStreamWriter(new FileOutputStream( getBackupDirectory() + File.separator + staticsFile.getPath(), false ), "UTF-8")
                                );
                    }catch( java.io.UnsupportedEncodingException ee ){
                        writer = new BackedUpBufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( getDataDirectory() + File.separator + staticsFile.getPath(), false ) ),
                                new OutputStreamWriter(new FileOutputStream( getBackupDirectory() + File.separator + staticsFile.getPath(), false ) )
                                );
                    }
                
                    // performance counter
                    writer.write( Long.toString( Performance.getIdCounter() ) + "\n" );          
                    // ticket counter
                    writer.write( Long.toString( Ticket.getIdCounter() ) + "\n" );
                    // contact counter
                    writer.write( Long.toString( Contact.getIdCounter() ) + "\n" );
                    // show counter
                    writer.write( Long.toString( Show.getIdCounter() ) + "\n" );
                    // booking counter
                    writer.write( Long.toString( Booking.getIdCounter() ) + "\n" );
                    // price counter
                    writer.write( Long.toString( Price.getIdCounter() ) + "\n" );
                    // room counter
                    writer.write( Long.toString( Room.getIdCounter() ) + "\n" );
                    writer.close(); 
                    
                //}
               
            
        }else{
            if( !staticsFile.exists() ){
                System.out.println("creating new statics file");
                staticsFile.createNewFile();   
                System.out.println("done");
            }
            if( staticsFile.canWrite() ){
                
                System.out.println("writing statics file only local");
                
                
                // ok, save only on local disk but mark as unlegal exit so that backup is regenerated on next login
                
                    
                
                    BufferedWriter writer = null;
                    try{
                        writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( getDataDirectory() + File.separator + staticsFile.getPath(), false ), "UTF-8")
                                );
                    }catch( java.io.UnsupportedEncodingException ee ){
                        writer = new BufferedWriter( 
                                new OutputStreamWriter(new FileOutputStream( getDataDirectory() + File.separator + staticsFile.getPath(), false ) )
                                );
                    }
                
                    // performance counter
                    writer.write( Long.toString( Performance.getIdCounter() ) + "\n" );          
                    // ticket counter
                    writer.write( Long.toString( Ticket.getIdCounter() ) + "\n" );
                    // contact counter
                    writer.write( Long.toString( Contact.getIdCounter() ) + "\n" );
                    // show counter
                    writer.write( Long.toString( Show.getIdCounter() ) + "\n" );
                    // booking counter
                    writer.write( Long.toString( Booking.getIdCounter() ) + "\n" );
                    // price counter
                    writer.write( Long.toString( Price.getIdCounter() ) + "\n" );
                    // room counter
                    writer.write( Long.toString( Room.getIdCounter() ) + "\n" );
                    writer.close(); 
                
            }else{
                // if local file cannot be written as well then cannot exit
                throw new IOException( "" + staticsFile.exists() +  backupStaticsFile.exists() + staticsFile.canWrite() + staticsFile.canWrite() );
            }
        }
    }
    
    
    public void saveSetup() throws IOException{
        // this function saves the whole setup in a setup file
        
        // create file
        
        File setupFile = new File( getDataDirectory(), "bouboutel.setup" );
        File backupFile = new File( getBackupDirectory(), "bouboutel.setup" );
        
        
        
        if( ( setupFile.canWrite() || !setupFile.exists() )  && ( backupFile.canWrite() || !backupFile.exists() ) ){
        
            setupFile.createNewFile();
            backupFile.createNewFile();
        
            //FileWriter writer = null;
            //writer = new FileWriter( setupFile, false );
        
        
            BackedUpBufferedWriter writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( getDataDirectory() + File.separator + setupFile.getPath(), false ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( getBackupDirectory() + File.separator + setupFile.getPath(), false ), "UTF-8")
                    );
        
            writer.write("bouboutelV1\n");

        
            // save performers
            for( Performer i:getPerformers() ){
                writer.write( "ADDPERFORMER|" + i.getId() + "|" + i.getSignature() + "|" + i.getName() + "|" + i.getStreet() + "|" + i.getTown() + "|" + i.getCountry() + "|" + i.getPhone() + "|" + i.getEmail() + "\n" );
            }
        
            // save shows
            for( Show i:getShows() ){
                writer.write( "ADDSHOW|" + i.getId() + "|" + i.getSignature() + "|" + i.getTitle() + "|" + i.getPerformer().getId() + "\n" );
            }
            // save rooms
            for( Room i:getRooms() ){
                writer.write( "ADDROOM|" + i.getId() + "|" + i.getSignature() + "|" + i.getCompany().getName() + "|" + i.getCompany().getStreet() + "|" + i.getCompany().getTown() + "|" + i.getCompany().getCountry() + "|" + i.getCompany().getPhone() + "|" + i.getCompany().getEmail() + "|" + i.getNbDefaultSeats() + "\n" ); 
            }
        
            // save performances
            for( Performance i:getPerformances() ){
                writer.write( "ADDPERFORMANCE|" + i.getId() + "|" + i.getSignature() + "|" + i.getFormatedDate() + "|" + i.getRoom().getId() + "|" + i.getShow().getId() + "|" + i.getNbTotalSeats() + "\n" ); 
            }
        
            writer.close();
        
            saveStatics(); 
        }else{
            throw new IOException();
        }
    }
    
    /*public void save() throws FileNotFoundException, IOException {  // DEPRECATED
        // this function used to save the whole setup in a binary file.
        
        //System.out.println("Saving " + dataDirectory.getPath() + File.separator + "system.data" );
        System.out.println("Saving system.data" );
        
   
        //ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( dataDirectory.getPath() + File.separator + "system.data" ) );
        ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream( "system.data" ) );
        
        try{
            
            out.writeObject( this );
            out.writeLong( Performance.getIdCounter() );
            out.writeLong( Ticket.getIdCounter() );
            out.writeLong( Contact.getIdCounter() );
            out.writeLong( Show.getIdCounter() );
            out.writeLong( Booking.getIdCounter() );
            out.writeLong( Price.getIdCounter() );
        
        }catch( java.io.NotSerializableException e ){
            System.out.println( "BookingSystem:save intercepted: " + e );
        }
        
        out.close();
           
    }*/
    
    
    public void saveAndUnlock() throws FileNotFoundException, IOException {
        System.out.println("BookingSystem::saveAndUnlock()");
        saveSetup();
        unlock();
    }
    public void unlock(){
        //File toto = new File( dataDirectory.getPath() + File.separator + "system.lock" );
        File toto = new File( "system.lock" );
        System.out.println("TRYING TO UNLOCK");
        if( toto.exists() ){
            System.out.println("FILE EXISTING, DELETE");
            toto.delete();
        }
    }
    
    public void cancelAllPrebookings(){
        for( Performance p : performances){
            p.cancelAllPrebookings();
        }
    }
    
    public void loadSetup( boolean flagShowOld ) throws IOException, ParseException, NoSuchElementException{
        // this function saves the whole setup in a setup file
        
        
        // create file
        
        System.out.println("now in BookingSystem.loadSetup");
        
        File setupFile = new File( "bouboutel.setup" );
        if( setupFile.exists() ){ 
            
            System.out.println("found file");
            clear();
            
            //BufferedReader in = new BufferedReader( new FileReader( setupFile ), "ASCII" );
            
            BufferedReader in = new BufferedReader( new InputStreamReader(new FileInputStream( setupFile.getPath() ), "UTF-8"));
            
            String s;
            long lineCounter = 0;
            
            while( (s = in.readLine()) != null ){
                lineCounter++;
                StringTokenizer st = new key.StringTokenizerStrict( s, "|", false );
                
                if( lineCounter == 1 ){
                }else if( lineCounter > 1 ){
                    
                    System.out.println("Tokens: " + st.countTokens()  );
                 
                    if( st.countTokens() > 1 ){
                        
                        String t = st.nextToken().trim();
                        
                        String command = t;
                        System.out.println( " found command:" + command + ":" );
                        
                        if( command.equals( "ADDPERFORMER" ) ){
                            System.out.println( " executing command " + command );
                            
                            t = st.nextToken();
                            System.out.println( t );
                            Long anId = new Long( t );
                            
                            String aSignature = "";
                            String aName = "";
                            String aStreet = "";
                            String aTown = "";
                            String aCountry = "";
                            String aPhone = "";
                            String aEmail = "";
                            
                            
                            aSignature = st.nextToken();
                            aName = st.nextToken();
                            aStreet = st.nextToken();
                            aTown = st.nextToken();
                            aCountry = st.nextToken();
                            aPhone = st.nextToken();
                            aEmail = st.nextToken();
                            
                            
                            Performer p = new Performer( anId );
                            p.setName( aName );
                            p.setStreet( aStreet );
                            p.setTown( aTown );
                            p.setCountry( aCountry );
                            p.setPhone( aPhone );
                            p.setEmail( aEmail );
                            
                            performers.add( p );
                            
                        }else if( command.equals( "ADDSHOW") ){
                            System.out.println( " executing command " + command );

                            
                            
                            Long anId = new Long( st.nextToken() );
                            
                            String aSignature = "";
                            String aTitle = "";

                            try{
                                aSignature = st.nextToken();
                            }catch( NoSuchElementException e ){
                                
                            }
                            try{
                                aTitle = st.nextToken();
                            }catch( NoSuchElementException e ){
                                
                            }
                            
                            
                            Long aPerformerId = new Long( st.nextToken() );
                             
                            Performer aPerformer = getPerformerWithId( aPerformerId );
                            if( aPerformer != null ){
                                
                                Show aShow = new Show( anId );
                                aShow.setTitle( aTitle );
                                aShow.setPerformer( aPerformer );
                                shows.add( aShow );
                            }
                            
                            
                            
                        }else if( command.equals( "ADDROOM") ){
                            System.out.println( " executing command " + command );
                            
                            Long anId = new Long( st.nextToken() );
                            String aSignature = st.nextToken();
                            String aName = st.nextToken();
                            String aStreet = st.nextToken();
                            String aTown = st.nextToken();
                            String aCountry = st.nextToken();
                            String aPhone = st.nextToken();
                            String aEmail = st.nextToken();
                            int aDefaultNbSeat = new Integer( st.nextToken() );
                            
                            Room aRoom = new Room( anId );
                            aRoom.getCompany().setName( aName );
                            aRoom.getCompany().setStreet( aStreet );
                            aRoom.getCompany().setTown( aTown );
                            aRoom.getCompany().setCountry( aCountry );
                            aRoom.getCompany().setPhone( aPhone );
                            aRoom.getCompany().setEmail( aEmail );
                            aRoom.setNbDefaultSeats( aDefaultNbSeat );
                            
                            rooms.add( aRoom );
                        }else if( command.equals( "ADDPERFORMANCE") ){
                            System.out.println( " executing command " + command );
                            
                            Long anId = new Long( st.nextToken() );
                            String aSignature = st.nextToken();
                            String aDateString = st.nextToken();
                            Long aRoomId = new Long( st.nextToken() );
                            Long aShowId = new Long( st.nextToken() );
                            int aNbTotalSeat = new Integer( st.nextToken() );
                            
                            
                            Room aRoom = getRoomWithId( aRoomId );
                            Show aShow = getShowWithId( aShowId );
                            
                            SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
                            Date aDate = sf.parse( aDateString );    
                                
                            GregorianCalendar c = new GregorianCalendar();
                            c.setTime( aDate );
                            c.add( Calendar.HOUR, 1 );
                            
                            Date now = new Date();
                            
                            System.out.println("Perf to be shown? " + (now.before( c.getTime() ) || flagShowOld) + "," + now.before( c.getTime() ) + " or " + flagShowOld );
                            
                            if( aRoom != null && aShow != null && aDate != null && ( now.before( c.getTime() )  || flagShowOld ) ){
                                
                                Performance aP = new Performance( anId, this );
                                
                                aP.initPerformance( aDate , aRoom, aShow, getDataDirectory(), getBackupDirectory(), aNbTotalSeat );
                                
                                //if( aP != null ){
                                    /*aP.setDate( aDate );
                                
                                    aP.setRoom( aRoom );
                                    aP.setShow( aShow );
                                    aP.restoreNbTotalSeats( aNbTotalSeat );*/
                                
                                    performances.add( aP );
                                //}
                                
                            }
                        }
                        
                        
                    }
                }
            }

        
            in.close();
            
            replayBookingJournals();
        }
    }
    
    
    public static BookingSystem load() throws IOException, ClassNotFoundException, SystemAlreadyLoadedException, SystemLicenseException, ParseException, BackupAccessException {
        
        //System.out.println("BookingSytem.load() " + bF);
        
        // check if another system has already loaded that file (multiple systems running on the same file WILL make problems!!!
        /*if( new File( dataDirectory.getPath() + File.separator + "system.lock" ).exists() ){
            throw new SystemAlreadyLoadedException();
        }*/
        
        if( new File( "system.lock" ).exists() ){
            throw new SystemAlreadyLoadedException();
        }
        
        
        // load preferrences
        
        
        
        File aF = new File( "." + File.separator );
        BookingSystem aBS = new BookingSystem( aF, new File( "." + File.separator + "backup" + File.separator ) );
        
        aBS.loadPreferrences();
        System.out.println("*** Preferrences loaded");
        aBS.loadSetup( key.Preferrences.showOldPerformances() );
        System.out.println("*** Setup loaded");
        
        
        // load ticket image   
        key.Preferrences.loadTicketImage( getTicketImageFile() );
        

        // check license file
        aBS.setLicensePath( new File( "license.dat" ) );
        try{
            aBS.checkLicense();
        }catch( IOException e ){
            System.exit(1);
        }
        System.out.println("*** License checked");
    
        //new File( dataDirectory.getPath() + File.separator + "system.lock" ).createNewFile();
        new File( "system.lock" ).createNewFile();
        
        // set all shows unedited
        for( Performance p: aBS.getPerformances() ){
            p.setEditmode( false );
        }
        
        
        
        //aBS.replayBookingJournals();
        aBS.loadStatics();
        
        
        
        try{
            System.out.println("*** Checking backup integrity");
            aBS.checkBackupIntegrity();
            System.out.println("*** Backup OK");
        }catch( PerformancesBackupErrorException e ){
            System.out.println("*** Backup corrupted, trying to copy");
            try{
                aBS.makeFullBackup();
            }catch( java.io.IOException e2 ){
                throw new BackupAccessException( aBS );
            }
        } 


        
        
        // check backup integrity;

        
        
        
        //File aF = new File( dataDirectory.getPath() + File.separator + "system.data");
        //File aF = new File( "system.data");
        
        
        //if( aF.exists() ){
        //System.out.println( "LOADING " + dataDirectory.getPath() + File.separator + "system.data" );
        /*System.out.println( "LOADING system.data" );
                   
        ObjectInputStream in = new ObjectInputStream( new FileInputStream( aF ) );   
            aBS = (BookingSystem) in.readObject();
            Performance.setIdCounter( in.readLong() );
            Ticket.setIdCounter( in.readLong() );
            Contact.setIdCounter( in.readLong() );
            Show.setIdCounter( in.readLong() );
            Booking.setIdCounter( in.readLong() );
            Price.setIdCounter( in.readLong() );
        in.close();*/
        
        
        
        
        return( aBS );
    }
    
    private static File getTicketImageFile(){
        return( new File( key.Preferrences.getDataDirectory(), "defaultTicket.jpg") );
    }
    
    private void checkBackupIntegrity() throws PerformancesBackupErrorException{
        // if backup and standard file are not identical, mark performance as readOnly
              
        
        File b;
        File o;
        
        b = new File( getBackupDirectory(), "bouboutel.setup" ); 
        o = new File( getDataDirectory(), "bouboutel.setup" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "bouboutel.prefs" ); 
        o = new File( getDataDirectory(), "bouboutel.prefs" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "defaultTicket.jpg" ); 
        o = new File( getDataDirectory(), "defaultTicket.jpg" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "license.dat" ); 
        o = new File( getDataDirectory(), "license.dat" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "certif.dat" ); 
        o = new File( getDataDirectory(), "certif.dat" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "header.jpg" ); 
        o = new File( getDataDirectory(), "header.jpg" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        b = new File( getBackupDirectory(), "bouboutel.jar" ); 
        o = new File( getDataDirectory(), "bouboutel.jar" ); 
        if( !b.exists() && o.exists() ){
            try{
                key.FileUtil.copy( o, b );
            }catch( IOException e ){
                
            }
        }
        
        
        
        ArrayList<Performance> readOnlyPerformances = new ArrayList<Performance>();
        
        for( Performance p:performances){
            try{
                
                // check if backup file exists, if not, make it
                if( p.getBackupFile().exists() ){
                
                
                    // get CRC for original file
                    java.util.zip.CheckedInputStream or = new java.util.zip.CheckedInputStream( new FileInputStream( p.getDataFile()), new java.util.zip.CRC32() );
                    byte[] buf = new byte[128];
                    while(or.read(buf) >= 0) {
                    }

                    long orChecksum = or.getChecksum().getValue();
                
                
                    // get CRC for backup file
                
                    java.util.zip.CheckedInputStream back = new java.util.zip.CheckedInputStream( new FileInputStream( p.getBackupFile()), new java.util.zip.CRC32() );
                    byte[] buf2 = new byte[128];
                    while(back.read(buf2) >= 0) {
                    }

                    long backChecksum = back.getChecksum().getValue();
                                
            
                    // compare both crc
                
                    if( orChecksum == backChecksum ){
                        p.setReadOnly( false );
                    }else{
                        readOnlyPerformances.add( p );
                        p.setReadOnly( true );
                    }
                }else{
                    key.FileUtil.copy( p.getDataFile(), p.getBackupFile() );
                }
                
            }catch( java.io.IOException e ){
                readOnlyPerformances.add( p );
                p.setReadOnly( true );
            }
        }
        if( readOnlyPerformances.size() > 0 ){
            throw new PerformancesBackupErrorException( this, readOnlyPerformances );
        }
        
    }
    
    private void replayBookingJournals(){
        
        for( Performance performance : getPerformances() ){
            
            // load bookings and tickets
            try{
                performance.loadBookingsTicketsInMemory();
                performance.cancelAllPrebookings();        
            }catch( IOException e ){
                
            }
                    
            // update seatcounts (now included in loadBookingsTicketsInMemory()
            //performance.recomputeSeats();
            
            //System.out.println("----------------------------- " + performance.getNbTotalSeats() + " " + performance.getNbBookedSeats() + " " + performance.getNbSoldSeats() );
            
            performance.clearBookingsFromMemory();
            performance.clearTicketsFromMemory();
            performance.cancelAllPrebookings();  
        }

            
        performances.fireTableDataChanged();

    }
    
    
    private void loadStatics(){
        // restore static ticket counter     
        System.out.println("*******************************RELOADING STATICS****************************");
        File staticsFile = new File( "bouboutel.statics" );
        
        if( staticsFile.exists() ){
            try{
                //BufferedReader in = new BufferedReader( new FileReader( staticsFile ) );
                
                BufferedReader in;
                try{
                    in = new BufferedReader( new InputStreamReader(new FileInputStream( staticsFile.getPath() ), "UTF-8"));
                }catch( java.io.UnsupportedEncodingException ee ){
                    in = new BufferedReader( new FileReader( staticsFile ) );
                }
                String s;
                try{
                    // performance counter
                    s = in.readLine();
                    Performance.setIdCounter( Long.parseLong(s) );
                    System.out.println( Performance.getIdCounter() );
            
                    // ticket counter
                    s = in.readLine();
                    Ticket.setIdCounter( Long.parseLong(s) );
                    System.out.println( Ticket.getIdCounter() );
 
                    // contact counter
                    s = in.readLine();
                    Contact.setIdCounter( Long.parseLong(s) );
                    System.out.println( Contact.getIdCounter() );
            
                    // show counter
                    s = in.readLine();
                    Show.setIdCounter( Long.parseLong(s) );
                    System.out.println( Show.getIdCounter() );
            
                    // booking counter
                    s = in.readLine();
                    Booking.setIdCounter( Long.parseLong(s) );
                    System.out.println( Booking.getIdCounter() );
            
                    // price counter
                    s = in.readLine();
                    Price.setIdCounter( Long.parseLong(s) );
                    System.out.println( Price.getIdCounter() );
                    
                    // room counter
                    s = in.readLine();
                    Room.setIdCounter( Long.parseLong(s) );
                    System.out.println( Room.getIdCounter() );
                
                    in.close(); 
                    
                }catch( IOException e1 ){
                    System.out.println( e1 );
                }

                               
            }catch( java.io.FileNotFoundException e ){
                System.out.println( e );
            }
        }
    }
    
    
    
    
    
    private void loadPreferrences() throws IOException{
        // restore static ticket counter     
        System.out.println("RELOADING PREFERRENCES");
        File prefsFile = new File( "bouboutel.prefs" );
        
        if( prefsFile.exists() ){
            try{
                //BufferedReader in = new BufferedReader( new FileReader( staticsFile ) );
                
                BufferedReader in;
                try{
                    in = new BufferedReader( new InputStreamReader(new FileInputStream( prefsFile.getPath() ), "UTF-8"));
                }catch( java.io.UnsupportedEncodingException ee ){
                    in = new BufferedReader( new FileReader( prefsFile ) );
                }
                String s;
                
                    // data directory
                    s = in.readLine();
            
                    // backup directory
                    s = in.readLine();
                    key.Preferrences.setBackupDirectory( new File( s ) );
                    
                    // language
                    s = in.readLine();
                    key.Preferrences.setLocale( new Locale( s ) );
                    
                    
                    // normal exit?
                    // if false, make a manual backup
                    s = in.readLine();
                    if( s.trim().equals( "false" ) ){
                        try{
                            makeFullBackup();
                        }catch( IOException e2 ){
                            
                        }
                    }
                    
                    // print tickets background?
                    try{
                        s = in.readLine();
                        if( s != null ){
                            if( s.trim().equals( "false" ) ){
                                key.Preferrences.setPrintTicketBackground( false );
                            }else{
                                key.Preferrences.setPrintTicketBackground( true );
                            }
                        }else{
                            key.Preferrences.setPrintTicketBackground( true );
                        }
                    }catch( IOException e2 ){
                        key.Preferrences.setPrintTicketBackground( true );
                    }
                        
                   
                    in.close(); 
                               
            }catch( java.io.FileNotFoundException e ){
                System.out.println( e );
            }
        }
    }
    
    
    public void makeFullBackup() throws java.io.IOException {
            if( new File( getDataDirectory(), "bouboutel.setup" ).exists() ){
                key.FileUtil.copy( new File( getDataDirectory(), "bouboutel.setup" ), new File( getBackupDirectory(), "bouboutel.setup" ) );
            }
            if( new File( getDataDirectory(), "bouboutel.statics" ).exists() ){
                key.FileUtil.copy( new File( getDataDirectory(), "bouboutel.statics" ), new File( getBackupDirectory(), "bouboutel.statics" ) );
            }
            if( new File( getDataDirectory(), "bouboutel.prefs" ).exists() ){
                key.FileUtil.copy( new File( getDataDirectory(), "bouboutel.prefs" ), new File( getBackupDirectory(), "bouboutel.prefs" ) );
            }
            key.FileUtil.copy( new File( getDataDirectory(), "certif.dat" ), new File( getBackupDirectory(), "certif.dat" ) );
            key.FileUtil.copy( new File( getDataDirectory(), "license.dat" ), new File( getBackupDirectory(), "license.dat" ) );
            key.FileUtil.copy( new File( getDataDirectory(), "defaultTicket.jpg" ), new File( getBackupDirectory(), "defaultTicket.jpg" ) );
            if( new File( getDataDirectory(), "header.jpg" ).exists() ){
                key.FileUtil.copy( new File( getDataDirectory(), "header.jpg" ), new File( getBackupDirectory(), "header.jpg" ) );
            }
            if( new File( getDataDirectory(), "bouboutel.jar" ).exists() ){
                key.FileUtil.copy( new File( getDataDirectory(), "bouboutel.jar" ), new File( getBackupDirectory(), "bouboutel.jar" ) );
            }else{
                if( new File( getDataDirectory(), "dist"+File.separator+"bouboutel.jar" ).exists() ){
                    key.FileUtil.copy( new File( getDataDirectory(), "dist"+File.separator+"bouboutel.jar" ), new File( getBackupDirectory(), "bouboutel.jar" ) );
                }else{
                    if( new File( getDataDirectory(), "dist"+File.separator+"myticket.jar" ).exists() ){
                        key.FileUtil.copy( new File( getDataDirectory(), "dist"+File.separator+"myticket.jar" ), new File( getBackupDirectory(), "bouboutel.jar" ) );
                    }
                }
            }
            for( Performance p:getPerformances() ){
                String a = Long.toString( p.getId() );
                key.FileUtil.copy( new File( getDataDirectory(), a + ".bookings" ), new File( getBackupDirectory(), a + ".bookings" ) );
            }
    }
 
    
    // EMAIL RELATED FUNCTIONS
    
    public void setEmailOutgoing( String aHostname, int aPort ){
        emailOutgoingHostname = aHostname;
        emailOutgoingPort = aPort;
    }
    
    public String getEmailOutgoingHostname(){
        return emailOutgoingHostname;
    }
    public int getEmailOutgoingPort(){
        return emailOutgoingPort;
    }
    public void setEmail( String aEmail ){
        email = aEmail;
    }
    public String getEmail(){
        return email;
    }
    public String getName(){
        return name;
    }
    public void setName( String aName ){
        name = aName;
    }
    
    public Locale getLocale(){
        return Preferrences.getLocale();
    }
    
    /*
    public void setLocale( Locale l ){
        locale = l;
    }*/
    
    
    public long getNbHandledSeats(){
        long t = 0;
        for( Performance i:performances ){
            System.out.println( i.getNbTotalSeats() - i.getNbFreeSeats() );
            t += i.getNbTotalSeats() - i.getNbFreeSeats();
        }
        return( t );
    }
    
    public long getNbSoldSeats(){
        long t = 0;
        for( Performance i:performances ){
            System.out.println( i.getNbTotalSeats() - i.getNbFreeSeats() );
            t += i.getNbSoldSeats();
        }
        return( t );
    }
    
    public long getNbBookedSeats(){
        long t = 0;
        for( Performance i:performances ){
            System.out.println( i.getNbTotalSeats() - i.getNbFreeSeats() );
            t += i.getNbBookedSeats();
        }
        return( t );
    }
    
    public double getTotalIncome(){
        double t = 0;
        for( Performance i:performances ){
            System.out.println( i.getNbTotalSeats() - i.getNbFreeSeats() );
            t += i.getTotalIncome();
        }
        return( t );
    }
    
    public String getStatString(){
        
        String stats = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbSoldSeats" ) + ": ";
        stats += getNbSoldSeats();
        stats += " = ";
        stats += getTotalIncome();
        stats += "  /  " + java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbBookedSeats" ) + ": ";
        stats += getNbBookedSeats();
        stats += "  /  " + java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("labelNbHandledSeats" ) + ": ";
        stats += getNbHandledSeats();
        
        return stats;
    }
    
    
    //private ArrayList<Performance> performances = new ArrayList<Performance>();
    //private Performances performances = new Performances();
    //private ArrayList<Show> shows = new ArrayList<Show>();
    //private Shows shows = new Shows();
    //private ArrayList<Room> rooms = new ArrayList<Room>();
    //private HashMap<Long, Client> clients = new HashMap<Long, Client>();
    
    private ShowableArrayList<Performance> performances = new ShowableArrayList<Performance>();
    private ShowableArrayList<Room> rooms = new ShowableArrayList<Room>();
    private ShowableArrayList<Show> shows = new ShowableArrayList<Show>();
    private ShowableArrayList<Performer> performers = new ShowableArrayList<Performer>();
    
    private Client lastClient = new Client();
    //private File dataDirectory = null;
    private File licencePath = null;
    private Date licenceLimit = null;
    
    // for e-mail
    private String emailOutgoingHostname = "smtp.net2000.ch";
    private int emailOutgoingPort = 25;
    private String email = "Jean-Luc.Perret@net2000.ch";
    private String name = "Test Booking System";
    
    
    // My licence public key:
    private String myPublicKey = "";
    
    
    
    
    
    //private Locale locale = Locale.FRENCH;
    
    //private Ticket sampleTicket = new Ticket( new Price("",0.0,"") );
    
    
    /*public void loadTest(){
        Performer p1 = new Performer( "Compagnie machin", "Pays machin", "Ville machin", "Rue machin", "tel machin", "email@machin.com" );
        addPerformer( p1 );
        Performer p2 = new Performer( "Compagnie machin 2", "Pays machin", "Ville machin 2", "Rue machin 2", "tel machin 2", "email@machin2.com" );
        addPerformer( p2 );
        
        Show s1 = new Show( p1, "Mon Super Show", "test" );
        addShow( s1 );
        
        Show s2 = new Show( p1, "Mon Super Show 2", "test");
        addShow( s2 );
        
        addShow( new Show( p1, "Mon Super Show 3", "test" ));
        addShow( new Show( p1, "Mon Super Show 4", "test" ));
        addShow( new Show( p1, "Mon Super Show 5", "test" ));
        addShow( new Show( p1, "Mon Super Show 6", "test" ));
        addShow( new Show( p1, "Mon Super Show 7", "test" ));
        addShow( new Show( p1, "Mon Super Show 8", "test" ));
        addShow( new Show( p1, "Mon Super Show 9", "test" ));
        addShow( new Show( p1, "Mon Super Show 10", "test" ));
        addShow( new Show( p1, "Mon Super Show 11", "test" ));
        addShow( new Show( p1, "Mon Super Show 12", "test" ));
        
        Company theatre = new Company( "Theatre bidon", "Pays bidon", "Ville bidon", "Rue bidon", "tel bidon", "email@bidon.com" );
        Room r1 = new Room( theatre, "grande salle par defaut" );
        addRoom( r1 );
        
        
        Performance pp1 = new Performance( 10, 10, 2005, 20, 30, r1, s1, new File( "/home/sdperret"), 100, this );
        addPerformance( pp1 );
        
        
        
    }*/
    
    
    
    public static void main( String[] args ){
        
        /*BookingSystem r = new BookingSystem( new File( "/home/sdperret" ) );
        r.loadTest();*/

    }
    
    
    public void checkLicense() throws SystemLicenseException, IOException{
        
        //System.out.println("Checking license");
        
        File licenseFile = getLicensePath();
        File myCertifFile = new File("certif.dat");
        
        
        key.License license = null;
        try{
            license = key.License.load( licenseFile );
        }catch( Exception e ){
            throw new SystemLicenseException( e.toString() );
        }
        // check if signature is valid
        try{
            
            //System.out.println("  Checking signature");
            license.checkSignature( myCertifFile );

            // check if program name is valid
            //System.out.println("  Checking program name");
            if( !license.getProgramName().equals( "bouboutel" ) ){
                throw new SystemLicenseException( "this license is only valid for bouboutel (found " + license.getProgramName() + ")" );
            }
        
            //System.out.println("  Checking version");
            if( license.getVersion() < getVersion() ){
                throw new SystemLicenseException( "this license is only valid for Bouboutel up to version version " + license.getVersion() );
            }
        
            //System.out.println("  Checking date");
            // check if date is valid
            if( license.getDate().getTime() < new Date().getTime() ){
                throw new SystemLicenseException( "license expired on " + license.getDate() );
            }
            
            // set system name
            setName( license.getName() );
            
            //System.out.println("license ok");
            
            Preferrences.setLicense( license );
            
        }catch( Exception e ){
            throw new SystemLicenseException( e.toString() );
        }
       
    }
    
    public boolean signatureOK( String testSignature ){
        return( false );
    }

    public void clear() {
        performances.clear();
        rooms.clear();
        shows.clear();
        performers.clear();
    }
    
}
