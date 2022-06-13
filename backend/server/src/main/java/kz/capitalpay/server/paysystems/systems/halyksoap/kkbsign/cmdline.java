package kz.capitalpay.server.paysystems.systems.halyksoap.kkbsign;

import java.io.FileInputStream;
import java.io.FileOutputStream;

// 
// Decompiled by Procyon v0.5.36
// 

class cmdline {
    public static void main(final String[] array) {
        String s = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        try {
            if (array.length == 0) {
                System.out.println("Command line signer v 1.1 (c) SAE, KKB 2001");
                System.out.println("");
                System.out.println("Arguments: <s | v> <textfile> <signfile> <keystore> <alias> <keypass> <storepass>");
                System.out.println("Build base64 xml: b <config_file> <amount> <order_id>");
                System.out.println("");
                return;
            }
            final String s7 = new String(array[0]);
            final KKBSign kkbSign = new KKBSign();
            if (0 == s7.compareTo("b")) {
                System.out.println(kkbSign.build64(new String(array[1]), new String(array[2]), new String(array[3])));
            } else {
                final String name = new String(array[1]);
                s = new String(array[2]);
                s2 = new String(array[3]);
                s3 = new String(array[4]);
                s4 = new String(array[5]);
                s5 = new String(array[6]);
                final FileInputStream fileInputStream = new FileInputStream(name);
                final byte[] array2 = new byte[fileInputStream.available()];
                fileInputStream.read(array2);
                s6 = new String(array2);
            }
            if (0 == s7.compareTo("s")) {
                new FileOutputStream(s).write(kkbSign.sign64(s6, s2, s3, s4, s5).getBytes());
            }
            if (0 == s7.compareTo("v")) {
                final FileInputStream fileInputStream2 = new FileInputStream(s);
                final byte[] array3 = new byte[fileInputStream2.available()];
                fileInputStream2.read(array3);
                if (kkbSign.verify(s6, new String(array3), s2, s3, s5)) {
                    System.out.println("Signature verified successfully");
                } else {
                    System.out.println("Signature error");
                }
            }
        } catch (Exception ex) {
            System.err.println("Caught exception " + ex.toString());
        }
    }
}
