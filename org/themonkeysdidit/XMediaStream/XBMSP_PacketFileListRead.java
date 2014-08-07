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
** A simple class to represent a filelist_read packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileListRead extends XBMSP_BasePacket {
    public XBMSP_PacketFileListRead() {
    }
    
        public XBMSP_PacketFileListRead(byte[] rawData) {
        super(rawData);
        byte[] dirHandle = new byte[4];
        dirHandle[0] = rawData[9];
        dirHandle[1] = rawData[10];
        dirHandle[2] = rawData[11];
        dirHandle[3] = rawData[12];
        DIR_HANDLE = new XBMSP_Int32(true, dirHandle);
    }
    
    public void setDirHandle(XBMSP_Int32 dirHandle) {
        DIR_HANDLE = dirHandle;
    }
    
    public XBMSP_Int32 getDirHandle() {
        return DIR_HANDLE;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] dirHandle = DIR_HANDLE.getNetworkBytes();
        byte[] retVal = new byte[13];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < 4 ; j++) {
            retVal[j+base.length] = dirHandle[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("DIR_HANDLE = ");
        retVal.append(DIR_HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 DIR_HANDLE;
    
}
