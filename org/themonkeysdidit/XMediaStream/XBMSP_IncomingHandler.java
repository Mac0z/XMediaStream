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
** The XBMSP_IncomingHandler sends transactions to the txn handler that write
** incoming packets from the xbox to the database.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_IncomingHandler extends Thread {
    public XBMSP_IncomingHandler(InputStream in, int id) {
        IN = in;
        ID = id;
    }
    
    public void run() {
        
    }
    
    private InputStream IN;
    private int ID;
}