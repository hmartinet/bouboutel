/*
 * JFrameBooking.java
 *
 * Created on October 1, 2005, 9:54 AM
 */
package ch.poudriere.bouboutel.ui;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.files.BookingFile;
import ch.poudriere.bouboutel.models.Booking;
import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.Performance;
import ch.poudriere.bouboutel.models.Price;
import ch.poudriere.bouboutel.models.Ticket;
import ch.poudriere.bouboutel.utils.Preferences;
import ch.poudriere.bouboutel.utils.TicketsPrintThread;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.text.*;

/**
 *
 * @author sdperret
 */
public class JDialogBooking extends javax.swing.JDialog {
    public boolean flagOK = false;
    private Performance performance = null;
    private Booking booking = null;
    private final BookingSystem bookingSystem;
    private final Map<Long, JTextField> priceFields = new HashMap<>();

    public JDialogBooking(JFrame parent, Performance performance) {
        super(parent, true);
        this.bookingSystem = BookingSystem.getInstance();
        anInit(performance, null);
        this.bookingPanel.setVisible(false);
        pack();
        setSize(600, getHeight());
    }

    public JDialogBooking(JDialog parent, Booking booking) {
        super(parent, true);
        this.bookingSystem = BookingSystem.getInstance();
        anInit(booking.getPerformance(), booking);
        this.bookingLabel.setText(String.format(
                "Réservation : %s (%s)", booking.getNbSeats(), 
                booking.getComment()));
        pack();
        setSize(600, getHeight());
    }

    private void anInit(Performance performance, Booking booking) {
        this.performance = performance;
        this.booking = booking;

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    event.getComponent().transferFocus();
                }
                if (Objects.equals(event.getComponent().getName(), "PRICE")) {
                    updateSeats();
                    updatePrice();
                }
            }
        };
        DocumentFilter docFilter = new DocumentFilter() {
            Pattern regEx = Pattern.compile("\\d*");

            @Override
            public void replace(DocumentFilter.FilterBypass fb, int offset,
                    int length,
                    String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                if (!matcher.matches()) {
                    return;
                }
                super.replace(fb, offset, length, text, attrs);
            }
        };

        initComponents();

        int row = 0;
        NumberFormatter numberFormatter = new NumberFormatter(NumberFormat.
                getIntegerInstance());
        numberFormatter.setValueClass(Long.class);
        numberFormatter.setAllowsInvalid(false);
        numberFormatter.setMinimum(0L);
        List<Price> prices = performance.getPrices().stream()
                .sorted(Comparator.comparing(Price::getAction).reversed())
                .sorted(Comparator.comparing(Price::getPrice).reversed())
                .collect(Collectors.toList());
        for (Price p : prices) {
            JTextField priceTextField = new JTextField("");
            ((AbstractDocument) priceTextField.getDocument()).setDocumentFilter(
                    docFilter);
            priceTextField.setName("PRICE");
            priceFields.put(p.getId(), priceTextField);
            priceTextField.setHorizontalAlignment(JTextField.RIGHT);
            priceTextField.addKeyListener(keyAdapter);
            addPriceComponent(priceTextField,
                    row, 0, 0.);

            addPriceComponent(
                    new JLabel(I18n.get("enum.price.action.%s".formatted(
                            p.getAction()))),
                    row, 1, 1.);
            addPriceComponent(new JLabel(p.getTitle()),
                    row, 2, 1.);
            addPriceComponent(new JLabel(I18n.formatCurrency(p.getPrice()),
                    JLabel.RIGHT),
                    row, 3, 1.);
            row++;
        }

        jTextFieldName.addKeyListener(keyAdapter);
        jTextFieldPhone.addKeyListener(keyAdapter);

        updateSeats();
        updatePrice();

        InputMap im = getRootPane().getInputMap(
                JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getRootPane().getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        am.put("cancel", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void addPriceComponent(Component c, int row, int column,
            double weightx) {
        c.setFont(c.getFont().deriveFont(c.getFont().getSize() + 2f));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = weightx;
        pricesPanel.add(c, gbc);
    }

    public void updateSeats() {
        jLabelNbFreeSeats.setText(I18n.get("labelSeatsAvailableForBooking").
                formatted(getFreePlaces()));
    }

    public void updatePrice() {
        jLabelTotal.setText(String.format(
                Preferences.getLocale(), "%s %s = %s",
                getSumPlaces(),
                I18n.get(getSumPlaces() < 2 ? "labelSeat" : "labelSeats"),
                I18n.formatCurrency(getSumPrice())));
    }

    public double getSumPrice() {
        return performance.getPrices().stream().mapToDouble(p -> p.getPrice()
                * getIntValue(priceFields.get(p.getId()))).sum();
    }

    public int getSumPlaces() {
        return priceFields.values().stream().mapToInt(
                f -> getIntValue(f)).sum();
    }

    public int getFreePlaces() {
        return performance.getNbFreeSeats() + (
                booking == null ? 0 : booking.getNbSeats()) - getSumPlaces();
    }

    private static Integer getIntValue(JTextField field) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            return 0;
        }
        return Integer.valueOf(field.getText());
    }

    public void cancel() {
        dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanelBooking = new javax.swing.JPanel();
        jPanelTarifs = new javax.swing.JPanel();
        jPanelPerformance = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabelNbFreeSeats = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        bookingPanel = new javax.swing.JPanel();
        bookingLabel = new javax.swing.JLabel();
        pricesPanel = new javax.swing.JPanel();
        jLabelTotal = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldName = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextFieldPhone = new javax.swing.JTextField();
        jButtonRecall = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();

        setTitle(I18n.get("titrePrebooking" )); // NOI18N

        jPanelBooking.setLayout(new java.awt.BorderLayout());

        jPanelTarifs.setBackground(new java.awt.Color(255, 255, 255));
        jPanelTarifs.setLayout(new java.awt.BorderLayout());

        jPanelPerformance.setBackground(new java.awt.Color(255, 255, 255));
        jPanelPerformance.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanelPerformance.setLayout(new java.awt.GridBagLayout());

        jLabel14.setFont(jLabel14.getFont().deriveFont(jLabel14.getFont().getStyle() | java.awt.Font.BOLD, jLabel14.getFont().getSize()+10));
        jLabel14.setText(performance.getShow().getTitle());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 1.0;
        jPanelPerformance.add(jLabel14, gridBagConstraints);

        jLabel15.setFont(jLabel15.getFont().deriveFont(jLabel15.getFont().getSize()+4f));
        jLabel15.setText(performance.getShow().getCompany().getName());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanelPerformance.add(jLabel15, gridBagConstraints);

        jLabel16.setFont(jLabel16.getFont().deriveFont(jLabel16.getFont().getSize()+4f));
        jLabel16.setText(performance.getRoom().getName());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPerformance.add(jLabel16, gridBagConstraints);

        jLabel17.setFont(jLabel17.getFont().deriveFont(jLabel17.getFont().getSize()+4f));
        jLabel17.setText(I18n.formatDate(performance.getDate()));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        jPanelPerformance.add(jLabel17, gridBagConstraints);

        jLabelNbFreeSeats.setFont(jLabelNbFreeSeats.getFont().deriveFont(jLabelNbFreeSeats.getFont().getStyle() | java.awt.Font.BOLD, jLabelNbFreeSeats.getFont().getSize()+6));
        jLabelNbFreeSeats.setForeground(new java.awt.Color(191, 23, 29));
        jLabelNbFreeSeats.setText(performance.getNbFreeSeats() + " seats free");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        jPanelPerformance.add(jLabelNbFreeSeats, gridBagConstraints);

        jPanelTarifs.add(jPanelPerformance, java.awt.BorderLayout.PAGE_START);

        jPanel1.setLayout(new java.awt.BorderLayout());

        bookingPanel.setBackground(new java.awt.Color(204, 255, 255));
        bookingPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        bookingPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        bookingLabel.setFont(bookingLabel.getFont().deriveFont(bookingLabel.getFont().getSize()+4f));
        bookingLabel.setText("Réservation");
        bookingPanel.add(bookingLabel);

        jPanel1.add(bookingPanel, java.awt.BorderLayout.NORTH);

        pricesPanel.setBackground(new java.awt.Color(255, 255, 204));
        pricesPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        pricesPanel.setLayout(new java.awt.GridBagLayout());
        jPanel1.add(pricesPanel, java.awt.BorderLayout.CENTER);

        jPanelTarifs.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabelTotal.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabelTotal.setForeground(new java.awt.Color(191, 23, 29));
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelTotal.setText(" ");
        jLabelTotal.setAlignmentX(0.5F);
        jLabelTotal.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 10, 10, 10));
        jPanelTarifs.add(jLabelTotal, java.awt.BorderLayout.SOUTH);

        jPanelBooking.add(jPanelTarifs, java.awt.BorderLayout.CENTER);

        jPanel9.setBackground(new java.awt.Color(255, 255, 204));
        jPanel9.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        jPanel9.setEnabled(false);
        jPanel9.setLayout(new java.awt.GridBagLayout());

        jLabel4.setFont(jLabel4.getFont().deriveFont(jLabel4.getFont().getSize()+2f));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(I18n.get("label.name" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        jPanel9.add(jLabel4, gridBagConstraints);

        jTextFieldName.setFont(jTextFieldName.getFont().deriveFont(jTextFieldName.getFont().getSize()+2f));
        jTextFieldName.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldName.setNextFocusableComponent(jTextFieldPhone);
        jTextFieldName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextFieldNameKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        jPanel9.add(jTextFieldName, gridBagConstraints);

        jLabel19.setFont(jLabel19.getFont().deriveFont(jLabel19.getFont().getSize()+2f));
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel19.setText(I18n.get("labelPhone" )); // NOI18N
        jLabel19.setName("Nb Place"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 10, 5);
        jPanel9.add(jLabel19, gridBagConstraints);

        jTextFieldPhone.setFont(jTextFieldPhone.getFont().deriveFont(jTextFieldPhone.getFont().getSize()+2f));
        jTextFieldPhone.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        jTextFieldPhone.setMaximumSize(new java.awt.Dimension(200, 2147483647));
        jTextFieldPhone.setNextFocusableComponent(jButtonOK);
        jTextFieldPhone.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel9.add(jTextFieldPhone, gridBagConstraints);

        jButtonRecall.setText(I18n.get("buttonRecall" )); // NOI18N
        jButtonRecall.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecallActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 10, 10);
        jPanel9.add(jButtonRecall, gridBagConstraints);

        jPanelBooking.add(jPanel9, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanelBooking, java.awt.BorderLayout.CENTER);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel7.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jButtonCancel.setText(I18n.get("buttonCancel" )); // NOI18N
        jButtonCancel.setNextFocusableComponent(pricesPanel);
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonCancel);

        jButtonOK.setText(I18n.get("buttonOK" )); // NOI18N
        jButtonOK.setNextFocusableComponent(jButtonCancel);
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jPanel7.add(jButtonOK);

        getContentPane().add(jPanel7, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        // check if there there are bookings to be made. If yes verify that a name was entered
        boolean hasBookings = false;
        boolean hasTickets = false;
        for (Price p : performance.getPrices()) {
            int nb = getIntValue(priceFields.get(p.getId()));
            if (p.getAction() == Price.Action.BOOKING && nb > 0) {
                hasBookings = true;
            } else if (p.getAction() == Price.Action.TICKET && nb > 0) {
                hasTickets = true;
            }
        }
        if (hasBookings && jTextFieldName.getText().equals("")) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("message8"),
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
            return;
        } else if (!hasBookings && !hasTickets) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("message9"),
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
            return;
        } else if (getFreePlaces() < 0) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    "Il n'y a pas suffisement de sièges libres.",
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
            return;
        }

        // for all tarifs print ticket or register booking
        bookingSystem.setLastCustomerName(jTextFieldName.getText());
        bookingSystem.setLastCustomerPhone(jTextFieldPhone.getText());

        List<Ticket> tickets = new ArrayList<>();

        try {
            if (booking != null) {
                performance.getBookings().remove(booking.getId());
                performance.updateDataFile(booking.
                        asData(BookingFile.Command.UNBOOK));
            }
            for (Price p : performance.getPrices()) {
                int nb = getIntValue(priceFields.get(p.getId()));
                if (nb <= 0) {
                    continue;
                }
                if (p.getAction() == Price.Action.BOOKING) {
                    Booking b = new Booking(performance);
                    b.setCustomerName(jTextFieldName.getText());
                    b.setCustomerPhone(jTextFieldPhone.getText());
                    b.setComment(p.getValidityComment());
                    b.setNbSeats(nb);
                    performance.getBookings().add(b);
                    performance.updateDataFile(b.
                            asData(BookingFile.Command.BOOK));
                } else {
                    for (int i = 0; i < nb; i++) {
                        Ticket t = new Ticket(performance);
                        t.setPrice(p.getPrice());
                        t.setTitle(p.getTitle());
                        t.setValidityComment(p.getValidityComment());
                        performance.getTickets().add(t);
                        performance.updateDataFile(t.
                                asData(BookingFile.Command.SELL));
                        tickets.add(t);
                    }
                }
            }
        } catch (IOException | BackupAccessException ex) {
            JOptionPane.showMessageDialog(null,
                    "Impossible de terminer le processus de réservation, "
                    + "merci de réessayer!",
                    "Erreur de réservation", JOptionPane.OK_OPTION);
            return;
        }
        if (!tickets.isEmpty()) {
            JDialog dialog = new JDialogSelling(this, tickets);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            new Thread(new TicketsPrintThread(tickets)).start();
        }
        flagOK = true;
        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        cancel();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void jButtonRecallActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecallActionPerformed
        jTextFieldName.setText(bookingSystem.getLastCustomerName());
        jTextFieldPhone.setText(bookingSystem.getLastCustomerPhone());
    }//GEN-LAST:event_jButtonRecallActionPerformed

    private void jTextFieldNameKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldNameKeyTyped
        if (jTextFieldName.getText().length() > 35) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            evt.consume();
        }
    }//GEN-LAST:event_jTextFieldNameKeyTyped
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel bookingLabel;
    private javax.swing.JPanel bookingPanel;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonRecall;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelNbFreeSeats;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelBooking;
    private javax.swing.JPanel jPanelPerformance;
    private javax.swing.JPanel jPanelTarifs;
    private javax.swing.JTextField jTextFieldName;
    private javax.swing.JTextField jTextFieldPhone;
    private javax.swing.JPanel pricesPanel;
    // End of variables declaration//GEN-END:variables
}
