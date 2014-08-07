/*
 ******************************************************************************
 *
 * Copyright 2008 Oliver Wardell
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
import java.sql.*;
import java.util.Enumeration;
import org.themonkeysdidit.util.*;

/**
******************************************************************************
**
** The MySQL_TableScanner.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class MySQL_TableScanner {
    public MySQL_TableScanner() {
        LAST_UPDATE_TIMESTAMP = 0;
        DBH = new MySQL_DatabaseHandler();
    }
    
    public MySQL_TableScanner(long lastUpdateTime) {
        LAST_UPDATE_TIMESTAMP = lastUpdateTime;
        DBH = new MySQL_DatabaseHandler();
    }
    
    /**
     * @param tableOfInterest The table to watch for updates on.
     * @param updateTypes The types of update we are interested in. In binary form:
     *                    00000001 = add
     *                    00000010 = update
     *                    00000100 = delete
     * Obviously these can be OR'd together.
     *
     * @return A ResultSet of the records of interest.
     */
    public DatabaseResult scan(String tableOfInterest, byte updateTypes) throws SQLException {
        return scan(tableOfInterest, updateTypes, 50);
    }
    
    /**
     * @param tableOfInterest The table to watch for updates on.
     * @param updateTypes The types of update we are interested in. In binary form:
     *                    00000001 = add
     *                    00000010 = update
     *                    00000100 = delete
     * Obviously these can be OR'd together.
     * @param pollPeriod How often to check for updates in ms.
     *
     * @return A DatabaseResult of the records of interest.
     */
    public DatabaseResult scan(String tableOfInterest, byte updateTypes, int pollPeriod) throws SQLException{
        
        Logger.log("DEBUG", "Scanning for updates on table: " + tableOfInterest);
        boolean keepScanning = true;
        DatabaseResult retVal = null;
        
        while(keepScanning) {
            DatabaseResult updates = scanForUpdates(tableOfInterest, updateTypes);
            if(updates.getNumRows() > 0) {
                Logger.log("DEBUG", "Update event occurred");
                Enumeration enumer = updates.getRows();
                // HERE, need to loop round pulling out every record from this set
                long[] recNumbers = new long[updates.getNumRows()];
                int i = 0;
                while(enumer.hasMoreElements()) {
                    DataRow dr = (DataRow)enumer.nextElement();
                    Long num = (Long)dr.getValue("updated_table_record_number");
                    recNumbers[i] = num.longValue();
                    i++;
                    Long updateTime = (Long)dr.getValue("internal_update_time");
                    if(updateTime.longValue() > LAST_UPDATE_TIMESTAMP) {
                        LAST_UPDATE_TIMESTAMP = updateTime.longValue();
                    }
                }
                retVal = getActualRecords(recNumbers, tableOfInterest);
                keepScanning = false;
            }
                    
            if(keepScanning) {
                try {
                    Logger.log("DEBUG", "Sleeping...");
                    Thread.currentThread().sleep(pollPeriod); 
                }
                catch(InterruptedException inter) {
                }
            }
        }
        
        return retVal;
    }
    
    public void connect(String hostname, int port, String username, String password, String database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DBH.connect(hostname, port, username, password, database);
    }
    
    public void disconnect() throws SQLException {
        DBH.disconnect();
    }
    
    private DatabaseResult scanForUpdates(String table, byte type) throws SQLException {
        
        String updateTypes = "AND (update_action = ";
        boolean prev = false;
        if((type & 0x01) == 0x01) {
            // care about inserts
            updateTypes = updateTypes.concat("'A'");
            prev = true;
        }
        if((type & 0x02) == 0x02) {
            // care about updates
            if(prev) {
                updateTypes = updateTypes.concat(" OR update_action = 'M'");
            }
            else {
                updateTypes = updateTypes.concat("update_action = 'M'");
            }
            prev = true;
        }
        if((type & 0x04) == 0x04) {
            //care about deletes
            if(prev) {
                updateTypes = updateTypes.concat(" OR update_action = 'D'");
            }
            else {
                updateTypes = updateTypes.concat("update_action = 'D'");
            }
            prev = true;
        }
        updateTypes = updateTypes.concat(")");
        
        StringBuffer sqlStatement = new StringBuffer("SELECT * FROM dynamic_update_queue");
        sqlStatement.append(" WHERE internal_update_time>");
        sqlStatement.append(LAST_UPDATE_TIMESTAMP);
        sqlStatement.append(" AND table_name = '");
        sqlStatement.append(table);
        sqlStatement.append("' ");
        sqlStatement.append(updateTypes);
        sqlStatement.append(" ORDER BY internal_update_time");
        
        return DBH.query(sqlStatement.toString());
    }
    
    private DatabaseResult getActualRecords(long[] recordNumbers, String tableName) throws SQLException {
        if(recordNumbers.length == 0) {
            Logger.log("ERROR", "MySQLTableScanner has no record numbers to get.");
            return null;
        }
        else {
            StringBuffer sqlStatement = new StringBuffer("SELECT * FROM ");
            sqlStatement.append(tableName);
            sqlStatement.append(" WHERE (");
            for(int i = 0 ; i < recordNumbers.length ; i++) {
                sqlStatement.append("record_number = ");
                sqlStatement.append(recordNumbers[i]);
                if(i != recordNumbers.length - 1) {
                    sqlStatement.append(" OR ");
                }
            }
            sqlStatement.append(")");
            return DBH.query(sqlStatement.toString());
        }
    }
    
    private long LAST_UPDATE_TIMESTAMP;
    private MySQL_DatabaseHandler DBH;
}