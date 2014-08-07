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
import java.io.IOException;

public interface MessageSession {
    
    public void initiate(Socket socket) throws IOException;
    
    public void terminate() throws IOException;
    
    public void transmit(SimpleMessageObject packet) throws IOException;
    
    public SimpleMessageObject receive() throws IOException;
}
