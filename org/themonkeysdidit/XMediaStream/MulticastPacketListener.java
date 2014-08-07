/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import java.net.UnknownHostException;
import java.io.IOException;

import org.themonkeysdidit.util.SimpleQueue;
import org.themonkeysdidit.io.MulticastTransceiver;
import org.themonkeysdidit.util.Logger;
import org.themonkeysdidit.io.SimpleMessageObject;

public class MulticastPacketListener extends Thread {
    public MulticastPacketListener(SimpleQueue queue) {
        QUEUE = queue;
        KEEP_RUNNING = true;
    }
    
    public void setSource(String source) {
        SOURCE = source;
    }
    
    public void setMulticastDetails(String address, int port, int ttl) throws UnknownHostException, IOException {
        MCAST_ADDRESS = address;
        MCAST_PORT = port;
        TTL = TTL;
        MCAST_TRANS = new MulticastTransceiver(MCAST_ADDRESS, MCAST_PORT, TTL);
    }
    
    public void run() {
        Logger.log("INFO", this.toString() + "MulticastPacketListener has started");

        try {

            // join the multicast group
            MCAST_TRANS.joinGroup();
            
            SimpleMessageObject smo;
            
            while(KEEP_RUNNING) {
            
                smo = MCAST_TRANS.receive();
                
                if(smo != null &&
                   smo.getString("MESSAGE_TYPE").compareTo("MULTICAST_MUSIC_DATA") == 0 &&
                   smo.getString("SOURCE_NAME").compareTo(SOURCE) == 0) {
                       
                       Logger.log("DEBUG", this.toString() + " Adding multicast packet to queue.");
                       QUEUE.add(smo);
                       Logger.log("DEBUG", this.toString() + " Multicast packet added to queue.");
                }
                
                long now = System.currentTimeMillis();
                long lastReadTime = QUEUE.getLastReadTime();
                if((now - lastReadTime)>30000) {
                    Logger.log("WARNING", "Nothing has been read from the queue for 30s, assuming client is dead and exiting.");
                    KEEP_RUNNING = false;
                }
            }

        }
        
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to receive Multicast data.");
            Logger.log("ERROR", io.getMessage());
            KEEP_RUNNING = false;
        }
        /*
        catch(SQLException sql) {
            Logger.log("ERROR", this.toString() + "Problem reading from database");
            Logger.log("ERROR", sql.getMessage());
        }
        catch(ClassNotFoundException cnf) {
            Logger.log("ERROR", this.toString() + "Unable to load database driver.");
            Logger.log("ERROR", cnf.getMessage());
        }
        catch(InstantiationException ie) {
            Logger.log("ERROR", this.toString() + "Unable to instantiate database driver class.");
            Logger.log("ERROR", ie.getMessage());
        }
        catch(IllegalAccessException iae) {
            Logger.log("ERROR", this.toString() + "Illegal access excption thrown while scanning database");
            Logger.log("ERROR", iae.getMessage());
        }
*/
        Logger.log("INFO", this.toString() + "ShoutcastServerStreamer ending.");
        
    }

    private SimpleQueue QUEUE;
    private String SOURCE;
    private String MCAST_ADDRESS;
    private int MCAST_PORT;
    private int TTL;
    private MulticastTransceiver MCAST_TRANS;
    private boolean KEEP_RUNNING;
}
