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
** A simple class to represent a file_info packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileInfo extends XBMSP_BasePacket {
    public XBMSP_PacketFileInfo() {
    }
  
    public XBMSP_PacketFileInfo(byte[] rawData) {
        super(rawData);
        byte[] stringPart = new byte[rawData.length - 9];
        for(int i = 0 ; i < stringPart.length ; i++) {
            stringPart[i] = rawData[i+9];
        }
        FILE_NAME = new XBMSP_String(stringPart);
    }
    
    public void setFileName(XBMSP_String fileName) {
        FILE_NAME = fileName;
    }
    
    public XBMSP_String getFileName() {
        return FILE_NAME;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] namePart = FILE_NAME.getNetworkBytes();
        byte[] retVal = new byte[base.length + namePart.length];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < namePart.length ; j++) {
            retVal[j+base.length] = namePart[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_NAME = ");
        retVal.append(FILE_NAME.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String FILE_NAME;

}
