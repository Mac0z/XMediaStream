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

import org.themonkeysdidit.util.Logger;
import org.themonkeysdidit.io.SimpleMessageObject;
import java.sql.SQLException;
import java.util.Calendar;

public class FlowControlTxnHandler implements TxnHandlerInterface {
    public FlowControlTxnHandler() {
    
    }
 
    public void init(MySQL_DatabaseHandler dbh) throws SQLException {
        
    }
    
    public boolean handleTxn(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        dbh.storeGeneratedKeys();
        boolean retVal = false;
        if(checkTxn(txn)) {
            int numRows;
            StringBuffer sql;
            String action;
            if(recordPresent(txn, dbh)) {
                sql = new StringBuffer("UPDATE dynamic_flow_control SET last_read_time='");
                sql.append(txn.getString("TIME"));
                sql.append("' WHERE control_name='");
                sql.append(txn.getString("CONTROL_NAME"));
                sql.append("' AND thread='");
                sql.append(txn.getString("THREAD"));
                sql.append("' AND table_name='");
                sql.append(txn.getString("TABLE_NAME"));
                sql.append("'");
                action = UpdateObject.ADD_ACTION;
            }
            else {
                sql = new StringBuffer("INSERT INTO dynamic_flow_control (control_name, thread, table_name, last_read_time) VALUES('");
                sql.append(txn.getString("CONTROL_NAME"));
                sql.append("', '");
                sql.append(txn.getString("THREAD"));
                sql.append("', '");
                sql.append(txn.getString("TABLE_NAME"));
                sql.append("', '");
                sql.append(txn.getString("TIME"));
                sql.append("')");
                action = UpdateObject.MODIFY_ACTION;
            }
            numRows = dbh.update(sql.toString());

            DatabaseResult generatedKeys = dbh.getGeneratedKeys();
            if(generatedKeys.getNumRows() > 0) {
                retVal = true;
            }
            else {
                retVal = false;
            }
        }
        else {
            retVal = false;
        }
        
        return retVal;
    }
    
    private boolean checkTxn(SimpleMessageObject txn) {
        if(txn.getString("CONTROL_NAME").compareTo("-1") == 0) {
            Logger.log("WARNING", "CONTROL_NAME not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getString("THREAD").compareTo("-1") == 0) {
            Logger.log("WARNING", "THREAD not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getString("TABLE_NAME").compareTo("-1") == 0) {
            Logger.log("WARNING", "TABLE_NAME not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getString("TIME").compareTo("-1") == 0) {
            Logger.log("WARNING", "TIME not specified in transaction, rejecting.");
            return false;
        }
        
        return true;
    }
    
    private boolean recordPresent(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM dynamic_flow_control WHERE control_name='");
        sql.append(txn.getString("CONTROL_NAME"));
        sql.append("' AND thread='");
        sql.append(txn.getString("THREAD"));
        sql.append("' AND table_name='");
        sql.append(txn.getString("TABLE_NAME"));
        sql.append("'");
        
        DatabaseResult result = dbh.query(sql.toString());
        DataRow dr = result.getRow(0);
        Long tmpCount = (Long)dr.getValue("COUNT(*)");
        int count = tmpCount.intValue();
        if(count > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
