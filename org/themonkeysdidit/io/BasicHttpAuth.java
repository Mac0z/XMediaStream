/*
 ******************************************************************************
 *
 * Copyright 2010 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.io;
 
import java.net.*;

public class BasicHttpAuth extends Authenticator {
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(USERNAME, PASSWORD.toCharArray());
    }
    
    public void setAuthDetails(String username, String password) {
        USERNAME = username;
        PASSWORD = password;
    }
    
    private String USERNAME;
    private String PASSWORD;
}

