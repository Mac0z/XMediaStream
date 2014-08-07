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

import org.themonkeysdidit.io.*;
import org.themonkeysdidit.util.*;
import java.sql.*;
/**
******************************************************************************
**
** The BluetoothUserTxnHandler is responsible for handling transactions sent
** to add or delete users from the dynamic_bluetooth_users table in the
** MySQL database. It accepts the following TXN_TYPEs<br />
** <code><br />
** TXN_DELETE_ALL_BLUETOOTH_USERS<br />
** TXN_ADD_BLUETOOTH_USER<br />
** TXN_DELETE_BLUETOOTH_USER<br />
** </code><br />
** More detail can be found on each of these transactions in the handleTxn()
** comments below.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class BluetoothUserTxnHandler implements TxnHandlerInterface {

    /**
    * Default constructor takes no arguments.
    */
    public BluetoothUserTxnHandler() {
    }
    
    /**
    * The init method is here purely to implement the TxnHandlerInterface,
    * no specific initialisation is performed.
    *
    * @param dbh The MySQL_DatabaseHandler that will be used to interact
    *            with the MySQL database.
    */
    public void init(MySQL_DatabaseHandler dbh) throws SQLException {
        
    }
    
    /**
    * Takes the transaction in the SimpleMessageObject (txn) and makes the
    * necessary changes to the database according to the TXN_TYPE.
    * <ul>
    * <li><strong>TXN_DELETE_ALL_BLUETOOTH_USERS</strong>. Deletes all records in
    * the dynamic_bluetooth_users table.<br /
    * SimpleMessageObject format:<br />
    * String: TXN_TYPE - One of TXN_DELETE_ALL_BLUETOOTH_USERS, 
    * TXN_ADD_BLUETOOTH_USER, TXN_DELETE_BLUETOOTH_USER. For
    * TXN_DELETE_ALL_BLUETOOTH_USERS no other fields are required and are ignored
    * if present.<br />
    * String: ID - The bluetooth hardware ID of the device found.<br />
    * String: NAME - The free format name of the bluetooth device scanned.</li>
    * <li><strong>TXN_ADD_BLUETOOTH_USER</strong>. If a dynamic_bluetooth_users
    * record already exists for this ID, do nothing. Else look for the bluetooth_id
    * in the system_static_users table; if found create a dynamic_bluetooth_users
    * record with that user_id, if not found add a record to the dynamic_bluetooth_users
    * table as the guest user. If no guest user is found, do nothing.</li>
    * <li><strong>TXN_DELETE_BLUETOOTH_USER</strong>. Delete the dynamic_bluetooth_users
    * record where bluetooth_id is equal to ID. If there is no record, do nothing.</li>
    *
    * @param txn The SimplaeMessageObject of the transaction, defined above.
    * @param dbh The MySQL_DatabaseHandler (that is already connected to
    *            the databse) used to perform the operations on the database.
    *
    * @return True if the transaction was successfully applied to the database,
    *         false otherwise.
    */
    public boolean handleTxn(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        dbh.storeGeneratedKeys();
        boolean retVal = false;
        String type = txn.getString("TXN_TYPE");
        if(checkTxn(txn)) {
            if(type.compareTo("TXN_DELETE_ALL_BLUETOOTH_USERS") == 0) {
                retVal = deleteAllUsers(dbh);
            }
            else if(type.compareTo("TXN_ADD_BLUETOOTH_USER") == 0) {
                retVal = addUser(txn, dbh);
            }
            else if(type.compareTo("TXN_DELETE_BLUETOOTH_USER") == 0) {
                retVal = deleteUser(txn, dbh);
            }
            else {
                Logger.log("WARNING", "Unknown transaction type passed to BluetoothUserTxnHandler, ignoring");
                retVal = false;
            }
        }
        return retVal;
    }
    
    private boolean checkTxn(SimpleMessageObject txn) {
        if(txn.getString("TXN_TYPE").compareTo("TXN_DELETE_ALL_BLUETOOTH_USERS") == 0) {
            return true;
        }
        
        if(txn.getString("ID").compareTo("-1") == 0) {
            Logger.log("WARNING", "ID not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getString("NAME").compareTo("-1") == 0) {
            Logger.log("WARNING", "NAME not specified in transaction, rejecting");
            return false;
        }
     
        return true;
    }
    
    private boolean deleteAllUsers(MySQL_DatabaseHandler dbh) throws SQLException {
        String sql = "DELETE FROM dynamic_bluetooth_users";
        dbh.delete(sql);
        return true;
    }
    
    private boolean addUser(SimpleMessageObject smo, MySQL_DatabaseHandler dbh) throws SQLException {
        // Is this user already logged in?
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM dynamic_bluetooth_users ");
        sql.append("WHERE bluetooth_id = '");
        sql.append(smo.getString("ID"));
        sql.append("'");
        DatabaseResult res = dbh.query(sql.toString());
        DataRow dataRow = res.getRow(0);
        Long tmpCount = (Long)dataRow.getValue("COUNT(*)");
        if(tmpCount.intValue() > 0) {
            return true;
        }
        else {
            
            // First try and find this user
            sql = new StringBuffer("SELECT user_id FROM ");
            sql.append("system_static_users WHERE bluetooth_id = '");
            sql.append(smo.getString("ID"));
            sql.append("'");
            
            DatabaseResult result = dbh.query(sql.toString());
            Integer user_id;
            if(result.getNumRows() != 0) {
                DataRow dr = result.getRow(0);
                user_id = (Integer)dr.getValue("user_id");
            }
            else {
                StringBuffer getGuest = new StringBuffer("SELECT user_id ");
                getGuest.append("FROM system_static_users WHERE user_name ");
                getGuest.append("= 'guest'");
                DatabaseResult tmpRes = dbh.query(getGuest.toString());
                if(tmpRes.getNumRows() == 0) {
                    Logger.log("WARNING", "No guest user set up, dropping bluetooth user: " + smo.getString("NAME"));
                    user_id = null;
                    return false;
                }
                else {
                    DataRow row = tmpRes.getRow(0);
                    user_id = (Integer)row.getValue("user_id");
                }
            }
            
            // user_id is in user_id
            String user_idString = user_id.toString().replaceAll("'", "''");
            String user_name = smo.getString("NAME").replaceAll("'", "''");
            String btId = smo.getString("ID").replaceAll("'", "''");
            sql = new StringBuffer("INSERT INTO dynamic_bluetooth_users ");
            sql.append("(user_id, user_string, bluetooth_id) ");
            sql.append("VALUES ('");
            sql.append(user_id.toString());
            sql.append("', '");
            sql.append(user_name);
            sql.append("', '");
            sql.append(btId);
            sql.append("')");
            
            int updates = dbh.insert(sql.toString());
            if(updates > 0) {
                return true;
            }
            else {
                return false;
            }
        }
    }
    
    private boolean deleteUser(SimpleMessageObject smo, MySQL_DatabaseHandler dbh) throws SQLException {
        StringBuffer sql = new StringBuffer("DELETE FROM dynamic_bluetooth_users ");
        sql.append("WHERE bluetooth_id = '");
        sql.append(smo.getString("ID"));
        sql.append("'");
        
        if(dbh.delete(sql.toString()) > 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
