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
** A simple class to represent a config option set packet according to the XBMSP protocol.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_PacketSetConfigurationOption extends XBMSP_BasePacket {
    public XBMSP_PacketSetConfigurationOption() {
    }
    
    public XBMSP_PacketSetConfigurationOption(byte[] rawData) {
        super(rawData);
        byte[] string1Length = new byte[4];
        string1Length[0] = rawData[9];
        string1Length[1] = rawData[10];
        string1Length[2] = rawData[11];
        string1Length[3] = rawData[12];
        XBMSP_Int32 length1 = new XBMSP_Int32(true, string1Length);
        byte[] string1 = new byte[(int)(length1.getData() & 0x00000000FFFFFFFF)+4];
        for(int i = 0 ; i < string1.length ; i++) {
            string1[i] = rawData[i+9];
        }
        OPTION_NAME = new XBMSP_String(string1);
        
        byte[] string2Length = new byte[4];
        string2Length[0] = rawData[9+string1.length];
        string2Length[1] = rawData[9+string1.length+1];
        string2Length[2] = rawData[9+string1.length+2];
        string2Length[3] = rawData[9+string1.length+3];
        XBMSP_Int32 length2 = new XBMSP_Int32(true, string2Length);
        byte[] string2 = new byte[(int)(length2.getData() & 0x00000000FFFFFFFF)+4];
        for(int j = 0 ; j < string2.length ; j++) {
            string2[j] = rawData[j+9+string1.length];
        }
        OPTION_VALUE = new XBMSP_String(string2);
    }
    
    public void setOptionName(XBMSP_String name) {
        OPTION_NAME = name;
    }
    
    public void setOptionValue(XBMSP_String value) {
        OPTION_VALUE = value;
    }
    
    public XBMSP_String getOptionName() {
        return OPTION_NAME;
    }
    
    public XBMSP_String getOptionValue() {
        return OPTION_VALUE;
    }
    
    public byte[] getNetworkBytes() {
        byte[] base = super.getNetworkBytes();
        byte[] name = OPTION_NAME.getNetworkBytes();
        byte[] value = OPTION_VALUE.getNetworkBytes();
        byte[] retVal = new byte[base.length+name.length+value.length];
        
        for(int i = 0 ; i < base.length ; i++) {
            retVal[i] = base[i];
        }
        
        for(int j = 0 ; j < name.length ; j++) {
            retVal[j+base.length] = name[j];
        }
        
        for(int k = 0 ; k < value.length ; k++) {
            retVal[k+base.length+name.length] = value[k];
        }
        
        return retVal;
    }
    
    public String toString() {
        StringBuffer retVal = new StringBuffer(super.toString());
        
        retVal.append("OPTION_NAME = ");
        retVal.append(OPTION_NAME.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        retVal.append("OPTION_VALUE = ");
        retVal.append(OPTION_VALUE.toString());
        retVal.append(Constants.LINE_SEPARATOR);
        
        return retVal.toString();
    }
    
    private XBMSP_String OPTION_NAME;
    private XBMSP_String OPTION_VALUE;
}
