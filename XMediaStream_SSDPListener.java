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
    
public class XMediaStream_SSDPListener {

    public static void main(String[] args) {
        Logger.log("INFO", "XMediaStream_SSDPListener starting");
        if(args.length != 1) {
            Logger.log("ERROR", "No config file defined");
            Logger.log("INFO", "Usage: java XMediaStream_SSDPListener <config_file>.xml");
            end();
        }
        XML_ConfigReader cfg = new XML_ConfigReader(args[0]);

        UPnP_DiscoveryListener app = new UPnP_DiscoveryListener(cfg);
        try {
            app.startListening();
        }
        catch(Exception e) {
            Logger.log("ERROR", e.getMessage());
        }
        
        end();
    }
    
    public static void end() {
        Logger.log("INFO", "XMediaStream_SSDPListener ended");
        System.exit(-1);
    }

}
