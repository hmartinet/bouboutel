/*
 * JFrameEditPerformance.java
 *
 * Created on October 11, 2005, 7:52 AM
 */
package ch.poudriere.bouboutel.ui;

import ch.poudriere.bouboutel.bundles.I18n;
import ch.poudriere.bouboutel.exceptions.BackupAccessException;
import ch.poudriere.bouboutel.models.BookingSystem;
import ch.poudriere.bouboutel.models.ModelList;
import ch.poudriere.bouboutel.models.Performance;
import ch.poudriere.bouboutel.models.Price;
import ch.poudriere.bouboutel.models.PriceList;
import ch.poudriere.bouboutel.models.Room;
import ch.poudriere.bouboutel.models.Show;
import ch.poudriere.bouboutel.ui.models.DataTableModel;
import ch.poudriere.bouboutel.ui.models.TableCellRendererManager;
import com.formdev.flatlaf.ui.FlatRoundBorder;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author sdperret
 */
public class JDialogEditPerformance extends javax.swing.JDialog {
    private final BookingSystem bookingSystem;
    private DatePickerSettings dateSettings = new DatePickerSettings();
    private TimePickerSettings timeSettings = new TimePickerSettings();
    private ModelList<Price> prices = new ModelList<>();
    private final DataTableModel<Price> pricesTableModel;

    public JDialogEditPerformance(JDialog parent, Performance performance,
            boolean newPerformance) {
        super(parent, true);
        bookingSystem = BookingSystem.getInstance();
        this.performance = performance;
        this.newPerformance = newPerformance;

        for (Price p : performance.getPrices()) {
            prices.add(p.clone());
        }

        dateSettings.setFormatForDatesCommonEra("EEE dd.MM.yyyy");

        initComponents();

        actionsCombo = new JComboBox();
        actionsCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                setText(I18n.get(
                        "enum.price.action.%s".formatted((Price.Action) value)));
                return this;
            }
        });
        actionsCombo.addItem(Price.Action.BOOKING);
        actionsCombo.addItem(Price.Action.TICKET);
        pricesTableModel = new DataTableModel(
                prices, Price.class, "action", "title", "price",
                "validityComment");
        pricesTable.setModel(pricesTableModel);
        pricesTable.getSelectionModel().setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        TableCellRendererManager.setAsDefault(pricesTable);
        pricesTable.setDefaultEditor(Price.Action.class, new DefaultCellEditor(
                actionsCombo));
        pricesTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        pricesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        pricesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        pricesTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        pricesTable.getColumnModel().getColumn(3).setPreferredWidth(300);

        dateTimePicker.getDatePicker().getComponentDateTextField().setBorder(
                new FlatRoundBorder());
        dateTimePicker.getDatePicker().getComponentDateTextField().setMargin(
                new Insets(2, 6, 2, 6));
        dateTimePicker.getTimePicker().getComponentTimeTextField().setBorder(
                new FlatRoundBorder());
        dateTimePicker.getTimePicker().getComponentTimeTextField().setMargin(
                new Insets(2, 6, 2, 6));

        for (Show s : bookingSystem.getShows()) {
            jComboBoxShow.addItem(s);
        }
        jComboBoxShow.setSelectedItem(performance.getShow());
        for (Room r : bookingSystem.getRooms()) {
            jComboBoxRoom.addItem(r);
        }
        jComboBoxRoom.setSelectedItem(performance.getRoom());
        for (PriceList pl : bookingSystem.getPriceLists()) {
            priceListsComboBox.addItem(pl);
        }

        nbSeatsTextField.
                setText(Integer.toString(performance.getNbTotalSeats()));
        dateTimePicker.setDateTimeStrict(performance.getDate());
    }

    public void importPriceList(boolean reset) {
        if (reset) {
            prices.clear();
        }
        PriceList pl = (PriceList) priceListsComboBox.getSelectedItem();
        for (Price p : pl.getPrices()) {
            prices.add(p.duplicate());
        }
        pricesTableModel.fireTableDataChanged();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabel1 = new javax.swing.JLabel();
        jComboBoxShow = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxRoom = new javax.swing.JComboBox<>();
        nbSeatsLabel = new javax.swing.JLabel();
        nbSeatsPanel = new javax.swing.JPanel();
        nbSeatsTextField = new javax.swing.JTextField();
        nbSeatsDefaultButton = new javax.swing.JButton();
        dateLabel = new javax.swing.JLabel();
        dateTimePicker = new DateTimePicker(dateSettings, timeSettings);
        tarifsLabel = new javax.swing.JLabel();
        tarifsButtonsPanel = new javax.swing.JPanel();
        jButtonAddPrice = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        jButtonDeletePrice = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        priceListsComboBox = new javax.swing.JComboBox<>();
        addPriceList = new javax.swing.JButton();
        replaceByPriceList = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pricesTable = new javax.swing.JTable();
        buttonsPanel = new javax.swing.JPanel();
        jButtonCancel = new javax.swing.JButton();
        jButtonOK = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Performance");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText(I18n.get("label.show" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        jComboBoxShow.setMinimumSize(new java.awt.Dimension(300, 24));
        jComboBoxShow.setPreferredSize(new java.awt.Dimension(300, 24));
        jComboBoxShow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxShowActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 223;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 10);
        getContentPane().add(jComboBoxShow, gridBagConstraints);

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(I18n.get("label.room" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(jLabel2, gridBagConstraints);

        jComboBoxRoom.setMinimumSize(new java.awt.Dimension(300, 24));
        jComboBoxRoom.setPreferredSize(new java.awt.Dimension(300, 24));
        jComboBoxRoom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxRoomActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 223;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        getContentPane().add(jComboBoxRoom, gridBagConstraints);

        nbSeatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nbSeatsLabel.setText(I18n.get("label.nbTotalSeats" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(nbSeatsLabel, gridBagConstraints);

        nbSeatsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        nbSeatsTextField.setMinimumSize(new java.awt.Dimension(80, 23));
        nbSeatsTextField.setPreferredSize(new java.awt.Dimension(80, 23));
        nbSeatsTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nbSeatsTextFieldActionPerformed(evt);
            }
        });
        nbSeatsTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nbSeatsTextFieldKeyPressed(evt);
            }
        });
        nbSeatsPanel.add(nbSeatsTextField);

        nbSeatsDefaultButton.setText(I18n.get("button.default" )); // NOI18N
        nbSeatsDefaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nbSeatsDefaultButtonActionPerformed(evt);
            }
        });
        nbSeatsPanel.add(nbSeatsDefaultButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(nbSeatsPanel, gridBagConstraints);

        dateLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dateLabel.setText(I18n.get("label.date" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(dateLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(dateTimePicker, gridBagConstraints);

        tarifsLabel.setText(I18n.get("labelPrices" )); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.ABOVE_BASELINE_LEADING;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(tarifsLabel, gridBagConstraints);

        tarifsButtonsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jButtonAddPrice.setText(I18n.get("button.new" )); // NOI18N
        jButtonAddPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddPriceActionPerformed(evt);
            }
        });
        tarifsButtonsPanel.add(jButtonAddPrice);

        upButton.setText("↑");
        upButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        upButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        tarifsButtonsPanel.add(upButton);

        downButton.setText("↓");
        downButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        downButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        tarifsButtonsPanel.add(downButton);

        jButtonDeletePrice.setText(I18n.get("button.delete" )); // NOI18N
        jButtonDeletePrice.setAutoscrolls(true);
        jButtonDeletePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeletePriceActionPerformed(evt);
            }
        });
        tarifsButtonsPanel.add(jButtonDeletePrice);

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        tarifsButtonsPanel.add(jSeparator1);

        jLabel3.setText("Listes de prix");
        tarifsButtonsPanel.add(jLabel3);

        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 0, 5));
        jPanel1.add(priceListsComboBox);

        addPriceList.setText("Ajouter");
        addPriceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPriceListActionPerformed(evt);
            }
        });
        jPanel1.add(addPriceList);

        replaceByPriceList.setText("Remplacer");
        replaceByPriceList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceByPriceListActionPerformed(evt);
            }
        });
        jPanel1.add(replaceByPriceList);

        tarifsButtonsPanel.add(jPanel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        getContentPane().add(tarifsButtonsPanel, gridBagConstraints);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(32000, 303));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(800, 303));
        jScrollPane1.setViewportView(pricesTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jButtonCancel.setText(I18n.get("buttonCancel" )); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });
        buttonsPanel.add(jButtonCancel);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/poudriere/bouboutel/bundles/I18n"); // NOI18N
        jButtonOK.setText(bundle.getString("buttonOK")); // NOI18N
        jButtonOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOKActionPerformed(evt);
            }
        });
        buttonsPanel.add(jButtonOK);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        getContentPane().add(buttonsPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDeletePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletePriceActionPerformed
        int row = pricesTable.getSelectedRow();
        if (row > -1) {
            int index = pricesTable.convertRowIndexToModel(row);
            prices.remove(pricesTableModel.getRowModel(index).getId());
            pricesTableModel.fireTableDataChanged();
        }
    }//GEN-LAST:event_jButtonDeletePriceActionPerformed

    private void jButtonAddPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAddPriceActionPerformed
        prices.add(new Price());
        pricesTableModel.fireTableDataChanged();
    }//GEN-LAST:event_jButtonAddPriceActionPerformed

    private void jComboBoxShowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxShowActionPerformed

    }//GEN-LAST:event_jComboBoxShowActionPerformed

    private void jComboBoxRoomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxRoomActionPerformed
        JComboBox cb = (JComboBox) evt.getSource();
        Room r = (Room) cb.getSelectedItem();
        nbSeatsTextField.setText(Integer.toString(r.getNbDefaultSeats()));
    }//GEN-LAST:event_jComboBoxRoomActionPerformed

    private void nbSeatsDefaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nbSeatsDefaultButtonActionPerformed
        Room aR = bookingSystem.getRooms().getByIndex(jComboBoxRoom.
                getSelectedIndex());
        nbSeatsTextField.setText(Integer.toString(aR.getNbDefaultSeats()));
    }//GEN-LAST:event_nbSeatsDefaultButtonActionPerformed

    private void nbSeatsTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nbSeatsTextFieldKeyPressed
// TODO add your handling code here:
        char c = evt.getKeyChar();
        if (!((Character.isDigit(c) || (c == KeyEvent.VK_ENTER) || (c
                == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))) {
            getToolkit().beep();
            evt.consume();
            nbSeatsTextField.setText(Integer.toString(performance.getRoom().
                    getNbDefaultSeats()));
        }
    }//GEN-LAST:event_nbSeatsTextFieldKeyPressed

    private void nbSeatsTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nbSeatsTextFieldActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_nbSeatsTextFieldActionPerformed

    private void jButtonOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOKActionPerformed

        try {
            performance.setPrices(prices);
        } catch (IOException | BackupAccessException ex) {
            Logger.getLogger(JDialogEditPerformance.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        // SETTING DATE
        try {
            performance.setDate(dateTimePicker.getDateTimeStrict());
        } catch (IOException | BackupAccessException e) {
            System.out.println("error setting date");
        }

        // SETTING SEATS
        try {
            int n = Integer.parseInt(nbSeatsTextField.getText().trim());

            try {
                performance.setNbTotalSeats(n);
            } catch (IOException | BackupAccessException e) {
                System.out.println("error setting the nbTotalSeats");
            }
        } catch (NumberFormatException ne) {
            String message = "Error, nb seats should be a number";
            JOptionPane.showMessageDialog(null, message, "Booking error",
                    JOptionPane.OK_OPTION);
            return;
        }

        // SET SHOW
        try {
            performance.setShow((Show) jComboBoxShow.getSelectedItem());
        } catch (IOException | BackupAccessException e) {
            System.out.println("error setting the show");
        }

        // SET ROOM
        try {
            performance.setRoom((Room) jComboBoxRoom.getSelectedItem());
        } catch (IOException | BackupAccessException e) {
            System.out.println("error setting the room");
        }

        if (newPerformance) {
            bookingSystem.getPerformances().add(performance);
        }

        dispose();
    }//GEN-LAST:event_jButtonOKActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        dispose();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    private void addPriceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPriceListActionPerformed
        importPriceList(false);
    }//GEN-LAST:event_addPriceListActionPerformed

    private void replaceByPriceListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceByPriceListActionPerformed
        importPriceList(true);
    }//GEN-LAST:event_replaceByPriceListActionPerformed

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int row = pricesTable.getSelectedRow();

        if (row > 0) {
            ModelList<Price> nps = new ModelList<>();
            int index = pricesTable.convertRowIndexToModel(row);
            Long ppid =  pricesTableModel.getRowModel(index - 1).getId();
            Price mp =  prices.remove(pricesTableModel.getRowModel(index).getId());

            for (Price p : prices) {
                if (p.getId().equals(ppid)) {
                    nps.add(new Price(
                        mp.getAction(), mp.getTitle(), mp.getPrice(), mp.getValidityComment()));
                nps.add(new Price(
                    p.getAction(), p.getTitle(), p.getPrice(), p.getValidityComment()));
        } else {
            nps.add(new Price(
                p.getAction(), p.getTitle(), p.getPrice(), p.getValidityComment()));
        }
        }
        prices.clear();
        prices.addAll(nps);
        pricesTableModel.fireTableDataChanged();
        pricesTable.getSelectionModel().setSelectionInterval(index - 1, index - 1);
        }
    }//GEN-LAST:event_upButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int row = pricesTable.getSelectedRow();

        if (row > -1 && row < pricesTableModel.getRowCount() - 1) {
            ModelList<Price> nps = new ModelList<>();
            int index = pricesTable.convertRowIndexToModel(row);
            Long npid =  pricesTableModel.getRowModel(index + 1).getId();
            Price mp =  prices.remove(pricesTableModel.getRowModel(index).getId());

            for (Price p : prices) {
                if (p.getId().equals(npid)) {
                    nps.add(new Price(
                        p.getAction(), p.getTitle(), p.getPrice(), p.getValidityComment()));
                nps.add(new Price(
                    mp.getAction(), mp.getTitle(), mp.getPrice(), mp.getValidityComment()));
        } else {
            nps.add(new Price(
                p.getAction(), p.getTitle(), p.getPrice(), p.getValidityComment()));
        }
        }
        prices.clear();
        prices.addAll(nps);
        pricesTableModel.fireTableDataChanged();
        pricesTable.getSelectionModel().setSelectionInterval(index + 1, index + 1);
        }
    }//GEN-LAST:event_downButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPriceList;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel dateLabel;
    private com.github.lgooddatepicker.components.DateTimePicker dateTimePicker;
    private javax.swing.JButton downButton;
    private javax.swing.JButton jButtonAddPrice;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonDeletePrice;
    private javax.swing.JButton jButtonOK;
    private javax.swing.JComboBox<Room> jComboBoxRoom;
    private javax.swing.JComboBox<Show> jComboBoxShow;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JButton nbSeatsDefaultButton;
    private javax.swing.JLabel nbSeatsLabel;
    private javax.swing.JPanel nbSeatsPanel;
    private javax.swing.JTextField nbSeatsTextField;
    private javax.swing.JComboBox<PriceList> priceListsComboBox;
    private javax.swing.JTable pricesTable;
    private javax.swing.JButton replaceByPriceList;
    private javax.swing.JPanel tarifsButtonsPanel;
    private javax.swing.JLabel tarifsLabel;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    private Performance performance;
    private boolean newPerformance;
    private JComboBox actionsCombo;
}
