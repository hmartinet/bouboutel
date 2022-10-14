/*

 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import java.time.LocalDateTime;
import javax.swing.JTable;

/**
 *
 * @author Hervé Martinet
 */
public class TableCellRendererManager {
    public static void setAsDefault(JTable table) {
        table.setDefaultRenderer(
                Double.class, new TableAmountCellRenderer());
        table.setDefaultRenderer(
                LocalDateTime.class, new DateTableCellRenderer());
        table.setDefaultRenderer(
                Enum.class, new TableEnumCellRenderer());
    }
    public static void setAsDefault(JTable table, RowColorSelector selector) {
        table.setDefaultRenderer(
                Object.class, new RowColorTableCellRenderer(selector));
        table.setDefaultRenderer(
                Integer.class, new RowColorTableCellRenderer(selector));
        table.setDefaultRenderer(
                Double.class, new TableAmountCellRenderer(selector));
        table.setDefaultRenderer(
                LocalDateTime.class, new DateTableCellRenderer(selector));
        table.setDefaultRenderer(
                Enum.class, new TableEnumCellRenderer(selector));
    }
}
