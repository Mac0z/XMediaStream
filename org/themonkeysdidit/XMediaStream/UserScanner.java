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
import org.themonkeysdidit.io.*;
import java.util.*;
import java.net.Socket;
import java.io.*;

public class UserScanner {
    public UserScanner(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
        LOGGED_IN_USERS = new Hashtable<String, BluetoothUser>();
    }
    
    public void runApp() throws IOException {
        Logger.log("INFO", "Application has started.");
        
        // First, remove any "old" users that are logged in
        deleteAllUsers();
        BluetoothScanner scanner = new BluetoothScanner();
        while(true) {
            try {
                updateUsers(scanner.scan());
                Logger.log("DEBUG", "updateUsers() returned");
                Thread.currentThread().sleep(SCAN_PERIOD);
            }
            catch(InterruptedException ie) {
                // If we are interrupted, it doesn't matter so
                // just carry on.
                Logger.log("WARNING", ie.getMessage());
            }
        }
    }
    
    private boolean storeConfig(ConfigReader cfg) {
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
    
    private void deleteAllUsers() throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_DELETE_ALL_BLUETOOTH_USERS");
        
        sendTransaction(smo);
    }
    
    private void sendTransaction(SimpleMessageObject txn) throws IOException {
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(txn);
        SimpleMessageObject result = bs.receive();
        bs.terminate();
    }
    
    private void updateUsers(BluetoothUser[] scannedUsers) throws IOException {
        Logger.log("DEBUG", "Updating users.");
        Enumeration enumer = LOGGED_IN_USERS.elements();
        while(enumer.hasMoreElements()) {
            BluetoothUser tmpUser = (BluetoothUser)enumer.nextElement();
            tmpUser.setSeen(false);
        }
        
        Logger.log("DEBUG", "Found " + new Integer(scannedUsers.length).toString() + " users.");
        for(int i = 0 ; i < scannedUsers.length ; i++) {
            String id = scannedUsers[i].getId();
            if(LOGGED_IN_USERS.contains(id)) {
                BluetoothUser tmpUser = LOGGED_IN_USERS.get(id);
                tmpUser.setSeen(true);
                Logger.log("DEBUG", "Adding existing user: " + scannedUsers[i].getName());
            }
            else {
                LOGGED_IN_USERS.put(id, scannedUsers[i]);
                Logger.log("DEBUG", "Adding new user: " + scannedUsers[i].getName());
            }
        }
        
        // At this point, any entry in LOGGED_IN_USERS with isSeen == true
        // has been seen in this run through, no harm in adding them again
        // though.
        // Any entries in LOGGED_IN_USERS with isSeen == false need to be
        // deleted from the database and then removed from the hashtable.
        Enumeration enumer2 = LOGGED_IN_USERS.elements();
        Logger.log("DEBUG", new Integer(LOGGED_IN_USERS.size()).toString());
        while(enumer2.hasMoreElements()) {
            Logger.log("DEBUG", "Looping through users");
            BluetoothUser bu = (BluetoothUser)enumer2.nextElement();
            if(bu.isSeen()) {
                Logger.log("DEBUG", "Adding user: " + bu.getName());
                addUser(bu);
            }
            else {
                Logger.log("DEBUG", "Deleting user: " + bu.getName());
                deleteUser(bu);
                LOGGED_IN_USERS.remove(bu.getId());
            }
        }
        Logger.log("DEBUG", "updateUsers() returning");
    }
    
    private void addUser(BluetoothUser bu) throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_ADD_BLUETOOTH_USER");
        smo.addString("ID", bu.getId());
        smo.addString("NAME", bu.getName());
        sendTransaction(smo);
    }
    private void deleteUser(BluetoothUser bu) throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_DELETE_BLUETOOTH_USER");
        smo.addString("ID", bu.getId());
        smo.addString("NAME", bu.getName());
        sendTransaction(smo);
    }
    
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private int DEVICE_ID;
    private int SCAN_PERIOD;
    private Hashtable<String, BluetoothUser> LOGGED_IN_USERS;
}
