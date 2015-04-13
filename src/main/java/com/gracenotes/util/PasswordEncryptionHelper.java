package com.gracenotes.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by adam on 4/9/15.
 */
public class PasswordEncryptionHelper {

    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = "ADBSJHJS12547896".getBytes();

    public static String encrypt(String plainText) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.ENCRYPT_MODE, key);
        // 1 to bytes
        byte[] byteText = plainText.getBytes("UTF-8");
        // 2 encrypt
        byte[] byteCipherText = c.doFinal(byteText);
        // 3 encode
        byte[] byteCipherEncodedText = new Base64().encode(byteCipherText);
        // 4 to string
        String encryptedText = new String(byteCipherEncodedText, "UTF-8");
        return encryptedText;
    }

    public static String decrypt(String encryptedText) throws Exception {
        Key key = generateKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        // 1 to bytes
        byte[] byteCipherEncodedText = encryptedText.getBytes("UTF-8");
        // 2 decode
        byte[] byteCipherText = new Base64().decode(byteCipherEncodedText);
        // 3 decrypt
        byte[] byteText = c.doFinal(byteCipherText);
        // 4 to string
        String plainText = new String(byteText, "UTF-8");
        return plainText;
    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }
}
