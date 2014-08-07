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
** A simple class to represent a file_seek packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileSeek extends XBMSP_BasePacket {
    public XBMSP_PacketFileSeek() {
    }
    
    public XBMSP_PacketFileSeek(byte[] rawData) {
        super(rawData);
        byte[] fileHandle = new byte[4];
        fileHandle[0] = rawData[9];
        fileHandle[1] = rawData[10];
        fileHandle[2] = rawData[11];
        fileHandle[3] = rawData[12];
        FILE_HANDLE = new XBMSP_Int32(true, fileHandle);
        
        SEEK_TYPE = new XBMSP_Byte(rawData[13]);
        
        byte[] offset = new byte[8];
        for(int i = 0 ; i < offset.length ; i++) {
            offset[i] = rawData[i+14];
        }
        OFFSET = new XBMSP_Int64(true, offset);
    }
    
    public void setFileHandle(XBMSP_Int32 fileHandle) {
        FILE_HANDLE = fileHandle;
    }
    
    public void setSeekType(XBMSP_Byte seekType) {
        SEEK_TYPE = seekType;
    }
    
    public void setOffset(XBMSP_Int64 offset) {
        OFFSET = offset;
    }
    
    public XBMSP_Int32 getFileHandle() {
        return FILE_HANDLE;
    }
    
    public XBMSP_Byte getSeekType() {
        return SEEK_TYPE;
    }
    
    public XBMSP_Int64 getOffset() {
        return OFFSET;
    }
    
    public byte[] getNetworkBYtes() {
        byte[] base = super.getNetworkBytes();
        byte[] fileHandle = FILE_HANDLE.getNetworkBytes();
        byte[] seekType = SEEK_TYPE.getNetworkBytes();
        byte[] offset = OFFSET.getNetworkBytes();
        byte[] retVal = new byte[base.length + fileHandle.length + seekType.length + offset.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < fileHandle.length ; j++) {
            retVal[j+base.length] = fileHandle[j];
        }
        
        retVal[base.length+fileHandle.length] = seekType[0];
        
        for(int k = 0 ; k < offset.length ; k++) {
            retVal[k+base.length+fileHandle.length+seekType.length] = offset[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_HANDLE = ");
        retVal.append(FILE_HANDLE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("SEEK_TYPE = ");
        retVal.append(SEEK_TYPE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("OFFSET = ");
        retVal.append(OFFSET.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 FILE_HANDLE;
    private XBMSP_Byte SEEK_TYPE;
    private XBMSP_Int64 OFFSET;

}
