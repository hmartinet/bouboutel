/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ch.poudriere.bouboutel.ui.models;

import ch.poudriere.bouboutel.models.AbstractModel;
import ch.poudriere.bouboutel.models.ModelList;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Hervé Martinet <herve.martinet@gmail.com>
 * @param <T>
 */
public class DataTableModel<T extends AbstractModel> extends AbstractTableModel
        implements TableModel {
    ModelList<T> modelList;
    List<String> names;
    Map<Integer, DataTableModelColumn> columns = new HashMap<>();
    private boolean editable = true;

    public DataTableModel(ModelList<T> modelList, Class<T> modelClass,
            String... names) {
        this.modelList = modelList;
        this.names = Arrays.asList(names);
        try {
            BeanInfo info = Introspector.getBeanInfo(modelClass);
            for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
                if (this.names.contains(pd.getName())) {
                    columns.put(this.names.indexOf(pd.getName()),
                            new DataTableModelColumn(pd));
                }
            }
        } catch (IntrospectionException ex) {
            System.out.println("IntrospectionException");
            Logger.getLogger(DataTableModel.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public void setColumn(DataTableModelColumn column) {
        columns.put(this.names.indexOf(column.getName()), column);
    }

    @Override
    public int getRowCount() {
        return modelList.size();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getLabel();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getClazz();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if (!editable) {
            return false;
        }
        return columns.get(columnIndex).isEditable();
    }

    public T getRowModel(int rowIndex) {
        return modelList.getByIndex(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return columns.get(columnIndex).getGetter().apply(getRowModel(rowIndex));
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        columns.get(columnIndex).getSetter().
                apply(getRowModel(rowIndex), aValue);
    }

    /**
     * @return the editable
     */
    public boolean isEditable() {
        return editable;
    }

    /**
     * @param editable the editable to set
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public ModelList<T> getModelList() {
        return modelList;
    }

    public void setModelList(ModelList<T> modelList) {
        this.modelList = modelList;
    }
}
