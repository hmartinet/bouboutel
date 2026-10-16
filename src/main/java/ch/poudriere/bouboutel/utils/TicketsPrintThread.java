/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.utils;

import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.Ticket;
import freemarker.template.TemplateException;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class TicketsPrintThread implements Runnable {
    private final BookingSystem bookingSystem;
    private final List<Ticket> tickets;

    public TicketsPrintThread(List<Ticket> tickets) {
        this.bookingSystem = BookingSystem.getInstance();
        this.tickets = tickets;
    }

    @Override
    public void run() {
        Semaphore semaphore = bookingSystem.ticketPrintSemaphore;
        try {
            semaphore.acquire();
        } catch (InterruptedException ex) {
            return;
        }
        for (Ticket t : tickets) {
            System.out.println("PRINT ticket " + t.getId());
            try {
                bookingSystem.pdfManager.printTicket(t.getPrintParams());
            } catch (PrinterAbortException ex) {
                JOptionPane.showMessageDialog(null,
                        "L'impression du billet a été annulée.",
                        "Annulation d'impression",
                        JOptionPane.OK_OPTION);
            } catch (PrinterException | IOException | TemplateException ex) {
                Logger.getLogger(TicketsPrintThread.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null,
                        "Impossible d'imprimer le billet, "
                        + "merci de réessayer!",
                        "Erreur d'impression",
                        JOptionPane.OK_OPTION);
            }
        }
        semaphore.release();
    }
}
