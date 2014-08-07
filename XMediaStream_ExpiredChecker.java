/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 *
 */
 
import org.themonkeysdidit.XMediaStream.*;
import org.themonkeysdidit.util.*;
    
public class XMediaStream_ExpiredChecker {

    public static void main(String[] args) {
        Logger.log("INFO", "XMediaStream_ExpiredChecker starting");
        if(args.length != 1) {
            Logger.log("ERROR", "No config file defined");
            Logger.log("INFO", "Usage: java XMediaStream_ExpiredChecker <config_file>.xml");
            end();
        }
        XML_ConfigReader cfg = new XML_ConfigReader(args[0]);

        UPnP_ExpiredChecker app = new UPnP_ExpiredChecker(cfg);
        try {
            app.runApp();
        }
        catch(Exception e) {
            Logger.log("ERROR", e.getMessage());
        }
        
        end();
    }
    
    public static void end() {
        Logger.log("INFO", "XMediaStream_ExpiredChecker ended");
        System.exit(-1);
    }

}
