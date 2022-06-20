/*
 * Main.java
 *
 * Created on September 7, 2005, 5:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

/**
 *
 * @author sdperret
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main( String[] args ) {
        // TODO code application logic here
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainJFrame toto = new MainJFrame();
                //toto.myTicket.loadTest();
                toto.setVisible( true );
                //new MainJFrame().setVisible(true);
            }
        });
    }
    
}
