/*
 * EmptyTicket.java
 *
 * Created on November 2, 2005, 12:05 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.awt.*;
import javax.swing.*;
import java.awt.print.*;

import java.awt.print.Printable;

import java.util.*;
import java.text.*;
import java.awt.image.*;

/**
 *
 * @author sdperret
 */
public class EmptyTicket extends Ticket{
    
    /** Creates a new instance of EmptyTicket */
    public EmptyTicket(){
        
    }
    
    
    
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        
        final int NO_SUCH_PAGE = 1;
        final int PAGE_EXISTS = 0;
        
        if (pageIndex > 0) {
            
          return(NO_SUCH_PAGE);
          
        }else{
            
            Graphics2D g2d = (Graphics2D) g;

            
        
            // BONNES VALEURS:
            g2d.translate( pageFormat.getImageableX() + pageFormat.getImageableWidth() - (419-34) + 8 , pageFormat.getImageableY() + pageFormat.getImageableHeight() / 2 - ( (294-34) /2 ) -7 );
            g2d.scale( (419-34-17) / pageFormat.getImageableWidth(), (294-34) / pageFormat.getImageableHeight() );
            
    
            int titleFS = 28;
            int performerFS = 20;
            int commentFS = 16;
            int priceFS = 20;
            

            
            // draw background image
            
            //try{

              
            BufferedImage ticketImage = key.Preferrences.getTicketImage(); //ImageIO.read( performance.getTicketImageFile() );
            g2d.drawImage( ticketImage, 0, 0, null );
                

            return(PAGE_EXISTS);
        }
      }
    
    
    
    
}
