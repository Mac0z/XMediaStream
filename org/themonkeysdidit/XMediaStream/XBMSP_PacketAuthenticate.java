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
** A simple class to represent an auth packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketAuthenticate extends XBMSP_BasePacket {
    public XBMSP_PacketAuthenticate() {
    }
    
    public XBMSP_PacketAuthenticate(byte[] rawData) {
        super(rawData);
        
        byte[] fileHandle = new byte[4];
        fileHandle[0] = rawData[9];
        fileHandle[1] = rawData[10];
        fileHandle[2] = rawData[11];
        fileHandle[3] = rawData[12];
        FILE_HANDLE = new XBMSP_Int32(true, fileHandle);
        
        byte[] data = new byte[rawData.length-9-4];
        for(int i = 0 ; i < data.length ; i++) {
            data[i] = rawData[i+9+fileHandle.length];
        }
    }
    
    public void setFileHandle(XBMSP_Int32 handle) {
        FILE_HANDLE = handle;
    }
    
    public void setData(XBMSP_Data data) {
        DATA = data;
    }
    
    public XBMSP_Int32 getFileHandle() {
        return FILE_HANDLE;
    }
    
    public XBMSP_Data getData() {
        return DATA;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] fileHandle = FILE_HANDLE.getNetworkBytes();
        byte[] data = DATA.getNetworkBytes();
        byte[] retVal = new byte[base.length+fileHandle.length+data.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < fileHandle.length ; j++) {
            retVal[j+base.length] = fileHandle[j];
        }
        
        for(int k = 0 ; k < data.length ; k++) {
            retVal[k+base.length+fileHandle.length] = data[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_HANDLE = ");
        retVal.append(FILE_HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("DATA = ");
        retVal.append(DATA.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 FILE_HANDLE;
    private XBMSP_Data DATA;
}
