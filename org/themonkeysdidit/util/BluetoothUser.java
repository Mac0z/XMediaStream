/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
package org.themonkeysdidit.util;
 
public class BluetoothUser {
    public BluetoothUser(String id, String name) {
        IS_SEEN = true;
        ID = id;
        NAME = name;
    }
    
    public void setSeen(boolean isSeen) {
        IS_SEEN = isSeen;
    }
    
    public void setName(String name) {
        NAME = name;
    }
    
    public void setId(String id) {
        ID = id;
    }
    
    public String getName() {
        return NAME;
    }
    
    public String getId() {
        return ID;
    }
    
    public boolean isSeen() {
        return IS_SEEN;
    }
    
    private boolean IS_SEEN;
    private String ID;
    private String NAME;
}
