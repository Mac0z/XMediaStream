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
import org.themonkeysdidit.util.*;
import org.themonkeysdidit.db.*;
import org.themonkeysdidit.io.*;

public class ShoutcastServerStreamer extends Thread {
    public ShoutcastServerStreamer(Socket s) {
        DATA_SOCKET = s;
        
        MYSQL_HOSTNAME = "localhost";
        MYSQL_USERNAME = "user";
        MYSQL_PASSWORD = "password";
        MYSQL_DATABASE = "database";
        MYSQL_PORT = 3306;
        
        POLL_PERIOD = 50;
        
        SOURCE = "all";
        
        TXN_HANDLER_HOSTNAME = "localhost";
        TXN_HANDLER_PORT = 1025;
        
        KEEP_STREAMING = true;
        
        NUM_STREAMED_BYTES = 0;
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
    
    public void run() {
        
        Logger.log("INFO", this.toString() + "ShoutcastServerStreamer has started");
        
        OutputStream out;
        InputStream in;
        
        try {
            // Firstly just send a flow transaction to show we have started reading
            // so the writer needs to start writing!
            Timestamp now = new Timestamp(new Date().getTime());
            sendFlowTxn(now.toString());
            
            out = DATA_SOCKET.getOutputStream();
            in = DATA_SOCKET.getInputStream();
            
            byte[] tmpData = new byte[1024];
            in.read(tmpData);
            Logger.log("DEBUG", "Data read from client:\n" + DataConversions.byteArrayToHexDump(tmpData));
            
            // Send the shoutcast headers:
            send(out, sendHeaders().getBytes(), false);
            // stream away
            streamData(out);
        }
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to open stream to remote client.");
            Logger.log("ERROR", io.getMessage());
        }
        catch(SQLException sql) {
            Logger.log("WARNING", this.toString() + "Unable to retrieve song details from database.");
            Logger.log("WARNING", sql.getMessage());
        }
        catch(ClassNotFoundException cnf) {
            Logger.log("WARNING", this.toString() + "Unable to retrieve song details from database.");
            Logger.log("WARNING", cnf.getMessage());
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
    
    private String sendHeaders() {
        
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
    
    private void streamData(OutputStream out) throws IOException {
        

        
        Logger.log("DEBUG", this.toString() + "Starting to stream data");
        
        try {
            MySQL_TableScanner scanner = new MySQL_TableScanner(getLatestId());
            scanner.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
            
            Timestamp storedTime = new Timestamp(0L);
            
            while(KEEP_STREAMING) {
                DatabaseResult update = scanner.scan("dynamic_shoutcast_buffer", (byte)0x01, POLL_PERIOD);
                Timestamp updateTime;
                for(int i = 0 ; i < update.getNumRows() ; i++) {
                    DataRow dr = update.getRow(i);
                    String s = (String)dr.getValue("source_name");
                    if(s.compareTo(SOURCE) == 0) {
                        updateTime = (Timestamp)dr.getValue("update_time");
                        if(updateTime.compareTo(storedTime) > 0) {
                            storedTime = updateTime;
                        }
                        Integer musicId = (Integer)dr.getValue("music_id");
                        setMusicId(musicId.intValue());
                        byte[] dataFromDb = (byte[])dr.getValue("raw_data");
                        send(out, dataFromDb, true);
                        // Only send the flow transaction, if the update is for us
                        sendFlowTxn(storedTime.toString());
                    }
                }
                
            }
            scanner.disconnect();
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
    }
    
    private void sendFlowTxn(String time) throws IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_UPDATE_DYNAMIC_FLOW_CONTROL");
        smo.addString("CONTROL_NAME", SOURCE);
        smo.addString("THREAD", this.toString());
        smo.addString("TABLE_NAME", "dynamic_shoutcast_buffer");
        smo.addString("TIME", time);
        
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(smo);
        bs.receive();
        bs.terminate();
    }
    
    private void send(OutputStream out, byte[] data, boolean includeInCount) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        send(out, data, 0, data.length, includeInCount);
    }
    
    private void send(OutputStream out, byte[] data, int startPos, int length, boolean includeInCount) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        try {
            if(Logger.isEnabled("NETIO")) {
                Logger.log("NETIO", this.toString() + ": " + DataConversions.byteArrayToHexDump(data));
            }

            // Only the headers (which won't trigger the icy meta) and the last chunk
            // of a song will send a byte[] that is not ICY_METAINT in length. Therefore
            // we can (for the majority of the time), take the easy way and send the
            // icy meta info after every write. Just check the length of the array and 
            // do fancy stuff if it's smaller.
            if(length > ICY_METAINT) {
                // Oh dear, the stream will break
                Logger.log("ERROR", "Received more than " + Integer.toString(ICY_METAINT) + " bytes in one chunk, the sound is likely to be off");
            }
            out.write(data, startPos, length);
            if(includeInCount) {
                if(length < ICY_METAINT) {
                    int songPadCount = ICY_METAINT - length;
                    for(int i = 0 ; i < songPadCount ; i++) {
                        out.write(0x00);
                    }
                }
                
                byte[] icyMeta = getIcyMeta();
                if(Logger.isEnabled("DEBUG")) {
                    Logger.log("DEBUG", "Sending icyMeta: \n" + DataConversions.byteArrayToHexDump(icyMeta));
                }
                out.write(icyMeta, 0, icyMeta.length);

            }
            out.flush();
        }
        catch(IOException io) {
            Logger.log("ERROR", this.toString() + "Unable to stream data to client, exiting.");
            Logger.log("ERROR", io.getMessage());
            KEEP_STREAMING = false;
        }
    }
    
    private byte[] getIcyMeta() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DBH.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
        String sql = "SELECT track_name FROM static_music_file WHERE music_id = ";
        sql = sql.concat(Integer.toString(getMusicId()));
        DatabaseResult result = DBH.query(sql);
        DataRow dr = result.getRow(0);
        String trackName = "StreamTitle=".concat((String)dr.getValue("track_name"));
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
        
        DBH.disconnect();
        
        return retVal;
    }
    
    private void setMusicId(int musicId) {
        MUSIC_ID = musicId;
    }
    
    private int getMusicId() {
        return MUSIC_ID;
    }
    
    private long getLatestId() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        DBH.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
        String sql = "SELECT MAX(internal_update_time) FROM dynamic_update_queue";
        DatabaseResult result = DBH.query(sql);
        DataRow dr = result.getRow(0);
        Long retVal = (Long)dr.getValue("MAX(internal_update_time)");
        DBH.disconnect();
        return retVal.longValue();
    }
    
    private Socket DATA_SOCKET;
    private String MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE;
    private int MYSQL_PORT;
    private int POLL_PERIOD;
    private String SOURCE;
    private boolean KEEP_STREAMING;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    private final int ICY_METAINT = 32768;
    private int NUM_STREAMED_BYTES;
    private int MUSIC_ID;
    private MySQL_DatabaseHandler DBH;
}
