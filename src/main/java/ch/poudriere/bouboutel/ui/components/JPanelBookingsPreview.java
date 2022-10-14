/*
 * JPanelBookingsPreview.java
 *
 * Created on October 5, 2005, 11:07 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package ch.poudriere.bouboutel.ui.components;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.models.Booking;
import ch.poudriere.bouboutel.models.ModelList;
import ch.poudriere.bouboutel.models.Performance;
import java.awt.*;
import javax.swing.*;

/**
 *
 * @author sdperret
 */
public final class JPanelBookingsPreview extends JPanel {
    private static final int FONT_SIZE = 90;
    private javax.swing.JScrollPane jScrollPaneBookings;
    private javax.swing.JTable jTableBookings;
    private javax.swing.JLabel show;
    private ModelList<Booking> bookings = null;
    private Performance performance = null;

    /**
     * Creates new form JFrameBookingsPreview
     *
     * @param aBookings
     * @param aPerformance
     */
    public JPanelBookingsPreview(ModelList<Booking> aBookings,
            Performance aPerformance) {
        performance = aPerformance;
        bookings = aBookings;
        initComponents();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*Graphics2D g2d = (Graphics2D)g;
        int x = messageWidth/10;
        int y = fontSize*5/2;
        g2d.translate(x, y);
        g2d.setPaint(Color.lightGray);
        AffineTransform origTransform = g2d.getTransform();
        g2d.shear(-0.95, 0);
        g2d.scale(1, 3);
        g2d.drawString(message, 0, 0);
        g2d.setTransform(origTransform);
        g2d.setPaint(Color.black);
        g2d.drawString(message, 0, 0);*/
    }

    public void initComponents() {
        setBackground(Color.white);
        Font font = new Font("Serif", Font.PLAIN, FONT_SIZE);
        setFont(font);
        //FontMetrics metrics = getFontMetrics(font);
        //messageWidth = metrics.stringWidth(message);
        //int width = messageWidth*5/3;
        //int height = fontSize*3;
        //setPreferredSize(new Dimension(width, height));

        jScrollPaneBookings = new javax.swing.JScrollPane();
        jTableBookings = new javax.swing.JTable();

        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS));

        String message = "<html><H2>Bookings for " + performance.getShow().
                getTitle() + " by " + performance.getShow().getCompany().
                        getName() + "</H3>";
        message += "<H3>" + performance.getRoom().getName() + " / "
                + I18n.formatDate(performance.getDate()) + "</H3>";
        message += performance.getNbFreeSeats() + " free seats, " + performance.
                getNbSoldSeats() + " seats sold <BR>&nbsp</html>";

        show = new JLabel(message);
        add(show);

        jTableBookings.setFont(new java.awt.Font("Dialog", 1, 14));
//        jTableBookings.setModel(bookings);
        jScrollPaneBookings.setViewportView(jTableBookings);
        add(jScrollPaneBookings);
    }
}
