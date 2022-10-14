/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Hervé Martinet
 */
public class RowColorTableCellRenderer extends DefaultTableCellRenderer {
    private RowColorSelector selector = null;

    public RowColorTableCellRenderer() {
        super();
    }
    
    public RowColorTableCellRenderer(RowColorSelector selector) {
        super();
        this.selector = selector;
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
        if (selector != null) {
            this.setForeground(selector.getColor(row));
        }
        return this;
    }
}
