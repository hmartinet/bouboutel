/*
 * JFrameBookingsPreview.java
 *
 * Created on October 5, 2005, 11:10 AM
 */

package myticket;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.print.*;
/**
 *
 * @author  sdperret
 */
public class JFrameBookingsPreview extends javax.swing.JFrame implements ActionListener {
    
    /** Creates new form JFrameBookingsPreview */
    public JFrameBookingsPreview( ShowableArrayList<Booking> aBookings, Performance aPerformance ) {
        super("Bookings preint preview");
        
        bookings = aBookings;
        performance = aPerformance;
        
        //WindowUtilities.setNativeLookAndFeel();
        //addWindowListener(new ExitListener());
        Container content = getContentPane();
        //JButton printButton = new JButton("Print");
        //printButton.addActionListener(this);
        //JPanel buttonPanel = new JPanel();
        //buttonPanel.setBackground(Color.white);
        //buttonPanel.add(printButton);
        //content.add(buttonPanel, BorderLayout.SOUTH);
        JPanelBookingsPreview drawingPanel = new JPanelBookingsPreview( bookings, performance );
        content.add(drawingPanel, BorderLayout.CENTER);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    
        //initComponents();
    }

    public void actionPerformed(ActionEvent event) {
        PrintUtility.printComponent(this);
    }
    
    private ShowableArrayList<Booking> bookings = null;
    private Performance performance = null;
    
    
    
    
    
    
    
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //new JFrameBookingsPreview();
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    
}
