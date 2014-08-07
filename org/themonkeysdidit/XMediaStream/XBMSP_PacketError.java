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
public class XBMSP_PacketError extends XBMSP_BasePacket {
    public XBMSP_PacketError() {
    }
    
    public XBMSP_PacketError(byte[] rawData) {
        super(rawData);
        
        ERROR_CODE = new XBMSP_Byte(rawData[9]);
        
        byte[] text = new byte[rawData.length-9];
        for(int i = 0 ; i < text.length; i++) {
            text[i] = rawData[i+10];
        }
        ERROR_TEXT = new XBMSP_String(text);
    }
    
    public void setErrorCode(XBMSP_Byte code) {
        ERROR_CODE = code;
    }
    
    public void setErrorText(XBMSP_String text) {
        ERROR_TEXT = text;
    }
    
    public XBMSP_Byte getErrorCode() {
        return ERROR_CODE;
    }
    
    public XBMSP_String getErrorText() {
        return ERROR_TEXT;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] errorCode = ERROR_CODE.getNetworkBytes();
        byte[] errorText = ERROR_TEXT.getNetworkBytes();
        byte[] retVal = new byte[base.length+errorCode.length+errorText.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < errorCode.length ; j++) {
            retVal[j+base.length] = errorCode[j];
        }
        
        for(int k = 0 ; k < errorText.length ; k++) {
            retVal[k+base.length+errorCode.length] = errorText[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("ERROR_CODE = ");
        retVal.append(ERROR_CODE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("ERROR_TEXT = ");
        retVal.append(ERROR_TEXT.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_Byte ERROR_CODE;
    private XBMSP_String ERROR_TEXT;
}
