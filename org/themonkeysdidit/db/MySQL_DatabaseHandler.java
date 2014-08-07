/*
 ******************************************************************************
 *
 * Copyright 2008 Oliver Wardell
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

import org.themonkeysdidit.util.Logger;
import org.themonkeysdidit.util.DataConversions;
import java.sql.*;

public class MySQL_DatabaseHandler {
    public MySQL_DatabaseHandler() {
        GENERATED_KEYS = null;
        MYSQL_CONNECTION = null;
        STORE_GENERATED_KEYS = false;
        PREP_STATEMENTS = new PreparedStatement[64];
        for(int i = 0 ; i < PREP_STATEMENTS.length ; i++) {
            PREP_STATEMENTS[i] = null;
        }
    }
    
    public void connect(String hostname, int port, String username, String password, String database) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        
        StringBuffer url;
        url = new StringBuffer("jdbc:mysql://");
        url.append(hostname);
        url.append(":");
        url.append(port);
        url.append("/");
        url.append(database);
        
        Logger.log("DATAB", "Connecting to databse with: " + url.toString());
        
        MYSQL_CONNECTION = DriverManager.getConnection(url.toString(), username, password);
    }
    
    public void disconnect() throws SQLException {
        MYSQL_CONNECTION.close();
        Logger.log("DATAB", "Disconnected from database");
    }
    
    public DatabaseResult query(String sql) throws SQLException {
        
        Logger.log("DATAB", "Querying database with: " + sql);
        
        Statement stmt = MYSQL_CONNECTION.createStatement();
        ResultSet result =  stmt.executeQuery(sql);
        DatabaseResult retVal = new DatabaseResult(result);
        result.close();
        stmt.close();
        Logger.log("DATAB", "Database query complete.");
        return retVal;
    }
    
    public int insert(String sql) throws SQLException {
        return actionOnDb(sql);
    }
    
    public int delete(String sql) throws SQLException {
        return actionOnDb(sql);
    }
    
    public int update(String sql) throws SQLException {
        return actionOnDb(sql);
    }
    
    public DatabaseResult getGeneratedKeys() {
        return GENERATED_KEYS;
    }
    
    public void storeGeneratedKeys() {
        setStoreKeys(true);
    }
    
    public void doNotStoreGeneratedKeys() {
        setStoreKeys(false);
    }
    
    public int addPreparedStatement(String sql) throws SQLException {
        PreparedStatement ps = MYSQL_CONNECTION.prepareStatement(sql);
        int retVal = -1;
        for(int i = 0 ; i < PREP_STATEMENTS.length ; i++) {
            if(PREP_STATEMENTS[i] == null) {
                Logger.log("DEBUG", "Adding Prepared statement ID " + Integer.toString(i) + " as: " + sql);
                PREP_STATEMENTS[i] = ps;
                retVal = i;
                break;
            }
        }
        return retVal;
    }
    
    public void deletePreparedStatement(int id) throws SQLException {
        if(PREP_STATEMENTS[id] != null) {
            PREP_STATEMENTS[id].close();
            PREP_STATEMENTS[id] = null;
        }
    }
    
    public DatabaseResult executePreparedStatementQuery(int id) throws SQLException {
        Logger.log("DATAB", "Executing Prepared Statement Query: " + Integer.toString(id));
        DatabaseResult retVal = new DatabaseResult(PREP_STATEMENTS[id].executeQuery());
        if(STORE_GENERATED_KEYS) {
            GENERATED_KEYS = new DatabaseResult(PREP_STATEMENTS[id].getGeneratedKeys());
        }
        Logger.log("DATAB", "Database updated.");
        return retVal;
    }
    
    public int executePreparedStatementUpdate(int id) throws SQLException {
        Logger.log("DATAB", "Executing Prepared Statement Update: " + Integer.toString(id));
        int retVal = PREP_STATEMENTS[id].executeUpdate();
        if(STORE_GENERATED_KEYS) {
            GENERATED_KEYS = new DatabaseResult(PREP_STATEMENTS[id].getGeneratedKeys());
        }
        Logger.log("DATAB", "Database updated.");
        return retVal;
    }
    
    public void setPreparedStatementString(int id, int pos, String value) throws SQLException {
        Logger.log("DEBUG", "Setting position (String)" + Integer.toString(pos) + " of Prepared Statement ID " + Integer.toString(id) + " as " + value);
        PREP_STATEMENTS[id].setString(pos, value);
    }
    
    public void setPreparedStatementInt(int id, int pos, int value) throws SQLException {
        Logger.log("DEBUG", "Setting position (int) " + Integer.toString(pos) + " of Prepared Statement ID " + Integer.toString(id) + " as " + Integer.toString(value));
        PREP_STATEMENTS[id].setInt(pos, value);
    }
    
    public void setPreparedStatementLong(int id, int pos, long value) throws SQLException {
        Logger.log("DEBUG", "Setting position (long) " + Integer.toString(pos) + " of Prepared Statement ID " + Integer.toString(id) + " as " + Long.toString(value));
        PREP_STATEMENTS[id].setLong(pos, value);
    }
    
    public void setPreparedStatementBytes(int id, int pos, byte[] value) throws SQLException {
        Logger.log("DEBUG", "Setting position (byte[]) " + Integer.toString(pos) + " of Prepared Statement ID " + Integer.toString(id) + " as " + DataConversions.byteArrayToHexDump(value));
        PREP_STATEMENTS[id].setBytes(pos, value);
    }
    
    private int actionOnDb(String sql) throws SQLException {
        int retVal;
        
        Logger.log("DATAB", "Updating database with: " + sql);
        
        Statement stmt = MYSQL_CONNECTION.createStatement();
        int numRows = stmt.executeUpdate(sql);
        if(numRows > 0 && STORE_GENERATED_KEYS) {
            GENERATED_KEYS = new DatabaseResult(stmt.getGeneratedKeys());
        }
        stmt.close();
        
        Logger.log("DATAB", "Database updated.");
        return numRows;
    }
    
    private void setStoreKeys(boolean storeKeys) {
        STORE_GENERATED_KEYS = storeKeys;
    }
    
    private Connection MYSQL_CONNECTION;
    private DatabaseResult GENERATED_KEYS;
    private boolean STORE_GENERATED_KEYS;
    private PreparedStatement[] PREP_STATEMENTS;
}
