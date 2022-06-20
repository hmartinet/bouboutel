/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.bundles.I18n;
import java.awt.Component;
import java.time.LocalDateTime;
import javax.swing.JTable;

/**
 *
 * @author Hervé Martinet
 */
public class DateTableCellRenderer extends RowColorTableCellRenderer {
    
    public DateTableCellRenderer() {
        super();
    }
    
    public DateTableCellRenderer(RowColorSelector selector) {
        super(selector);
    }    
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        value = I18n.formatDate((LocalDateTime) value);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        return this;
    }
}
