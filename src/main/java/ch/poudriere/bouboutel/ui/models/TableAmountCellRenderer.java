/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.bundles.I18n;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;

/**
 *
 * @author Hervé Martinet
 */
public class TableAmountCellRenderer extends RowColorTableCellRenderer {
    
    public TableAmountCellRenderer() {
        super();
    }
    
    public TableAmountCellRenderer(RowColorSelector selector) {
        super(selector);
    }
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        value = I18n.formatCurrency((Double) value);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        setHorizontalAlignment(JLabel.RIGHT);
        return this;
    }
}
