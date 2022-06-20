/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Hervé Martinet
 */
public class TableHeaderCellRenderer implements javax.swing.table.TableCellRenderer {
    private final javax.swing.table.TableCellRenderer parentRenderer;
    
    public TableHeaderCellRenderer(JTable table) {
        parentRenderer = table.getTableHeader().getDefaultRenderer();
    }
    
    @Override
    public Component getTableCellRendererComponent(
              JTable table, Object value, boolean isSelected, boolean hasFocus,
              int row, int column) {
        Component c = parentRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
        if (c instanceof DefaultTableCellRenderer label) {
            if (Number.class.isAssignableFrom(table.getModel().getColumnClass(column))) {
                label.setHorizontalAlignment(JLabel.RIGHT);
                label.setHorizontalTextPosition(JLabel.TRAILING);
            } else {
                label.setHorizontalAlignment(JLabel.LEFT);
                label.setHorizontalTextPosition(JLabel.LEADING);
            }
            return label;
        }
        return c;
    }
}
