/*
 * JFrameEditBookings.java
 *
 * Created on October 2, 2005, 11:25 PM
 */
package ch.poudriere.bouboutel.ui;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.files.BookingFile;
import ch.poudriere.bouboutel.models.Booking;
import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.Performance;
import ch.poudriere.bouboutel.models.Ticket;
import ch.poudriere.bouboutel.ui.models.DataTableModel;
import ch.poudriere.bouboutel.ui.models.TableAmountCellRenderer;
import ch.poudriere.bouboutel.ui.models.TableCellRendererManager;
import freemarker.template.TemplateException;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author sdperret
 */
public class JDialogEditBookings extends javax.swing.JDialog {
    private final Performance performance;
    private final BookingSystem bookingSystem;
    private final DataTableModel<Booking> bookingsTableModel;
    private final DataTableModel<Ticket> ticketsTableModel;

    public JDialogEditBookings(JFrame parent, Performance performance) {
        super(parent, true);
        bookingSystem = BookingSystem.getInstance();
        this.performance = performance;

        initComponents();

        bookingsTableModel = new DataTableModel(
                performance.getBookings(), Booking.class,
                "customerName", "nbSeats", "comment", "date");
        bookingsTableModel.setEditable(false);
        bookingsTable.setModel(bookingsTableModel);
        bookingsTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(bookingsTable);
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        TableRowSorter<DataTableModel> bookingsSorter
                = new TableRowSorter<>(bookingsTableModel);
        List bookingsSortKeys = new ArrayList();
        bookingsSortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        bookingsTable.setRowSorter(bookingsSorter);
        bookingsTable.getRowSorter().setSortKeys(bookingsSortKeys);

        ticketsTableModel = new DataTableModel(
                performance.getTickets(), Ticket.class,
                "id", "title", "price");
        ticketsTableModel.setEditable(false);
        ticketsTable.setModel(ticketsTableModel);
        ticketsTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(bookingsTable);
        ticketsTable.getColumnModel().getColumn(2).setCellRenderer(new TableAmountCellRenderer());
        ticketsTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        ticketsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        ticketsTable.getColumnModel().getColumn(2).setPreferredWidth(10);

        TableRowSorter<DataTableModel> ticketsSorter
                = new TableRowSorter<>(ticketsTableModel);
        List ticketsSortKeys = new ArrayList();
        ticketsSortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        ticketsTable.setRowSorter(ticketsSorter);
        ticketsTable.getRowSorter().setSortKeys(ticketsSortKeys);

        ticketsTableModel.addTableModelListener((TableModelEvent e) -> {
            jLabelTotal.setText(I18n.formatCurrency(performance.
                    getTotalIncome()));
        });
        jLabelTotal.setText(I18n.formatCurrency(performance.
                getTotalIncome()));

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String f = searchField.getText();
                if (f != null && !f.isBlank()) {
                    bookingsSorter.setRowFilter(RowFilter.regexFilter(f.
                            toUpperCase(), 0));
                } else {
                    bookingsSorter.setRowFilter(null);
                }
            }
        });

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

    private void bookingsPDF(boolean save) throws FileNotFoundException,
            IOException,
            TemplateException,
            PrinterException {
        Map params = new HashMap();
        params.put("performance", performance);
        params.put("now", LocalDateTime.now());

        if (save) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Enregistrer sous");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "*.pdf",
                    "PDF");
            fileChooser.setFileFilter(filter);
            fileChooser.setSelectedFile(
                    new File("Réservation_%d.pdf".formatted(
                            performance.getId())));
            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                bookingSystem.pdfManager.save("bookings.ftlh", params, file);
            }
        } else {
            bookingSystem.pdfManager.print("bookings.ftlh", params);
        }
    }

    private void editBooking() {
        int sortedSelected = bookingsTable.getSelectedRow();

        if (sortedSelected > -1) {
            int selected = bookingsTable.convertRowIndexToModel(sortedSelected);

            JDialogEditBooking toto = new JDialogEditBooking(this,
                    bookingsTableModel.getRowModel(selected));
            toto.setLocationRelativeTo(this);
            toto.setVisible(true);
            dispose();
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("messageSelectBooking"),
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jButtonEditBooking = new javax.swing.JButton();
        jButtonDeleteBooking = new javax.swing.JButton();
        jButtonMakeTickets = new javax.swing.JButton();
        jScrollPaneBookings = new javax.swing.JScrollPane();
        bookingsTable = new javax.swing.JTable();
        jPanelListAction = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jButtonPrint = new javax.swing.JButton();
        jButtonSavePDF = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonCancelTickets = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        ticketsTable = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabelTotal = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();
        jPanelPerformance = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabelNbFreeSeats1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel6.setBackground(new java.awt.Color(255, 204, 204));
        jPanel6.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 1, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jLabel2.setFont(jLabel2.getFont().deriveFont(jLabel2.getFont().getSize()+2f));
        jLabel2.setText(I18n.get("labelBookings" )); // NOI18N
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel6.add(jLabel2);

        jPanel9.setBackground(new java.awt.Color(255, 204, 204));
        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel9.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jButtonEditBooking.setText(I18n.get("buttonEdit" )); // NOI18N
        jButtonEditBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEditBookingActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonEditBooking);

        jButtonDeleteBooking.setText(I18n.get("buttonDelete" )); // NOI18N
        jButtonDeleteBooking.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteBookingActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonDeleteBooking);

        jButtonMakeTickets.setText(I18n.get("buttonMakeTickets" )); // NOI18N
        jButtonMakeTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMakeTicketsActionPerformed(evt);
            }
        });
        jPanel9.add(jButtonMakeTickets);

        jPanel6.add(jPanel9);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel6, gridBagConstraints);

        jScrollPaneBookings.setBackground(new java.awt.Color(255, 204, 204));
        jScrollPaneBookings.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));

        bookingsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bookingsTableMouseClicked(evt);
            }
        });
        jScrollPaneBookings.setViewportView(bookingsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPaneBookings, gridBagConstraints);

        jPanelListAction.setBackground(new java.awt.Color(255, 204, 204));
        jPanelListAction.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 1, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        jPanelListAction.setLayout(new javax.swing.BoxLayout(jPanelListAction, javax.swing.BoxLayout.LINE_AXIS));

        jPanel10.setBackground(new java.awt.Color(255, 204, 204));
        jPanel10.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEADING));

        jLabel4.setText("Filtrer");
        jPanel10.add(jLabel4);

        searchField.setPreferredSize(new java.awt.Dimension(150, 22));
        jPanel10.add(searchField);

        jPanelListAction.add(jPanel10);

        jPanel4.setBackground(new java.awt.Color(255, 204, 204));
        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jButtonPrint.setText(I18n.get("button.print")); // NOI18N
        jButtonPrint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonPrint);

        jButtonSavePDF.setText(I18n.get("button.savePDF")); // NOI18N
        jButtonSavePDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSavePDFActionPerformed(evt);
            }
        });
        jPanel4.add(jButtonSavePDF);

        jPanelListAction.add(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanelListAction, gridBagConstraints);

        jPanel7.setBackground(new java.awt.Color(204, 255, 204));
        jPanel7.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));
        jPanel7.setLayout(new javax.swing.BoxLayout(jPanel7, javax.swing.BoxLayout.LINE_AXIS));

        jLabel3.setFont(jLabel3.getFont().deriveFont(jLabel3.getFont().getSize()+2f));
        jLabel3.setText(I18n.get("labelTickets" )); // NOI18N
        jLabel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel7.add(jLabel3);

        jPanel2.setBackground(new java.awt.Color(204, 255, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jButtonCancelTickets.setText(I18n.get("buttonCancelTicket" )); // NOI18N
        jButtonCancelTickets.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelTicketsActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonCancelTickets);

        jPanel7.add(jPanel2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel7, gridBagConstraints);

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setViewportView(ticketsTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel1.add(jScrollPane2, gridBagConstraints);

        jPanel8.setBackground(new java.awt.Color(204, 255, 204));
        jPanel8.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, javax.swing.UIManager.getDefaults().getColor("Button.borderColor")));

        jPanel5.setBackground(new java.awt.Color(204, 255, 204));

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getSize()+2f));
        jLabel1.setText("Total income: ");
        jPanel5.add(jLabel1);

        jLabelTotal.setFont(jLabelTotal.getFont().deriveFont(jLabelTotal.getFont().getSize()+2f));
        jLabelTotal.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabelTotal.setText("CHF 0.00");
        jLabelTotal.setMaximumSize(new java.awt.Dimension(500, 20));
        jLabelTotal.setPreferredSize(new java.awt.Dimension(120, 20));
        jPanel5.add(jLabelTotal);

        jPanel8.add(jPanel5);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(jPanel8, gridBagConstraints);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING));

        jButtonOK.setText(I18n.get("button.ok" )); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonOK);

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

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

        jLabelNbFreeSeats1.setFont(jLabelNbFreeSeats1.getFont().deriveFont(jLabelNbFreeSeats1.getFont().getStyle() | java.awt.Font.BOLD, jLabelNbFreeSeats1.getFont().getSize()+6));
        jLabelNbFreeSeats1.setForeground(new java.awt.Color(191, 23, 29));
        jLabelNbFreeSeats1.setText(performance.getNbFreeSeats() + " seats free");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_END;
        jPanelPerformance.add(jLabelNbFreeSeats1, gridBagConstraints);

        getContentPane().add(jPanelPerformance, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void bookingsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bookingsTableMouseClicked
//        if (evt.getClickCount() == 2) {
//            editBooking();
//        }
    }//GEN-LAST:event_bookingsTableMouseClicked

    private void jButtonCancelTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelTicketsActionPerformed
        System.out.println("Now in jButtonCancelTicketsActionPerformed");

        int sortedSelected = ticketsTable.getSelectedRow();

        if (sortedSelected > -1) {
            int selected = ticketsTable.convertRowIndexToModel(sortedSelected);
            System.out.println(" treating ticket " + selected);
            try {
                Ticket ticket = ticketsTableModel.getRowModel(selected);
                performance.updateDataFile(ticket.asData(
                        BookingFile.Command.UNSELL));
                performance.getTickets().remove(ticket.getId());
                ticketsTableModel.fireTableDataChanged();
            } catch (IOException | BackupAccessException e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "Could not cancel ticket on file", "File error",
                        JOptionPane.OK_OPTION);
            }
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("messageSelectBooking"),
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_jButtonCancelTicketsActionPerformed

    private void jButtonMakeTicketsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMakeTicketsActionPerformed
        int sortedSelected = bookingsTable.getSelectedRow();

        if (sortedSelected > -1) {
            int selected = bookingsTable.convertRowIndexToModel(sortedSelected);
            Booking aBooking = performance.getBookings().getByIndex(selected);

            JDialogBooking toto = new JDialogBooking(this, aBooking);
            toto.setLocationRelativeTo(this);
            toto.setVisible(true);
            dispose();
        }
    }//GEN-LAST:event_jButtonMakeTicketsActionPerformed

    private void jButtonDeleteBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteBookingActionPerformed
        int sortedSelected = bookingsTable.getSelectedRow();

        if (sortedSelected > -1) {
            int selected = bookingsTable.convertRowIndexToModel(sortedSelected);
            Booking booking = bookingsTableModel.getRowModel(selected);
            try {
                performance.updateDataFile(booking.asData(
                        BookingFile.Command.UNBOOK));
                performance.getBookings().remove(booking.getId());
                bookingsTableModel.fireTableDataChanged();
            } catch (IOException | BackupAccessException e) {
                java.awt.Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(null,
                        "Could not cancel booking for " + booking.
                                getCustomerName(),
                        "System error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("messageSelectBooking"),
                    I18n.get("errorBookingM"),
                    JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_jButtonDeleteBookingActionPerformed

    private void jButtonSavePDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSavePDFActionPerformed
        try {
            bookingsPDF(true);
        } catch (PrinterException | IOException | TemplateException ex) {
            Logger.getLogger(JDialogEditBookings.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonSavePDFActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing

    }//GEN-LAST:event_formWindowClosing

    private void jButtonEditBookingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEditBookingActionPerformed
        editBooking();
    }//GEN-LAST:event_jButtonEditBookingActionPerformed

    private void jButtonPrintActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintActionPerformed
        try {
            bookingsPDF(false);
        } catch (PrinterException | IOException | TemplateException ex) {
            Logger.getLogger(JDialogEditBookings.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonPrintActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable bookingsTable;
    private javax.swing.JButton jButtonCancelTickets;
    private javax.swing.JButton jButtonDeleteBooking;
    private javax.swing.JButton jButtonEditBooking;
    private javax.swing.JButton jButtonMakeTickets;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JButton jButtonPrint;
    private javax.swing.JButton jButtonSavePDF;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabelNbFreeSeats1;
    private javax.swing.JLabel jLabelTotal;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPanel jPanelListAction;
    private javax.swing.JPanel jPanelPerformance;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPaneBookings;
    private javax.swing.JTextField searchField;
    private javax.swing.JTable ticketsTable;
    // End of variables declaration//GEN-END:variables
}
