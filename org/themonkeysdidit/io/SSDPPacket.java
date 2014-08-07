/*
 ******************************************************************************
 *
 * Copyright 2009 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.io;
 
import java.net.*;
import java.util.StringTokenizer;

import org.themonkeysdidit.util.*;

/*
NOTIFY * HTTP/1.1
HOST: 239.255.255.250:1900
CACHE-CONTROL: max-age = seconds until advertisement expires
LOCATION: URL for UPnP description for root device
NT: notification type
NTS: ssdp:alive
SERVER: OS/version UPnP/1.1 product/version
USN: composite identifier for the advertisement
BOOTID.UPNP.ORG: number increased each time device sends an initial announce or an update
message
CONFIGID.UPNP.ORG: number used for caching description information
SEARCHPORT.UPNP.ORG:
number identifies port on which device responds to unicast M-SEARCH
*/
 
public class SSDPPacket {
    public SSDPPacket(DatagramPacket p) {
        storePacketDetails(p);
    }
    
    public String getType() {
        return TYPE;
    }
    
    public String getHost() {
        return HOST;
    }
    
    public int getMaxAge() {
        return MAX_AGE;
    }
    
    public String getLocation() {
        return LOCATION;
    }
    
    public String getNT() {
        return NT;
    }
    
    public String getNTS() {
        return NTS;
    }
    
    public String getServer() {
        return SERVER;
    }
    
    public String getUSN() {
        return USN;
    }
    
    public int getBootID() {
        return BOOT_ID;
    }
    
    public int getConfigID() {
        return CONFIG_ID;
    }
    
    public int getSearchPort() {
        return SEARCH_PORT;
    }
    
    public String dump() {
        StringBuffer sb = new StringBuffer();
        sb.append("Type: ");
        sb.append(getType());
        sb.append(System.getProperty("line.separator"));
        sb.append("Host: ");
        sb.append(getHost());
        sb.append(System.getProperty("line.separator"));
        sb.append("Max Age: ");
        sb.append(getMaxAge());
        sb.append(System.getProperty("line.separator"));
        sb.append("Location: ");
        sb.append(getLocation());
        sb.append(System.getProperty("line.separator"));
        sb.append("NT: ");
        sb.append(getNT());
        sb.append(System.getProperty("line.separator"));
        sb.append("NTS: ");
        sb.append(getNTS());
        sb.append(System.getProperty("line.separator"));
        sb.append("Server: ");
        sb.append(getServer());
        sb.append(System.getProperty("line.separator"));
        sb.append("USN: ");
        sb.append(getUSN());
        sb.append(System.getProperty("line.separator"));
        sb.append("BOOTID.UPNP.ORG: ");
        sb.append(getBootID());
        sb.append(System.getProperty("line.separator"));
        sb.append("CONFIGID.UPNP.ORG: ");
        sb.append(getConfigID());
        sb.append(System.getProperty("line.separator"));
        sb.append("SEARCHPORT.UPNP.ORG: ");
        sb.append(getSearchPort());
        sb.append(System.getProperty("line.separator"));
        
        return sb.toString();
    }
    
    private void storePacketDetails(DatagramPacket p) {
        String data = new String(p.getData());
        
        // The protocol specifies that each line is terminated with \n\r
        // so lets split our data up based on that
        Logger.log("DEBUG", "Data read from packet: " + System.getProperty("line.separator") + data);
        
        StringTokenizer breaker = new StringTokenizer(data, "\n\r");
        
        // First line always contains the type.
        setType(breaker.nextToken());
        
        // Now parse the rest.
        while(breaker.hasMoreTokens()) {
            storeDetails(breaker.nextToken());
        }
    }
    
    private void setType(String line) {
        StringTokenizer breaker = new StringTokenizer(line);
        TYPE = breaker.nextToken();
    }
    
    private void storeDetails(String line) {
        StringTokenizer breaker = new StringTokenizer(line);
        String name = breaker.nextToken();
        String value = new String();
        while(breaker.hasMoreTokens()) {
            value = value.concat(breaker.nextToken());
        }
            
        if(name.compareToIgnoreCase("HOST:") == 0) {
            setHost(value.trim());
        }
        else if(name.compareToIgnoreCase("CACHE-CONTROL:") == 0) {
            setCacheControl(value.trim());
        }
        else if(name.compareToIgnoreCase("LOCATION:") == 0) {
            setLocation(value.trim());
        }
        else if(name.compareToIgnoreCase("NT:") == 0) {
            setNT(value.trim());
        }
        else if(name.compareToIgnoreCase("NTS:") == 0) {
            setNTS(value.trim());
        }
        else if(name.compareToIgnoreCase("SERVER:") == 0) {
            setServer(value.trim());
        }
        else if(name.compareToIgnoreCase("USN:") == 0) {
            setUSN(value.trim());
        }
        else if(name.compareToIgnoreCase("BOOTID.UPNP.ORG:") == 0) {
            setBootID(Integer.parseInt(value.trim()));
        }
        else if(name.compareToIgnoreCase("CONFIGID.UPNP.ORG:") == 0) {
            setConfigID(Integer.parseInt(value.trim()));
        }
        else if(name.compareToIgnoreCase("SEARCHPORT.UPNP.ORG:") == 0) {
            setSearchPort(Integer.parseInt(value.trim()));
        }
    }
    
    private void setHost(String s) {
        if(s.compareToIgnoreCase(DEFAULT_HOST) != 0) {
            Logger.log("WARNING", "None default host received: " + s);
        }
        HOST = s;
        
    }
    
    private void setCacheControl(String s) {
        StringTokenizer breaker = new StringTokenizer(s, "=");
        String name = breaker.nextToken();
        String value = breaker.nextToken();
        if(name.trim().compareToIgnoreCase("max-age") == 0) {
            MAX_AGE = Integer.parseInt(value.trim());
        }
        else {
            Logger.log("WARNING", "Unknown CACHE-CONTROL received: " + s);
        }
    }
    
    private void setLocation(String s) {
        LOCATION = s;
    }
    
    private void setNT(String s) {
        NT = s;
    }
    
    private void setNTS(String s) {
        NTS = s;
    }
    
    private void setServer(String s) {
        SERVER = s;
    }
    
    private void setUSN(String s) {
        USN = s;
    }
    
    private void setBootID(int i) {
        BOOT_ID = i;
    }
    
    private void setConfigID(int i) {
        CONFIG_ID = i;
    }
    
    private void setSearchPort(int i) {
        SEARCH_PORT = i;
    }
    
    private String TYPE;
    private String HOST;
    private int MAX_AGE;
    private String LOCATION;
    private String NT;
    private String NTS;
    private String SERVER;
    private String USN;
    private int BOOT_ID;
    private int CONFIG_ID;
    private int SEARCH_PORT;
    private final String DEFAULT_HOST = "239.255.255.250:1900";
}
