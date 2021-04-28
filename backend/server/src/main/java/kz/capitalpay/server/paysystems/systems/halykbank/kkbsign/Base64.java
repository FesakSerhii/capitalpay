package kz.capitalpay.server.paysystems.systems.halykbank.kkbsign;

import java.io.*;

// 
// Decompiled by Procyon v0.5.36
// 

public class Base64
{
    private static char[] alphabet;
    private static byte[] codes;
    
    public static char[] encode(final byte[] array) {
        final char[] array2 = new char[(array.length + 2) / 3 * 4];
        for (int i = 0, n = 0; i < array.length; i += 3, n += 4) {
            boolean b = false;
            boolean b2 = false;
            int n2 = (0xFF & array[i]) << 8;
            if (i + 1 < array.length) {
                n2 |= (0xFF & array[i + 1]);
                b2 = true;
            }
            int n3 = n2 << 8;
            if (i + 2 < array.length) {
                n3 |= (0xFF & array[i + 2]);
                b = true;
            }
            array2[n + 3] = Base64.alphabet[b ? (n3 & 0x3F) : 64];
            final int n4 = n3 >> 6;
            array2[n + 2] = Base64.alphabet[b2 ? (n4 & 0x3F) : 64];
            final int n5 = n4 >> 6;
            array2[n + 1] = Base64.alphabet[n5 & 0x3F];
            array2[n] = Base64.alphabet[n5 >> 6 & 0x3F];
        }
        return array2;
    }
    
    public static byte[] decode(final char[] array) {
        int length = array.length;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] > '\u00ff' || Base64.codes[array[i]] < 0) {
                --length;
            }
        }
        int n = length / 4 * 3;
        if (length % 4 == 3) {
            n += 2;
        }
        if (length % 4 == 2) {
            ++n;
        }
        final byte[] array2 = new byte[n];
        int n2 = 0;
        int n3 = 0;
        int j = 0;
        for (int k = 0; k < array.length; ++k) {
            final byte b = (byte)((array[k] <= '\u00ff') ? Base64.codes[array[k]] : -1);
            if (b >= 0) {
                final int n4 = n3 << 6;
                n2 += 6;
                n3 = (n4 | b);
                if (n2 >= 8) {
                    n2 -= 8;
                    array2[j++] = (byte)(n3 >> n2 & 0xFF);
                }
            }
        }
        if (j != array2.length) {
            throw new Error("Miscalculated data length (wrote " + j + " instead of " + array2.length + ")");
        }
        return array2;
    }
    
    public static void main(final String[] array) {
        boolean b = false;
        if (array.length == 0) {
            System.out.println("usage:  java Base64 [-d[ecode]] filename");
            System.exit(0);
        }
        for (int i = 0; i < array.length; ++i) {
            if ("-decode".equalsIgnoreCase(array[i])) {
                b = true;
            }
            else if ("-d".equalsIgnoreCase(array[i])) {
                b = true;
            }
        }
        final String s = array[array.length - 1];
        final File file = new File(s);
        if (!file.exists()) {
            System.out.println("Error:  file '" + s + "' doesn't exist!");
            System.exit(0);
        }
        if (b) {
            writeBytes(file, decode(readChars(file)));
            return;
        }
        writeChars(file, encode(readBytes(file)));
    }
    
    private static byte[] readBytes(final File file) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            final byte[] array = new byte[16384];
            int read;
            while ((read = bufferedInputStream.read(array)) != -1) {
                if (read > 0) {
                    byteArrayOutputStream.write(array, 0, read);
                }
            }
            bufferedInputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
    
    private static char[] readChars(final File file) {
        final CharArrayWriter charArrayWriter = new CharArrayWriter();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            final char[] array = new char[16384];
            int read;
            while ((read = bufferedReader.read(array)) != -1) {
                if (read > 0) {
                    charArrayWriter.write(array, 0, read);
                }
            }
            bufferedReader.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return charArrayWriter.toCharArray();
    }
    
    private static void writeBytes(final File file, final byte[] b) {
        try {
            final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(b);
            bufferedOutputStream.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static void writeChars(final File file, final char[] cbuf) {
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(cbuf);
            bufferedWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    static {
        Base64.alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
        Base64.codes = new byte[256];
        for (int i = 0; i < 256; ++i) {
            Base64.codes[i] = -1;
        }
        for (int j = 65; j <= 90; ++j) {
            Base64.codes[j] = (byte)(j - 65);
        }
        for (int k = 97; k <= 122; ++k) {
            Base64.codes[k] = (byte)(26 + k - 97);
        }
        for (int l = 48; l <= 57; ++l) {
            Base64.codes[l] = (byte)(52 + l - 48);
        }
        Base64.codes[43] = 62;
        Base64.codes[47] = 63;
    }
}
