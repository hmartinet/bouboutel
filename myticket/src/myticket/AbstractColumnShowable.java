/*
 * AbstractColumnShowable.java
 *
 * Created on October 10, 2005, 8:46 AM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package myticket;

import java.io.*;
//import javax.swing.*;
//import java.awt.print.*;
import java.util.*;

import java.awt.*;
import javax.swing.*;

/**
 *
 * @author sdperret
 */
public abstract class AbstractColumnShowable extends Component implements ColumnShowable, Serializable {
     
    public int getComparableColumn(){
        return comparableColumn;
    }
    
    public void setComparableColumn( int a ){
        comparableColumn = a;
    }
    
 
    private int comparableColumn = 0;
        
}
