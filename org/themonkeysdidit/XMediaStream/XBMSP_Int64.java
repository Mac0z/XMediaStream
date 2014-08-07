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
** A simple class to represent an Int64 according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_Int64 {
    public XBMSP_Int64() {
    }
    
    public XBMSP_Int64(boolean isMSB, byte[] data) {
        if(isMSB) {
            setDataMSB(data);
        }
        else {
            setDataLSB(data);
        }
    }
    
    public XBMSP_Int64(long MSB, long LSB) {
        this.MSB = new XBMSP_Int32(MSB);
        this.LSB = new XBMSP_Int32(LSB);
    }
    
    public XBMSP_Int64(XBMSP_Int32 MSB, XBMSP_Int32 LSB) {
        this.MSB = MSB;
        this.LSB = LSB;
    }
    
    public void setData(long MSB, long LSB) {
        this.MSB = new XBMSP_Int32(MSB);
        this.LSB = new XBMSP_Int32(LSB);
    }
    
    public XBMSP_Int32[] getDataMSB() {
        XBMSP_Int32[] retVal = new XBMSP_Int32[2];
        retVal[0] = MSB;
        retVal[1] = LSB;
        return retVal;
    }
    
    public XBMSP_Int32[] getDataLSB() {
        XBMSP_Int32[] retVal = new XBMSP_Int32[2];
        retVal[0] = LSB;
        retVal[1] = MSB;
        return retVal;
    }
    
    public byte[] getByteArrayMSB() {
        byte[] retVal = new byte[8];
        byte[] msb = MSB.getDataMSB();
        byte[] lsb = LSB.getDataMSB();
        for(int i = 0 ; i < msb.length ; i++) {
            retVal[i] = msb[i];
            retVal[i+msb.length] = lsb[i];
        }
        return retVal;
    }
    
    public byte[] getByteArrayLSB() {
        byte[] retVal = new byte[8];
        byte[] msb = MSB.getDataLSB();
        byte[] lsb = LSB.getDataLSB();
        for(int i = 0 ; i < lsb.length ; i++) {
            retVal[i] = lsb[i];
            retVal[i+lsb.length] = msb[i];
        }
        return retVal;
    }
    
    public byte[] getNetworkBytes() {
        return getByteArrayMSB();
    }
    
    public String toString() {
        String retVal = MSB.toString();
        retVal += LSB.toString();
        return retVal;
    }
    
    private void setDataMSB(byte[] data) {
        MSB = new XBMSP_Int32(true, data, 0);
        LSB = new XBMSP_Int32(true, data, 4);
    }
    
    private void setDataLSB(byte[] data) {
        MSB = new XBMSP_Int32(false, data, 4);
        LSB = new XBMSP_Int32(false, data, 0);
    }
    
    // Could try and use a 64bit unsigned data type but we've already written
    //the bit handling routines for XBMS_Int32, lets use them
    private XBMSP_Int32 MSB;
    private XBMSP_Int32 LSB;
}
