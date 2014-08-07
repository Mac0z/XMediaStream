/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import java.net.Socket;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import org.themonkeysdidit.util.SimpleQueue;
import org.themonkeysdidit.io.SimpleMessageObject;
import org.themonkeysdidit.util.Logger;
import org.themonkeysdidit.util.DataConversions;;

public class QueueShoutcastServerStreamer extends Thread {
    public QueueShoutcastServerStreamer(SimpleQueue queue, Socket s) {
        QUEUE = queue;
        DATA_SOCKET = s;
        KEEP_STREAMING = true;
        BYTES_SENT = 0;
    }
    
    public void run() {
        Logger.log("INFO", this.toString() + "QueueShoutcastServerStreamer has started");
        
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
            
            // Send the shoutcast headers:
            send(out, getHeaders().getBytes(), false);
            // stream away
            streamData(out);
        }
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to open stream to remote client.");
            Logger.log("ERROR", io.getMessage());
        }

        Logger.log("INFO", this.toString() + "ShoutcastServerStreamer ending.");
        
    }
    
    private void streamData(OutputStream out) throws IOException {
        
        //int flowTxnCount = 0;
        
        while(KEEP_STREAMING) {
            // wait for a multicast packet
            SimpleMessageObject smo = (SimpleMessageObject)QUEUE.get();

            setCurrentMulticastPacket(smo);
                   
            // message is for us!
            send(out, smo.getByteArray("DATA"), true);
        }
        
    }
    
    private void send(OutputStream out, byte[] data, boolean includeInCount) {
        send(out, data, 0, data.length, includeInCount);
    }
    
    private void send(OutputStream out, byte[] data, int startPos, int bytesToSend, boolean includeInCount) {
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
    
    private byte[] getIcyMeta() {
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
    
    private SimpleQueue QUEUE;
    private Socket DATA_SOCKET;
    private boolean KEEP_STREAMING;
    private int BYTES_SENT;
    private SimpleMessageObject CURRENT_PACKET;
    private final int ICY_METAINT = 32768;
}
