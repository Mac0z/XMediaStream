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
** A simple class to represent a String according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_String {
    public XBMSP_String() {
        DATA_LENGTH = new XBMSP_Int32(0L);
    }
    
    public XBMSP_String(XBMSP_Int32 length, byte[] data) {
        DATA_LENGTH = length;
        DATA = data;
    }
    
    
    /**
     *
     * @param data The raw data required to construct this data type. Note that
     * the length of the string (i.e. the first 4 bytes) are included and in
     * MSB format.
     */
    public XBMSP_String(byte[] data) {
        byte[] len = new byte[4];
        len[0] = data[0];
        len[1] = data[1];
        len[2] = data[2];
        len[3] = data[3];
        DATA_LENGTH = new XBMSP_Int32(true, len);
        DATA = data;
    }

    /**
     * @param data The data argument here does not have the length of the
     * string in the first 4 bytes.
     */
    public void setData(byte[] data) {
        DATA = data;
        DATA_LENGTH = new XBMSP_Int32((long)data.length);
    }
    
    public XBMSP_Int32 getLength() {
        return DATA_LENGTH;
    }
    
    public byte[] getData() {
        return DATA;
    }
    
    public byte[] getNetworkBytes() {
        byte[] retVal = new byte[DATA.length+4];
        byte[] intPart = DATA_LENGTH.getDataMSB();
        for(int i = 0 ; i < intPart.length ; i++) {
            retVal[i] = intPart[i];
        }
        for(int j = 0 ; j < DATA.length ; j++) {
            retVal[j+4] = DATA[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        String retVal = DATA_LENGTH.toString() + ":";
        retVal += new String(DATA);
        return retVal;
    }

    private XBMSP_Int32 DATA_LENGTH;
    private byte[] DATA;
}
