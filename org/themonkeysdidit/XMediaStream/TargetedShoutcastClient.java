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

public class TargetedShoutcastClient extends ShoutcastClient {
    public TargetedShoutcastClient(ConfigReader cfg) {

        if(!storeConfig(cfg)) {
            Logger.log("ERROR", "Unable to read config, exiting...");
            System.exit(1);
        }
        
        DBH = new MySQL_DatabaseHandler();
        MODE = RANDOM;
    }
    
    protected DatabaseResult chooseSong() throws SQLException {
        
        DatabaseResult retVal;
        
        // DBH is connected at this point
        Logger.log("DEBUG", "Current mode is: " + MODE + ".");
        String oldMode = MODE;
        MODE = updateMode();
        if(oldMode.compareTo(MODE) != 0) {
            // We have a mode change
            Logger.log("INFO", "Mode changing from " + oldMode + " to " + MODE + ".");
        }
        if(MODE.compareTo(RANDOM) == 0) {
            Logger.log("DEBUG", "Using super class to choose song.");
            retVal = super.chooseSong();
        }
        else if(MODE.compareTo(USER) == 0) {
            Logger.log("DEBUG", "Using user mode to choose song.");
            retVal = chooseSongUser();
        }
        else if(MODE.compareTo(DINNER_PARTY) == 0) {
            Logger.log("DEBUG", "Using dinner party mode to choose song.");
            retVal = chooseSongDinnerParty();
        }
        else if(MODE.compareTo(PARTY) == 0) {
            Logger.log("DEBUG", "Using party mode to choose song.");
            retVal = chooseSongParty();
        }
        else {
            // Shouldn't ever get here, but return some thing sensible anyway
            Logger.log("WARNING", "Unknown Mode: " + MODE + ".");
            retVal = super.chooseSong();
        }
        
        if(retVal.getNumRows() == 0) {
            retVal = super.chooseSong();
        }
        
        return retVal;
    }
    
    private String updateMode() throws SQLException {

        // How many guests are logged in?
        StringBuffer sql = new StringBuffer("SELECT COUNT(system_static_users.user_name) ");
        sql.append("FROM system_static_users JOIN dynamic_bluetooth_users ");
        sql.append("ON system_static_users.user_id = dynamic_bluetooth_users.user_id ");
        sql.append("WHERE system_static_users.user_name = 'guest'");
        
        DatabaseResult result = DBH.query(sql.toString());
        DataRow dr = result.getRow(0);
        Long i = (Long)dr.getValue("COUNT(system_static_users.user_name)");
        Long numGuests = i.longValue();
        if(numGuests > MINUMUM_PARTY_NUMBERS) {
            return PARTY;
        }
        else if(numGuests > MINIMUM_DINNER_PARTY_NUMBERS) {
            return DINNER_PARTY;
        }
        else {
            // not enough guests for a party mode, any known users logged in?
            sql = new StringBuffer("SELECT COUNT(system_static_users.user_name) ");
            sql.append("FROM system_static_users JOIN dynamic_bluetooth_users ");
            sql.append("ON system_static_users.user_id = dynamic_bluetooth_users.user_id ");
            sql.append("WHERE system_static_users.user_name != 'guest'");
            
            result = DBH.query(sql.toString());
            dr = result.getRow(0);
            i = (Long)dr.getValue("COUNT(system_static_users.user_name)");
            long knownUsers = i.longValue();
            if(knownUsers > 0L) {
                return USER;
            }
            else {
                return RANDOM;
            }
        }
    }
    
    private DatabaseResult chooseSongUser() throws SQLException {
        
        double tmp = Math.random();
        Double TMP = new Double(tmp);
        Logger.log("DEBUG", "Random number is: " + TMP.toString());
        int rndm = (int)(tmp * 100);
        Integer RNDM = new Integer(rndm);
        Logger.log("DEBUG", "Which translates to an int as: " + RNDM.toString());
        if(rndm > USER_SONG_PERCENTAGE) {
            return super.chooseSong();
        }
        
        else {
            StringBuffer sql = new StringBuffer("SELECT smf.file_location, smf.music_id ");
            sql.append("FROM system_static_users sst ");
            sql.append("JOIN dynamic_bluetooth_users dbu ");
            sql.append("ON sst.user_id = dbu.user_id ");
            sql.append("JOIN static_music_tags smt ");
            sql.append("ON smt.tag = dbu.user_id ");
            sql.append("JOIN static_music_file smf ");
            sql.append("ON smf.music_id = smt.music_id ");
            sql.append("WHERE sst.user_name != 'guest' ");
            sql.append("ORDER BY RAND() LIMIT 0,1");
    
            return DBH.query(sql.toString());
        }
    }
    
    private DatabaseResult chooseSongDinnerParty() throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT smf.file_location, smf.music_id ");
        sql.append("FROM static_music_file smf ");
        sql.append("JOIN static_music_tags smt ");
        sql.append("ON smf.music_id = smt.music_id ");
        sql.append("WHERE smt.tag = 'dinner_party' ");
        sql.append("ORDER BY RAND() LIMIT 0,1");
        
        return DBH.query(sql.toString());
    }
    
    private DatabaseResult chooseSongParty() throws SQLException {
        StringBuffer sql = new StringBuffer("SELECT smf.file_location, smf.music_id ");
        sql.append("FROM static_music_file smf ");
        sql.append("JOIN static_music_tags smt ");
        sql.append("ON smf.music_id = smt.music_id ");
        sql.append("WHERE smt.tag = 'party' ");
        sql.append("ORDER BY RAND() LIMIT 0,1");
        
        return DBH.query(sql.toString());
    }
    
    protected boolean storeConfig(ConfigReader cfg) {
        if(!super.storeConfig(cfg)) {
            return false;
        }
        
        if(!cfg.enterNode("general")) {
            Logger.log("ERROR", "Unable to find general details in config.");
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

    private String MODE;
    private final String RANDOM = "RANDOM";
    private final String USER = "USER";
    private final String DINNER_PARTY = "DINNER_PARTY";
    private final String PARTY = "PARTY";
    private int MINUMUM_PARTY_NUMBERS;
    private int MINIMUM_DINNER_PARTY_NUMBERS;
    private int USER_SONG_PERCENTAGE;
    private String DINNER_PARTY_MODE_FILE, PARTY_MODE_FILE, RANDOM_MODE_FILE, USER_MODE_FILE;
}
