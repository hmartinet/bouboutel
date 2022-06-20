/*
 * MainJFrame.java
 *
 * Created on September 13, 2005, 2:06 AM
 */

package myticket;

import javax.swing.*;
import javax.swing.table.*;
import java.io.*;

import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.util.Locale;
import key.Preferrences;

import java.text.*;
import javax.swing.event.*;


/*import java.awt.*;
import javax.imageio.*;
import java.io.*;
import java.awt.image.*;

import javax.swing.*;
import java.awt.print.*;

import java.awt.print.Printable;*/


/**
 *
 * @author  sdperret
 */
public class MainJFrame extends javax.swing.JFrame {
    
    /** Creates new form MainJFrame */
    public MainJFrame() {
        //myTicket.loadTest();
        //load();
            
        try{
            
            load();

            initComponents();
            
            
            TableModelListener toto = new TableModelListener( ){
                public void tableChanged( TableModelEvent e ){
                    jLabelStat.setText( myTicket.getStatString() );
                }
            };

            myTicket.getPerformances().addTableModelListener( toto );
            
            
            
            jLabelStat.setText( myTicket.getStatString() );
            
            jTableRepresentations.getColumnModel().getColumn(0).setPreferredWidth(300);
            jTableRepresentations.getColumnModel().getColumn(1).setPreferredWidth(20);
            jTableRepresentations.getColumnModel().getColumn(2).setPreferredWidth(250);
            jTableRepresentations.getColumnModel().getColumn(3).setPreferredWidth(250);
            
            jTableRepresentations.getColumnModel().getColumn(4).setPreferredWidth(60);
            jTableRepresentations.getColumnModel().getColumn(5).setPreferredWidth(50);
            jTableRepresentations.getColumnModel().getColumn(6).setPreferredWidth(50);
            jTableRepresentations.getColumnModel().getColumn(7).setPreferredWidth(50);
            jTableRepresentations.getColumnModel().getColumn(7).setPreferredWidth(30);
            //jTableRepresentations.getColumnModel().getColumn(7).setPreferredWidth(30);
            
            //jTableRepresentations.moveColumn(1,7);
            jTableRepresentations.setIntercellSpacing( new Dimension( 10, 1 ) );
            
            sorter.setColumnComparator( Integer.class, TableSorter.NUMERICAL_COMPARATOR );
            sorter.setColumnComparator( Long.class, TableSorter.NUMERICAL_COMPARATOR );
            sorter.setColumnComparator( Double.class, TableSorter.NUMERICAL_COMPARATOR );
            sorter.setColumnComparator( Integer.class, TableSorter.NUMERICAL_COMPARATOR );
            sorter.setColumnComparator( SimpleDateFormat.class, TableSorter.SIMPLEDATE_COMPARATOR );

            sorter.setSortingStatus( 0, TableSorter.ASCENDING);
            sorter.setSortingStatus( 2, TableSorter.ASCENDING);
            
            myTicket.addTableModelListener( this.jTableRepresentations );
            myTicket.getPerformances().addTableModelListener( this.jTableRepresentations );
            myTicket.getShows().addTableModelListener( this.jTableRepresentations );
            
            myTicket.getPerformances().fireTableDataChanged();
            
        
            System.out.println( "MYTICKET NOW HAS " + myTicket.getDataDirectory() + " as dataDirectory" );
            Preferrences.setLocale( Locale.FRENCH );
            System.out.println( "Locale = " + myTicket.getLocale());
            
            jTableRepresentations.setRowHeight( 23 );
        
            setExtendedState(MAXIMIZED_BOTH);
            
            try{
                BufferedImage headerImage = ImageIO.read( new File( "header.jpg" ) );
                jPanelHeader.add(new JLabel(new ImageIcon( headerImage )));
            }catch( IOException e ){
                System.out.println("Could not find header image " + e );
            }
            
            //update();
            System.out.println( "MYTICKET NOW HAS " + myTicket.getRowCount() + "Performances" );
            System.out.println( "MYTICKET NOW HAS " + myTicket.getDataDirectory() + " as dataDirectory" );
        
        }catch( SystemAlreadyLoadedException e ){
            //JOptionPane.showMessageDialog( null, "SYSTEM WAS ALREADY STARTED or STOPPED IMPROPERLY\n\nremove system.lock if you are sure there is no other instance \nof the program running on this computer", "Booking error", JOptionPane.OK_OPTION );
            
            // Check with network socket if another process is running
            
            // if yes: warn user to check task bar. if no same instance, wait 2 minutes and retry
            
            //String message = java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message1" );
            
            /*String message = "Warning another instance of the booking system seems to be running on your system\n";
            message += "please check the taskbar.\n\n";
            message += "If no other instance of the program is running the system was probably not stopped correctly\n";
            message += "please delete file system.lock in the installation directory.";
            JOptionPane.showMessageDialog( null, message, "Sytem startup error", JOptionPane.OK_OPTION );*/
            
            
            JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("message1"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("errorSystemStartupM"), 
                            JOptionPane.OK_OPTION );
            
            
            
            
            
            // if no: delete lock file, instanctiate the BookingSystem and replay all journals!       
            //JOptionPane.showMessageDialog( null, message, "Booking error", Pane.OK_OPTION );
             
            System.exit(1);
        }
    }
   
    //BookingSystem myTicket = new BookingSystem( new File( "/home/sdperret" ) );
    BookingSystem myTicket = null; //new BookingSystem( new File( "." + File.separator ) );
   
    public void update(){
        myTicket.getPerformances().fireTableDataChanged();
        myTicket.getShows().fireTableDataChanged();
        jLabelStat.setText( myTicket.getStatString() );
        jTableRepresentations.updateUI();
    }
    
    public void load() throws SystemAlreadyLoadedException{
        
        

        
        try{
            try{ 
                System.out.println("trying to load System.data");
                
                try{
                    try{
                        //myTicket = BookingSystem.load( new File( "." + File.separator ) );
                        try{
                            myTicket = BookingSystem.load();
                        }catch( BackupAccessException e5 ){
                            JOptionPane.showMessageDialog( null, "Acces au backup impossible, passage en mode lecture seule" , "System startup error", JOptionPane.OK_OPTION );
                            myTicket = e5.getBookingSystem();
                        }
                    }catch( java.text.ParseException e4 ){
                        JOptionPane.showMessageDialog( null, e4 , "System startup error", JOptionPane.OK_OPTION );
                        System.exit(1);
                    }
                }catch( SystemLicenseException e3 ){
                    JOptionPane.showMessageDialog( null, e3 , "Licence error", JOptionPane.OK_OPTION );
                    System.exit(1);
                }
            }catch( ClassNotFoundException e2){
                System.out.println("could not read System.data ("+ e2 +")");
                
                JOptionPane.showMessageDialog( null, 
                        "could not read System.data ("+ e2 +")" + "\n\n",
                        "system startup error",
                        JOptionPane.OK_OPTION );
                
                //JOptionPane.showMessageDialog( null, "Could not read System.data (ClassNotFound)", "System startup error", JOptionPane.OK_OPTION );
                System.exit(1);
                //myTicket = new BookingSystem( new File( "." + File.separator ) );
            }
        }catch( IOException e ){
            System.out.println("could not find System.data ("+ e +"), creating new system");
            
            JOptionPane.showMessageDialog( null, 
                        e + "\n\nCreating new system.data",
                        "system startup error",
                        JOptionPane.OK_OPTION );
            
            /*JOptionPane.showMessageDialog( null, java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket" ).getString("message3" ) + "\n\n" + e,
                    java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorSystemStartupM"),
                    JOptionPane.OK_OPTION );*/
            
            //JOptionPane.showMessageDialog( null, "Could not find System.data (IOException), creating a new one", "System startup error", JOptionPane.OK_OPTION );
            myTicket = new BookingSystem( new File( "bouboutel.data" ), new File( "backup" + File.separator ) );
            //System.exit(1);
        }
        
        /*if( myTicket == null){
            myTicket = new BookingSystem( new File( "." + File.separator ) );
        }*/
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jPanelHeader = new javax.swing.JPanel();
        jPanelRepresentations = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRepresentations = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonBook = new javax.swing.JButton();
        jButtonBookings = new javax.swing.JButton();
        jButtonPerson = new javax.swing.JButton();
        jButtonUpdateFreeSeats = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonExportStatistics = new javax.swing.JButton();
        jButtonPrintEmptyTicket = new javax.swing.JButton();
        jButtonSetup = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabelStat = new javax.swing.JLabel();

        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("MyTicket");
        setBackground(new java.awt.Color(191, 23, 29));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelHeader.setBackground(new java.awt.Color(191, 23, 29));
        getContentPane().add(jPanelHeader);

        jPanelRepresentations.setLayout(new java.awt.BorderLayout());

        jPanelRepresentations.setBackground(new java.awt.Color(191, 23, 29));
        jPanelRepresentations.setBorder(new javax.swing.border.TitledBorder(null, java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("labelPerformancesM" ), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14), new java.awt.Color(204, 204, 204)));
        jScrollPane1.setBackground(new java.awt.Color(153, 153, 153));
        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        jTableRepresentations.setFont(new java.awt.Font("Dialog", 0, 18));
        sorter = new TableSorter( myTicket ); //ADDED THIS
        //JTable table = new JTable(new MyTableModel());          //OLD
        sorter.setTableHeader(jTableRepresentations.getTableHeader()); //ADDED THIS

        jTableRepresentations.setModel(sorter);
        jTableRepresentations.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        jTableRepresentations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableRepresentationsMouseClicked(evt);
            }
        });

        jScrollPane1.setViewportView(jTableRepresentations);

        jPanelRepresentations.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jToolBar1.setName("Shows");
        jButtonBook.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonBookSeats" ));
        jButtonBook.setToolTipText("book seats for a show");
        jButtonBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBookActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonBook);

        jButtonBookings.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonViewBookings" ));
        jButtonBookings.setToolTipText("bookings for one show");
        jButtonBookings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBookingsActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonBookings);

        jButtonPerson.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonViewBookingsPerson" ));
        jButtonPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPersonActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonPerson);

        jButtonUpdateFreeSeats.setText("u");
        jButtonUpdateFreeSeats.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUpdateFreeSeatsActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonUpdateFreeSeats);

        jSeparator1.setBackground(new java.awt.Color(204, 204, 204));
        jSeparator1.setForeground(new java.awt.Color(204, 204, 204));
        jSeparator1.setPreferredSize(new java.awt.Dimension(0, 0));
        jToolBar1.add(jSeparator1);

        jButtonExportStatistics.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonExportStatistics" ));
        jButtonExportStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportStatisticsActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonExportStatistics);

        jButtonPrintEmptyTicket.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonPrintEmptyTicketM" ));
        jButtonPrintEmptyTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintEmptyTicketActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonPrintEmptyTicket);

        jButtonSetup.setText(java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("buttonSetup" ));
        jButtonSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetupActionPerformed(evt);
            }
        });

        jToolBar1.add(jButtonSetup);

        jPanelRepresentations.add(jToolBar1, java.awt.BorderLayout.NORTH);

        getContentPane().add(jPanelRepresentations);

        jLabelStat.setFont(new java.awt.Font("Dialog", 0, 12));
        jLabelStat.setText("jLabelStat");
        jPanel1.add(jLabelStat);

        getContentPane().add(jPanel1);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents

    private void jButtonExportStatisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportStatisticsActionPerformed
// TODO add your handling code here:
        try{
            myTicket.exportStatistics();
        }catch( IOException e ){
            
        }
    }//GEN-LAST:event_jButtonExportStatisticsActionPerformed

    private void jButtonPrintEmptyTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintEmptyTicketActionPerformed
// TODO add your handling code here:
            EmptyTicket t = new EmptyTicket();
            try{
                t.printTicket( 1 );
            }catch( Exception e ){
                
            }
    }//GEN-LAST:event_jButtonPrintEmptyTicketActionPerformed

    private void jButtonPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPersonActionPerformed
// TODO add your handling code here:
        JDialogEditPersons toto = new JDialogEditPersons( myTicket, this );
        toto.setVisible( true );
    }//GEN-LAST:event_jButtonPersonActionPerformed

    private void jButtonBookingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBookingsActionPerformed
// TODO add your handling code here:
        
        int sortedSelected = jTableRepresentations.getSelectedRow();
        if( sortedSelected > -1 ){
       
            int selected = sorter.modelIndex( sortedSelected ); 
            
        
            if( selected > -1 ){
                Performance p = myTicket.getPerformances().get( selected );
           
                //int selected = jTableRepresentations.getSelectedRow();
        
                if( p.isBeingEdited() ){
                    // performance in edit mode
                    JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message4"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                            JOptionPane.OK_OPTION );
                    
                    //JOptionPane.showMessageDialog( null, "This performance is actually being edited and does not accept bookings currently", "Booking error", JOptionPane.OK_OPTION );
                }else{
                    try{
                        JDialogEditBookings toto = new JDialogEditBookings( p, myTicket, this );
                        toto.setVisible( true );
                    }catch( IOException e ){
                        try{
                            p.createDataFile( 0 );
                            JDialogEditBookings toto = new JDialogEditBookings( p, myTicket, this );
                            toto.setVisible( true );
                        }catch( IOException e2 ){
                            // performance full
                            JOptionPane.showMessageDialog( null, 
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message5"),
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                                JOptionPane.OK_OPTION );
                            
                            //JOptionPane.showMessageDialog( null, "This performance is full", "Booking error", JOptionPane.OK_OPTION );    
                        }
                    }
                }
            }
        }else{
                // select performance first
                JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message6"),
                            "", 
                            JOptionPane.OK_OPTION );
                //JOptionPane.showMessageDialog( null, "Please select a performance first", "", JOptionPane.OK_OPTION );
        }
        
        
    }//GEN-LAST:event_jButtonBookingsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
// TODO add your handling code here:
        //try{
            //myTicket.saveAndUnlock();
            
            try{
                myTicket.cancelAllPrebookings();
                
                System.out.println("saving statics");
                myTicket.saveStatics();
                System.out.println("saving prefs");
                myTicket.savePreferrences();
                System.out.println("unlocking");
                myTicket.unlock();
                System.exit(0);  //dispose();
                
            }catch( IOException e ){
                System.out.println("GOT EXIT ERROR");
                
                
                JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("messageCouldNotWrite") + "\n\n"+e,
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", key.Preferrences.getLocale() ).getString("errorSystem"), 
                            JOptionPane.OK_OPTION );
            }
            
            
        /*}catch( IOException e ){
            System.out.println("COULD NOT SAVE SYSTEM.DATA");
            
            JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message7MAJ"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorSystem"), 
                            JOptionPane.OK_OPTION );
            
            JOptionPane.showMessageDialog( null, "COULD NOT SAVE SYSTEM.DATA", "System error", JOptionPane.OK_OPTION );
        }*/
    }//GEN-LAST:event_formWindowClosing

    private void jTableRepresentationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableRepresentationsMouseClicked
// TODO add your handling code here:
        
        if ( evt.getClickCount() == 2){
            book();
        }else{
      
        
         /*   //System.out.println( jTableRepresentations.getSelectedRowCount() );
            if( jTableRepresentations.getSelectedRowCount() > 0 ){
                jButtonBook.setEnabled( true );
                jButtonBookings.setEnabled( true );
            }else{
                jButtonBook.setEnabled( false );
                jButtonBookings.setEnabled( false );
            }*/
        }
    }//GEN-LAST:event_jTableRepresentationsMouseClicked

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
// TODO add your handling code here:
       
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void jButtonUpdateFreeSeatsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUpdateFreeSeatsActionPerformed
// TODO add your handling code here:
        update();
    }//GEN-LAST:event_jButtonUpdateFreeSeatsActionPerformed

    private void jButtonBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBookActionPerformed
// TODO add your handling code here:
        
        book();
        
    }//GEN-LAST:event_jButtonBookActionPerformed

    private void book(){
        int sortedSelected = jTableRepresentations.getSelectedRow();
        
        
        if( sortedSelected > -1 ){
        
            //Performance p = myTicket.getPerformances().get( selected );
            int selected = sorter.modelIndex( sortedSelected );

        
            if( selected > -1 ){
                Performance p = myTicket.getPerformances().get( selected );
                
                if( p.isReadOnly() ){
                    
                    // essayer de rĂŠacceder au backup
                    if( p.getBackupFile().canWrite() ){
                        
                        // check backup integrity
                        
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
                                    p.setReadOnly( true );
                                    
                                    java.awt.Toolkit.getDefaultToolkit().beep();
                                    JOptionPane.showMessageDialog( null, 
                                    java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("messageReadOnly"),
                                    java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                                    JOptionPane.OK_OPTION );
                                    return;
                                }
                            }else{
                                key.FileUtil.copy( p.getDataFile(), p.getBackupFile() );
                            }
                
                        }catch( java.io.IOException e ){
                            
                            p.setReadOnly( true );
                            
                            java.awt.Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("messageReadOnly"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                            JOptionPane.OK_OPTION );
                            return;
                        }
                        
                        
                    }else{
                    
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("messageReadOnly"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                            JOptionPane.OK_OPTION );
                        return;
                    }
                }
                
                
                //System.out.println( p + " now in editmode: " + p.isBeingEdited() );
                
                if( p.isBeingEdited() ){
                    // performance in edit mode
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message4"),
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                            JOptionPane.OK_OPTION );
                    //JOptionPane.showMessageDialog( null, "This performance is actually being edited and thus cannot accept bookings now", "Booking error", JOptionPane.OK_OPTION );
                }else{
          
                    if( p.getNbFreeSeats() > 0 ){ 
                        Booking aBooking = new Booking( myTicket.getPerformances().get(selected) );
                
                        try{
                            myTicket.addPreBooking( aBooking );
                            JDialogBooking toto = new JDialogBooking( this, aBooking, myTicket, true );
                            toto.setVisible( true );
                            toto.dispose();
                        }catch( PerformanceFullException ee ){
                            java.awt.Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog( null, 
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message5"),
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                                JOptionPane.OK_OPTION );
                            //JOptionPane.showMessageDialog( null, "This performance is full!", "Booking error", JOptionPane.OK_OPTION );
                        }
                
                    }else{
                        java.awt.Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog( null, 
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message5"),
                                java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("errorBookingM"), 
                                JOptionPane.OK_OPTION );
                        //JOptionPane.showMessageDialog( null, "This performance is full!", "Booking error", JOptionPane.OK_OPTION );
                    }
                }
            }
        }else{
            // select performance first
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog( null, 
                            java.util.ResourceBundle.getBundle( "myticket/BundleMyTicket", Preferrences.getLocale() ).getString("message6"),
                            "", 
                            JOptionPane.OK_OPTION );
            //JOptionPane.showMessageDialog( null, "Please select a performance first", "", JOptionPane.OK_OPTION );
        }
    }
    
    private void jButtonSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetupActionPerformed
// TODO add your handling code here:
        
        // ask password
        key.JDialogLogin login = new key.JDialogLogin( this, "bouboutel" );
        
        
        if ( login.isOK() ) {
            String username = login.getName();
            String password = login.getPassword();
            
            if( password.toUpperCase().equals( "bouboutel".toUpperCase() ) ){
                login.dispose();
                JFrameSetup toto = new JFrameSetup( myTicket );     
                toto.setVisible( true );
            }else{
                Toolkit.getDefaultToolkit().beep();
            }
        }else{
            login.dispose();
        }
  
    }//GEN-LAST:event_jButtonSetupActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        //loadTest();
        //toto.jTableRepresentations.repaint();
        //System.out.println( toto.myTicket.getRowCount() );
        
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainJFrame toto = new MainJFrame();
                //toto.myTicket.loadTest();
                toto.setVisible( true );
                //new MainJFrame().setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBook;
    private javax.swing.JButton jButtonBookings;
    private javax.swing.JButton jButtonExportStatistics;
    private javax.swing.JButton jButtonPerson;
    private javax.swing.JButton jButtonPrintEmptyTicket;
    private javax.swing.JButton jButtonSetup;
    private javax.swing.JButton jButtonUpdateFreeSeats;
    private javax.swing.JLabel jLabelStat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelRepresentations;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable jTableRepresentations;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    
    private TableSorter sorter;
    
}
