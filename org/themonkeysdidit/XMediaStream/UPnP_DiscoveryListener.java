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
 
import org.themonkeysdidit.util.*;
import org.themonkeysdidit.io.*;

public class UPnP_DiscoveryListener {
    public UPnP_DiscoveryListener(ConfigReader cfg) {
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
    }
    
    public void startListening() throws IOException, UnknownHostException {
        
        // Set up multicast details
        configureMulticastSocket();
        
        Logger.log("INFO", "Configuration complete, waiting for data.");

        while(true) {
            // Get a packet
            SSDPPacket packet = receive();
            
            Logger.log("DEBUG", "Received SSDP packet, processsing.");
            
            //process it
            processSSDP(packet);
        }

    }
    
    private boolean storeConfig(ConfigReader cfg) {
        
        if(!cfg.enterNode("multicast")) {
            Logger.log("ERROR", "Unable to find multicast details in config.");
            return false;
        }
        else {
            if(!storeMulticastDetails(cfg)) {
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
    
    private boolean storeMulticastDetails(ConfigReader cfg) {
        if(!cfg.enterNode("address")) {
            Logger.log("ERROR", "No multicast address specified.");
            return false;
        }
        else {
            MCAST_ADDRESS = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("port")) {
            Logger.log("ERROR", "No multicast port specified.");
            return false;
        }
        else {
            MCAST_PORT = Integer.parseInt(cfg.getValue());
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
    
    private void configureMulticastSocket() throws IOException, UnknownHostException {
        MSOCK = new MulticastSocket(MCAST_PORT);
        MSOCK.joinGroup(InetAddress.getByName(MCAST_ADDRESS));
    }
    
    private SSDPPacket receive() throws IOException {
        byte[] buffer = new byte[1024];
        
        DatagramPacket p = new DatagramPacket(buffer, buffer.length);
        MSOCK.receive(p);
        
        SSDPPacket data = new SSDPPacket(p);
        
        if(Logger.isEnabled("NETIO")) {
            Logger.log("NETIO", "RX <<<" + System.getProperty("line.separator") + data.dump());
        }
        
        return data;
    }
    
    private void processSSDP(SSDPPacket p) throws IOException {
        String type = p.getType();
        if(type.compareToIgnoreCase("NOTIFY") == 0) {
            Logger.log("DEBUG", "Processing NOTIFY packet");
            processNotify(p);
        }
        else {
            Logger.log("WARNING", "Unknown SSDP type received: " + type);
        }
    }
    
    private void processNotify(SSDPPacket p) throws IOException {
        if(p.getNTS().compareToIgnoreCase("ssdp:alive") == 0) {
            processNotifyAlive(p);
        }
        else if(p.getNTS().compareToIgnoreCase("ssdp:byebye") == 0) {
            processNotifyByeBye(p);
        }
        else {
            Logger.log("WARNING", "Unknown NTS: " + p.getNTS());
        }
    }
    
    private void processNotifyAlive(SSDPPacket p) throws IOException {
        // Calculate when this host will expire:
        long expireTime = System.currentTimeMillis() + (long)(p.getMaxAge() * 1000);

        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_ADD_UPNP_DEVICE");
        smo.addString("HOST", p.getHost());
        smo.addString("LOCATION", p.getLocation());
        smo.addString("NT", p.getNT());
        smo.addString("USN", p.getUSN());
        smo.addInt("BOOTID.UPNP.ORG", p.getBootID());
        smo.addInt("CONFIG.UPNP.ORG", p.getConfigID());
        smo.addInt("SEARCHPORT.UPNP.ORG", p.getSearchPort());
        smo.addLong("EXPIRE_TIME", expireTime);
        
        sendTransaction(smo);
    }
    
    private void processNotifyByeBye(SSDPPacket p) throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_REMOVE_UPNP_DEVICE");
        smo.addString("NT", p.getNT());
        smo.addString("USN", p.getUSN());
        
        sendTransaction(smo);
    }
    
    private void sendTransaction(SimpleMessageObject smo) throws IOException {
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(smo);
        SimpleMessageObject result = bs.receive();
        bs.terminate();
    }
    
    private String MCAST_ADDRESS;
    private int MCAST_PORT;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private MulticastSocket MSOCK;
}
