/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 * XMediaStream is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * XMediaStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.db;

import java.util.Vector;
import java.util.Enumeration;

/**
******************************************************************************
**
** A DataRow is a wrapper to model a row returned when querying a database.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class DataRow {
    
    /**
    * Default constructor does nothing special.
    */
    public DataRow() {
        COLUMNS = new Vector<NameVal>();
    }
    
    /**
    * Add the NameVal object to this DataRow.
    *
    * @param object The NameVal object to add to this row.
    */
    public void add(NameVal object) {
        COLUMNS.add(object);
    }
    
    /**
    * Get the number of columns in this row.
    *
    * @return The number of columns in this row.
    */
    public int getNumColumns() {
        return COLUMNS.size();
    }
    
    /**
    * Get all the columns as an Enumeration.
    *
    * @return An Enumeration of all columns in this DataRow. The elements
    * of the Enumeration must be cast as NameVal objects before use.
    */
    public Enumeration getColumns() {
        return COLUMNS.elements();
    }
    
    /**
    * Access method to get the value stored in the column passed as an argument.
    *
    * @param colName The co,umn name to retrieve the value for.
    * @return The object stored in the named column.
    */
    public Object getValue(String colName) {
        Enumeration enumer = getColumns();
        while(enumer.hasMoreElements()) {
            NameVal col = (NameVal)enumer.nextElement();
            if(col.getName().compareTo(colName) == 0) {
                return col.getValue();
            }
        }
        return null;
    }
    
    /**
    * Access method to get all the column names in this DataRow.
    *
    * @return A String array of all the column names in this DataRow.
    */
    public String[] getColumnNames() {
        Enumeration enumer = getColumns();
        String[] retVal = new String[getNumColumns()];
        int i = 0;
        while(enumer.hasMoreElements()) {
            NameVal col = (NameVal)enumer.nextElement();
            retVal[i] = col.getName();
            i++;
        }
        
        return retVal;
    }
    
    private Vector<NameVal> COLUMNS;
}
