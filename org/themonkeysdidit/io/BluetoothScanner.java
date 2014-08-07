/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.io;

import org.themonkeysdidit.util.*;
import java.util.Enumeration;
import java.util.Vector;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class BluetoothScanner {
    public BluetoothScanner() {
        //setDeviceId(0);
        //BZ = new BlueZ();
    }
    /*
    public BluetoothScanner(int deviceId) {
        setDeviceId(deviceId);
        BZ = new BlueZ();
    }
    
    public void setDeviceId(int deviceId) {
        DEVICE_ID = deviceId;
    }
    */
    /*
    public BluetoothUser[] scan() throws BlueZException {
        Logger.log("DEBUG", "Scanning for bluetooth devices.");
        BluetoothUser[] retVal;
        
        InquiryInfo inqInfo = BZ.hciInquiry(DEVICE_ID);
        Vector devices = inqInfo.devices();
        Enumeration enumer = devices.elements();
        retVal = new BluetoothUser[devices.size()];
        int pos = 0;
        while(enumer.hasMoreElements()) {
            InquiryInfoDevice dev = (InquiryInfoDevice)enumer.nextElement();
            retVal[pos] = new BluetoothUser(dev.bdaddr.toString(), getName(dev));
            pos++;
        }
        Logger.log("DEBUG", "Scan complete.");
        return retVal;
    }
    */
    
    public BluetoothUser[] scan() throws IOException {
        Logger.log("DEBUG", "Scanning for bluetooth devices.");
        BluetoothUser[] retVal;
        
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("/usr/bin/hcitool scan");
        BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line = br.readLine(); // throw away this line
        Vector<BluetoothUser> tmp = new Vector<BluetoothUser>();

        for(line = br.readLine() ; line != null ; line = br.readLine()) {
            String[] splitData = line.trim().split("\t");
            String id = splitData[0];
            String name = "";
            for(int i = 1 ; i < splitData.length ; i++) {
                name = name.concat(splitData[i]);
            }
            tmp.add(new BluetoothUser(id, name));
        }
        
        Enumeration enumer = tmp.elements();
        retVal = new BluetoothUser[tmp.size()];
        int count = 0;
        while(enumer.hasMoreElements()) {
            retVal[count] = (BluetoothUser)enumer.nextElement();
            count++;
        }
        
        return retVal;
    }
    /*
    public String getName(InquiryInfoDevice device) throws BlueZException {
        String retVal = null;
        int dd = BZ.hciOpenDevice(DEVICE_ID);
        BZ.hciCreateConnection(dd, device.bdaddr, 8, 0, (short)0, 30000);
        retVal = BZ.hciRemoteName(dd, device.bdaddr);
        BZ.hciCloseDevice(dd);
        return retVal;
    }
    */
    //private int DEVICE_ID;
    //private BlueZ BZ;
}
