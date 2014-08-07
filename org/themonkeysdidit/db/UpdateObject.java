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
 
package org.themonkeysdidit.db;

public class UpdateObject {
    public UpdateObject() {
        TABLE = "NONE";
        NUMBER = -1;
        ACTION = UpdateObject.NULL_ACTION;
        TIMESTAMP = "NONE";
    }
    
    public UpdateObject(String table, long number, String action, String timestamp) {
        TABLE= table;
        NUMBER = number;
        ACTION = action;
        TIMESTAMP = timestamp;
    }
    
    public void setTable(String name) {
        TABLE = name;
    }
    
    public void setRecordNumber(long number) {
        NUMBER = number;
    }
    
    public void setAction(String action) {
        ACTION = action;
    }
    
    public void setTimestamp(String timestamp) {
        TIMESTAMP = timestamp;
    }
    
    public String getTableName() {
        return TABLE;
    }
    
    public long getRecordNumber() {
        return NUMBER;
    }
    
    public String getAction() {
        return ACTION;
    }
    
    public String getTimestamp() {
        return TIMESTAMP;
    }
    
    private String TABLE;
    private long NUMBER;
    private String ACTION;
    private String TIMESTAMP;
    public static final String MODIFY_ACTION = "M";
    public static final String ADD_ACTION = "A";
    public static final String DELETE_ACTION = "D";
    public static final String NULL_ACTION = "X";
}
