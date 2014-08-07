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
** A simple class to represent a upcwd packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketUpCwd extends XBMSP_BasePacket {
    public XBMSP_PacketUpCwd() {
    }
    
    public XBMSP_PacketUpCwd(byte[] rawData) {
        super(rawData);
        byte[] numLevels = new byte[4];
        numLevels[0] = rawData[9];
        numLevels[1] = rawData[10];
        numLevels[2] = rawData[11];
        numLevels[3] = rawData[12];
        NUM_LEVELS = new XBMSP_Int32(true, numLevels);
    }
    
    public void setNumLevels(XBMSP_Int32 numLevels) {
        NUM_LEVELS = numLevels;
    }
    
    public XBMSP_Int32 getNumLevels() {
        return NUM_LEVELS;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] numLevels = NUM_LEVELS.getNetworkBytes();
        byte[] retVal = new byte[13];
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        for(int j = 0 ; j < 4 ; j++) {
            retVal[j+base.length] = numLevels[j];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("NUM_LEVELS = ");
        retVal.append(NUM_LEVELS.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Int32 NUM_LEVELS;

}
