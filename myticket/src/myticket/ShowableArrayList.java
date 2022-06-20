/*
 * ShowableArrayList.java
 *
 * Created on September 22, 2005, 8:16 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.util.ArrayList;
import java.util.Date;
import javax.swing.ListModel;
import javax.swing.table.TableModel;
import javax.swing.ComboBoxModel;
import java.util.EventListener; 
import javax.swing.event.*;
import javax.print.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.text.*;

import java.util.StringTokenizer;
import java.util.Collections;

/**
 *
 * @author sdperret
 */
public class ShowableArrayList<E> extends ArrayList<E> implements ListModel, TableModel, Printable, ComboBoxModel {

    String tableTitle = "";
    String tableFooter = "";
    
    private Object selectedObject = null;
    
    
    // REQUIRED BY TABLEMODEL
    
    
    /**
     *  Returns a default name for the column using spreadsheet conventions:
     *  A, B, C, ... Z, AA, AB, etc.  If <code>column</code> cannot be found,
     *  returns an empty string.
     *
     * @param column  the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        if( getSize()> 0 ){
            return ((ColumnShowable) get(0)).getColumnName( column );
        }else{
	  String result = "";
	  for (; column >= 0; column = column / 26 - 1) {
	      result = (char)((char)(column%26)+'A') + result;
	  }
          return result;
        }
    }

    /**
     * Returns a column given its name.
     * Implementation is naive so this should be overridden if
     * this method is to be called often. This method is not
     * in the <code>TableModel</code> interface and is not used by the
     * <code>JTable</code>.
     *
     * @param columnName string containing name of column to be located
     * @return the column with <code>columnName</code>, or -1 if not found
     */
    public int findColumn(String columnName) {
        for (int i = 0; i < getColumnCount(); i++) {
            if (columnName.equals(getColumnName(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
     *
     *  @param columnIndex  the column being queried
     *  @return the Object.class
     */
    public Class<?> getColumnClass(int columnIndex){
	//return Object.class;
        if( size() > 0 ){
            E element = get(0);
            return ( (ColumnShowable) element ).getColumnClass( columnIndex );
        }else{
            return Object.class;
        }
    }

    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  rowIndex  the row being queried
     *  @param  columnIndex the column being queried
     *  @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return ((ColumnShowable) get( rowIndex )).isCellEditable( columnIndex );
    }
    public void setEditableColumns( boolean[] aBoolArray ){
        //if( getRowCount() > 0 ){
            //((ColumnShowable) this.get(0)).setCellEditable( aBoolArray );
        //}
        for( E element : this ){
            ( (ColumnShowable) element ).setCellEditable( aBoolArray );
        }
            
    }

    /**
     *  This empty implementation is provided so users don't have to implement
     *  this method if their data model is not editable.
     *
     *  @param  aValue   value to assign to cell
     *  @param  rowIndex   row of cell
     *  @param  columnIndex  column of cell
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        ((ColumnShowable) get( rowIndex )).setColumn( aValue, columnIndex );
        fireTableDataChanged();
    }


//
//  Managing Listeners
//

    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void addTableModelListener(TableModelListener l) {
	listenerList.add(TableModelListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a
     * change to the data model occurs.
     *
     * @param	l		the TableModelListener
     */
    public void removeTableModelListener(TableModelListener l) {
	listenerList.remove(TableModelListener.class, l);
    }

    /**
     * Returns an array of all the table model listeners 
     * registered on this model.
     *
     * @return all of this model's <code>TableModelListener</code>s 
     *         or an empty
     *         array if no table model listeners are currently registered
     *
     * @see #addTableModelListener
     * @see #removeTableModelListener
     *
     * @since 1.4
     */
    public TableModelListener[] getTableModelListeners() {
        return (TableModelListener[])listenerList.getListeners(
                TableModelListener.class);
    }

//
//  Fire methods
//

    /**
     * Notifies all listeners that all cell values in the table's
     * rows may have changed. The number of rows may also have changed
     * and the <code>JTable</code> should redraw the
     * table from scratch. The structure of the table (as in the order of the
     * columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     * @see javax.swing.JTable#tableChanged(TableModelEvent)
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed.
     * The number of columns in the table, and the names and types of
     * the new columns may be different from the previous state.
     * If the <code>JTable</code> receives this event and its
     * <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the
     * same as calling <code>setModel(TableModel)</code> on the
     * <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param  firstRow  the first row
     * @param  lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     *
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been updated.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsUpdated(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow  the first row
     * @param lastRow   the last row
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
    }

    /**
     * Notifies all listeners that the value of the cell at 
     * <code>[row, column]</code> has been updated.
     *
     * @param row  row of cell which has been updated
     * @param column  column of cell which has been updated
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableCellUpdated(int row, int column) {
        fireTableChanged(new TableModelEvent(this, row, row, column));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered
     * themselves as listeners for this table model.
     *
     * @param e  the event to be forwarded
     *
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i]==TableModelListener.class) {
		((TableModelListener)listeners[i+1]).tableChanged(e);
	    }
	}
    }

    /**
     * Returns an array of all the objects currently registered
     * as <code><em>Foo</em>Listener</code>s
     * upon this <code>AbstractTableModel</code>.
     * <code><em>Foo</em>Listener</code>s are registered using the
     * <code>add<em>Foo</em>Listener</code> method.
     *
     * <p>
     *
     * You can specify the <code>listenerType</code> argument
     * with a class literal,
     * such as
     * <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a
     * model <code>m</code>
     * for its table model listeners with the following code:
     *
     * <pre>TableModelListener[] tmls = (TableModelListener[])(m.getListeners(TableModelListener.class));</pre>
     *
     * If no such listeners exist, this method returns an empty array.
     *
     * @param listenerType the type of listeners requested; this parameter
     *          should specify an interface that descends from
     *          <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s on this component,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code>
     *          doesn't specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     * 
     * @see #getTableModelListeners
     *
     * @since 1.3
     */
    public <T extends EventListener> T[] getListeners(Class<T> listenerType) { 
	return listenerList.getListeners(listenerType); 
    }


    
    
    // REQUIRED BY LISTMODEL
    
    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be added
     */  
    public void addListDataListener(ListDataListener l) {
	listenerList.add(ListDataListener.class, l);
    }


    /**
     * Removes a listener from the list that's notified each time a 
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */  
    public void removeListDataListener(ListDataListener l) {
	listenerList.remove(ListDataListener.class, l);
    }


    /**
     * Returns an array of all the list data listeners
     * registered on this <code>AbstractListModel</code>.
     *
     * @return all of this model's <code>ListDataListener</code>s,
     *         or an empty array if no list data listeners
     *         are currently registered
     * 
     * @see #addListDataListener
     * @see #removeListDataListener
     * 
     * @since 1.4
     */
    public ListDataListener[] getListDataListeners() {
        return (ListDataListener[])listenerList.getListeners(
                ListDataListener.class);
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements of the list change.  The changed elements
     * are specified by the closed interval index0, index1 -- the endpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).contentsChanged(e);
	    }	       
	}
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements are added to the model.  The new elements
     * are specified by a closed interval index0, index1 -- the enpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalAdded(e);
	    }	       
	}
    }


    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model. 
     * <code>index0</code> and <code>index1</code> are the end points
     * of the interval that's been removed.  Note that <code>index0</code>
     * need not be less than or equal to <code>index1</code>.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval,
     *               including <code>index0</code>
     * @param index1 the other end of the removed interval,
     *               including <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
	Object[] listeners = listenerList.getListenerList();
	ListDataEvent e = null;

	for (int i = listeners.length - 2; i >= 0; i -= 2) {
	    if (listeners[i] == ListDataListener.class) {
		if (e == null) {
		    e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
		}
		((ListDataListener)listeners[i+1]).intervalRemoved(e);
	    }	       
	}
    }

    /**
     * Returns an array of all the objects currently registered as
     * <code><em>Foo</em>Listener</code>s
     * upon this model.
     * <code><em>Foo</em>Listener</code>s
     * are registered using the <code>add<em>Foo</em>Listener</code> method.
     * <p>
     * You can specify the <code>listenerType</code> argument
     * with a class literal, such as <code><em>Foo</em>Listener.class</code>.
     * For example, you can query a list model
     * <code>m</code>
     * for its list data listeners
     * with the following code:
     *
     * <pre>ListDataListener[] ldls = (ListDataListener[])(m.getListeners(ListDataListener.class));</pre>
     *
     * If no such listeners exist,
     * this method returns an empty array.
     *
     * @param listenerType  the type of listeners requested;
     *          this parameter should specify an interface
     *          that descends from <code>java.util.EventListener</code>
     * @return an array of all objects registered as
     *          <code><em>Foo</em>Listener</code>s
     *          on this model,
     *          or an empty array if no such
     *          listeners have been added
     * @exception ClassCastException if <code>listenerType</code> doesn't
     *          specify a class or interface that implements
     *          <code>java.util.EventListener</code>
     *
     * @see #getListDataListeners
     *
     * @since 1.3
     */
    //public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
	//return listenerList.getListeners(listenerType); 
    //}
    
    
    public Object getElementAt( int i ){
        return( getValueAt( i , 0 ) );
    }
    public int getSize(){
        return( getRowCount() );
    }
    
    
    public Object getValueAt( int aRow, int aColumn ){
            return ((ColumnShowable) get( aRow )).getColumn( aColumn );
    }
    
    public int getRowCount(){
        return size();
    }
    
    public int getColumnCount(){
        if( getRowCount() > 0 ){
            return( ((ColumnShowable) this.get(0)).getColumnCount() );
        }else{
          return( 1 );
        }
    }
    
    public boolean add( E o ){
        boolean answer = super.add( o );
        fireTableDataChanged();
        return( answer );
    }
    
    public boolean remove(Object o){
        boolean answer = super.remove( o );
        fireTableDataChanged();
        return( answer );
    }
    
    
    public String getTableTitle(){
        return( tableTitle );
    }
    
    public void setTableTitle( String a ){
        tableTitle = a;
    }
    
    public String getTableFooter(){
        return( tableFooter );
    }
    
    public void setTableFooter( String a ){
        tableFooter = a;
    }
    
    
    public Object getSelectedItem(){
        return selectedObject;
    }
    public void setSelectedItem( Object item ){
        selectedObject = item;
        fireContentsChanged(this, -1, -1);
    }
    
    
    public int print( Graphics g, PageFormat pageFormat, int pageIndex ){
    
        if( pageIndex * 40 > size() ){
            return Printable.NO_SUCH_PAGE; 
        }else{
        
            /* We'll assume that Jav2D is available.
            */
            Graphics2D g2d = (Graphics2D) g;

            /* Move the origin from the corner of the Paper to the corner
            * of the imageable area.
            */
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            /* Set the text color.
            */
            g2d.setPaint(Color.black);
        
            /* Use a LineBreakMeasurer instance to break our text into
            * lines that fit the imageable area of the page.
            */
            Point2D.Float pen = new Point2D.Float();
        
            //System.out.println( "PRINT: " + size() + " " + pageIndex );
        
        
            int lc = 14;
        
            g2d.setFont( new Font("Monospaced", Font.BOLD, 12) );
            
            StringTokenizer st = new StringTokenizer( getTableTitle(), "|" );
            while( st.hasMoreTokens() ){
                g2d.drawString( st.nextToken(), 5, lc );
                lc += 14;
            }   
        
        
            
            g2d.setFont( new Font("Monospaced", Font.PLAIN, 12) );
        
            //g2d.drawString( getTitle(), 2, 14 );
            g2d.drawString( "(page " + (pageIndex + 1) + " imprimĂŠe le: " + key.Preferrences.getDateFormater().format( new Date() ) +")", 5, lc );
            
            lc += 14;
            lc += 14;
        
        
            // for each line in the table
        
            for( int i = pageIndex * 40; i < ( Math.min( size(), ( (pageIndex + 1 ) * 40) ) ); i++ ){
                System.out.println( "PRINT: adding line " + i );
                String line = get( i ).toString();
                g2d.drawString( line, 5, lc );
                lc += 14;
            
            }
        
            
            g2d.setFont( new Font("Monospaced", Font.BOLD, 10) );
            lc += 14;
            if( ((pageIndex+1) * 40) > size() ){
                // print totals
                st = new StringTokenizer( getTableFooter(), "|" );
                while( st.hasMoreTokens() ){
                    g2d.drawString( st.nextToken(), 5, lc );
                    lc += 14;
                }
            }
            
            
            return Printable.PAGE_EXISTS;
        }
    }
    
    
    
    
    /** Creates a new instance of ShowableArrayList */
    //public ShowableArrayList() {
    //}
    
    
    
    
}
