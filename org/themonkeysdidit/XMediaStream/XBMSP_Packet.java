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
** A simple class to represent a basic packet according to the XBMSP protocol.
** This class should not be instantiated as it serves no purpose in the
** XBMSP protocol, however, it will be made public to expose the available
** static constants
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public abstract class XBMSP_Packet {

    // Packets from client
    public static final int XBMSP_PACKET_NULL =                         10;
    public static final int XBMSP_PACKET_SETCWD =                       11;
    public static final int XBMSP_PACKET_FILELIST_OPEN =                12;
    public static final int XBMSP_PACKET_FILELIST_READ =                13;
    public static final int XBMSP_PACKET_FILE_INFO =                    14;
    public static final int XBMSP_PACKET_FILE_OPEN =                    15;
    public static final int XBMSP_PACKET_FILE_READ =                    16;
    public static final int XBMSP_PACKET_FILE_SEEK =                    17;
    public static final int XBMSP_PACKET_CLOSE =                        18;
    public static final int XBMSP_PACKET_CLOSE_ALL =                    19;
    public static final int XBMSP_PACKET_SET_CONFIGURATION_OPTION =     20;
    public static final int XBMSP_PACKET_AUTHENTICATION_INIT =          21;
    public static final int XBMSP_PACKET_AUTHENTICATE =                 22;
    public static final int XBMSP_PACKET_UPCWD =                        23;
    
    // Packets from server
    public static final int XBMSP_PACKET_OK =                           1;
    public static final int XBMSP_PACKET_ERROR =                        2;
    public static final int XBMSP_PACKET_HANDLE =                       3;
    public static final int XBMSP_PACKET_FILE_DATA =                    4;
    public static final int XBMSP_PACKET_FILE_CONTENTS =                5;
    public static final int XBMSP_PACKET_AUTHENTICATION_CONTINUE =      6;
    
    // Server discovery protocol
    public static final int XBMSP_PACKET_SERVER_DISCOVERY_QUERY =       90;
    public static final int XBMSP_PACKET_SERVER_DISCOVERY_REPLY =       91;
    
    // Seek type constants
    public static final int XBMSP_SEEK_FORWARD_FROM_START =             0;
    public static final int XBMSP_SEEK_BACKWARD_FROM_END =              1;
    public static final int XBMSP_SEEK_FORWARD_FROM_CURRENT =           2;
    public static final int XBMSP_SEEK_BACKWARD_FROM_CURRENT =          3;
    
    // ERROR code constants
    public static final int XBMSP_ERROR_OK =                            0;
    public static final int XBMSP_ERROR_FAILURE =                       1;
    public static final int XBMSP_ERROR_UNSUPPORTED =                   2;
    public static final int XBMSP_ERROR_NO_SUCH_FILE =                  3;
    public static final int XBMSP_ERROR_INVALID_FILE =                  4;
    public static final int XBMSP_ERROR_INVALID_HANDLE =                5;
    public static final int XBMSP_ERROR_OPEN_FAILED =                   6;
    public static final int XBMSP_ERROR_TOO_MANY_OPEN_FILES =           7;
    public static final int XBMSP_ERROR_TOO_LONG_READ =                 8;
    public static final int XBMSP_ERROR_ILLEGAL_SEEK =                  9;
    public static final int XBMSP_ERROR_OPTION_IS_READ_ONLY =           10;
    public static final int XBMSP_ERROR_INVALID_OPTION_VALUE =          11;
    public static final int XBMSP_ERROR_AUTHENTICATION_NEEDED =         12;
    public static final int XBMSP_ERROR_AUTHENTICATION_FAILED =         13;

}
