/*
 ******************************************************************************
 *
 * Copyright 2007 Oliver Wardell
 * This file is part of XMediaStream.
 *
 ******************************************************************************
 */
 
 package org.themonkeysdidit.XMediaStream;
 
 import java.io.*;
 /**
 ******************************************************************************
 **
 ** A class to facilitate dumping things in hex format, mainly used for testing.
 **
 ** @author Oliver Wardell
 ** @version 0.1
 **
 ******************************************************************************
 */
public class HexDump {
    
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
