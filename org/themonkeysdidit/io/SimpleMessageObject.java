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

import org.themonkeysdidit.util.*;
import java.util.*;

/**
 * A SimpleMessageObject is a basic packet that can hold all the primative data
 * types that provides methods to get a byte[] representation and the ability
 * to create an instance from a byte[], thus making it suitable for use over a
 * network link. Note no compression or encryption is applied to the data, if this
 * is required it must be implemented by the caller or the stream it is being
 * sent over.
 *
 * @version 0.1
 * @author Oliver Wardell
 */
public class SimpleMessageObject {
    public SimpleMessageObject() {
        
        STRINGS = new Vector<SimpleMessageObjectStringItem>();
        INTS = new Vector<SimpleMessageObjectIntItem>();
        LONGS = new Vector<SimpleMessageObjectLongItem>();
        DOUBLES = new Vector<SimpleMessageObjectDoubleItem>();
        FLOATS = new Vector<SimpleMessageObjectFloatItem>();
        CHARS = new Vector<SimpleMessageObjectCharItem>();
        BYTES = new Vector<SimpleMessageObjectByteArrayItem>();
    }
    
    /**
     * Add a string to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addString(String name, String value) {
        STRINGS.add(new SimpleMessageObjectStringItem(name, value));
    }
    
    /**
     * Add an int to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addInt(String name, int value) {
        INTS.add(new SimpleMessageObjectIntItem(name, value));
    }
    
    /**
     * Add an long to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addLong(String name, long value) {
        LONGS.add(new SimpleMessageObjectLongItem(name, value));
    }
    
    /**
     * Add a double to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addDouble(String name, double value) {
        DOUBLES.add(new SimpleMessageObjectDoubleItem(name, value));
    }
    
    /**
     * Add a float to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addFloat(String name, float value) {
        FLOATS.add(new SimpleMessageObjectFloatItem(name, value));
    }
    
    /**
     * Add a char to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addChar(String name, char value) {
        CHARS.add(new SimpleMessageObjectCharItem(name, value));
    }
    
    /**
     * Add a byte array to this SimpleMessageObject.
     *
     * @param name The name of the variable.
     * @param value The value of the variable.
     */
    public void addByteArray(String name, byte[] value) {
        BYTES.add(new SimpleMessageObjectByteArrayItem(name, value));
    }

    /**
     * Get the named string variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or "-1" if it doesn't exist.
     */
    public String getString(String name) {
        Enumeration enumer = STRINGS.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectStringItem item = (SimpleMessageObjectStringItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        return "-1";
    }
    
    /**
     * Get the named int variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or -1 if it doesn't exist.
     */
    public int getInt(String name) {
        Enumeration enumer = INTS.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectIntItem item = (SimpleMessageObjectIntItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        return -1;
    }
    
    /**
     * Get the named long variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or -1 if it doesn't exist.
     */
    public long getLong(String name) {
        Enumeration enumer = LONGS.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectLongItem item = (SimpleMessageObjectLongItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                 return item.getValue();
            }
        }
        return -1L;
    }
    
    /**
     * Get the named double variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or -1 if it doesn't exist.
     */
    public double getDouble(String name) {
        Enumeration enumer = DOUBLES.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectDoubleItem item = (SimpleMessageObjectDoubleItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        return -1D;
    }
    
    /**
     * Get the named float variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or -1 if it doesn't exist.
     */
    public float getFloat(String name) {
        Enumeration enumer = FLOATS.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectFloatItem item = (SimpleMessageObjectFloatItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        return -1F;
    }
    
    /**
     * Get the named char variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or '-1' if it doesn't exist.
     */
    public char getChar(String name) {
        Enumeration enumer = CHARS.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectCharItem item = (SimpleMessageObjectCharItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        return (char)-1;
    }
    
    /**
     * Get the named byte array variable from this SimpleMessageObject
     *
     * @param name The name of the variable to retrieve.
     * @return The value of the variable or '-1' if it doesn't exist.
     */
    public byte[] getByteArray(String name) {
        Enumeration enumer = BYTES.elements();
        while(enumer.hasMoreElements()) {
            SimpleMessageObjectByteArrayItem item = (SimpleMessageObjectByteArrayItem) enumer.nextElement();
            if(item.getName().compareTo(name) == 0) {
                return item.getValue();
            }
        }
        byte[] noSuchArray = new byte[1];
        noSuchArray[0] = (byte)-1;
        return noSuchArray;
    }

    /**
     * Get a stdout friendly dump of this message; useful for logging purposes.
     *
     * @return A pretty printed String containing the contents of this message.
     */
    public String dump() {
        StringBuffer retVal = new StringBuffer();
        if(STRINGS.size() > 0) {
            Enumeration enumer = STRINGS.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectStringItem item = (SimpleMessageObjectStringItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".s = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(INTS.size() > 0) {
            Enumeration enumer = INTS.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectIntItem item = (SimpleMessageObjectIntItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".i = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(LONGS.size() > 0) {
            Enumeration enumer = LONGS.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectLongItem item = (SimpleMessageObjectLongItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".l = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(DOUBLES.size() > 0) {
            Enumeration enumer = DOUBLES.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectDoubleItem item = (SimpleMessageObjectDoubleItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".d = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(FLOATS.size() > 0) {
            Enumeration enumer = FLOATS.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectFloatItem item = (SimpleMessageObjectFloatItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".f = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(CHARS.size() > 0) {
            Enumeration enumer = CHARS.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectCharItem item = (SimpleMessageObjectCharItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".c = ");
                retVal.append(item.getValue());
                retVal.append("\n");
            }
        }
        
        if(BYTES.size() > 0) {
            Enumeration enumer = BYTES.elements();
            while(enumer.hasMoreElements()) {
                SimpleMessageObjectByteArrayItem item = (SimpleMessageObjectByteArrayItem) enumer.nextElement();
                retVal.append(item.getName());
                retVal.append(".b[] =\n");
                byte[] buffer = item.getValue();
                retVal.append(DataConversions.byteArrayToHexDump(buffer));

            }
        }
        
        return retVal.toString();
    }
    
    /**
     * Get a byte array representation of the packet to send over a network.
     * Packets that are sent in this way can e reconstructed from the byte array
     * by passing it as an argument to createMessageObject().
     *
     * @return a byte array representation of the SimpleMessageObject.
     */
    public byte[] getNetworkPacket() {
    /*
        The packet layout will be:
        byte(datatype)|int(length of name)|byte[](name)|int(length of value)|byte[](value)|...
    */
        byte[] data = new byte[2];
        int currentPosition = 0;
        
        if(STRINGS.size() > 0) {
            Enumeration enumer = STRINGS.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x73;
                currentPosition++;

                SimpleMessageObjectStringItem item = (SimpleMessageObjectStringItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = item.getValue().getBytes();
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(INTS.size() > 0) {
            Enumeration enumer = INTS.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x69;
                currentPosition++;

                SimpleMessageObjectIntItem item = (SimpleMessageObjectIntItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = DataConversions.getBytes(item.getValue());
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(LONGS.size() > 0) {
            Enumeration enumer = LONGS.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x6C;
                currentPosition++;

                SimpleMessageObjectLongItem item = (SimpleMessageObjectLongItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = DataConversions.getBytes(item.getValue());
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(DOUBLES.size() > 0) {
            Enumeration enumer = DOUBLES.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x64;
                currentPosition++;

                SimpleMessageObjectDoubleItem item = (SimpleMessageObjectDoubleItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = DataConversions.getBytes(item.getValue());
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(FLOATS.size() > 0) {
            Enumeration enumer = FLOATS.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x66;
                currentPosition++;

                SimpleMessageObjectFloatItem item = (SimpleMessageObjectFloatItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = DataConversions.getBytes(item.getValue());
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(CHARS.size() > 0) {
            Enumeration enumer = CHARS.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x63;
                currentPosition++;

                SimpleMessageObjectCharItem item = (SimpleMessageObjectCharItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = DataConversions.getBytes(item.getValue());
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }
        
        if(BYTES.size() > 0) {
            Enumeration enumer = BYTES.elements();
            while(enumer.hasMoreElements()) {
                if(currentPosition == data.length) {
                    data = growArray(data, 1);
                }
                // Add the type
                data[currentPosition] = 0x62;
                currentPosition++;

                SimpleMessageObjectByteArrayItem item = (SimpleMessageObjectByteArrayItem) enumer.nextElement();
                int spaceLeft = data.length - currentPosition;
                byte[] name = item.getName().getBytes();
                if(spaceLeft < name.length + 4) {
                    data = growArray(data, name.length - spaceLeft + 4);
                }
                
                //Add the length of the name
                byte[] nameLength = DataConversions.getBytes(name.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = nameLength[i];
                    currentPosition++;
                }
                
                //Add the name
                for(int i = 0 ; i < name.length ; i++) {
                    data[currentPosition] = name[i];
                    currentPosition++;
                }
                
                spaceLeft = data.length - currentPosition;
                byte[] value = item.getValue();
                if(spaceLeft < value.length + 4) {
                    data = growArray(data, value.length - spaceLeft + 4);
                }
                
                //Add the length of the value
                byte[] valueLength = DataConversions.getBytes(value.length);
                for(int i = 0 ; i < 4 ; i++) {
                    data[currentPosition] = valueLength[i];
                    currentPosition++;
                }
                
                //Add the value
                for(int i = 0 ; i < value.length ; i++) {
                    data[currentPosition] = value[i];
                    currentPosition++;
                }
            }
        }

        return data;
    }
    
    /**
     * Reconstruct a SimpleMessageObject form a byte array that was created by
     * a call to getNetworkBytes().
     *
     * @param data A byte array created from a call to getNetworkBytes().
     * @return A SimpleMessageObject instance for this data.
     */
    public static SimpleMessageObject createMessageObject(byte[] data) {
        SimpleMessageObject retVal = new SimpleMessageObject();
        
        // data is of the form:
        // byte(data type)|int(length of name)|name|int(length of value)|value
        boolean keepGoing = true;
        int currentPointer = 0;
        while(keepGoing) {
            int type = data[currentPointer];
            int bytesToRead = 0;
            String name;
            currentPointer++;
            switch(type) {
                //String
                case 0x73:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    String stringValue = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addString(name, stringValue);
                break;
                
                //int
                case 0x69:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    int intValue = DataConversions.getInt(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addInt(name, intValue);                
                break;
                
                //long
                case 0x6c:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    long longValue = DataConversions.getLong(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addLong(name, longValue);
                break;
                
                //double
                case 0x64:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    double doubleValue = DataConversions.getDouble(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addDouble(name, doubleValue);                
                break;
                
                //float
                case 0x66:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    float floatValue = DataConversions.getFloat(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addFloat(name, floatValue); 
                break;
                
                //char
                case 0x63:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    char charValue = DataConversions.getChar(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addChar(name, charValue);
                break;
                
                //byte array
                case 0x62:
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                     name = DataConversions.getString(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    bytesToRead = DataConversions.getInt(data, currentPointer, 4);
                    currentPointer += 4;
                    byte[] byteValue = DataConversions.getByteArray(data, currentPointer, bytesToRead);
                    currentPointer += bytesToRead;
                    retVal.addByteArray(name, byteValue);
                break;
            }
            
            if(currentPointer == data.length) {
                keepGoing = false;
            }
        }
        
        return retVal;
    }
    
    private byte[] growArray(byte[] array, int growSize) {
        byte[] retVal = new byte[array.length + growSize];
        for(int i = 0 ; i < array.length ; i++) {
            retVal[i] = array[i];
        }
        return retVal;
    }
    
    private Vector<SimpleMessageObjectStringItem> STRINGS;
    private Vector<SimpleMessageObjectIntItem> INTS;
    private Vector<SimpleMessageObjectLongItem> LONGS;
    private Vector<SimpleMessageObjectDoubleItem> DOUBLES;
    private Vector<SimpleMessageObjectFloatItem> FLOATS;
    private Vector<SimpleMessageObjectCharItem> CHARS;
    private Vector<SimpleMessageObjectByteArrayItem> BYTES;
}
