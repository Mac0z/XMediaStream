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
 ** A simple class to represent an Int32 according to the XBMSP protocol.
 **
 ** @author Oliver Wardell
 ** @version 0.1
 **
 ******************************************************************************
 */
 public class XBMSP_Int32 {
     public XBMSP_Int32() {
         
     }
     
     public XBMSP_Int32(boolean isMSB, byte[] data) {
         if(isMSB) {
             setDataMSB(data, 0);
         }
         else {
             setDataLSB(data, 0);
         }
     }
     
     public XBMSP_Int32(long data) {
         DATA = data;
     }
     
     public XBMSP_Int32(boolean isMSB, byte[] data, int offset) {
         if(isMSB) {
             setDataMSB(data, offset);
         }
         else {
             setDataLSB(data, offset);
         }
     }
     
     public void setData(boolean isMSB, byte[] data) {
         if(isMSB) {
             setDataMSB(data, 0);
         }
         else {
             setDataLSB(data, 0);
         }
     }
     
     public void setData(boolean isMSB, byte[] data, int offset) {
         if(isMSB) {
             setDataMSB(data, offset);
         }
         else {
             setDataLSB(data, offset);
         }
     }
     
     public void setData(long data) {
         DATA = data;
     }
     
     public long getData() {
         return DATA;
     }
     
     public byte[] getDataMSB() {
         byte[] retVal = new byte[4];
         int bit0, bit1, bit2, bit3;
         
         bit0 = (int)(DATA & 0x00000000000000FF);
         bit1 = (int)((DATA & 0x000000000000FF00) >>> 8);
         bit2 = (int)((DATA & 0x0000000000FF0000) >>> 16);
         bit3 = (int)((DATA & 0x00000000FF000000) >>> 24);
         
         retVal[0] = (byte)bit3;
         retVal[1] = (byte)bit2;
         retVal[2] = (byte)bit1;
         retVal[3] = (byte)bit0;
         
         return retVal;
     }
     
     public byte[] getDataLSB() {
         
         byte[] retVal = new byte[4];
         int bit0, bit1, bit2, bit3;
         
         bit0 = (int)(DATA & 0x00000000000000FF);
         bit1 = (int)((DATA & 0x000000000000FF00) >>> 8);
         bit2 = (int)((DATA & 0x0000000000FF0000) >>> 16);
         bit3 = (int)((DATA & 0x00000000FF000000) >>> 24);
         
         retVal[3] = (byte)bit3;
         retVal[2] = (byte)bit2;
         retVal[1] = (byte)bit1;
         retVal[0] = (byte)bit0;
         
         return retVal;
     }
     
     public byte[] getNetworkBytes() {
         return getDataMSB();
     }
     
     public String toString() {
         return String.valueOf(DATA);
     }
     
     /*
      * data is in the form:
      * MSB . . LSB
      */
     private void setDataMSB(byte[] data, int offset) {
         DATA = 0x00L;
         DATA = data[0+offset] << 24 | data[1+offset] << 16 | data[2+offset] << 8 | data[3+offset];
     }
     
     /*
      * data is in the form:
      * LSB . . MSB
      */
     private void setDataLSB(byte[] data, int offset) {
         DATA = 0x00L;
         DATA = data[3+offset] << 24 | data[2+offset] << 16 | data[1+offset] << 8 | data[0+offset];
     }
     
     // XBMSP defines Int32 as unsigned which Java is unaware of, therefore
     // we must use a type larger than an int to hold it.
     private long DATA;
 }
