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
import java.util.Vector;

/**
******************************************************************************
**
** The XBMSP_ConnectionListener takes care of starting the incoming and
** outgoing threads. The incoming thread inserts the packets received into the
** database, an internal process reads the packet and creates the correct
** outgoing record. The outgoing record is then picked up byt the outgoing thread,
** all the while the XBMSP_ConnectionListener starts waiting for the next connection.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class XBMSP_ConnectionListener extends Thread {
    public XBMSP_ConnectionListener(int port) {
        LISTEN_PORT = port;
        KEEP_GOING = true;
        INCOMING_HANDLERS = new Vector<XBMSP_IncomingHandler>();
        OUTGOING_HANDLERS = new Vector<XBMSP_OutgoingHandler>();
    }
    
    public void run() {
        int connectionHandlerId = 0;
        try {
            ServerSocket servSocket = new ServerSocket(LISTEN_PORT);
            // If we time out every second, we can keep checking the status of keepGoing()
            servSocket.setSoTimeout(1000);
            while(keepGoing()) {
                try {
                    Socket s = servSocket.accept();
                    XBMSP_IncomingHandler incoming = new XBMSP_IncomingHandler(s.getInputStream(), connectionHandlerId);
                    INCOMING_HANDLERS.add(incoming);
                    incoming.start();
                    XBMSP_OutgoingHandler outgoing = new XBMSP_OutgoingHandler(s.getOutputStream(), connectionHandlerId);
                    OUTGOING_HANDLERS.add(outgoing);
                    outgoing.start();
                    connectionHandlerId++;
                }
                catch(SocketTimeoutException ste) {
                    // intentionally don't do anything
                }
                
            }
        }
        catch(IOException io) {
            System.out.println(io);
        }
    }
    
    public void terminate() {
        // terminate all the currently running threads, then terminate this.
        KEEP_GOING = false;
    }
    
    private boolean keepGoing() {
        return KEEP_GOING;
    }
    
    private int LISTEN_PORT;
    private boolean KEEP_GOING;
    private Vector<XBMSP_IncomingHandler> INCOMING_HANDLERS;
    private Vector<XBMSP_OutgoingHandler> OUTGOING_HANDLERS;
}