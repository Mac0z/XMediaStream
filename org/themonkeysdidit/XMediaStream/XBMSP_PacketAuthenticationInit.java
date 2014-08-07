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
** A simple class to represent an auth init packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketAuthenticationInit extends XBMSP_BasePacket {
    public XBMSP_PacketAuthenticationInit() {
    }
  
    public XBMSP_PacketAuthenticationInit(byte[] rawData) {
        super(rawData);
        byte[] stringPart = new byte[rawData.length - 9];
        for(int i = 0 ; i < stringPart.length ; i++) {
            stringPart[i] = rawData[i+9];
        }
        METHOD = new XBMSP_String(stringPart);
    }
    
    public void setFileName(XBMSP_String method) {
        METHOD = method;
    }
    
    public XBMSP_String getMethod() {
        return METHOD;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] namePart = METHOD.getNetworkBytes();
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
        
        retVal.append("METHOD = ");
        retVal.append(METHOD.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String METHOD;
    
}
