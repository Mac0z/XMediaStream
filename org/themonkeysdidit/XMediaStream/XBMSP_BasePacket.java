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
** A simple class to represent a base packet extended by all other packets in XBMSP.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_BasePacket {
    public XBMSP_BasePacket() {
    }
    
    public XBMSP_BasePacket(byte[] rawData) {
        MESSAGE_LENGTH = new XBMSP_Int32(true, rawData, 0);
        MESSAGE_TYPE = new XBMSP_Byte(rawData[4]);
        MESSAGE_ID = new XBMSP_Int32(true, rawData, 5);
    }
    
    public void setMessageId(XBMSP_Int32 messageId) {
        MESSAGE_ID = messageId;
    }
    
    public void setMessageLength(XBMSP_Int32 length) {
        MESSAGE_LENGTH = length;
    }
    
    public void setMessageType(XBMSP_Byte messageType) {
        MESSAGE_TYPE = messageType;
    }
    
    public XBMSP_Byte getMessageType() {
        return MESSAGE_TYPE;
    }
    
    public XBMSP_Int32 getMessageLength() {
        return MESSAGE_LENGTH;
    }
    
    public XBMSP_Int32 getMessageId() {
        return MESSAGE_ID;
    }
    
    public byte[] getNetworkBytes() {
        byte[] retVal = new byte[9];
        
        byte[] tmp = MESSAGE_LENGTH.getDataMSB();
        for(int i = 0 ; i < 4 ; i++) {
            retVal[i] = tmp[i];
        }
        
        retVal[4] = MESSAGE_TYPE.getByte();
        
        tmp = MESSAGE_ID.getDataMSB();
        for(int i = 0 ; i < 4 ; i++) {
            retVal[i+5] = tmp[i];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal;
        retVal = new StringBuffer("MESSAGE_LENGTH = ");
        retVal.append(MESSAGE_LENGTH.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("MESSAGE_TYPE = ");
        retVal.append(MESSAGE_TYPE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("MESSAGE_ID = ");
        retVal.append(MESSAGE_ID.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
        
    }
    
    protected XBMSP_Int32 MESSAGE_ID;
    protected XBMSP_Int32 MESSAGE_LENGTH;
    protected XBMSP_Byte MESSAGE_TYPE;

}
