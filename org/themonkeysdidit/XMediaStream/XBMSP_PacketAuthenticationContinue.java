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
** A simple class to represent a auth continue packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketAuthenticationContinue extends XBMSP_BasePacket {
    public XBMSP_PacketAuthenticationContinue() {
    }
    
    public XBMSP_PacketAuthenticationContinue(byte[] rawData) {
        super(rawData);
        
        byte[] data = new byte[rawData.length-9];
        for(int i = 0 ; i < data.length ; i++) {
            data[i] = rawData[i+9];
        }
        DATA = new XBMSP_Data(data);
    }
    
    public void setData(XBMSP_Data data) {
        DATA = data;
    }
    
    public XBMSP_Data getData() {
        return DATA;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] data = DATA.getNetworkBytes();
        byte[] retVal = new byte[base.length+data.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < data.length ; j++) {
            retVal[j+base.length] = data[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("DATA = ");
        retVal.append(DATA.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Data DATA;
}
