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
import org.themonkeysdidit.io.*;
import org.themonkeysdidit.util.*;
import java.util.*;
import java.sql.*;
import java.net.*;
import java.io.*;


/**
******************************************************************************
**
** The MySQL_TransactionHandler deals with the actual database manipulation.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class MySQL_TransactionHandler {
    public MySQL_TransactionHandler(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
        DBH = new MySQL_DatabaseHandler();
        DB_KEEPALIVE = new MySQL_DatabasePoller(DBH);
        
        ACK = new SimpleMessageObject();
        ACK.addString("TXN_TYPE", "ACK");
        
        NACK = new SimpleMessageObject();
        NACK.addString("TXN_TYPE", "NACK");
        
    }
    
    public void runApp() throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        // First, lets initialize the actual txn handling objects
        initTxnHandlers();
        
        // Next listen for a connection:
        ServerSocket server = new ServerSocket(LISTEN_PORT);
        while(true) {
            Socket conn = server.accept();
                
            // Got a connection, handle what we get:
            BufferedSession sess = new BufferedSession();
            sess.initiate(conn);
            SimpleMessageObject txn = sess.receive();
            if(handleTxn(txn)) {
                sess.transmit(ACK);
                checkAndTrimUpdates();
            }
            else {
                sess.transmit(NACK);
            }
        }
    }
    
    private boolean storeConfig(ConfigReader cfg) {
        if(!cfg.enterNode("mysql")) {
            Logger.log("ERROR", "Unable to find mysql details in config");
            return false;
        }
        else {
            if(!storeMysqlDetails(cfg)) {
                return false;
            }
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("general")) {
            Logger.log("ERROR", "Unable to find generald etails in config.");
            return false;
        }
        else {
            if(!storeGeneralDetails(cfg)) {
                return false;
            }
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storeMysqlDetails(ConfigReader cfg) {
        if(!cfg.enterNode("hostname")) {
            Logger.log("ERROR", "No mysql hostname defined.");
            return false;
        }
        else {
            MYSQL_HOSTNAME = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("username")) {
            Logger.log("ERROR", "No mysql username defined.");
            return false;
        }
        else {
            MYSQL_USERNAME = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("password")) {
            Logger.log("ERROR", "No mysql password defined.");
            return false;
        }
        else {
            MYSQL_PASSWORD = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("database")) {
            Logger.log("ERROR", "No mysql database defined.");
            return false;
        }
        else {
            MYSQL_DATABASE = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("port")) {
            Logger.log("ERROR", "No mysql port defined.");
            return false;
        }
        else {
            MYSQL_PORT = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        return true;
        
    }
    
    private boolean storeGeneralDetails(ConfigReader cfg) {
        if(!cfg.enterNode("port")) {
            Logger.log("ERROR", "No port for the txn handler to listen on defined");
            return false;
        }
        else {
            LISTEN_PORT = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        if(!cfg.enterNode("updates_to_keep")) {
            Logger.log("WARNING", "Number of updates tokeep not defined, defaulting to 500");
            UPDATES_TO_KEEP = 500;
        }
        else {
            UPDATES_TO_KEEP = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        return true;
    }
    
    private void initTxnHandlers() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DBH.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
        DB_KEEPALIVE.start();
        SHOUTCAST_BUFFER_TXN_HANDLER = new ShoutcastBufferTxnHandler();
        SHOUTCAST_BUFFER_TXN_HANDLER.init(DBH);
        
        FLOW_CONTROL_TXN_HANDLER = new FlowControlTxnHandler();
        FLOW_CONTROL_TXN_HANDLER.init(DBH);
    
        BLUETOOTH_USER_TXN_HANDLER = new BluetoothUserTxnHandler();
        BLUETOOTH_USER_TXN_HANDLER.init(DBH);
        
        SSDP_TXN_HANDLER = new SSDPTxnHandler();
        SSDP_TXN_HANDLER.init(DBH);
        
        TWITTER_TXN_HANDLER = new TwitterTxnHandler();
        TWITTER_TXN_HANDLER.init(DBH);
    }
    
    private boolean handleTxn(SimpleMessageObject txn) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        boolean retVal = false;
        String txnType = txn.getString("TXN_TYPE");
        if(txnType.compareTo("TXN_ADD_TO_SHOUTCAST_BUFFER") == 0) {
            retVal = SHOUTCAST_BUFFER_TXN_HANDLER.handleTxn(txn, DBH);
            
        }
        else if(txnType.compareTo("TXN_UPDATE_DYNAMIC_FLOW_CONTROL") == 0) {
            retVal = FLOW_CONTROL_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_ADD_BLUETOOTH_USER") == 0) {
            retVal = BLUETOOTH_USER_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_DELETE_BLUETOOTH_USER") == 0) {
            retVal = BLUETOOTH_USER_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_DELETE_ALL_BLUETOOTH_USERS") == 0) {
            retVal = BLUETOOTH_USER_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_ADD_UPNP_DEVICE") == 0) {
            retVal = SSDP_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_REMOVE_UPNP_DEVICE") == 0) {
            retVal = SSDP_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else if(txnType.compareTo("TXN_ADD_RAW_STATUS") == 0) {
            retVal = TWITTER_TXN_HANDLER.handleTxn(txn, DBH);
        }
        else {
            Logger.log("WARNING", "Unknown txn type: " + txnType);
        }
        
        return retVal;
    }
    
    private void checkAndTrimUpdates() throws SQLException {
        Logger.log("DEBUG", "Entering checkAndTrimUpdates");
        String sql = "SELECT COUNT(*) FROM dynamic_update_queue";
        DatabaseResult result = DBH.query(sql);
        DataRow dr = result.getRow(0);
        Long tmpCount = (Long)dr.getValue("COUNT(*)");
        int count = tmpCount.intValue();
        Logger.log("DEBUG", "There are " + Integer.toString(count) + " update queue records");
        if(count > UPDATES_TO_KEEP) {
            trimUpdates(count - UPDATES_TO_KEEP);
        }
    }
    
    private void trimUpdates(int numToDelete) throws SQLException {
        Logger.log("DEBUG", "Entering trimUpdates");
        StringBuffer sqlStmt = new StringBuffer("DELETE FROM dynamic_update_queue ORDER BY update_time limit ");
        sqlStmt.append(Integer.toString(numToDelete));
        int numDeleted = DBH.delete(sqlStmt.toString());
        Logger.log("DEBUG", "Deleted " + Integer.toString(numDeleted) + " records from database");
    }
    
    private String MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE;
    private int MYSQL_PORT;
    private int LISTEN_PORT;
    private ShoutcastBufferTxnHandler SHOUTCAST_BUFFER_TXN_HANDLER;
    private FlowControlTxnHandler FLOW_CONTROL_TXN_HANDLER;
    private BluetoothUserTxnHandler BLUETOOTH_USER_TXN_HANDLER;
    private SSDPTxnHandler SSDP_TXN_HANDLER;
    private TwitterTxnHandler TWITTER_TXN_HANDLER;
    private SimpleMessageObject ACK;
    private SimpleMessageObject NACK;
    private int UPDATES_TO_KEEP;
    private MySQL_DatabaseHandler DBH;
    private MySQL_DatabasePoller DB_KEEPALIVE;
}
