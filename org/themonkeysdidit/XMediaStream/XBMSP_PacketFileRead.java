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
** A simple class to represent a file_read packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileRead extends XBMSP_BasePacket {
    public XBMSP_PacketFileRead() {
    }
    
    public XBMSP_PacketFileRead(byte[] rawData) {
        super(rawData);
        
        byte[] fileHandle = new byte[4];
        fileHandle[0] = rawData[9];
        fileHandle[1] = rawData[10];
        fileHandle[2] = rawData[11];
        fileHandle[3] = rawData[12];
        FILE_HANDLE = new XBMSP_Int32(true, fileHandle);
        
        byte[] numBytes = new byte[4];
        numBytes[0] = rawData[13];
        numBytes[1] = rawData[14];
        numBytes[2] = rawData[15];
        numBytes[3] = rawData[16];
        NUM_BYTES = new XBMSP_Int32(true, numBytes);
    }
    
    public void setFileHandle(XBMSP_Int32 fileHandle) {
        FILE_HANDLE = fileHandle;
    }
    
    public void setNumBytes(XBMSP_Int32 numBytes) {
        NUM_BYTES = numBytes;
    }
    
    public XBMSP_Int32 getFileHandle() {
        return FILE_HANDLE;
    }
    
    public XBMSP_Int32 getNumBytes() {
        return NUM_BYTES;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] fileHandle = FILE_HANDLE.getNetworkBytes();
        byte[] numBytes = NUM_BYTES.getNetworkBytes();
        byte[] retVal = new byte[base.length + fileHandle.length + numBytes.length];
    
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < fileHandle.length ; j++) {
            retVal[j+base.length] = fileHandle[j];
        }
        for(int k = 0 ; k < numBytes.length ; k++) {
            retVal[k+base.length+fileHandle.length] = numBytes[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_HANDLE = ");
        retVal.append(FILE_HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("NUM_BYTES = ");
        retVal.append(NUM_BYTES.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 FILE_HANDLE;
    private XBMSP_Int32 NUM_BYTES;

}
