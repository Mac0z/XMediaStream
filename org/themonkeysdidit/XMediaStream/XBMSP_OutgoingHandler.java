/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.XMediaStream;

import java.net.*;
import java.io.*;

/**
******************************************************************************
**
** The XBMSP_OutgoingHandler reads records from the database and writes the byte
** data out to the xbox.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_OutgoingHandler extends Thread {
    public XBMSP_OutgoingHandler(OutputStream out, int id) {
        OUT = out;
        ID = id;
    }
    
    public void run() {
        
    }
    
    private OutputStream OUT;
    private int ID;
}