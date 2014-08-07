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
** A simple class to represent a file data packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileData extends XBMSP_BasePacket {
    public XBMSP_PacketFileData() {
    }
    
    public XBMSP_PacketFileData(byte[] rawData) {
        super(rawData);
        byte[] string1Length = new byte[4];
        string1Length[0] = rawData[9];
        string1Length[1] = rawData[10];
        string1Length[2] = rawData[11];
        string1Length[3] = rawData[12];
        XBMSP_Int32 length1 = new XBMSP_Int32(true, string1Length);
        byte[] string1 = new byte[(int)(length1.getData() & 0x00000000FFFFFFFF)+4];
        for(int i = 0 ; i < string1.length ; i++) {
            string1[i] = rawData[i+9];
        }
        FILE_NAME = new XBMSP_String(string1);
        
        byte[] string2Length = new byte[4];
        string2Length[0] = rawData[9+string1.length];
        string2Length[1] = rawData[9+string1.length+1];
        string2Length[2] = rawData[9+string1.length+2];
        string2Length[3] = rawData[9+string1.length+3];
        XBMSP_Int32 length2 = new XBMSP_Int32(true, string2Length);
        byte[] string2 = new byte[(int)(length2.getData() & 0x00000000FFFFFFFF)+4];
        for(int j = 0 ; j < string2.length ; j++) {
            string2[j] = rawData[j+9+string1.length];
        }
        FILE_DATA = new XBMSP_String(string2);
    }
    
    public void setFileName(XBMSP_String name) {
        FILE_NAME = name;
    }
    
    public void setFileData(XBMSP_String data) {
        FILE_DATA = data;
    }
    
    public XBMSP_String getFileName() {
        return FILE_NAME;
    }
    
    public XBMSP_String getFileData() {
        return FILE_DATA;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] name = FILE_NAME.getNetworkBytes();
        byte[] data = FILE_DATA.getNetworkBytes();
        byte[] retVal = new byte[base.length+name.length+data.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < name.length ; j++) {
            retVal[j+base.length] = name[j];
        }
        
        for(int k = 0 ; k < data.length ; k++) {
            retVal[k+base.length+name.length] = data[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_NAME = ");
        retVal.append(FILE_NAME.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("FILE_DATA = ");
        retVal.append(FILE_DATA.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String FILE_NAME;
    private XBMSP_String FILE_DATA;
}
