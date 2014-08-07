/*
 ******************************************************************************
 *
 * Copyright 2010 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */

package org.themonkeysdidit.db;

import java.sql.*;
import java.util.*;
import org.themonkeysdidit.io.*;
import org.themonkeysdidit.util.*;

public class TwitterTxnHandler implements TxnHandlerInterface {
    public TwitterTxnHandler() {
    }
    
    public boolean handleTxn(SimpleMessageObject txn, MySQL_DatabaseHandler dbh) throws SQLException {
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO dynamic_twitter_raw_data(data) VALUES(\'");
        sql.append(txn.getString("STATUS"));
        sql.append("\')");
        if(dbh.insert(sql.toString()) > 0) {
            return true;
        }
        else {
            return false;
        }
    }
    
    public void init(MySQL_DatabaseHandler dbh) throws SQLException {
        
    }
}
