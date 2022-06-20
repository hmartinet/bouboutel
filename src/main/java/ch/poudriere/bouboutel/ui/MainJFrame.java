/*
 * MainJFrame.java
 *
 * Created on September 13, 2005, 2:06 AM
 */
package ch.poudriere.bouboutel.ui;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.exceptions.MigrationException;
import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.Company;
import ch.poudriere.bouboutel.models.Performance;
import ch.poudriere.bouboutel.models.Ticket;
import ch.poudriere.bouboutel.ui.models.DataTableModel;
import ch.poudriere.bouboutel.ui.models.DataTableModelColumn;
import ch.poudriere.bouboutel.ui.models.TableCellRendererManager;
import ch.poudriere.bouboutel.utils.Preferences;
import freemarker.template.TemplateException;
import java.awt.*;
import java.awt.image.*;
import java.awt.print.PrinterAbortException;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author sdperret
 */
public final class MainJFrame extends javax.swing.JFrame {
    private final BookingSystem bookingSystem;
    private DataTableModel<Performance> tableModel;
    private FileChannel lockChannel;

    /**
     * Creates new form MainJFrame
     *
     * @throws ch.poudriere.bouboutel.exceptions.MigrationException
     * @throws java.io.IOException
     */
    public MainJFrame() throws MigrationException, IOException {
        File file = new File("system.lock");
        this.setTitle("Bouboutel %s".formatted(Preferences.VERSION));
        try {
            lockChannel = FileChannel.open(file.toPath(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE);
            FileLock lock = lockChannel.tryLock();
            if (lock == null) {
                JOptionPane.showMessageDialog(
                        null,
                        I18n.get("message1"),
                        I18n.get("errorSystemStartupM"),
                        JOptionPane.OK_OPTION);
                System.exit(0);
            }
        } catch (IOException e) {
            throw new Error(e);
        }

        BookingSystem bs;
        try {
            bs = BookingSystem.load();
        } catch (BackupAccessException ex) {
            JOptionPane.showMessageDialog(null,
                    "Impossible d'accéder à la sauvegarde. "
                    + "Merci de vérifier la configuration.",
                    "Erreur de sauvegarde", JOptionPane.OK_OPTION);
            safeOpenSetup(false);
            try {
                bs = BookingSystem.load();
            } catch (BackupAccessException ex1) {
                System.exit(0);
                bookingSystem = null;
                return;
            }
        }
        bookingSystem = bs;
        tableModel = new DataTableModel(
                bookingSystem.getPerformances(), Performance.class,
                "show", "company", "date", "room", "nbFreeSeats",
                "nbBookedSeats", "nbSoldSeats");
        tableModel.setColumn(new DataTableModelColumn<>(
                "company", Company.class,
                (Performance p) -> p.getShow().getCompany()));
        tableModel.setEditable(false);

        initComponents();

        TableModelListener toto = (TableModelEvent e) -> {
            jLabelStat.setText(bookingSystem.getStatString());
        };
        tableModel.addTableModelListener(toto);
        jLabelStat.setText(bookingSystem.getStatString());

        jTableRepresentations.setModel(tableModel);

        jTableRepresentations.getColumnModel().getColumn(0).
                setPreferredWidth(200);
        jTableRepresentations.getColumnModel().getColumn(1).
                setPreferredWidth(200);
        jTableRepresentations.getColumnModel().getColumn(2).
                setPreferredWidth(180);
        jTableRepresentations.getColumnModel().getColumn(3).
                setPreferredWidth(180);

        jTableRepresentations.getColumnModel().getColumn(4).
                setPreferredWidth(60);
        jTableRepresentations.getColumnModel().getColumn(5).
                setPreferredWidth(60);
        jTableRepresentations.getColumnModel().getColumn(6).
                setPreferredWidth(60);

        TableRowSorter<DataTableModel> sorter
                = new TableRowSorter<>(tableModel);
        List sortKeys = new ArrayList();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        jTableRepresentations.setRowSorter(sorter);
        jTableRepresentations.getRowSorter().setSortKeys(sortKeys);
        sorter.setRowFilter(new RowFilter<>() {
            @Override
            public boolean include(
                    RowFilter.Entry<? extends DataTableModel, ? extends Integer> entry) {
                if (Preferences.isShowOldPerformances()) {
                    return true;
                }
                DataTableModel<Performance> dtm = entry.getModel();
                Performance perf = dtm.getRowModel(entry.getIdentifier());
                return !perf.getDate().toLocalDate().isBefore(LocalDate.
                        now());
            }
        });
        jTableRepresentations.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);

        TableCellRendererManager.setAsDefault(jTableRepresentations, (int row) -> {
            Performance p = tableModel.getRowModel(
                    jTableRepresentations.convertRowIndexToModel(row));
            if (p.getNbFreeSeats() == 0) {
                return Color.RED;
            }
            return Color.BLACK;
        });

//        Font font = jTableRepresentations.getFont();
//        jTableRepresentations.setFont(
//                font.deriveFont((float) (font.getSize2D() * 1.25)));
        int rowHeight = jTableRepresentations.getFontMetrics(
                jTableRepresentations.getFont()).getHeight() + 14;
        jTableRepresentations.setRowHeight(rowHeight);
        jTableRepresentations.getTableHeader().setPreferredSize(
                new Dimension(0, rowHeight));

        setExtendedState(MAXIMIZED_BOTH);

        try {
            BufferedImage headerImage = ImageIO.read(new File("header.jpg"));
            jPanelHeader.add(new JLabel(new ImageIcon(headerImage)));
        } catch (IOException e) {
            System.out.println("Could not find header image " + e);
        }
    }

    public void close() {
        try {
            Preferences.save();
        } catch (IOException | BackupAccessException ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private void openSetup(boolean reloadTable) {
        JDialogSetup setup = new JDialogSetup(this);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setup.setSize((int) (screen.getWidth() * .8), (int) (screen.getHeight()
                * .8));
        setup.setLocationRelativeTo(this);
        setup.setVisible(true);
        if (reloadTable) {
            tableModel.fireTableDataChanged();
        }
    }

    private void safeOpenSetup(boolean reloadTable) {
        if (Preferences.getPassword().isBlank()) {
            openSetup(reloadTable);
            return;
        }
        JDialogLogin login = new ch.poudriere.bouboutel.ui.JDialogLogin(this);
        if (login.isOK()) {
            if (login.getPassword().equals(Preferences.getPassword())) {
                login.dispose();
                openSetup(reloadTable);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        } else {
            login.dispose();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelHeader = new javax.swing.JPanel();
        jPanelRepresentations = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonBook = new javax.swing.JButton();
        jButtonBookings = new javax.swing.JButton();
        jButtonPerson = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        jButtonExportStatistics1 = new javax.swing.JButton();
        jButtonExportStatistics = new javax.swing.JButton();
        jButtonPrintEmptyTicket = new javax.swing.JButton();
        jButtonSetup = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableRepresentations = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabelStat = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.Y_AXIS));

        jPanelHeader.setBackground(new java.awt.Color(255, 255, 255));
        getContentPane().add(jPanelHeader);

        jPanelRepresentations.setBackground(new java.awt.Color(191, 23, 29));
        jPanelRepresentations.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle( "ch/poudriere/bouboutel/bundles/I18n", ch.poudriere.bouboutel.utils.Preferences.getLocale() ); // NOI18N
        jButtonBook.setText(bundle.getString("buttonBookSeats" )); // NOI18N
        jButtonBook.setToolTipText("book seats for a show");
        jButtonBook.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBookActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonBook);

        jButtonBookings.setText(bundle.getString("buttonViewBookings" )); // NOI18N
        jButtonBookings.setToolTipText("bookings for one show");
        jButtonBookings.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonBookings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBookingsActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonBookings);

        jButtonPerson.setText(bundle.getString("buttonViewBookingsPerson" )); // NOI18N
        jButtonPerson.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonPerson.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPersonActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPerson);

        jPanel2.add(jToolBar1, java.awt.BorderLayout.WEST);

        jToolBar2.setFloatable(false);

        jButtonExportStatistics1.setText(bundle.getString("buttonExportStatisticsCSV" )); // NOI18N
        jButtonExportStatistics1.setFocusable(false);
        jButtonExportStatistics1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportStatistics1.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonExportStatistics1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportStatistics1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportStatistics1ActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonExportStatistics1);

        jButtonExportStatistics.setText(bundle.getString("buttonExportStatisticsXLSX" )); // NOI18N
        jButtonExportStatistics.setFocusable(false);
        jButtonExportStatistics.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonExportStatistics.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonExportStatistics.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonExportStatistics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonExportStatisticsActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonExportStatistics);

        jButtonPrintEmptyTicket.setText(bundle.getString("buttonPrintEmptyTicketM" )); // NOI18N
        jButtonPrintEmptyTicket.setFocusable(false);
        jButtonPrintEmptyTicket.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPrintEmptyTicket.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonPrintEmptyTicket.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPrintEmptyTicket.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrintEmptyTicketActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonPrintEmptyTicket);

        jButtonSetup.setText(bundle.getString("buttonSetup" )); // NOI18N
        jButtonSetup.setFocusable(false);
        jButtonSetup.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSetup.setMargin(new java.awt.Insets(5, 10, 5, 10));
        jButtonSetup.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSetup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSetupActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonSetup);

        jPanel2.add(jToolBar2, java.awt.BorderLayout.EAST);

        jPanelRepresentations.add(jPanel2, java.awt.BorderLayout.NORTH);

        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        jTableRepresentations.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableRepresentationsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableRepresentations);

        jPanelRepresentations.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelRepresentations);

        jLabelStat.setText("jLabelStat");
        jPanel1.add(jLabelStat);

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonExportStatisticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportStatisticsActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer sous");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.xlsx",
                "xlsx");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("Bouboutel-Statistiques.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                bookingSystem.exportStatisticsXLSX(file);
            } catch (IOException e) {

            }
        }
    }//GEN-LAST:event_jButtonExportStatisticsActionPerformed

    private void jButtonPrintEmptyTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrintEmptyTicketActionPerformed
        new Thread(() -> {
            try {
                bookingSystem.pdfManager.printTicket(Ticket.getEmptyPrintParams());
            } catch (PrinterAbortException ex) {
                JOptionPane.showMessageDialog(null,
                        "L'impression du billet a été annulée.",
                        "Annulation d'impression",
                        JOptionPane.OK_OPTION);
            } catch (PrinterException | IOException | TemplateException ex) {
                JOptionPane.showMessageDialog(null,
                        "Impossible d'imprimer le billet, "
                        + "merci de réessayer!",
                        "Erreur d'impression",
                        JOptionPane.OK_OPTION);
            }
        }).start();
    }//GEN-LAST:event_jButtonPrintEmptyTicketActionPerformed

    private void jButtonPersonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPersonActionPerformed
        try {
            JDialogEditPersons toto = new JDialogEditPersons(this);
            toto.setSize((int) (this.getWidth() * .8), (int) (this.getHeight()
                    * .8));
            toto.setLocationRelativeTo(this);
            toto.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }//GEN-LAST:event_jButtonPersonActionPerformed

    private void jButtonBookingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBookingsActionPerformed
        int sortedSelected = jTableRepresentations.getSelectedRow();

        if (sortedSelected > -1) {

            int selected = jTableRepresentations.convertRowIndexToModel(
                    sortedSelected);

            if (selected > -1) {
                Performance p = tableModel.getRowModel(selected);

                JDialogEditBookings dial = new JDialogEditBookings(this, p);
                Dimension screen = Toolkit.getDefaultToolkit().
                        getScreenSize();
                dial.setSize((int) (screen.getWidth() * .8), (int) (screen.
                        getHeight()
                        * .8));
                dial.setLocationRelativeTo(this);
                dial.setVisible(true);
                dial.dispose();
            }
        } else {
            // select performance first
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("message6"),
                    "",
                    JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_jButtonBookingsActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void jTableRepresentationsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableRepresentationsMouseClicked
        if (evt.getClickCount() == 2) {
            book();
        }
    }//GEN-LAST:event_jTableRepresentationsMouseClicked

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked

    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void jButtonBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBookActionPerformed
        book();
    }//GEN-LAST:event_jButtonBookActionPerformed

    private void book() {
        int sortedSelected = jTableRepresentations.getSelectedRow();

        if (sortedSelected > -1) {

            int selected = jTableRepresentations.convertRowIndexToModel(
                    sortedSelected);

            if (selected > -1) {
                Performance p = tableModel.getRowModel(selected);

                if (p.getNbFreeSeats() > 0) {
                    JDialogBooking dialog = new JDialogBooking(this, p);
                    dialog.setLocationRelativeTo(this);
                    dialog.setVisible(true);
                    dialog.dispose();
                } else {
                    java.awt.Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null,
                            I18n.get("message5"),
                            I18n.get("errorBookingM"),
                            JOptionPane.OK_OPTION);
                }
            }
        } else {
            // select performance first
            java.awt.Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null,
                    I18n.get("message6"),
                    "",
                    JOptionPane.OK_OPTION);
        }
    }

    private void jButtonSetupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSetupActionPerformed
        safeOpenSetup(true);

    }//GEN-LAST:event_jButtonSetupActionPerformed

    private void jButtonExportStatistics1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonExportStatistics1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Enregistrer sous");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.csv",
                "csv");
        fileChooser.setFileFilter(filter);
        fileChooser.setSelectedFile(new File("Bouboutel-Statistiques.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                bookingSystem.exportStatistics(file);
            } catch (IOException ex) {
                Logger.getLogger(MainJFrame.class.getName()).log(Level.SEVERE,
                        null,
                        ex);
            }
        }
    }//GEN-LAST:event_jButtonExportStatistics1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonBook;
    private javax.swing.JButton jButtonBookings;
    private javax.swing.JButton jButtonExportStatistics;
    private javax.swing.JButton jButtonExportStatistics1;
    private javax.swing.JButton jButtonPerson;
    private javax.swing.JButton jButtonPrintEmptyTicket;
    private javax.swing.JButton jButtonSetup;
    private javax.swing.JLabel jLabelStat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelHeader;
    private javax.swing.JPanel jPanelRepresentations;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableRepresentations;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    // End of variables declaration//GEN-END:variables
}
