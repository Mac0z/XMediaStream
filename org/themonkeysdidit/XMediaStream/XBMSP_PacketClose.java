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
** A simple class to represent a close packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketClose extends XBMSP_BasePacket {
    public XBMSP_PacketClose() {
    }
    
        public XBMSP_PacketClose(byte[] rawData) {
        super(rawData);
        byte[] fileHandle = new byte[4];
        fileHandle[0] = rawData[9];
        fileHandle[1] = rawData[10];
        fileHandle[2] = rawData[11];
        fileHandle[3] = rawData[12];
        FILE_HANDLE = new XBMSP_Int32(true, fileHandle);
    }
    
    public void setDirHandle(XBMSP_Int32 fileHandle) {
        FILE_HANDLE = fileHandle;
    }
    
    public XBMSP_Int32 getFileHandle() {
        return FILE_HANDLE;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] fileHandle = FILE_HANDLE.getNetworkBytes();
        byte[] retVal = new byte[13];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < 4 ; j++) {
            retVal[j+base.length] = fileHandle[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
                
        retVal.append("FILE_HANDLE = ");
        retVal.append(FILE_HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 FILE_HANDLE;
}
