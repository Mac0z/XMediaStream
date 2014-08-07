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
 
package org.themonkeysdidit.util;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Calendar;

/**
 * A <code>static</code> class used to write log messages to the specified
 * System.out. (System.out can be overridden).
 *
 * This wrapper should be used to write the log messages as it performs some standard
 * checks on the log message and only writes correctly formatted messages.
 *
 * @author Oliver Wardell
 * @version 1.0
 */
public abstract class Logger {
    
    /**
     * Log a message to the specified output stream.
     *
     * @param level The logging level to write the message at.
     * @param message The message to log.
     */
    public static void log(String level, String message) {
        if(isEnabled(level)) {
            Calendar rightNow = Calendar.getInstance();
            int year = rightNow.get(Calendar.YEAR);
            int month = rightNow.get(Calendar.MONTH) + 1;
            int date = rightNow.get(Calendar.DAY_OF_MONTH);
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minute = rightNow.get(Calendar.MINUTE);
            int seconds = rightNow.get(Calendar.SECOND);
            int millis = rightNow.get(Calendar.MILLISECOND);
            StringBuffer sb = new StringBuffer(Integer.toString(year));
            if(month < 10) {
                sb.append("0");
            }
            sb.append(Integer.toString(month));
            if(date < 10) {
                sb.append("0");
            }
            sb.append(Integer.toString(date));
            sb.append(" ");
            if(hour < 10) {
                sb.append("0");
            }
            sb.append(Integer.toString(hour));
            sb.append(":");
            if(minute < 10) {
                sb.append("0");
            }
            sb.append(Integer.toString(minute));
            sb.append(":");
            if(seconds < 10) {
                sb.append("0");
            }
            sb.append(Integer.toString(seconds));
            sb.append(".");
            sb.append(Integer.toString(millis));
            
            System.out.println("LOG_LEVEL_" + level + " " + sb.toString());
            System.out.println(message + System.getProperty("line.separator"));
            System.out.flush();

        }
    }
    
    public static void setLevel(String level) {
        System.setProperty("LOGGING_LEVEL", level);
    }
    
    public static boolean isEnabled(String level) {
        String levels = System.getProperty("LOGGING_LEVEL", "INFO WARNING ERROR");
        StringTokenizer breaker = new StringTokenizer(levels);
        while(breaker.hasMoreTokens()) {
            if(breaker.nextToken().compareTo(level) == 0) {
                return true;
            }
        }
        
        return false;
    }

}
