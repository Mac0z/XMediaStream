package org.themonkeysdidit.util;

import java.nio.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public abstract class DataConversions {
    public static byte[] getBytes(int number) {
        byte[] retVal = new byte[4];
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(number);
        buf.flip();
        buf.get(retVal,0 , retVal.length);
        return retVal;
    }
    
    public static byte[] getBytes(long number) {
        byte[] retVal = new byte[8];
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(number);
        buf.flip();
        buf.get(retVal, 0, retVal.length);
        return retVal;
    }
    
    public static byte[] getBytes(double number) {
        byte[] retVal = new byte[8];
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(number);
        buf.flip();
        buf.get(retVal, 0, retVal.length);
        return retVal;
    }
    
    public static byte[] getBytes(float number) {
        byte[] retVal = new byte[4];
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(number);
        buf.flip();
        buf.get(retVal, 0, retVal.length);
        return retVal;
    }
    
    public static byte[] getBytes(char character) {
        byte[] retVal = new byte[2];
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putChar(character);
        buf.flip();
        buf.get(retVal, 0, retVal.length);
        return retVal;
    }
    
    public static String getString(byte[] data, int offset, int length) {
        return new String(data, offset, length);
    }
    
    public static int getInt(byte[]data, int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(data, offset, length);
        return buf.getInt();
    }
    
    public static long getLong(byte[]data, int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(data, offset, length);
        return buf.getLong();
    }
    
    public static double getDouble(byte[] data, int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(data, offset, length);
        return buf.getDouble();
    }
    
    public static float getFloat(byte[] data, int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(data, offset, length);
        return buf.getFloat();
    }
    
    public static char getChar(byte[] data, int offset, int length) {
        ByteBuffer buf = ByteBuffer.wrap(data, offset, length);
        return buf.getChar();
    }
    
    public static byte[] getByteArray(byte[] data, int offset, int length) {
        byte[] retVal = new byte[length];
        for(int i = 0 ; i < length ; i++) {
            retVal[i] = data[i + offset];
        }
        
        return retVal;
    }
    
    public static String byteArrayToHexDump(byte[] data) {
        return dump(data);
    }
    


    private static String dump(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            dump(data, 0, data.length, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    private static String dump(byte[] data, int off, int len) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            dump(data, off, len, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out.toString();
    }

    private static void dump(byte data[], OutputStream out) throws IOException {
        dump(data, 0, data.length, out);
    }

    private static void dump(byte[] data, int off, int len, OutputStream out) throws IOException {
        String hexoff;
        int dumpoff, hexofflen, i, nextbytes, end = len + off;
        int val;

        for (dumpoff = off; dumpoff < end; dumpoff += FORMAT_BYTES_PER_ROW) {
            // Pad the offset with 0's (i miss my beloved sprintf()...)
            hexoff = Integer.toString(dumpoff, 16);
            hexofflen = hexoff.length();
            for (i = 0; i < FORMAT_OFFSET_PADDING - hexofflen; ++i) {
                hexoff = "0" + hexoff;
            }
            out.write((hexoff + " ").getBytes());

            // Bytes to be printed in the current line
            nextbytes = (FORMAT_BYTES_PER_ROW < (end - dumpoff) ? FORMAT_BYTES_PER_ROW : (end - dumpoff));

            for (i = 0; i < FORMAT_BYTES_PER_ROW; ++i) {
                // Put two spaces to separate 8-bytes blocks
                if ((i % 8) == 0) {
                    out.write(" ".getBytes());
                }
                if (i >= nextbytes) {
                    out.write("   ".getBytes());
                } else {
                    val = data[dumpoff + i] & 0xff;
                    out.write(HEXCHARS[val >>> 4]);
                    out.write(HEXCHARS[val & 0xf]);
                    out.write(" ".getBytes());
                }
            }

            out.write(" |".getBytes());

            for (i = 0; i < FORMAT_BYTES_PER_ROW; ++i) {
                if (i >= nextbytes) {
                    out.write(" ".getBytes());
                } else {
                    val = data[i + dumpoff];
                    // Is it a printable character?
                    if ((val > 31) && (val < 127)) {
                        out.write(val);
                    } else {
                        out.write(".".getBytes());
                    }
                }
            }

            out.write("|\n".getBytes());
        }
    }
    
    private static final int FORMAT_OFFSET_PADDING = 8;
    private static final int FORMAT_BYTES_PER_ROW = 16;
    private static final byte[] HEXCHARS = "0123456789abcdef".getBytes();

}
