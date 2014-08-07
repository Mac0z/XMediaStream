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
 ** A simple class to represent a byte of data according to the XBMSP protocol.
 **
 ** @author Oliver Wardell
 ** @version 0.1
 **
 ******************************************************************************
 */
 public class XBMSP_Byte {
     public XBMSP_Byte() {
         DATA = new byte[1];
     }
     
     /**
      * @param data The value to set the byte to.
      */
     public XBMSP_Byte(byte data) {
         DATA = new byte[1];
         DATA[0] = data;
     }
     
     /**
      * @param data The value to set the byte to.
      */
     public XBMSP_Byte(byte[] data) {
         DATA = data;
     }
     
     /**
      * @param data An int of data but we ignore everything bar the last
      * byte.
      */
     public XBMSP_Byte(int data) {
         DATA = new byte[1];
         DATA[0] = (byte)(data & 0x000000FF);
     }
     
     /**
      * @param data The value to set the byte to.
      */
     public void setByte(byte data) {
         DATA[0] = data;
     }
     
     /**
      * @param data The value to set the byte to.
      */
     public void setByte(byte[] data) {
         DATA = data;
     }
     
     /**
      * @return The value of the byte, as a native Java byte
      */
     public byte getByte() {
         return DATA[0];
     }
     
     /**
      * @return The value of the byte as an array (1 byte long).
      */
     public byte[] getBytes() {
         return DATA;
     }
     
     public byte[] getNetworkBytes() {
         return getBytes();
     }
     
     public String toString() {
         int i = 0x00 << 24 | 0x00 << 16 | 0x00 << 8 | DATA[0];
         return String.valueOf(i);
     }
     
     private byte[] DATA;
 
 }
