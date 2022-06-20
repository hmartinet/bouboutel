/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.bundles.I18n;
import java.awt.Component;
import javax.swing.JTable;

/**
 *
 * @author Hervé Martinet
 */
public class TableEnumCellRenderer extends RowColorTableCellRenderer {
    
    public TableEnumCellRenderer() {
        super();
    }
    
    public TableEnumCellRenderer(RowColorSelector selector) {
        super(selector);
    } 
    
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        value = I18n.get("enum.price.action.%s".formatted(value),
                value.toString());
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
                row, column);
        return this;
    }
}
