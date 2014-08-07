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

import org.themonkeysdidit.util.*;
import org.themonkeysdidit.io.*;
import java.sql.SQLException;
import java.io.*;
import java.util.Calendar;
import java.util.Hashtable;

/**
******************************************************************************
**
** Instances of this class are used to handle TXN_ADD_TO_SHOUTCAST_BUFFER
** transactions. When one is passed to the handleTxn method, a record is created
** in the dynamic_shoutcast_buffer table and a check is performed to see how
** many records are in there for this source. At present, only 1024 records are
** kept in the table at any one time to prevent the table growing to an
** unmanageable size.
**
** @version 0.1
** @author Oliver Wardell
**
******************************************************************************
*/
public class ShoutcastBufferTxnHandler implements TxnHandlerInterface {
    
    /**
     * The basic constructor performs nothing out of the ordinary.
     */
    public ShoutcastBufferTxnHandler() {
    }
    
    /**
     * The init() method is always called by the main transaction handler once
     * the instance has been created. It is used to perform any initialiseation
     * tasks required before it can start handling transactions.
     */
    public void init(MySQL_DatabaseHandler dbh) throws SQLException {
        PREPARED_STATEMENTS = new Hashtable<String, Integer>();
        int id = dbh.addPreparedStatement("INSERT INTO dynamic_shoutcast_buffer (music_id, raw_data, source_name) VALUES(?, ?, ?)");
        PREPARED_STATEMENTS.put(INSERT_KEY, new Integer(id));
    }
    
    /**
     * This method is called by the main transaction handler whenever a suitable
     * transaction is received. It first performs some checks on the
     * SimpleMessageObject to ensure the required fields are present and then
     * applies the transaction to the database.
     *
     * Once the transaction has been applied it checks to see if any records
     * need trimming from the database.
     *
     * @param txn The SimpleMessageObject of this transaction.
     * @param dbh An already connected MySQL_DatabaseHandler required for
     *        manipulating the database.
     * @return If the update was successful or not.
     */
    public boolean handleTxn(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        dbh.storeGeneratedKeys();
        return txnAddToShoutcastBuffer(txn, dbh);
    }
    
    private boolean txnAddToShoutcastBuffer(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        boolean retVal = false;
        if(checkAddToShoutcastBufferFields(txn)) {
            int musicId = txn.getInt("MUSIC_ID");
            byte[] data = txn.getByteArray("DATA");
            String source = txn.getString("SOURCE_NAME");
            
            Integer tmpInt = PREPARED_STATEMENTS.get(INSERT_KEY);
            int id = tmpInt.intValue();
            dbh.setPreparedStatementInt(id, 1, musicId);
            dbh.setPreparedStatementBytes(id, 2, data);
            dbh.setPreparedStatementString(id, 3, source);
            
            int count = dbh.executePreparedStatementUpdate(id);
            if(count > 0) {
                Logger.log("DEBUG", "Successfully added TXN_ADD_TO_SHOUTCAST_BUFFER transaction.");
                retVal = true;
                checkAndTrim(dbh, source);
            }
            else {
                retVal = false;
            }
        }
        else {
            retVal = false;
        }
        
        Logger.log("DEBUG", "txnAddToShoutcastBuffer returning");
        return retVal;
    }
    
    private boolean checkAddToShoutcastBufferFields(SimpleMessageObject txn) {
        if(txn.getInt("MUSIC_ID") == -1) {
            Logger.log("WARNING", "MUSIC_ID not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getByteArray("DATA")[0] == (byte)-1 && txn.getByteArray("DATA").length == 1) {
            Logger.log("WARNING", "DATA not specified in transaction, rejecting");
            return false;
        }
        
        if(txn.getString("SOURCE_NAME").compareTo("-1") == 0) {
            Logger.log("WARNING", "SOURCE_NAME not specified in transaction, rejecting");
            return false;
        }
        
        return true;
    }
    
    private void checkAndTrim(MySQL_DatabaseHandler dbh, String source) throws SQLException {
        Logger.log("DEBUG", "Checking whether we need to trim any records out.");
        StringBuffer sqlStmt = new StringBuffer("SELECT COUNT(*) FROM dynamic_shoutcast_buffer WHERE source_name = '");
        sqlStmt.append(source);
        sqlStmt.append("'");
        Logger.log("DEBUG", "Counting records with " + sqlStmt.toString());
        
        DatabaseResult dbr = dbh.query(sqlStmt.toString());
        DataRow dr = dbr.getRow(0);

        Long tmpCount = (Long)dr.getValue("COUNT(*)");
        int count = tmpCount.intValue();
        Logger.log("DEBUG", "There are " + Integer.toString(count) + " records for source " + source);
        if(count > MAX_NUM_ROWS) {
            trimRecords(count - MAX_NUM_ROWS, dbh, source);
        }
    }
    
    private void trimRecords(int numToDelete, MySQL_DatabaseHandler dbh, String source) throws SQLException {
        StringBuffer sql = new StringBuffer("DELETE FROM dynamic_shoutcast_buffer WHERE source_name = '");
        sql.append(source);
        sql.append("' ORDER BY update_time limit ");
        sql.append(numToDelete);

        int numDeleted = dbh.delete(sql.toString());
        Logger.log("DEBUG", "Deleted " + Integer.toString(numDeleted) + " records from database");
    }
        
    private final int MAX_NUM_ROWS = 1024;
    private Hashtable<String, Integer> PREPARED_STATEMENTS;
    private final String INSERT_KEY = "INSERT_KEY";
}
