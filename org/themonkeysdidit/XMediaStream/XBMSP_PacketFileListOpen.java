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
** A simple class to represent a filelist_open packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketFileListOpen extends XBMSP_BasePacket {
    public XBMSP_PacketFileListOpen() {
    }
    
    public XBMSP_PacketFileListOpen(byte[] rawData) {
        super(rawData);
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        return retVal.toString();
    }
    
}
