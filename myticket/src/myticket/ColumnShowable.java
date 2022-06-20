/*
 * ColumnComparable.java
 *
 * Created on September 22, 2005, 8:44 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

/**
 *
 * @author sdperret
 */
public interface ColumnShowable<E> extends Comparable {
    
        int getColumnCount();
        Object getColumn( int i );
        String getColumnName( int aColumn );
        boolean isCellEditable( int aColumn );
        void setCellEditable( boolean[] aBoolArray );
        
        void setColumn( Object aValue, int aColumn );
        
        /** returns the column used for ordering */
        int getComparableColumn();
        void setComparableColumn( int aColumn );
        
        public Class<?> getColumnClass(int columnIndex);
}
