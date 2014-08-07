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
** A simple class to represent a close_all packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketCloseAll extends XBMSP_BasePacket {
    public XBMSP_PacketCloseAll() {
    }
    
    public XBMSP_PacketCloseAll(byte[] rawData) {
        super(rawData);
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 MESSAGE_ID;
    private XBMSP_Int32 MESSAGE_LENGTH;
   
}
