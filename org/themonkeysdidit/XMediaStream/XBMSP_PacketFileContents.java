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
** A simple class to represent a file contents packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileContents extends XBMSP_BasePacket {
    public XBMSP_PacketFileContents() {
    }
  
    public XBMSP_PacketFileContents(byte[] rawData) {
        super(rawData);
        byte[] stringPart = new byte[rawData.length - 9];
        for(int i = 0 ; i < stringPart.length ; i++) {
            stringPart[i] = rawData[i+9];
        }
        FILE_DATA = new XBMSP_String(stringPart);
    }
    
    public void setFileName(XBMSP_String fileData) {
        FILE_DATA = fileData;
    }
    
    public XBMSP_String getFileData() {
        return FILE_DATA;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] dataPart = FILE_DATA.getNetworkBytes();
        byte[] retVal = new byte[base.length + dataPart.length];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < dataPart.length ; j++) {
            retVal[j+base.length] = dataPart[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("FILE_DATA = ");
        retVal.append(FILE_DATA.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String FILE_DATA;
    
}
