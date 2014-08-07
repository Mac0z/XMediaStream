/*
 ******************************************************************************
 *
 * Copyright 2008 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import java.sql.*;
import org.themonkeysdidit.io.*;
import org.themonkeysdidit.util.*;
import org.themonkeysdidit.db.*;
import java.util.*;
import java.net.*;
import java.io.*;

/**
******************************************************************************
**
** The Shoutcast client picks a song at random from the static_music_file table
** and sends txns to the transaction handler to write the binary data to the
** dynamic_shoutcast_buffer table in chunks.  It is the job of the
** http streamer to look up the song name and include this at regular
** intervals in the stream.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class ShoutcastClient {
    public ShoutcastClient() {
        
    }
    
    public ShoutcastClient(ConfigReader cfg) {
        
        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
        DBH = new MySQL_DatabaseHandler();
    }
    
    /**
     * Start the application streaming data to the database.
     */
    public void runApp() throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException, FileNotFoundException, IOException, UnknownHostException {
        
        Logger.log("INFO", "Application has started.");
        
        // Quite simple really
        while(true) {
            // Connect to the db
            DBH.connect(MYSQL_HOSTNAME, MYSQL_PORT, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE);
            
            // Choose a song to start playing at random
            DatabaseResult result = chooseSong();
            DataRow dr = result.getRow(0);
            String filePath = (String)dr.getValue("file_location");
            
            Logger.log("DEBUG", "Reading data from: " + filePath);
            Integer i = (Integer)dr.getValue("music_id");
            int id = i.intValue();
            
            
            
            // keep sending TXN_ADD_TO_SHOUTCAST_BUFFER to the transaction handler
            Logger.log("DEBUG", "Streaming data to txn handler.");
            streamSong(filePath, id);
            DBH.disconnect();
            // no more bytes in song, loop round again.
        }
    }
    
    protected DatabaseResult chooseSong() throws SQLException {

        // No genres specified
        if(GENRES[0] == null) {
            // Need a random record, so first get maximum
            // and minimum records:
            String sqlStatement;
            sqlStatement = new String("SELECT MAX(music_id) AS max_id, MIN(music_id) AS min_id FROM static_music_file");
            DatabaseResult result = DBH.query(sqlStatement);

            // Get the data
            DataRow dr = result.getRow(0);
            Integer iMax = (Integer)dr.getValue("max_id");
            Integer iMin = (Integer)dr.getValue("min_id");
            Logger.log("DEBUG", "iMax has value: " + iMax.toString());
            int max = iMax.intValue();
            int min = iMin.intValue();
        
            int  randomNum = generateRandom(min, max);
        
            sqlStatement = new String("SELECT static_music_file.file_location, static_music_file.music_id FROM static_music_file WHERE music_id >= " + Integer.toString(randomNum) + " LIMIT  0,1");
        
            DatabaseResult result2 = DBH.query(sqlStatement);
        
            return result2;
        }
        // genres specified
        else {
            /*
            SELECT static_music_file.file_location, static_music_file.music_id
            FROM static_music_file
            LEFT JOIN static_music_genre
            ON static_music_file.genre_id = static_music_genre.genre_id
            WHERE (static_music_genre.genre_name = 'metal'
            OR static_music_genre.genre_name = 'rock'...)
            ORDER BY RAND() LIMIT 0,1
            */
            
            StringBuffer sqlBuf = new StringBuffer("SELECT static_music_file.file_location, static_music_file.music_id ");
            sqlBuf.append("FROM static_music_file ");
            sqlBuf.append("LEFT JOIN static_music_genre ");
            sqlBuf.append("ON static_music_file.genre_id = static_music_genre.genre_id ");
            sqlBuf.append("WHERE (");
            for(int i = 0 ; i < GENRES.length ; i++) {
                sqlBuf.append("static_music_genre.genre_name = '");
                sqlBuf.append(GENRES[i]);
                sqlBuf.append("'");
                if(i < GENRES.length - 1) {
                    sqlBuf.append(" OR ");
                }
            }
            sqlBuf.append(") ORDER BY RAND() LIMIT 0,1");
            return DBH.query(sqlBuf.toString());
        }

    }
    
    protected void streamSong(String filePath, int musicId) throws FileNotFoundException, IOException, SQLException {
        // Open the file
        FileInputStream fin = new FileInputStream(filePath);
        Timestamp lastUpdateTime;
        // Read  bytes
        while(true) {
            byte[] data = new byte[BATCH_SIZE];
            int bytesRead = fin.read(data);
            if(bytesRead == -1) {
                Logger.log("DEBUG", "File sent");
                break;
            }
            sendTransaction(data, bytesRead, musicId);
            // Sent some data, update lastUpdateTime;
            lastUpdateTime = new Timestamp(new java.util.Date().getTime());
            // now need to check how far ahead the readers are and pause if
            // necessary
            try {
                while(needToSleep(lastUpdateTime)) {
                    Thread.currentThread().sleep(BATCH_TIME);
                }
            }
            catch(InterruptedException ie) {
                // If we are interrupted, it doesn't matter so
                // just carry on.
            }
        }
        
        fin.close();
        
    }
    
    private int generateRandom(int min, int max) {
        Random rand = new Random();
        long range = (long)max - (long)min + 1;
        long fraction = (long)(range * rand.nextDouble());
        return (int)(fraction + min);
    }
    
    private void  sendTransaction(byte[] data, int numBytes, int musicId) throws UnknownHostException, IOException {
        SimpleMessageObject smo = new SimpleMessageObject();
        smo.addString("TXN_TYPE", "TXN_ADD_TO_SHOUTCAST_BUFFER");
        smo.addInt("MUSIC_ID", musicId);
        smo.addString("SOURCE_NAME", SOURCE_NAME);
        byte[] buf = new byte[numBytes];
        for(int i = 0 ; i < numBytes ; i++) {
            buf[i] = data[i];
        }
        smo.addByteArray("DATA", buf);
        
        Socket s = new Socket(TXN_HANDLER_HOSTNAME, TXN_HANDLER_PORT);
        
        
        BufferedSession bs = new BufferedSession();
        bs.initiate(s);
        bs.transmit(smo);
        SimpleMessageObject result = bs.receive();
        bs.terminate();
    }
    
    protected boolean storeConfig(ConfigReader cfg) {
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
        if(!cfg.enterNode("batch_time")) {
            Logger.log("ERROR", "No batch_time defined.");
            return false;
        }
        else {
            BATCH_TIME = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("batch_size")) {
            Logger.log("ERROR", "No batch_size defined.");
            return false;
        }
        else {
            BATCH_SIZE = Integer.parseInt(cfg.getValue());
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
        
        if(!cfg.enterNode("genres")) {
            Logger.log("WARNING", "No genres defined, defaulting to all.");
            GENRES = new String[1];
            GENRES[0] = null;
        }
        else {
            String genres = cfg.getValue();
            StringTokenizer breaker = new StringTokenizer(genres, ",");
            GENRES = new String[breaker.countTokens()];
            for(int i = 0 ; i < GENRES.length ; i++) {
                GENRES[i] = breaker.nextToken();
                Logger.log("INFO", "Adding to genre list: " + GENRES[i]);
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
    
    protected boolean needToSleep(Timestamp lastUpdateTime) throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT * FROM dynamic_flow_control WHERE ");
        sql.append("control_name='");
        sql.append(SOURCE_NAME);
        sql.append("' AND table_name='");
        sql.append("dynamic_shoutcast_buffer' ORDER BY last_read_time DESC LIMIT 1");
        
        DatabaseResult result = DBH.query(sql.toString());
        if(result.getNumRows() < 1) {
            // There are no readers...
            return false;
        }
        else {
            DataRow dr = result.getRow(0);
            Timestamp time = (Timestamp)dr.getValue("last_read_time");
            // Here, need to work out the difference between the reader and this
            // so we know if we need to pause or not.
            long diff = (lastUpdateTime.getTime() - time.getTime()) / 1000;
            // diff is in seconds
            Logger.log("DEBUG", "Writer is at: " + lastUpdateTime.toString() + ", Reader is at: " + time.toString());
            if(diff > MIN_READ_AHEAD_TIME) {
                Logger.log("DEBUG", "No music updates required, reader is too far behind");
                return true;
            }
            else {
                Logger.log("DEBUG", "Need to send music update.");
                return false;
            }
        }
    }
    
    private String MYSQL_HOSTNAME, MYSQL_USERNAME, MYSQL_PASSWORD, MYSQL_DATABASE;
    private int MYSQL_PORT;
    protected String SOURCE_NAME;
    protected int BATCH_TIME, BATCH_SIZE;
    private String TXN_HANDLER_HOSTNAME;
    private int TXN_HANDLER_PORT;
    protected MySQL_DatabaseHandler DBH;
    private final long MIN_READ_AHEAD_TIME = 3L; // (3 seconds)
    private String[] GENRES;

}
