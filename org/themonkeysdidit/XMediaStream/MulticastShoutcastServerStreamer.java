/*
 ******************************************************************************
 *
 * Copyright 2008 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.net.UnknownHostException;
import org.themonkeysdidit.util.*;
import org.themonkeysdidit.db.*;
import org.themonkeysdidit.io.*;

public class MulticastShoutcastServerStreamer extends Thread {
    public MulticastShoutcastServerStreamer(Socket s) {
        DATA_SOCKET = s;
        
        KEEP_STREAMING = true;
        BYTES_SENT = 0;
    }
    
    public void setDatabaseDetails(String host, String user, String password, String database, int port) {
        MYSQL_HOSTNAME = host;
        MYSQL_USERNAME = user;
        MYSQL_PASSWORD = password;
        MYSQL_DATABASE = database;
        MYSQL_PORT = port;
        
        DBH = new MySQL_DatabaseHandler();
        
    }
    
    public void setPollPeriod(int pollPeriod) {
        POLL_PERIOD = pollPeriod;
    }
    
    public void setSource(String source) {
        SOURCE = source;
    }
    
    public void setTxnHandlerDetails(String host, int port) {
        TXN_HANDLER_HOSTNAME = host;
        TXN_HANDLER_PORT = port;
    }
    
    public void setMulticastDetails(String address, int port, int ttl) throws UnknownHostException, IOException {
        MCAST_ADDRESS = address;
        MCAST_PORT = port;
        TTL = TTL;
        MCAST_TRANS = new MulticastTransceiver(MCAST_ADDRESS, MCAST_PORT, TTL);
    }
    
    public void run() {
        Logger.log("INFO", this.toString() + "MulticastShoutcastServerStreamer has started");
        
        OutputStream out;
        InputStream in;
        try {
            // Firstly just send a flow transaction to show we have started reading
            // so the writer needs to start writing!
            //sendFlowTxn();
            
            out = DATA_SOCKET.getOutputStream();
            in = DATA_SOCKET.getInputStream();
            
            byte[] tmpData = new byte[1024];
            in.read(tmpData);
            Logger.log("DEBUG", "Data read from client:\n" + DataConversions.byteArrayToHexDump(tmpData));
            
            // join the multicast group
            MCAST_TRANS.joinGroup();
            
            // Send the shoutcast headers:
            send(out, getHeaders().getBytes(), false);
            // stream away
            streamData(out);
        }
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to open stream to remote client.");
            Logger.log("ERROR", io.getMessage());
        }
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

        Logger.log("INFO", this.toString() + "ShoutcastServerStreamer ending.");
        
    }
    
    private void streamData(OutputStream out) throws IOException, SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        //int flowTxnCount = 0;
        
        while(KEEP_STREAMING) {
            // wait for a multicast packet
            SimpleMessageObject smo = MCAST_TRANS.receive();
            if(smo != null &&
               smo.getString("MESSAGE_TYPE").compareTo("MULTICAST_MUSIC_DATA") == 0 &&
               smo.getString("SOURCE_NAME").compareTo(SOURCE) == 0) {
                   
               setCurrentMulticastPacket(smo);
                   
               // message is for us!
               send(out, smo.getByteArray("DATA"), true);
               
               //flowTxnCount++;
               
               //if(flowTxnCount == 10) {
               //    sendFlowTxn();
               //    flowTxnCount = 0;
               //}
            }
        }
        
    }
    
    private void send(OutputStream out, byte[] data, boolean includeInCount) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        send(out, data, 0, data.length, includeInCount);
    }
    
    private void send(OutputStream out, byte[] data, int startPos, int bytesToSend, boolean includeInCount) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        try {
            if(Logger.isEnabled("NETIO")) {
                Logger.log("NETIO", this.toString() + ": " + DataConversions.byteArrayToHexDump(data));
            }
            
            for(int i = 0 ; i < bytesToSend ; i++) {
                out.write(data[i]);
                if(includeInCount) {
                    BYTES_SENT++;
                    if(BYTES_SENT == ICY_METAINT) {
                        byte[] icyMeta = getIcyMeta();
                        Logger.log("DEBUG", "Sending icy meta data.");
                        send(out, icyMeta, false);
                        BYTES_SENT = 0;
                    }
                }
            }
        }
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to stream data to client, exiting.");
            Logger.log("ERROR", io.getMessage());
            KEEP_STREAMING = false;
        }
    }
    
    private byte[] getIcyMeta() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String trackName = "StreamTitle='".concat(getCurrentMulticastPacket().getString("TRACK_NAME").concat("'"));
        
        byte[] namePortion = trackName.getBytes();
        int multiplier = namePortion.length / 16;
        multiplier++;
        byte[] retVal = new byte[1 + (16*multiplier)];
        Logger.log("DEBUG", "namePortion: " + Integer.toString(namePortion.length));
        Logger.log("DEBUG", "multiplier: " + Integer.toString(multiplier));
        byte len = (byte)(multiplier);
        Logger.log("DEBUG", "len: " + Integer.toString((int)len));
        
        //init the array
        for(int i = 0 ; i < retVal.length ; i++) {
            retVal[i] = 0;
        }
        
        // add the length header
        retVal[0] = len;
        
        // add the song data
        for(int i = 0 ; i < namePortion.length ; i++) {
            retVal[i+1] = namePortion[i];
        }
        
        return retVal;
    }
    
    private String getHeaders() {
        
        StringBuffer retVal = new StringBuffer("ICY 200 OK\r\n");
        retVal.append("icy-notice1:<BR>This stream requires <a href=\"http://www.winamp.com/\">Winamp</a><BR>");
        retVal.append("icy-notice2:XMediaStream implementation of SHOUTcast Distributed Network Audio Server/ v1.0.0<BR>");
        retVal.append("icy-name:XMediaStream\r\n");
        retVal.append("icy-genre:Mixed\r\n");
        retVal.append("icy-url:http://www.themonkeysdidit.org\r\n");
        retVal.append("Content-Type:audio/mpeg\r\n");
        retVal.append("icy-pub:0\r\n");
        retVal.append("icy-br:192\r\n");
        retVal.append("icy-metaint:" + Integer.toString(ICY_METAINT) + "\r\n");
        retVal.append("\r\n");

        return retVal.toString();
        
    }
    
    private void setCurrentMulticastPacket(SimpleMessageObject smo) {
        CURRENT_PACKET = smo;
    }
    
    private SimpleMessageObject getCurrentMulticastPacket() {
        return CURRENT_PACKET;
    }
    
    private void sendFlowTxn() throws IOException {
        Timestamp t = new Timestamp(new Date().getTime());
        
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_UPDATE_DYNAMIC_FLOW_CONTROL");
        smo.addString("CONTROL_NAME", SOURCE);
        smo.addString("THREAD", this.toString());
        smo.addString("TABLE_NAME", "dynamic_shoutcast_buffer");
        smo.addString("TIME", t.toString());
        
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(smo);
        bs.receive();
        bs.terminate();
    }
    
    private Socket DATA_SOCKET;
    private String MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE;
    private int MYSQL_PORT;
    private int POLL_PERIOD;
    private String SOURCE;
    private boolean KEEP_STREAMING;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private MySQL_DatabaseHandler DBH;
    private String MCAST_ADDRESS;
    private int MCAST_PORT;
    private int TTL;
    private MulticastTransceiver MCAST_TRANS;
    private int BYTES_SENT;
    private SimpleMessageObject CURRENT_PACKET;
    private final int ICY_METAINT = 32768;

}
