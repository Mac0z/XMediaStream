/*
 ******************************************************************************
 *
 * Copyright 2010 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.Twitter;
 
import java.net.*;
import java.io.*;
 
import org.themonkeysdidit.util.*;
import org.themonkeysdidit.io.*;

public class TwitterFeedReader {
    
    public TwitterFeedReader(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
    }
    
    public void runApp() throws MalformedURLException, IOException {
    
        BasicHttpAuth auth = new BasicHttpAuth();
        auth.setAuthDetails(TWITTER_USERNAME, TWITTER_PASSWORD);
        Authenticator.setDefault(auth);
        
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        SESSION = new BufferedSession();
        SESSION.initiate(s);
        
        while(true) {
            
            try {
                URL url = new URL(FEED_URL);
                BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
                for( String line = br.readLine() ; line != null ; line = br.readLine()) {
                    sendTwitterStatus(line);
                }
            }
            catch(IOException io) {
                Logger.log("ERROR", "An IO exception occurred when pulling data from Twitter.");
                Logger.log("ERROR", io.getMessage());
            }
            
            Logger.log("INFO", "Backing off for 30s to be nice to the twitter servers.");
            try {
                Thread.currentThread().sleep(30000);
            }
            catch(InterruptedException ie) {
            }
            
            Logger.log("INFO", "Resuming feed.");
                
        }
    }
    
    private void sendTwitterStatus(String status) throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("STATUS", status);
        smo.addString("TXN_TYPE", "TXN_ADD_RAW_STATUS");
        sendTxn(smo);
    }
    
    private void sendTxn(SimpleMessageObject smo) throws IOException {
        SESSION.transmit(smo);
        SESSION.receive();
    }
    
    private boolean storeConfig(ConfigReader cfg) {
        
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
        
        if(!cfg.enterNode("twitter")) {
            Logger.log("ERROR", "Unable to find twitter details in config.");
            return false;
        }
        else {
            if(!storeTwitterDetails(cfg)) {
                return false;
            }
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
    
    private boolean storeTwitterDetails(ConfigReader cfg) {
        if(!cfg.enterNode("feed_url")) {
            Logger.log("ERROR", "No Feed URL defined.");
            return false;
        }
        else {
            FEED_URL = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("username")) {
            Logger.log("ERROR", "No username defined.");
            return false;
        }
        else {
            TWITTER_USERNAME = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("password")) {
            Logger.log("ERROR", "No password defined.");
            return false;
        }
        else {
            TWITTER_PASSWORD = cfg.getValue();
            cfg.exitNode();
        }
        
        return true;
    }
    
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private String FEED_URL, TWITTER_USERNAME, TWITTER_PASSWORD;
    private BufferedSession SESSION;

}
