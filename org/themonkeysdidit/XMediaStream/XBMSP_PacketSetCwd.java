/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;
 
/**
******************************************************************************
**
** A simple class to represent a setcwd packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketSetCwd extends XBMSP_BasePacket {
    public XBMSP_PacketSetCwd() {
    }
    
    public XBMSP_PacketSetCwd(byte[] rawData) {
        super(rawData);
        byte[] stringPart = new byte[rawData.length - 9];
        for(int i = 0 ; i < stringPart.length ; i++) {
            stringPart[i] = rawData[i+9];
        }
        DIR_NAME = new XBMSP_String(stringPart);
    }
    
    public void setDirName(XBMSP_String dirName) {
        DIR_NAME = dirName;
    }
    
    public XBMSP_String getDirName() {
        return DIR_NAME;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] dirPart = DIR_NAME.getNetworkBytes();
        byte[] retVal = new byte[base.length + dirPart.length];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < dirPart.length ; j++) {
            retVal[j+base.length] = dirPart[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("DIR_NAME = ");
        retVal.append(DIR_NAME.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String DIR_NAME;
    
}
