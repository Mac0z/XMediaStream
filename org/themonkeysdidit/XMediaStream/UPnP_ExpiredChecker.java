/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.XMediaStream;

import java.net.*;
import java.io.*;
import java.sql.*;

import org.themonkeysdidit.util.*;
import org.themonkeysdidit.db.*;
import org.themonkeysdidit.io.*;

public class UPnP_ExpiredChecker {
    public UPnP_ExpiredChecker(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        DBH = new MySQL_DatabaseHandler();
    }
    
    public void runApp() throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        while(true) {
            // get any expired upnp servers
            DatabaseResult expired = getExpired();
            
            // delete them
            deleteExpired(expired);
            
            // sleep
            try {
                Thread.currentThread().sleep(SCAN_PERIOD);
            }
            catch(InterruptedException inter) {
            }
        }
    }
    
    private DatabaseResult getExpired() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String sql = "SELECT usn, nt FROM dynamic_upnp_instances WHERE EXPIRE_TIME<CURRENT_TIMESTAMP";
        
        DBH.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
        DatabaseResult result = DBH.query(sql);
        DBH.disconnect();
        
        return result;
    }
    
    private void deleteExpired(DatabaseResult expired) throws IOException {
        
        for(int i = 0 ; i < expired.getNumRows() ; i++) {
            DataRow dr = expired.getRow(i);
            String usn = (String)dr.getValue("usn");
            String nt = (String)dr.getValue("nt");
            Logger.log("INFO", "The USN: " + usn + " NT: " + nt + " has expired, deleting.");
            SimpleMessageObject smo = new SimpleMessageObject();
            smo.addString("TXN_TYPE", "TXN_REMOVE_UPNP_DEVICE");
            smo.addString("USN", usn);
            smo.addString("NT", nt);
            sendTxn(smo);
        }
    }
    
    private void sendTxn(SimpleMessageObject smo) throws IOException {
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(smo);
        SimpleMessageObject result = bs.receive();
        bs.terminate();
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
            Logger.log("ERROR", "Unable to find general details in config.");
            return false;
        }
        else {
            if(!storeGeneralDetails(cfg)) {
                return false;
            }
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("txn_handler")) {
            Logger.log("ERROR", "Unable to find txn_handler details in config.");
            return false;
        }
        else {
            if(!storeTxnHandlerDetails(cfg)) {
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
        if(!cfg.enterNode("scan_period")) {
            Logger.log("ERROR", "No scan_period defined.");
            return false;
        }
        else {
            SCAN_PERIOD = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storeTxnHandlerDetails(ConfigReader cfg) {
        if(!cfg.enterNode("hostname")) {
            Logger.log("ERROR", "No txn handler hostname defined.");
            return false;
        }
        else {
            TXN_HANDLER_HOSTNAME = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("port")) {
            Logger.log("ERROR", "No txn handler port defined.");
            return false;
        }
        else {
            TXN_HANDLER_PORT = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        return true;
    }
    
    private String MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE;
    private int MYSQL_PORT;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private int SCAN_PERIOD;
    private MySQL_DatabaseHandler DBH;
}
