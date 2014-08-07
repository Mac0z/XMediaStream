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
** A simple class to represent an ok packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketOk extends XBMSP_BasePacket {
    public XBMSP_PacketOk() {
    }
    
    public XBMSP_PacketOk(byte[] rawData) {
        super(rawData);
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 MESSAGE_ID;
    private XBMSP_Int32 MESSAGE_LENGTH;
   
}
