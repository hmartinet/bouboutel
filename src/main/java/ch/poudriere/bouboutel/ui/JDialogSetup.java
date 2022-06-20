/*
 * JDialogSetup.java
 *
 * Created on September 14, 2005, 12:49 AM
 */
package ch.poudriere.bouboutel.ui;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.Company;
import ch.poudriere.bouboutel.models.Performance;
import ch.poudriere.bouboutel.models.PriceList;
import ch.poudriere.bouboutel.models.Room;
import ch.poudriere.bouboutel.models.Show;
import ch.poudriere.bouboutel.models.Ticket;
import ch.poudriere.bouboutel.ui.models.DataTableModel;
import ch.poudriere.bouboutel.ui.models.DataTableModelColumn;
import ch.poudriere.bouboutel.ui.models.TableCellRendererManager;
import ch.poudriere.bouboutel.utils.Preferences;
import freemarker.template.TemplateException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.print.PrinterException;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.w3c.dom.Document;

/**
 *
 * @author sdperret
 */
public final class JDialogSetup extends javax.swing.JDialog {
    private final BookingSystem bookingSystem;
    private final DataTableModel<Company> companiesTableModel;
    private final DataTableModel<Show> showsTableModel;
    private final DataTableModel<Room> roomsTableModel;
    private final DataTableModel<Performance> performancesTableModel;
    private final DataTableModel<PriceList> priceListsTableModel;

    /**
     * Creates new form JFrameSetup
     *
     * @param parent
     */
    public JDialogSetup(JFrame parent) {
        super(parent, true);
        this.bookingSystem = BookingSystem.getInstance();

        initComponents();

        showOldPerfomranceCheckBox.setSelected(Preferences.
                isShowOldPerformances());
        printTicketBackgroundCheckBox.setSelected(Preferences.
                isPrintTicketBackground());
        jTextFieldBackupDirectory.setText(Preferences.getBackupDirectory());

        companiesTableModel = new DataTableModel(
                bookingSystem.getCompanies(), Company.class, "name");
        companiesTable.setModel(companiesTableModel);
        companiesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(companiesTable);

        showsTableModel = new DataTableModel(
                bookingSystem.getShows(), Show.class, "title", "company");
        showsTable.setModel(showsTableModel);
        showsTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(showsTable);
        companiesCombo = new JComboBox();
        for (Company e : bookingSystem.getCompanies()) {
            companiesCombo.addItem(e);
        }
        TableColumnModel columnModel = showsTable.getColumnModel();
        TableColumn performersColumn = columnModel.getColumn(1);
        performersColumn.setCellEditor(new DefaultCellEditor(companiesCombo));

        roomsTableModel = new DataTableModel(
                bookingSystem.getRooms(), Room.class, "name", "street", "city",
                "nbDefaultSeats");
        roomsTable.setModel(roomsTableModel);
        roomsTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(roomsTable);
        roomsTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        roomsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        roomsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        roomsTable.getColumnModel().getColumn(3).setPreferredWidth(10);

        performancesTableModel = new DataTableModel(
                bookingSystem.getPerformances(), Performance.class,
                "show", "company", "date", "room", "nbTotalSeats", "id");
        performancesTableModel.setColumn(new DataTableModelColumn<>(
                "company", Company.class,
                (Performance p) -> p.getShow().getCompany()));
        performancesTableModel.setEditable(false);
        performancesTable.setModel(performancesTableModel);
        performancesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(performancesTable);
        performancesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        performancesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        performancesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        performancesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        performancesTable.getColumnModel().getColumn(4).setPreferredWidth(10);
        performancesTable.getColumnModel().getColumn(5).setPreferredWidth(10);

        priceListsTableModel = new DataTableModel(
                bookingSystem.getPriceLists(), PriceList.class,
                "name");
        priceListsTableModel.setEditable(false);
        priceListsTable.setModel(priceListsTableModel);
        priceListsTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(priceListsTable);

        companiesTable.setRowHeight(companiesTable.getFontMetrics(
                companiesTable.getFont()).getHeight() + 10);
        showsTable.setRowHeight(showsTable.getFontMetrics(
                showsTable.getFont()).getHeight() + 10);
        roomsTable.setRowHeight(roomsTable.getFontMetrics(
                roomsTable.getFont()).getHeight() + 10);
        performancesTable.setRowHeight(performancesTable.getFontMetrics(
                performancesTable.getFont()).getHeight() + 10);
        priceListsTable.setRowHeight(priceListsTable.getFontMetrics(
                priceListsTable.getFont()).getHeight() + 10);
              
        ticketXHTMLPanel.setDoubleBuffered(true);
        refreshHTML();
    }

    private boolean checkBackupDirectoryValid() {
        File backupDir = new File(jTextFieldBackupDirectory.getText());
        File dataDir = new File(".");

        if (!backupDir.exists() || !backupDir.isDirectory()) {
            JOptionPane.showMessageDialog(this,
                    "Le dossier de sauvegarder n'existe pas.",
                    "Dossier de sauvegarde invalide", JOptionPane.OK_OPTION);
            return false;
        }
        if (!backupDir.canWrite()) {
            JOptionPane.showMessageDialog(this,
                    "L'application ne dispose pas des droits d'écriture "
                    + "dans le dossier de auvegarde.",
                    "Dossier de sauvegarde invalide", JOptionPane.OK_OPTION);
            return false;
        }
        try {
            if (backupDir.getCanonicalPath().equals(dataDir.getCanonicalPath())) {
                JOptionPane.showMessageDialog(this,
                        "Le dossier de auvegarde ne peut pas être identique "
                        + "au dossier de l'application.",
                        "Dossier de sauvegarde invalide", JOptionPane.OK_OPTION);
                return false;
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Le dossier de sauvegarder n'existe pas.",
                    "Dossier de sauvegarde invalide", JOptionPane.OK_OPTION);
            return false;
        }
        return true;
    }

    @Override
    public void dispose() {
        if (!checkBackupDirectoryValid()) {
            return;
        }
        Preferences.setBackupDirectory(jTextFieldBackupDirectory.getText());
        try {
            bookingSystem.saveSetup();
            Preferences.save();
            super.dispose();
        } catch (IOException | BackupAccessException ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur d'écriture des fichiers.",
                    "Erreur système", JOptionPane.OK_OPTION);
        }
    }

    private void editPerformance() {
        int selected = performancesTable.getSelectedRow();

        if (selected > -1) {
            int index = performancesTable.convertRowIndexToModel(selected);
            Performance aPerformance = performancesTableModel.getRowModel(index);
            JDialogEditPerformance d = new JDialogEditPerformance(
                    this, aPerformance, false);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
            performancesTableModel.fireTableDataChanged();
        }
    }

    private void editPriceList() {
        int selected = priceListsTable.getSelectedRow();

        if (selected > -1) {
            int index = priceListsTable.convertRowIndexToModel(selected);
            PriceList pl = priceListsTableModel.getRowModel(index);
            JDialogEditPriceList d = new JDialogEditPriceList(this, pl, false);
            d.setLocationRelativeTo(this);
            d.setVisible(true);
            priceListsTableModel.fireTableDataChanged();
        }
    }
    
    private void refreshHTML() {
        try {
            Map params = Ticket.getPreviewParams();
            params.put("select", selectComboBox.getSelectedIndex());
            Document doc = bookingSystem.pdfManager.getDocument(
                    "ticket.ftlh", params);
            ticketXHTMLPanel.setDocument(doc);
            ticketXHTMLPanel.setBackground(Color.DARK_GRAY);
        } catch (IOException | TemplateException ex) {
            Logger.getLogger(
                    JDialogSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void loadX(boolean enabled) {
        horizSpinner.setEnabled(enabled);
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> horizSpinner.setValue(Preferences.getImageX());
            case 2 -> horizSpinner.setValue(Preferences.getShowX());
            case 3 -> horizSpinner.setValue(Preferences.getPriceX());
            case 4 -> horizSpinner.setValue(Preferences.getOneEntryX());
            default -> horizSpinner.setValue(0);
        }
    }    
    
    private void saveX() {
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> Preferences.setImageX((int) horizSpinner.getValue());
            case 2 -> Preferences.setShowX((int) horizSpinner.getValue());
            case 3 -> Preferences.setPriceX((int) horizSpinner.getValue());
            case 4 -> Preferences.setOneEntryX((int) horizSpinner.getValue());
        }
        refreshHTML();
    }
    
    private void loadY(boolean enabled) {
        vertSpinner.setEnabled(enabled);
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> vertSpinner.setValue(Preferences.getImageY());
            case 2 -> vertSpinner.setValue(Preferences.getShowY());
            case 3 -> vertSpinner.setValue(Preferences.getPriceY());
            case 4 -> vertSpinner.setValue(Preferences.getOneEntryY());
            default -> vertSpinner.setValue(0);
        }
    }
    
    private void saveY() {
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> Preferences.setImageY((int) vertSpinner.getValue());
            case 2 -> Preferences.setShowY((int) vertSpinner.getValue());
            case 3 -> Preferences.setPriceY((int) vertSpinner.getValue());
            case 4 -> Preferences.setOneEntryY((int) vertSpinner.getValue());
        }
        refreshHTML();
    }
    
    private void loadScale(boolean enabled) {
        sizeSpinner.setEnabled(enabled);
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> sizeSpinner.setValue(Preferences.getImageScale());
            default -> sizeSpinner.setValue(100);
        }
    }
    
    private void saveScale() {
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> {
                Preferences.setImageScale((int) sizeSpinner.getValue());
                Preferences.loadTicketImage();
            }
        }
        refreshHTML();
    }
    
    private void loadDefaults() {
        switch(selectComboBox.getSelectedIndex()) {
            case 1 -> {
                Preferences.setImageX(Preferences.DEFAULT_IMAGE_X);
                Preferences.setImageY(Preferences.DEFAULT_IMAGE_Y);
                Preferences.setImageScale(Preferences.DEFAULT_IMAGE_SCALE);
                horizSpinner.setValue(Preferences.getImageX());
                vertSpinner.setValue(Preferences.getImageY());
                sizeSpinner.setValue(Preferences.getImageScale());
                Preferences.loadTicketImage();
            }
            case 2 -> {
                Preferences.setShowX(Preferences.DEFAULT_SHOW_X);
                Preferences.setShowY(Preferences.DEFAULT_SHOW_Y);
                horizSpinner.setValue(Preferences.getShowX());
                vertSpinner.setValue(Preferences.getShowY());
            }
            case 3 -> {
                Preferences.setPriceX(Preferences.DEFAULT_PRICE_X);
                Preferences.setPriceY(Preferences.DEFAULT_PRICE_Y);
                horizSpinner.setValue(Preferences.getPriceX());
                vertSpinner.setValue(Preferences.getPriceY());
            }
            case 4 -> {
                Preferences.setOneEntryX(Preferences.DEFAULT_ONE_ENTRY_X);
                Preferences.setOneEntryY(Preferences.DEFAULT_ONE_ENTRY_Y);
                horizSpinner.setValue(Preferences.getOneEntryX());
                vertSpinner.setValue(Preferences.getOneEntryY());
            }
        }
        refreshHTML();
    }    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mainTabbedPane = new javax.swing.JTabbedPane();
        systemPanel = new javax.swing.JPanel();
        optionsLabel = new javax.swing.JLabel();
        languageLabel = new javax.swing.JLabel();
        languageComboBox = new javax.swing.JComboBox();
        showOldPerfomranceCheckBox = new javax.swing.JCheckBox();
        printTicketBackgroundCheckBox = new javax.swing.JCheckBox();
        backupLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jTextFieldBackupDirectory = new javax.swing.JTextField();
        jButtonBrowse1 = new javax.swing.JButton();
        jButtonMakeBackup = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        companiesPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        newCompanyButton = new javax.swing.JButton();
        deleteCompanyButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        companiesTable = new javax.swing.JTable();
        showsPanel = new javax.swing.JPanel();
        showToolbar = new javax.swing.JToolBar();
        newShowButton = new javax.swing.JButton();
        deleteShowButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        showsTable = new javax.swing.JTable();
        roomsPanel = new javax.swing.JPanel();
        jToolBar4 = new javax.swing.JToolBar();
        newRoomButton = new javax.swing.JButton();
        deleteRoomButton = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        roomsTable = new javax.swing.JTable();
        performancesPanel = new javax.swing.JPanel();
        performancesToolBar = new javax.swing.JToolBar();
        newPerformanceButton = new javax.swing.JButton();
        editPerformanceButton = new javax.swing.JButton();
        deletePerformanceButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        performancesTable = new javax.swing.JTable();
        priceListsPanel = new javax.swing.JPanel();
        priceListsToolBar = new javax.swing.JToolBar();
        newPriceListButton = new javax.swing.JButton();
        editPriceListButton = new javax.swing.JButton();
        deletePriceListButton = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        priceListsTable = new javax.swing.JTable();
        ticketsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        selectComboBox = new javax.swing.JComboBox<>();
        horizSeparator = new javax.swing.JSeparator();
        horizLabel = new javax.swing.JLabel();
        horizSpinner = new javax.swing.JSpinner();
        vertSeparator = new javax.swing.JSeparator();
        vertLabel = new javax.swing.JLabel();
        vertSpinner = new javax.swing.JSpinner();
        sizeSeparator = new javax.swing.JSeparator();
        sizeLabel = new javax.swing.JLabel();
        sizeSpinner = new javax.swing.JSpinner();
        defaultSeparator = new javax.swing.JSeparator();
        defaultButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        printButton = new javax.swing.JButton();
        printButton1 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        ticketXHTMLPanel = new org.xhtmlrenderer.simple.XHTMLPanel();
        jLabel1 = new javax.swing.JLabel();
        actionPanel = new javax.swing.JPanel();
        jButtonOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("MyTicket Setup");

        systemPanel.setLayout(new java.awt.GridBagLayout());

        optionsLabel.setText(I18n.get("label.options")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        systemPanel.add(optionsLabel, gridBagConstraints);

        languageLabel.setText(I18n.get("label.language")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        systemPanel.add(languageLabel, gridBagConstraints);

        languageComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Francais" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        systemPanel.add(languageComboBox, gridBagConstraints);

        showOldPerfomranceCheckBox.setText(I18n.get("label.show_old_performances")); // NOI18N
        showOldPerfomranceCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showOldPerfomranceCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        systemPanel.add(showOldPerfomranceCheckBox, gridBagConstraints);

        printTicketBackgroundCheckBox.setSelected(true);
        printTicketBackgroundCheckBox.setText(I18n.get("label.print_tickets_with_background")); // NOI18N
        printTicketBackgroundCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printTicketBackgroundCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        systemPanel.add(printTicketBackgroundCheckBox, gridBagConstraints);

        backupLabel.setText(I18n.get("label.backup")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        systemPanel.add(backupLabel, gridBagConstraints);

        jPanel8.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jPanel8.add(jTextFieldBackupDirectory, gridBagConstraints);

        jButtonBrowse1.setText("...");
        jButtonBrowse1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBrowse1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel8.add(jButtonBrowse1, gridBagConstraints);

        jButtonMakeBackup.setText(I18n.get("label.backup_now" )); // NOI18N
        jButtonMakeBackup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMakeBackupActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel8.add(jButtonMakeBackup, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        systemPanel.add(jPanel8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        systemPanel.add(filler1, gridBagConstraints);

        mainTabbedPane.addTab(I18n.get("label.system" ), systemPanel); // NOI18N

        companiesPanel.setLayout(new java.awt.BorderLayout());

        newCompanyButton.setText(I18n.get("button.new")); // NOI18N
        newCompanyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCompanyButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(newCompanyButton);

        deleteCompanyButton.setText(I18n.get("button.delete")); // NOI18N
        deleteCompanyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCompanyButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(deleteCompanyButton);

        companiesPanel.add(jToolBar1, java.awt.BorderLayout.NORTH);

        jScrollPane1.setViewportView(companiesTable);

        companiesPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab(I18n.get("label.companies"), companiesPanel); // NOI18N

        showsPanel.setLayout(new java.awt.BorderLayout());

        newShowButton.setText(I18n.get("button.new")); // NOI18N
        newShowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newShowButtonActionPerformed(evt);
            }
        });
        showToolbar.add(newShowButton);

        deleteShowButton.setText(I18n.get("button.delete")); // NOI18N
        deleteShowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteShowButtonActionPerformed(evt);
            }
        });
        showToolbar.add(deleteShowButton);

        showsPanel.add(showToolbar, java.awt.BorderLayout.NORTH);

        jScrollPane2.setViewportView(showsTable);

        showsPanel.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab(I18n.get("label.shows"), showsPanel); // NOI18N

        roomsPanel.setLayout(new java.awt.BorderLayout());

        newRoomButton.setText(I18n.get("button.new")); // NOI18N
        newRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newRoomButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(newRoomButton);

        deleteRoomButton.setText(I18n.get("button.delete")); // NOI18N
        deleteRoomButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRoomButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(deleteRoomButton);

        roomsPanel.add(jToolBar4, java.awt.BorderLayout.NORTH);

        jScrollPane4.setViewportView(roomsTable);

        roomsPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab(I18n.get("label.rooms"), roomsPanel); // NOI18N

        performancesPanel.setLayout(new java.awt.BorderLayout());

        newPerformanceButton.setText(I18n.get("button.new")); // NOI18N
        newPerformanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPerformanceButtonActionPerformed(evt);
            }
        });
        performancesToolBar.add(newPerformanceButton);

        editPerformanceButton.setText(I18n.get("button.edit")); // NOI18N
        editPerformanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPerformanceButtonActionPerformed(evt);
            }
        });
        performancesToolBar.add(editPerformanceButton);

        deletePerformanceButton.setText(I18n.get("button.delete")); // NOI18N
        deletePerformanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePerformanceButtonActionPerformed(evt);
            }
        });
        performancesToolBar.add(deletePerformanceButton);

        performancesPanel.add(performancesToolBar, java.awt.BorderLayout.NORTH);

        //performancesTable.getSelectionModel().setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        //
        //TableColumnModel columnModelPerformances = performancesTable.getColumnModel();
        //
        //showsCombo = new JComboBox();
        //        for( Show e : bookingSystem.getShows() ){
            //            System.out.println( "ADDING " + e + " TO COMBO");
            //            showsCombo.addItem( e );
            //        }
        //
        //
        //TableColumn performancesShowsColumn = columnModelPerformances.getColumn( 0 );
        //performancesShowsColumn.setCellEditor( new DefaultCellEditor( showsCombo ) );
        //
        //
        //roomsCombo = new JComboBox();
        //        for( Room e : bookingSystem.getRooms() ){
            //            System.out.println( "ADDING " + e + " TO COMBO");
            //            roomsCombo.addItem( e );
            //        }
        //
        //
        //TableColumn roomsShowsColumn = columnModelPerformances.getColumn( 2 );
        //roomsShowsColumn.setCellEditor( new DefaultCellEditor( roomsCombo ) );
        performancesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                performancesTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(performancesTable);

        performancesPanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab(I18n.get("label.performances"), performancesPanel); // NOI18N

        priceListsPanel.setLayout(new java.awt.BorderLayout());

        newPriceListButton.setText(I18n.get("button.new")); // NOI18N
        newPriceListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPriceListButtonActionPerformed(evt);
            }
        });
        priceListsToolBar.add(newPriceListButton);

        editPriceListButton.setText(I18n.get("button.edit")); // NOI18N
        editPriceListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPriceListButtonActionPerformed(evt);
            }
        });
        priceListsToolBar.add(editPriceListButton);

        deletePriceListButton.setText(I18n.get("button.delete")); // NOI18N
        deletePriceListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePriceListButtonActionPerformed(evt);
            }
        });
        priceListsToolBar.add(deletePriceListButton);

        priceListsPanel.add(priceListsToolBar, java.awt.BorderLayout.NORTH);

        priceListsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                priceListsTableMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(priceListsTable);

        priceListsPanel.add(jScrollPane5, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Listes de prix", priceListsPanel);

        ticketsPanel.setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        selectComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "---", "Image", "Block représentation", "Block prix", "Block « 1 entrée »" }));
        selectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectComboBoxActionPerformed(evt);
            }
        });
        jPanel1.add(selectComboBox);
        jPanel1.add(horizSeparator);

        horizLabel.setText("Horizontal (mm)");
        jPanel1.add(horizLabel);

        horizSpinner.setEnabled(false);
        horizSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                horizSpinnerStateChanged(evt);
            }
        });
        horizSpinner.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                horizSpinnerMouseWheelMoved(evt);
            }
        });
        jPanel1.add(horizSpinner);
        jPanel1.add(vertSeparator);

        vertLabel.setText("Vertical (mm)");
        jPanel1.add(vertLabel);

        vertSpinner.setEnabled(false);
        vertSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                vertSpinnerStateChanged(evt);
            }
        });
        vertSpinner.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                vertSpinnerMouseWheelMoved(evt);
            }
        });
        jPanel1.add(vertSpinner);
        jPanel1.add(sizeSeparator);

        sizeLabel.setText("Taille (%)");
        jPanel1.add(sizeLabel);

        sizeSpinner.setEnabled(false);
        sizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                sizeSpinnerStateChanged(evt);
            }
        });
        sizeSpinner.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                sizeSpinnerMouseWheelMoved(evt);
            }
        });
        jPanel1.add(sizeSpinner);
        jPanel1.add(defaultSeparator);

        defaultButton.setText("Valeurs par défaut");
        defaultButton.setEnabled(false);
        defaultButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        defaultButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });
        jPanel1.add(defaultButton);
        jPanel1.add(jSeparator1);

        printButton.setText("Ouvrir PDF");
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        jPanel1.add(printButton);

        printButton1.setText("Imprimer");
        printButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(printButton1);

        ticketsPanel.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        ticketXHTMLPanel.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText("jLabel1");
        jLabel1.setAlignmentX(1.0F);
        jLabel1.setAutoscrolls(true);
        jLabel1.setInheritsPopupMenu(false);
        ticketXHTMLPanel.add(jLabel1, new java.awt.GridBagConstraints());

        jScrollPane6.setViewportView(ticketXHTMLPanel);

        ticketsPanel.add(jScrollPane6, java.awt.BorderLayout.CENTER);

        mainTabbedPane.addTab("Billets", ticketsPanel);

        getContentPane().add(mainTabbedPane, java.awt.BorderLayout.CENTER);
        mainTabbedPane.getAccessibleContext().setAccessibleName(I18n.get("label.system")); // NOI18N

        actionPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.TRAILING, 10, 10));

        jButtonOK.setText(I18n.get("button.ok")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        actionPanel.add(jButtonOK);

        getContentPane().add(actionPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void printTicketBackgroundCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printTicketBackgroundCheckBoxActionPerformed
        Preferences.setPrintTicketBackground(showOldPerfomranceCheckBox.
                isSelected());
    }//GEN-LAST:event_printTicketBackgroundCheckBoxActionPerformed

    private void showOldPerfomranceCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showOldPerfomranceCheckBoxActionPerformed
        Preferences.setShowOldPerformances(showOldPerfomranceCheckBox.
                isSelected());
    }//GEN-LAST:event_showOldPerfomranceCheckBoxActionPerformed

    private void jButtonMakeBackupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMakeBackupActionPerformed
        if (checkBackupDirectoryValid()) {
            try {
                bookingSystem.makeBackup();
            } catch (IOException | BackupAccessException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de la sauvegarde.",
                        "Erreur système", JOptionPane.OK_OPTION);
            }
        }
    }//GEN-LAST:event_jButtonMakeBackupActionPerformed

    private void jButtonBrowse1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBrowse1ActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File(Preferences.getBackupDirectory()));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showDialog(this, I18n.get("button.ok"));
        if (result == JFileChooser.APPROVE_OPTION) {
            jTextFieldBackupDirectory.setText(chooser.getSelectedFile().
                    getPath());
        }
    }//GEN-LAST:event_jButtonBrowse1ActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void deleteRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRoomButtonActionPerformed
        int row = roomsTable.getSelectedRow();
        if (row > -1) {
            int index = roomsTable.convertRowIndexToModel(row);
            int choice = JOptionPane.showConfirmDialog(null,
                    "Êtes-vous sûr de vouloir supprimer cette salle ?");
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            bookingSystem.getRooms().remove(
                    roomsTableModel.getRowModel(index).getId());
            roomsTableModel.fireTableDataChanged();
            performancesTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_deleteRoomButtonActionPerformed

    private void deleteShowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteShowButtonActionPerformed
        int row = showsTable.getSelectedRow();
        if (row > -1) {
            int index = showsTable.convertRowIndexToModel(row);
            int choice = JOptionPane.showConfirmDialog(null,
                    "Êtes-vous sûr de vouloir supprimer ce spectacle ?");
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            bookingSystem.getShows().remove(
                    showsTableModel.getRowModel(index).getId());
            showsTableModel.fireTableDataChanged();
            performancesTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_deleteShowButtonActionPerformed

    private void deleteCompanyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCompanyButtonActionPerformed
        int row = companiesTable.getSelectedRow();
        if (row > -1) {
            int index = companiesTable.convertRowIndexToModel(row);
            int choice = JOptionPane.showConfirmDialog(null,
                    "Êtes-vous sûr de vouloir supprimer cette troupe ?");
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            bookingSystem.getCompanies().remove(
                    companiesTableModel.getRowModel(index).getId());
            companiesTableModel.fireTableDataChanged();
            showsTableModel.fireTableDataChanged();
            performancesTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_deleteCompanyButtonActionPerformed

    private void editPerformanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPerformanceButtonActionPerformed
        editPerformance();
    }//GEN-LAST:event_editPerformanceButtonActionPerformed

    private void performancesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_performancesTableMouseClicked
        if (evt.getClickCount() == 2) {
            editPerformance();
        }
    }//GEN-LAST:event_performancesTableMouseClicked

    private void deletePerformanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePerformanceButtonActionPerformed
        int row = performancesTable.getSelectedRow();
        if (row > -1) {
            int index = performancesTable.convertRowIndexToModel(row);
            int choice = JOptionPane.showConfirmDialog(null,
                    "Êtes-vous sûr de vouloir supprimer cette représentation ?");
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            bookingSystem.getPerformances().remove(
                    performancesTableModel.getRowModel(index).getId());
            performancesTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_deletePerformanceButtonActionPerformed

    private void newPerformanceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPerformanceButtonActionPerformed
        if (bookingSystem.getShows().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vous devez créer au moins un spectacle pour ajouter "
                    + "des représentations.",
                    "Aucun spectacle trouvé", JOptionPane.OK_OPTION);
            return;
        }
        if (bookingSystem.getRooms().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vous devez créer au moins une salle pour ajouter "
                    + "des représentations.",
                    "Aucune salle trouvée", JOptionPane.OK_OPTION);
            return;
        }
        Performance p = new Performance();
        p.restoreShow(bookingSystem.getShows().getFirst());
        p.restoreRoom(bookingSystem.getRooms().getFirst());
        p.restoreDate(LocalDateTime.now());
        p.restoreNbTotalSeats(0);

        JDialogEditPerformance d = new JDialogEditPerformance(
                this, p, true);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        try {
            p.createDataFile();
        } catch (IOException | BackupAccessException ex) {
            Logger.getLogger(JDialogSetup.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
        performancesTableModel.fireTableDataChanged();
    }//GEN-LAST:event_newPerformanceButtonActionPerformed

    private void newShowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newShowButtonActionPerformed
        if (bookingSystem.getCompanies().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vous devez créer au moins une troupe pour ajouter "
                    + "des spectacles.",
                    "Aucune troupe trouvée", JOptionPane.OK_OPTION);
            return;
        }
        bookingSystem.getShows().add(new Show(bookingSystem.getCompanies().
                getFirst()));
        showsTableModel.fireTableDataChanged();
        int lastRow = companiesTable.getRowCount() - 1;
        companiesTable.setRowSelectionInterval(lastRow, lastRow);
        companiesTable.editCellAt(lastRow, 0);
        companiesTable.getEditorComponent().requestFocus();
    }//GEN-LAST:event_newShowButtonActionPerformed

    private void newCompanyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCompanyButtonActionPerformed
        bookingSystem.getCompanies().add(new Company());
        companiesTableModel.fireTableDataChanged();
        int lastRow = companiesTable.getRowCount() - 1;
        companiesTable.setRowSelectionInterval(lastRow, lastRow);
        companiesTable.editCellAt(lastRow, 0);
        companiesTable.getEditorComponent().requestFocus();
    }//GEN-LAST:event_newCompanyButtonActionPerformed

    private void newRoomButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRoomButtonActionPerformed
        bookingSystem.getRooms().add(new Room());
        roomsTableModel.fireTableDataChanged();
        int lastRow = roomsTable.getRowCount() - 1;
        roomsTable.setRowSelectionInterval(lastRow, lastRow);
        roomsTable.editCellAt(lastRow, 0);
        roomsTable.getEditorComponent().requestFocus();
    }//GEN-LAST:event_newRoomButtonActionPerformed

    private void newPriceListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPriceListButtonActionPerformed
        PriceList p = new PriceList();
        JDialogEditPriceList d = new JDialogEditPriceList(
                this, p, true);
        d.setLocationRelativeTo(this);
        d.setVisible(true);
        priceListsTableModel.fireTableDataChanged();
    }//GEN-LAST:event_newPriceListButtonActionPerformed

    private void editPriceListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPriceListButtonActionPerformed
        editPriceList();
    }//GEN-LAST:event_editPriceListButtonActionPerformed

    private void deletePriceListButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePriceListButtonActionPerformed
        int row = priceListsTable.getSelectedRow();
        if (row > -1) {
            int index = priceListsTable.convertRowIndexToModel(row);
            int choice = JOptionPane.showConfirmDialog(null,
                    "Êtes-vous sûr de vouloir supprimer cette liste de prix ?");
            if (choice != JOptionPane.OK_OPTION) {
                return;
            }
            bookingSystem.getPriceLists().remove(
                    priceListsTableModel.getRowModel(index).getId());
            priceListsTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_deletePriceListButtonActionPerformed

    private void priceListsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_priceListsTableMouseClicked
        if (evt.getClickCount() == 2) {
            editPriceList();
        }
    }//GEN-LAST:event_priceListsTableMouseClicked

    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultButtonActionPerformed
        loadDefaults();
    }//GEN-LAST:event_defaultButtonActionPerformed

    private void selectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectComboBoxActionPerformed
        int index = selectComboBox.getSelectedIndex();
        loadX(index != 0);
        loadY(index != 0);
        loadScale(index == 1);
        defaultButton.setEnabled(index != 0);       
        refreshHTML();
    }//GEN-LAST:event_selectComboBoxActionPerformed

    private void horizSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_horizSpinnerStateChanged
        saveX();
    }//GEN-LAST:event_horizSpinnerStateChanged

    private void horizSpinnerMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_horizSpinnerMouseWheelMoved
        horizSpinner.setValue(((int) horizSpinner.getValue()) + evt.getWheelRotation());
    }//GEN-LAST:event_horizSpinnerMouseWheelMoved

    private void vertSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_vertSpinnerStateChanged
        saveY();
    }//GEN-LAST:event_vertSpinnerStateChanged

    private void vertSpinnerMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_vertSpinnerMouseWheelMoved
        vertSpinner.setValue(((int) vertSpinner.getValue()) + evt.getWheelRotation());
    }//GEN-LAST:event_vertSpinnerMouseWheelMoved

    private void sizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_sizeSpinnerStateChanged
        saveScale();
    }//GEN-LAST:event_sizeSpinnerStateChanged

    private void sizeSpinnerMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_sizeSpinnerMouseWheelMoved
        sizeSpinner.setValue(((int) sizeSpinner.getValue()) + evt.getWheelRotation());
    }//GEN-LAST:event_sizeSpinnerMouseWheelMoved

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        try {
            File file = Files.createTempFile("ticket_", ".pdf").toFile();
            bookingSystem.pdfManager.save("ticket.ftlh", Ticket.getPrintPreviewParams(), file);
            Desktop.getDesktop().open(file);
        } catch (IOException | TemplateException ex) {
            Logger.getLogger(JDialogSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_printButtonActionPerformed

    private void printButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButton1ActionPerformed
        try {
            bookingSystem.pdfManager.printTicket(Ticket.getPrintPreviewParams());
        } catch (IOException | TemplateException | PrinterException ex) {
            Logger.getLogger(JDialogSetup.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_printButton1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionPanel;
    private javax.swing.JLabel backupLabel;
    private javax.swing.JPanel companiesPanel;
    private javax.swing.JTable companiesTable;
    private javax.swing.JButton defaultButton;
    private javax.swing.JSeparator defaultSeparator;
    private javax.swing.JButton deleteCompanyButton;
    private javax.swing.JButton deletePerformanceButton;
    private javax.swing.JButton deletePriceListButton;
    private javax.swing.JButton deleteRoomButton;
    private javax.swing.JButton deleteShowButton;
    private javax.swing.JButton editPerformanceButton;
    private javax.swing.JButton editPriceListButton;
    private javax.swing.Box.Filler filler1;
    private javax.swing.JLabel horizLabel;
    private javax.swing.JSeparator horizSeparator;
    private javax.swing.JSpinner horizSpinner;
    private javax.swing.JButton jButtonBrowse1;
    private javax.swing.JButton jButtonMakeBackup;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldBackupDirectory;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JComboBox languageComboBox;
    private javax.swing.JLabel languageLabel;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JButton newCompanyButton;
    private javax.swing.JButton newPerformanceButton;
    private javax.swing.JButton newPriceListButton;
    private javax.swing.JButton newRoomButton;
    private javax.swing.JButton newShowButton;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JPanel performancesPanel;
    private javax.swing.JTable performancesTable;
    private javax.swing.JToolBar performancesToolBar;
    private javax.swing.JPanel priceListsPanel;
    private javax.swing.JTable priceListsTable;
    private javax.swing.JToolBar priceListsToolBar;
    private javax.swing.JButton printButton;
    private javax.swing.JButton printButton1;
    private javax.swing.JCheckBox printTicketBackgroundCheckBox;
    private javax.swing.JPanel roomsPanel;
    private javax.swing.JTable roomsTable;
    private javax.swing.JComboBox<String> selectComboBox;
    private javax.swing.JCheckBox showOldPerfomranceCheckBox;
    private javax.swing.JToolBar showToolbar;
    private javax.swing.JPanel showsPanel;
    private javax.swing.JTable showsTable;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JSeparator sizeSeparator;
    private javax.swing.JSpinner sizeSpinner;
    private javax.swing.JPanel systemPanel;
    private org.xhtmlrenderer.simple.XHTMLPanel ticketXHTMLPanel;
    private javax.swing.JPanel ticketsPanel;
    private javax.swing.JLabel vertLabel;
    private javax.swing.JSeparator vertSeparator;
    private javax.swing.JSpinner vertSpinner;
    // End of variables declaration//GEN-END:variables
    private JComboBox showsCombo;
    private JComboBox companiesCombo;
    private JComboBox roomsCombo;
}
