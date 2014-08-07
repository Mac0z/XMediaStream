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
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.themonkeysdidit.util.Logger;

/**
******************************************************************************
**
** The DatabaseResult class i used as a wrapper around a result returned from
** performing an operation on a databse (a query for example). It contains
** methods used to get at the rows returned, as well as find how many rows
** were returned in total.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class DatabaseResult {
    
    /**
    * The constructor takes a java.sql.ResultSet as it's argument and parses
    * this into an internal format, ready for use by the other methods.
    *
    * @param rs The result set to contruct the DatabaseResult from.
    */
    public DatabaseResult(ResultSet rs) throws SQLException {
        
        NUM_ROWS = 0;
        ROWS = new Vector<DataRow>();

        while(rs.next()) {
            NUM_ROWS++;
            addToRows(rs);
        }
    }
    
    /**
    * Access method to get the number of rows in this DatabaseResult.
    *
    * @return the number of rows in this DatabaseResult.
    */
    public int getNumRows() {
        return NUM_ROWS;
    }
    
    /**
    * Access method to get an Enumeration of all the rows in this DatabaseResult.
    *
    * @return An Enumeration of all DataRow objects in this DatabaseReslt. Each
    * element of the Enumeration must be cast to a DataRow before use.
    */
    public Enumeration getRows() {
        return ROWS.elements();
    }
    
    /**
    * Get the DataRow from this DatabaseResult that was the rowNumber result
    * from the database query.
    *
    * @param rowNumber The row number to return.
    * @return thata DataRow for the specified rowNumber.
    */
    public DataRow getRow(int rowNumber) {
        return ROWS.elementAt(rowNumber);
    }
    
    private void addToRows(ResultSet rs) throws SQLException {
        DataRow dr = new DataRow();
        ResultSetMetaData rsmd = rs.getMetaData();
        Logger.log("DEBUG", "There are " + Integer.toString(rsmd.getColumnCount()) + " columns in this row");
        for(int i = 1 ; i <= rsmd.getColumnCount() ; i++) {
            String colName = rsmd.getColumnName(i);
            Logger.log("DEBUG", "Trying to find column: " + colName);
            // This is a horrible hack to allow use of generated keys as
            // these don't store a colName against them.
            Object value;
            if(colName == null) {
                colName = "KEY_VALUE";
                value = rs.getObject(i);
                Logger.log("DEBUG", "Key value detected in row");
            }
            else {
                value = rs.getObject(colName);
            }
            Logger.log("DEBUG", "Found column");
            Logger.log("DEBUG", "Storing column: " + colName);
            dr.add(new NameVal(colName, value));
        }
        
        ROWS.add(dr);
    }
    
    private int NUM_ROWS;
    private Vector<DataRow> ROWS;
}
