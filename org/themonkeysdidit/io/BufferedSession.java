/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 * XMediaStream is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * XMediaStream is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.io;

import java.net.*;
import java.io.*;
import java.util.zip.*;
import org.themonkeysdidit.util.*;

/**
******************************************************************************
**
** A BufferedSession is a simplified protocol for sending SimpleMessageObjects
** over a TCP link. Initiaton of the link is all taken care of bu this class.
**
** @author Oliver Wardell
** @version 0.1
**
******************************************************************************
*/
public class BufferedSession implements MessageSession {
    
    /**
     * The constructor does nothing really.
     */
    public BufferedSession() {
    }

    /**
     * The initiate function must be called before any user data is transmitted
     * as this establishes the link with the remote system.
     *
     * @param socket The socket to use fot the TCP link. It must already be
     *               connected so the IO streams are availble.
     * @throws IOException An IOException is thrown if an error occurs while
     * trying to iniate the link.
     */
    public void initiate(Socket socket) throws IOException {
        SOCK = socket;
        SOCK.setSoLinger(true, 30);
        Logger.log("DEBUG", "Initiating BufferedSession connection");
        
        OUT = new BufferedOutputStream(SOCK.getOutputStream());
        IN = new BufferedInputStream(SOCK.getInputStream());
        
        SimpleMessageObject ping = new SimpleMessageObject();
        ping.addString("ACTION", "HELLO");
        ping.addString("MESSAGE_TYPE", "CONTROL");
        transmit(ping);
        boolean waitOne = true;
        boolean waitTwo = true;
        
        while(waitOne || waitTwo) {
            SimpleMessageObject reply = receive();
            String replyString = reply.getString("ACTION");
            if(replyString.compareTo("-1") != 0) {
                if(replyString.compareTo("HELLO") == 0) {
                    SimpleMessageObject ack = new SimpleMessageObject();
                    ack.addString("ACTION", "ACK");
                    ack.addString("MESSAGE_TYPE", "CONTROL");
                    transmit(ack);
                    waitOne = false;
                }
                if(replyString.compareTo("ACK") == 0) {
                    waitTwo = false;
                }
            }
        }
        
        Logger.log("DEBUG", "BufferedSession connection initiated.");

    }
    
    /**
     * Closes the session, once this has been called no further messages can be
     * sent over this session.
     *
     * @throws IOException An IOException is thrown if there is an errpr closing
     * the session.
     */
    public void terminate() throws IOException {
        Logger.log("DEBUG", "BufferedSession closing.");
        SimpleMessageObject closer = new SimpleMessageObject();
        closer.addString("MESSAGE_TYPE", "CONTROL");
        closer.addString("ACTION", "TERMINATE");
        transmit(closer);
        close();
        Logger.log("DEBUG", "BufferedSession closed.");
    }
    
    /** 
     * Sends the supplied SimpleMessageObject over this session.
     *
     * @param packet The SimpleMessageObject to send over the link.
     * @throws IOException An IOException is thrown if there is an error sending
     * the data over the session.
     */
    public synchronized void transmit(SimpleMessageObject packet) throws IOException {
        if(Logger.isEnabled("NETIO")) {
            Logger.log("NETIO", "TX >>>" + System.getProperty("line.separator") + packet.dump());
        }
        byte[] data = packet.getNetworkPacket();
        int packetLength = data.length;
        OUT.write(DataConversions.getBytes(packetLength));
        OUT.write(data);
        OUT.flush();
    }
    
    /**
     * Receive a SimpleMessageObject over this session. This method blocks until
     * a SimpleMessageObject is received.
     *
     * @throws IOException An IOException is thrown if there is an error
     * receiving data over the session.
     * @return The SimpleMessageObject received over the session.
     */
    public synchronized SimpleMessageObject receive() throws IOException {
        byte[] length = new byte[4];
        for(int i = 0 ; i < 4 ; i++) {
            length[i] = (byte)IN.read();
        }
        byte[] data = new byte[DataConversions.getInt(length, 0, length.length)];
        IN.read(data, 0, data.length);
        SimpleMessageObject o = SimpleMessageObject.createMessageObject(data);
        if(Logger.isEnabled("NETIO")) {
            Logger.log("NETIO", "RX <<<" + System.getProperty("line.separator") + o.dump());
        }
        if(o.getString("MESSAGE_TYPE").compareTo("CONTROL") == 0 &&
           o.getString("ACTION").compareTo("TERMINATE") == 0) {
            close();
            return null;
        }
        else {
            return o;
        }
    }
    
    private void close() throws IOException {
        IN.close();
        OUT.close();
        SOCK.close();
    }
    
    private BufferedOutputStream OUT;
    private BufferedInputStream IN;
    private Socket SOCK;
}
