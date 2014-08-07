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
** A simple class to represent a handle packet in the XBMSP protocol..
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketHandle extends XBMSP_BasePacket {
    public XBMSP_PacketHandle() {
    }
    
    public XBMSP_PacketHandle(byte[] rawData) {
        super(rawData);
        byte[] handle = new byte[4];
        handle[0] = rawData[9];
        handle[1] = rawData[10];
        handle[2] = rawData[11];
        handle[3] = rawData[12];
        HANDLE = new XBMSP_Int32(true, handle);
    }
    
    public void setHandle(XBMSP_Int32 handle) {
        HANDLE = handle;
    }
    
    public XBMSP_Int32 getHandle() {
        return HANDLE;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] handle = HANDLE.getNetworkBytes();
        byte[] retVal = new byte[13];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < 4 ; j++) {
            retVal[j+base.length] = handle[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("HANDLE = ");
        retVal.append(HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 HANDLE;

}
