/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import org.themonkeysdidit.util.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.IOException;

public class ShoutcastServer {
    public ShoutcastServer(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting.");
            System.exit(1);
        }
        
    }
    
    public void runApp() throws IOException {
        
        SERVER_SOCKET = new ServerSocket(LISTEN_PORT);
        
        while(true) {
            
            // Listen for a connection
            Socket client = awaitConnection();
            
            // Got a client, create a streaming thread
            ShoutcastServerStreamer s = new ShoutcastServerStreamer(client);
            
            // Configure it to talk to the database
            s.setDatabaseDetails(MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE, MYSQL_PORT);
            
            // Configure the poll period
            s.setPollPeriod(POLL_PERIOD);
            
            // Configure the source we're listening to
            s.setSource(SOURCE_NAME);
            
            // Set up the txnHandler details
            s.setTxnHandlerDetails(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
            
            // Start it streaming
            s.start();
            
            // await the next connection
        
        }
    }

    private Socket awaitConnection() throws IOException {
        return SERVER_SOCKET.accept();
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
            Logger.log("ERROR", "Unable to find txn handler details in config.");
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
        if(!cfg.enterNode("poll_period")) {
            Logger.log("ERROR", "No batch_time defined.");
            return false;
        }
        else {
            POLL_PERIOD = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("listen_port")) {
            Logger.log("ERROR", "No batch_time defined.");
            return false;
        }
        else {
            LISTEN_PORT = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("source_name")) {
            Logger.log("ERROR", "No source_name defined.");
            return false;
        }
        else {
            SOURCE_NAME = cfg.getValue();
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
    private String SOURCE_NAME;
    private int POLL_PERIOD;
    private int LISTEN_PORT;
    private ServerSocket SERVER_SOCKET;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
}
