/*
 * Main.java
 *
 * Created on September 7, 2005, 5:25 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel;

import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.ui.MainJFrame;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 *
 * @author sdperret
 */
public class Main {
    private final static String LOCK_FILE_NAME = "system.lock";

    public static class ExceptionHandler
            implements Thread.UncaughtExceptionHandler {
        public void handle(Throwable thrown) {
            // for EDT exceptions
            handleException(Thread.currentThread(), thrown);
        }

        @Override
        public void uncaughtException(Thread thread, Throwable thrown) {
            // for other uncaught exceptions
            handleException(thread, thrown);
        }

        protected void handleException(Thread thread, Throwable thrown) {
            thrown.printStackTrace();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        System.setProperty("sun.awt.exception.handler",
                ExceptionHandler.class.getName());

        // Flat
        FlatLaf.registerCustomDefaultsSource("ch.poudriere.bouboutel.themes");
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> {
            try {
                MainJFrame frame = new MainJFrame();
                frame.setVisible(true);
            } catch (MigrationException | IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        });
    }
}
