package kz.capitalpay.server.paysystems.systems.halyksoap.kkbsign;

import java.io.FileInputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;

// 
// Decompiled by Procyon v0.5.36
// 

public class KKBSign implements Serializable {
    public String keystoretype;
    public String signalgorythm;
    public boolean invert;
    public boolean debug;
    public String debughash;

    public KKBSign() {
        this.keystoretype = new String("JKS");
        this.signalgorythm = new String("SHA1withRSA");
        this.invert = true;
        this.debug = false;
        this.debughash = new String("SHA");
    }

    public Map<String, String> getConfig(final String configFilePath) {
        Map<String, String> configMap = new HashMap<>();
        try {
            final FileInputStream fileInputStream = new FileInputStream(configFilePath);
            final byte[] array = new byte[fileInputStream.available()];
            fileInputStream.read(array);
            final String config = new String(array);
            configMap.put("keystore", this.value(config, "keystore"));
            configMap.put("alias", this.value(config, "alias"));
            configMap.put("keypass", this.value(config, "keypass"));
            configMap.put("storepass", this.value(config, "storepass"));
            configMap.put("template", this.value(config, "template"));
            configMap.put("certificate", this.value(config, "certificate"));
            configMap.put("merchant_id", this.value(config, "merchant_id"));
            configMap.put("currency", this.value(config, "currency"));
            configMap.put("merchant_name", this.value(config, "merchant_name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configMap;
    }

    public synchronized String build64(final String configFilePath, final String sum, final String ord) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(configFilePath);
            final byte[] array = new byte[fileInputStream.available()];
            fileInputStream.read(array);
            final String config = new String(array);
            final String keystore = this.value(config, "keystore");
            final String alias = this.value(config, "alias");
            final String keypass = this.value(config, "keypass");
            final String storepass = this.value(config, "storepass");
            final String template = this.value(config, "template");
            final String certificate = this.value(config, "certificate");
            final String merchant_id = this.value(config, "merchant_id");
            final String currency = this.value(config, "currency");
            final String merchant_name = this.value(config, "merchant_name");
            final FileInputStream fileInputStream2 = new FileInputStream(template);
            final byte[] array2 = new byte[fileInputStream2.available()];
            fileInputStream2.read(array2);
            final String replace = this.replace(this.replace(this.replace(this.replace(this.replace(this.replace(this.replace(new String(array2), "%order_id%", ord), "%amount%", sum), "%amount%", sum), "%certificate%", certificate), "%merchant_id%", merchant_id), "%currency%", currency), "%merchant_name%", merchant_name);
            final String string = "<document>" + (replace + "<merchant_sign type=\"RSA\">" + this.sign64(replace, keystore, alias, keypass, storepass) + "</merchant_sign>") + "</document>";
            final Base64 base64 = new Base64();
            return new String(Base64.encode(string.getBytes()));
        } catch (Exception ex) {
            System.err.println("sign exception " + ex.toString());
            return new String("");
        }
    }

    public synchronized String sign64(final String text, final String keystore, final String alias, final String keypass, final String storepass) {
        try {
            final Base64 base64 = new Base64();
            final byte[] bytes = text.getBytes();
            final char[] charArray = keypass.toCharArray();
            final char[] charArray2 = storepass.toCharArray();
            if (this.debug) {
                final char[] encode = Base64.encode(MessageDigest.getInstance(this.debughash).digest(bytes));
            }
            final KeyStore instance = KeyStore.getInstance(this.keystoretype);
            instance.load(new FileInputStream(keystore), charArray2);
            final Signature instance2 = Signature.getInstance(this.signalgorythm);
            instance2.initSign((PrivateKey) instance.getKey(alias, charArray));
            instance2.update(bytes);
            final byte[] sign = instance2.sign();
            if (this.invert) {
                for (int i = 0, length = sign.length; i < length / 2; ++i) {
                    final byte b = sign[i];
                    sign[i] = sign[length - i - 1];
                    sign[length - i - 1] = b;
                }
            }
            System.out.println("Send Signature len: " + sign.length);
            return new String(Base64.encode(sign));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("sign exception " + ex.toString());
            return new String("");
        }
    }

    public synchronized boolean verify(final String xml, final String signature, final String cert, final String alias, final String password) {
        try {
            final Base64 base64 = new Base64();
            final byte[] bytes = xml.getBytes();
            final byte[] decode = Base64.decode(signature.toCharArray());
            final char[] charArray = password.toCharArray();

            System.out.println("Verify Signature len: " + decode.length);

            final KeyStore instance = KeyStore.getInstance(this.keystoretype);
            instance.load(new FileInputStream(cert), charArray);
            final Signature instance2 = Signature.getInstance(this.signalgorythm);
            instance2.initVerify(instance.getCertificate(alias));
            instance2.update(bytes);
            if (this.invert) {
                for (int i = 0, length = decode.length; i < length / 2; ++i) {
                    final byte b = decode[i];
                    decode[i] = decode[length - i - 1];
                    decode[length - i - 1] = b;
                }
            }
            return instance2.verify(decode);
        } catch (Exception ex) {
            System.err.println("verify exception " + ex.toString());
            return false;
        }
    }

    public String getKeystoretype() {
        return this.keystoretype;
    }

    public void setKeystoretype(final String keystoretype) {
        this.keystoretype = keystoretype;
    }

    public String getSignalgorythm() {
        return this.signalgorythm;
    }

    public void setSignalgorythm(final String signalgorythm) {
        this.signalgorythm = signalgorythm;
    }

    public boolean getInvert() {
        return this.invert;
    }

    public void setInvert(final boolean invert) {
        this.invert = invert;
    }

    public boolean getDebug() {
        return this.debug;
    }

    public void setDebug(final boolean debug) {
        this.debug = debug;
    }

    public String getDebughash() {
        return this.debughash;
    }

    public void setDebughash(final String debughash) {
        this.debughash = debughash;
    }

    private String value(final String s, final String str) {
        final int n = s.indexOf(34, s.indexOf(str)) + 1;
        return s.substring(n, s.indexOf(34, n));
    }

    private String replace(final String s, final String str, final String str2) {
        final int index = s.indexOf(str);
        return s.substring(0, index) + str2 + s.substring(index + str.length());
    }
}
