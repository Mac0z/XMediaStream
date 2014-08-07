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

public class TargetedMulticastShoutcastClient extends MulticastShoutcastClient {
    public TargetedMulticastShoutcastClient() {
        setMode(RANDOM);
        FAVE_ARTISTS = new HashMap<String, String[]>();
    }
    
    public TargetedMulticastShoutcastClient(ConfigReader cfg) {
        super(cfg);
        setMode(RANDOM);
        FAVE_ARTISTS = new HashMap<String, String[]>();
        
        if(!storeTargetedConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
    }
    
    public DatabaseResult chooseSong() throws SQLException {
        DatabaseResult retVal;
        
        String oldMode = getMode();
        setMode(updateMode());
        
        if(oldMode.compareTo(getMode()) != 0) {
            Logger.log("INFO", "Switching from " + oldMode + " to " + getMode());
            sendModeChange();
        }
        
        if(getMode().compareTo(RANDOM) == 0) {
            retVal = super.chooseSong();
        }
        else if(getMode().compareTo(USER) == 0) {
            retVal = chooseSongUser();
        }
        else if(getMode().compareTo(DINNER_PARTY) == 0) {
            retVal = chooseSongDinnerParty();
        }
        else if(getMode().compareTo(PARTY) == 0) {
            retVal = chooseSongParty();
        }
        else {
            Logger.log("WARNING", "Unkown mode: " + getMode() + ". Using random");
            retVal = super.chooseSong();
        }
        
        if(retVal.getNumRows() == 0) {
            retVal = super.chooseSong();
        }
        
        return retVal;
    }
    
    private DatabaseResult chooseSongUser() throws SQLException {
        // Firstly, are we using the user specific or
        // does our random generator pick a random song
        double tmp = Math.random();
        Double TMP = new Double(tmp);
        Logger.log("DEBUG", "Random number is: " + TMP.toString());
        int rndm = (int)(tmp * 100);
        Integer RNDM = new Integer(rndm);
        Logger.log("DEBUG", "Which translates to an int as: " + RNDM.toString());
        if(rndm >= USER_SONG_PERCENTAGE) {
            return super.chooseSong();
        }
        else {
            // if we're here, then no...
            // Which users are logged in:
            String sql = "SELECT user_name from system_static_users ";
            sql = sql.concat("JOIN dynamic_bluetooth_users ");
            sql = sql.concat("ON system_static_users.user_id = dynamic_bluetooth_users.user_id");
            
            DatabaseResult userResult = DBH.query(sql);
            if(userResult.getNumRows() == 0) {
                // No users logged in
                // There's a chance someone logged out between
                // updating the mode and getting here so we
                // should account for this
                return super.chooseSong();
            }
            else {
                // We have users!
                // Do we have any fave artists for them?
                // (and record the users if we do)...
                boolean gotFaves = false;
                Vector<String> loggedInUsers = new Vector<String>();
                for(int i = 0 ; i < userResult.getNumRows() ; i++) {
                    DataRow row = userResult.getRow(i);
                    String name = (String)row.getValue("user_name");
                    if(FAVE_ARTISTS.containsKey(name)) {
                        gotFaves = true;
                        loggedInUsers.add(name);
                    }
                }
                if(!gotFaves) {
                    // No faves in config for the users we have logged in
                    return super.chooseSong();
                }
                else {
                    // We have users logged in and they have some fave
                    // artists, construct the sql...
                    StringBuffer tmpBuf = new StringBuffer("SELECT static_music_file.file_location, static_music_file.music_id ");
                    tmpBuf.append("FROM static_music_file LEFT JOIN static_music_artist ");
                    tmpBuf.append("ON static_music_file.artist_id = static_music_artist.artist_id ");
                    tmpBuf.append("WHERE (");
                    for(int i = 0 ; i < loggedInUsers.size() ; i++) {
                        String thisUser = loggedInUsers.elementAt(i);
                        String[] artists = FAVE_ARTISTS.get(thisUser);
                        Logger.log("DEBUG", "There are " + Integer.toString(artists.length) + "artists for user " + thisUser);
                        for(int j = 0 ; j < artists.length ; j++) {
                            tmpBuf.append("static_music_artist.artist_name = '");
                            tmpBuf.append(artists[j].replaceAll("'", "''"));
                            tmpBuf.append("'");
                            if(j < artists.length - 1) {
                                tmpBuf.append(" OR ");
                            }
                        }
                        if(i < loggedInUsers.size() -1) {
                            tmpBuf.append(" OR ");
                        }
                    }
                    tmpBuf.append(") ORDER BY RAND() LIMIT 0,1");
                    return DBH.query(tmpBuf.toString());
                }
            }
        }
    }
    
    private DatabaseResult chooseSongDinnerParty() throws SQLException {
        String sql = "SELECT static_music_file.file_location, static_music_file.music_id ";
        sql = sql.concat("FROM static_music_file LEFT JOIN static_music_tags ");
        sql = sql.concat("ON static_music_file.music_id = static_music_tags.music_id ");
        sql = sql.concat("WHERE (static_music_tags.tag = '");
        sql = sql.concat(DINNER_PARTY);
        sql = sql.concat("') ORDER BY RAND() LIMIT 0,1");
        
        return DBH.query(sql);
    }
    
    private DatabaseResult chooseSongParty() throws SQLException {
        String sql = "SELECT static_music_file.file_location, static_music_file.music_id ";
        sql = sql.concat("FROM static_music_file LEFT JOIN static_music_tags ");
        sql = sql.concat("ON static_music_file.music_id = static_music_tags.music_id ");
        sql = sql.concat("WHERE (static_music_tags.tag = '");
        sql = sql.concat(PARTY);
        sql = sql.concat("') ORDER BY RAND() LIMIT 0,1");
        
        return DBH.query(sql);
    }
    
    private boolean storeTargetedConfig(ConfigReader cfg) {
        if(!cfg.enterNode("general")) {
            Logger.log("ERROR", "Unable to find general details in config");
            return false;
        }
        else {
            if(!storePartyLimits(cfg)) {
                return false;
            }
            
            if(!storeUserPercentage(cfg)) {
                return false;
            }
            
            if(!storeModeChangeFiles(cfg)) {
                return false;
            }
            if(!storeKnownUsers(cfg)) {
                Logger.log("WARNING", "No known users defined, no targeted songs will be played.");
            }
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storePartyLimits(ConfigReader cfg) {
        if(!cfg.enterNode("minimum_party_number")) {
            Logger.log("ERROR", "No minimum party number defined.");
            return false;
        }
        else {
            MINUMUM_PARTY_NUMBERS = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("minimum_dinner_party_number")) {
            Logger.log("ERROR", "No minimum dinner party number defined.");
            return false;
        }
        else {
            MINIMUM_DINNER_PARTY_NUMBERS = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storeUserPercentage(ConfigReader cfg) {
        if(!cfg.enterNode("user_song_percentage")) {
            Logger.log("ERROR", "No user song percentage defined.");
            return false;
        }
        else {
            USER_SONG_PERCENTAGE = Integer.parseInt(cfg.getValue());
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storeModeChangeFiles(ConfigReader cfg) {
        if(!cfg.enterNode("user_mode_file")) {
            Logger.log("ERROR", "No user mode file defined.");
            return false;
        }
        else {
            USER_MODE_FILE = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("random_mode_file")) {
            Logger.log("ERROR", "No random mode file defined.");
            return false;
        }
        else {
            RANDOM_MODE_FILE = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("party_mode_file")) {
            Logger.log("ERROR", "No party mode file defined.");
            return false;
        }
        else {
            PARTY_MODE_FILE = cfg.getValue();
            cfg.exitNode();
        }
        
        if(!cfg.enterNode("dinner_party_mode_file")) {
            Logger.log("ERROR", "No dinner party mode file defined.");
            return false;
        }
        else {
            DINNER_PARTY_MODE_FILE = cfg.getValue();
            cfg.exitNode();
        }
        
        return true;
    }
    
    private boolean storeKnownUsers(ConfigReader cfg) {
        if(cfg.enterNode("defined_users")) {
            String tmp = cfg.getValue();
            StringTokenizer breaker = new StringTokenizer(tmp, "|");
            KNOWN_USERS = new String[breaker.countTokens()];
            cfg.exitNode();
            
            for(int i = 0 ; i < KNOWN_USERS.length ; i++) {
                KNOWN_USERS[i] = breaker.nextToken();
                Logger.log("INFO", "Added " + KNOWN_USERS[i] + " to known users.");
            }
            
            int j = 0;
            while(j < KNOWN_USERS.length) {
                if(cfg.enterNode(KNOWN_USERS[j])) {
                    String artists = cfg.getValue();
                    Logger.log("DEBUG", "artists read from config are: " + artists);
                    StringTokenizer artistSplitter = new StringTokenizer(artists, "|");
                    int k = 0;
                    String[] artistsArray = new String[artistSplitter.countTokens()];
                    Logger.log("DEBUG", "artistSplitter has " + Integer.toString(artistSplitter.countTokens()) + " tokens");
                    int numTokens = artistSplitter.countTokens();
                    while(k < numTokens) {
                        artistsArray[k] = artistSplitter.nextToken();
                        Logger.log("INFO", KNOWN_USERS[j] + " likes " + artistsArray[k]);
                        k++;
                    }
                    FAVE_ARTISTS.put(KNOWN_USERS[j], artistsArray);
                    cfg.exitNode();
                }
                else {
                    Logger.log("WARNING", "No artists defined for user: " + KNOWN_USERS[j]);
                }
                j++;
            }
            return true;
        }
        else {
            return false;
        }
    }
    
    private String getMode() {
        return MODE;
    }
    
    private void setMode(String mode) {
        MODE = mode;
    }
    
    private void sendModeChange() {
        try {
            if(getMode().compareTo(RANDOM) == 0) {
                streamModeChangeData(RANDOM_MODE_FILE);
            }
            else if(getMode().compareTo(USER) == 0) {
                streamModeChangeData(USER_MODE_FILE);
            }
            else if(getMode().compareTo(DINNER_PARTY) == 0) {
                streamModeChangeData(DINNER_PARTY_MODE_FILE);
            }
            else if(getMode().compareTo(PARTY) == 0) {
                streamModeChangeData(PARTY_MODE_FILE);
            }
        }
        catch(IOException io) {
            Logger.log("WARNING", "Unable to stream mode change file");
            Logger.log("WARNING", io.toString());
        }
    }
    
    private void streamModeChangeData(String filename) throws FileNotFoundException, IOException {
        FileInputStream fin = new FileInputStream(filename);
        
        while(true) {
            byte[] data = new byte[1024];
            int bytesRead = fin.read(data);
            if(bytesRead == -1) {
                break;
            }
            
            sendPacket(data, bytesRead, "Mode change");
        }
        
        fin.close();
    }
    
    private String updateMode() throws SQLException {
        String retVal = RANDOM;
        
        // Who is logged in?
        String sql = "SELECT user_id FROM dynamic_bluetooth_users";
        DatabaseResult result = DBH.query(sql);
        if(result.getNumRows() == 0) {
            // No one is logged in, go to random mode
            retVal = RANDOM;
        }
        else {
            int numUsers = result.getNumRows();
            // How many of these users are guests
            // and how many are known users?
            int guests = 0;
            int knownUsers = 0;
            DataRow dr;
            Integer tmp;
            for(int i = 0 ; i < numUsers ; i++) {
                dr = result.getRow(i);
                tmp = (Integer)dr.getValue("user_id");
                if(tmp.intValue() == GUEST_ID) {
                    guests++;
                }
                else {
                    knownUsers++;
                }
            }
            if(guests > MINUMUM_PARTY_NUMBERS) {
                retVal = PARTY;
            }
            else if(guests > MINIMUM_DINNER_PARTY_NUMBERS) {
                retVal = DINNER_PARTY;
            }
            else if(knownUsers > 0) {
                retVal = USER;
            }
            else {
                retVal = RANDOM;
            }
        }
        
        return retVal;
    }
    
    private String MODE;
    private final String RANDOM = "RANDOM";
    private final String USER = "USER";
    private final String DINNER_PARTY = "DINNER_PARTY";
    private final String PARTY = "PARTY";
    private final int GUEST_ID = 1;
    private int MINUMUM_PARTY_NUMBERS;
    private int MINIMUM_DINNER_PARTY_NUMBERS;
    private int USER_SONG_PERCENTAGE;
    private String DINNER_PARTY_MODE_FILE, PARTY_MODE_FILE, RANDOM_MODE_FILE, USER_MODE_FILE;
    private String[] KNOWN_USERS;
    private HashMap<String, String[]> FAVE_ARTISTS;
}
