/*
 * Ticket.java
 *
 * Created on October 2, 2005, 1:30 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;

import javax.swing.*;
import java.awt.print.*;

import javax.print.attribute.standard.*;


import java.util.*;
import java.text.*;


import com.java4less.textprinter.*;
import com.java4less.textprinter.ports.*;
import com.java4less.textprinter.printers.*;



/**
 *
 * @author sdperret
 */
public class Ticket extends AbstractColumnShowable implements Printable {
    
    public static final long serialVersionUID = -332785433;
    
    /** Creates a new instance of Ticket */
    public Ticket( Price aPrice, Performance aPerformance ) {
        price = aPrice;
        performance = aPerformance;
        id = idCounter;
        idCounter++;
        nb = 1;
    }
    
    public Ticket( Price aPrice, long anId, Performance aPerformance ) {
        System.out.println("creating ticket with price: " + aPrice);
            
        
        price = aPrice;
        performance = aPerformance;
        id = anId;
        nb = 1;
    }
    
    public Ticket(){
        
    }
    
    public void printTicket( int aNbCopy ) throws PrinterException {
        System.out.println( "PRINTING TICKET " + getId() );
        
        PrinterJob printJob = PrinterJob.getPrinterJob();
       
        //Paper paper = new Paper();
        
        double height = 595; //05.5*0.397*72; //300;//595;//15*0.397*72;
        double width = 2000; //15*0.397*72; //842;//5.6*0.397*72;
      
        //paper.setSize( 595.28, 844.72 ); // petites cartes
       
        //ticket format
        //paper.setImageableArea(20, 20, 399, 274);
        
        // A4
        //paper.setImageableArea( 24, 24, 595.28, 824.72 );        
        //paper.setImageableArea( 17, 17, (width - 34), (height - 34) );
                  
        
        
        
        //PageFormat page = new PageFormat();
        PageFormat page = printJob.defaultPage();
        Paper paper = page.getPaper();
        
        System.out.println("PAPER WIDTH " + paper.getWidth() );
        System.out.println("PAPER HEIGHT " + paper.getHeight() );
        System.out.println("PAPER IMAGEABLEWIDTH " + paper.getImageableWidth() );
        System.out.println("PAPER IMAGEABLEHEIGHT " + paper.getImageableHeight() );
        System.out.println("PAPER IMAGEABLEX " + paper.getImageableX() );
        System.out.println("PAPER IMAGEABLEY " + paper.getImageableY() );
        
        paper.setSize( width, height );
        paper.setImageableArea( 0, 0, width, height  );
        System.out.println("---------------");
        System.out.println("PAPER WIDTH " + paper.getWidth() );
        System.out.println("PAPER HEIGHT " + paper.getHeight() );
        System.out.println("PAPER IMAGEABLEWIDTH " + paper.getImageableWidth() );
        System.out.println("PAPER IMAGEABLEHEIGHT " + paper.getImageableHeight() );
        System.out.println("PAPER IMAGEABLEX " + paper.getImageableX() );
        System.out.println("PAPER IMAGEABLEY " + paper.getImageableY() );
        
        
        page.setPaper( paper );  
        //page.setOrientation( PageFormat.LANDSCAPE );
        page.setOrientation( PageFormat.PORTRAIT );
               
        printJob.setPrintable( this, page );
        printJob.setCopies( aNbCopy );
                
        System.out.println("Ticket::printTicket, lauch print()");
                
        printJob.printDialog();
        
        //page = printJob.defaultPage();
                
        System.out.println( page.getWidth() );
        System.out.println( page.getHeight() );
        System.out.println( page.getImageableWidth() );
        System.out.println( page.getImageableHeight() );
        System.out.println( page.getImageableX() );
        System.out.println( page.getImageableY() );
        System.out.println( page.getOrientation() );
        
        printJob.print();

    }
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        
        
        System.out.println("Ticket::print()");
        
        final int NO_SUCH_PAGE = 1;
        final int PAGE_EXISTS = 0;
        
        if (pageIndex > 0) {
          return(NO_SUCH_PAGE);
        }else{
            double height = 5.5*0.397*72; //783; //pageFormat.getImageableWidth();
            double width = 15*0.397*72; //559; //pageFormat.getImageableHeight();
            
            System.out.println("----- width : " + width );
            System.out.println("----- height: " + height );
            
            Graphics2D g2d = (Graphics2D) g;
            
            //g2d.rotate( -1.57, width/2, height/2 );
            g2d.translate(pageFormat.getImageableX()-200, pageFormat.getImageableY());
            g2d.scale( 1.5, 1.5 );
                 
            //g2d.scale( width/pageFormat.getImageableWidth(), height/pageFormat.getImageableHeight() );

            
            // BONNES VALEURS petites cartes:
            //int width = 294-34;
            //int height = 419-34;
            
            //g2d.translate( pageFormat.getImageableX() + pageFormat.getImageableWidth() - height + 8 , pageFormat.getImageableY() + pageFormat.getImageableHeight() / 2 - ( width /2 ) -7 );
            //g2d.scale( (height-17) / pageFormat.getImageableWidth(), width / pageFormat.getImageableHeight() );
            //g2d.scale( 0.5, 0.5);
                        
            int titleFS = 20;
            int performerFS = 12;
            int commentFS = 12;
            int priceFS = 12;
            
            int top = 0;//120; // 120
            int lineSpace = 1;  //10
            int leftMargin = 0;  //15 
            
       
            //g2d.translate( 50,100 );
            //g2d.translate( pageFormat.getImageableX() + pageFormat.getImageableWidth() - (419-34) + 8 , pageFormat.getImageableY() + pageFormat.getImageableHeight() / 2 - ( (294-34) /2 ) -7 );
            //g2d.scale( 0.2,0.2 );

            //g2d.translate( pageFormat.getImageableX() + pageFormat.getImageableWidth() - (419-34) + 8 , pageFormat.getImageableY() + pageFormat.getImageableHeight() / 2 - ( (294-34) /2 ) -7 );
            //g2d.scale( (419-34-17) / pageFormat.getImageableWidth(), (294-34) / pageFormat.getImageableHeight() );
            
                        
            Font priceFont = new Font("SansSerif", Font.BOLD, priceFS);
            Font titleFont = new Font("SansSerif", Font.BOLD, titleFS);
            Font performerFont = new Font("SansSerif", Font.ITALIC, performerFS);
            Font commentFont = new Font("SansSerif", Font.PLAIN, commentFS);
            
            // draw background image
            
            //try{
                System.out.println( "reading " + performance ); 
                System.out.println( "reading " + performance.getTicketImageFile() ); 
              
                if( key.Preferrences.printTicketBackground() ){
                    //BufferedImage ticketImage = key.Preferrences.getTicketImage(); //ImageIO.read( performance.getTicketImageFile() );
                    //g2d.drawImage( ticketImage, 0, 0, null );
                }                
                                
                // Turn off double buffering
                //disableDoubleBuffering(this);

            int cursor = top;
            
            cursor += 10;
            
            g2d.setFont( titleFont );
            cursor += titleFS + lineSpace;
            g2d.drawString( getPerformance().getShow().getTitle(), leftMargin, cursor );
            
            g2d.setFont( performerFont );
            cursor += performerFS + lineSpace;
            g2d.drawString( getPerformance().getShow().getPerformer().getName(), leftMargin, cursor  );
            
            
            g2d.setFont( titleFont );
            cursor += 0 + titleFS + lineSpace; // 15
            g2d.drawString( getPerformance().getRoom().getCompany().getName(), leftMargin, cursor );
            
            g2d.setFont( commentFont );
            cursor += commentFS + lineSpace;
            g2d.drawString( getPerformance().getRoom().getCompany().getStreet() + ", " + getPerformance().getRoom().getCompany().getTown(), leftMargin, cursor );
            
            g2d.setFont( titleFont );
            cursor += titleFS + lineSpace + 0;  //10
            g2d.drawString( getPerformance().getSimpleFormatedDate(), leftMargin, cursor );
            
            
            g2d.setFont( priceFont );
            cursor += priceFS + lineSpace + 0;  //15
            g2d.drawString( getPrice().getTitle(), leftMargin, cursor );
            
            g2d.setFont( commentFont );
            cursor += commentFS + lineSpace;
            g2d.drawString( getPrice().getValidityComment(), leftMargin, cursor );
            
            
            g2d.setFont( priceFont );
            cursor += priceFS + lineSpace + 0;  //15
            if( getNb() > 1 ){
                g2d.drawString( getNb() + " entres " , leftMargin, cursor );
            }else{
                g2d.drawString( getNb() + " entre " , leftMargin, cursor );
            }
            
            //g2d.setFont( new Font("SansSerif", Font.BOLD, 36) );
            cursor += priceFS + lineSpace;
            g2d.drawString( getPrice().getPrice() + " " + getPrice().getCurrency(), leftMargin + 200, cursor );
            
            //g2d.setFont( new Font("SansSerif", Font.PLAIN, 16) );
            cursor += priceFS + lineSpace;
                        
            
            g2d.drawString( "Billet no " + getId(), leftMargin , cursor );
            
            // safety line
            String safetyMessage = performance.getBookingSystem().getName();

            cursor += priceFS + lineSpace;
            g2d.drawString( safetyMessage + " / " + Integer.toString( getCode() ), leftMargin, cursor );
            
            //cursor += priceFS + lineSpace;
            //g2d.drawString( safetyMessage, leftMargin, cursor );
            
            //g2d.drawString( "Billetterie: Jean-Luc Perret", 600 , 580 );
                //enableDoubleBuffering(this);           
            
            System.out.println("Ticket::print() finished");
            
             
                       
            return(PAGE_EXISTS);
        }
      }
    
    
    public long getId(){
        return id;
    }
    
    public int getCode(){
        String a = getId() + performance.getBookingSystem().getName() + performance.toString();
        return ( a.hashCode() );
    }
    
    public static long getIdCounter(){
        return idCounter;
    }
    
    public static void setIdCounter( long a ){
        idCounter = a;
    }
    
    public Price getPrice(){
        return price;
    }
    
    public int getNb(){
        return nb;
    }
    
    public String toString(){
        return( "Ticket " + getId() + ": " + getPrice() );
    }
    
    public Performance getPerformance(){
        return( performance );
    }
    
    public void unsellTicket() throws IOException{
        System.out.println("now in Ticket:unsellTicket");
        performance.unsellTicket( this );
    }
    
    
    
    public Object getColumn( int aColumn ){
        Object rep = null;
        
        switch( aColumn ){
            
            case 0:
                    rep = getId();
                    break;
            case 1:
                    rep = getPrice().getTitle();
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    public int getColumnCount(){
        return 2; // set to 3 if the performance should be shown in table
    }
    
    public String getColumnName( int aColumn ){
        String rep = "";
        
        switch( aColumn ){
            
            case 0:
                    rep = "Id";
                    break;
            case 1:
                    rep = "Tarif";
                    break;
            default:
                    break;
            
        }
        
        return( rep );
    }
    
    
    public Class<?> getColumnClass( int aColumn ){
        
        //System.out.println("Now in Booking:getColumnClass");
        
        switch( aColumn ){
            case 0:
                return Integer.class;
            case 1:
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
    }
    
    
    public int compareTo( Object other ){
        if ( !( other instanceof Ticket ) )
            throw new ClassCastException("in Booking:compareTo: A Booking object expected.");
      
        if( this.getId() == ((Ticket) other).getId() ){
            return 0;
        }else if( this.getId() < ((Ticket) other).getId()){
            return -1;
        }else{
            return 1;
        }
    }
    
    
    public void journalSell( int aNb ) throws IOException{
        File dataFile = performance.getDataFile();
        
        if( !dataFile.exists() ){
            performance.createDataFile( aNb );
        }
    
        
        BackedUpBufferedWriter writer = null;
        
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( performance.getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( performance.getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( performance.getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( performance.getBackupFile().getPath(), true ) )
                    );
        }


        //FileWriter writer = new FileWriter( performance.getDataFile(), true ); 
        writer.write( "SELL" + "|" + getId() + "|" + sf.format( new Date() ) + "|" + getPrice().getTitle() + "|" + getPrice().getPrice() + "|" + (performance.getNbFreeSeats() ) + "\n" );
        writer.close();
    }
    
    public void journalUnsell( int aNb ) throws IOException{
        File dataFile = performance.getDataFile();
        
        if( !dataFile.exists() ){
            performance.createDataFile( aNb );
        }
        
        BackedUpBufferedWriter writer = null;
        
        try{
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( performance.getDataFile().getPath(), true ), "UTF-8"),
                    new OutputStreamWriter(new FileOutputStream( performance.getBackupFile().getPath(), true ), "UTF-8")
                    );
        }catch( java.io.UnsupportedEncodingException ee ){
            writer = new BackedUpBufferedWriter( 
                    new OutputStreamWriter(new FileOutputStream( performance.getDataFile().getPath(), true ) ),
                    new OutputStreamWriter(new FileOutputStream( performance.getBackupFile().getPath(), true ) )
                    );
        }
        
        //FileWriter writer = new FileWriter( dataFile, true ); 
        writer.write( "UNSELL" + "|" + getId() + "|" + sf.format( new Date() ) + "|" + (performance.getNbFreeSeats() + aNb ) + "\n" );
        writer.close();
    }
    
    
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
    
    
    
    
    private Price price;
    static long idCounter = 0;
    long id = 0;
    
    private Performance performance = null;
    
    private static boolean[] editable = { false, false, false, false };
    
    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM.yyyy 'at' HH:mm:ss z");
    
    private int nb = 0;
    
}
