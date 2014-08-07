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

import org.themonkeysdidit.util.Logger;
import java.sql.SQLException;

public class MySQL_DatabasePoller extends Thread {
    public MySQL_DatabasePoller(MySQL_DatabaseHandler dbh) {
        DBH = dbh;
    }
    
    public void run() {
        final String poll = "SELECT COUNT(*) FROM dynamic_update_queue";
        int id = -1;
        try {
            id = DBH.addPreparedStatement(poll);
        }
        catch(SQLException sql) {
            Logger.log("WARNING", "Could not create PreparedStatement.");
            Logger.log("WARNING", sql.getMessage());
        }
        while(keepRunning()) {
            try {
                DBH.executePreparedStatementQuery(id);
                Thread.currentThread().sleep(30000);
            }
            catch(SQLException sql) {
                Logger.log("WARNING", "Unable to poll database, are we still connected?");
                Logger.log("WARNING", sql.getMessage());
            }
            catch(InterruptedException ie) {
                // Just loop round again
            }
        }
    }
    
    private boolean keepRunning() {
        return true;
    }
    
    private MySQL_DatabaseHandler DBH;
}
