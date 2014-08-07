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
** A simple class to represent a Data according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_Data {
    public XBMSP_Data() {
    }
    
    public XBMSP_Data(byte[] data) {
        DATA = data;
    }
    
    public void setData(byte[] data) {
        DATA = data;
    }
    
    public byte[] getData() {
        return DATA;
    }
    
    public byte[] getNetworkBytes() {
        return getData();
    }
    
    public String toString() {
        return new String(DATA);
    }
    
    private byte[] DATA;
}
