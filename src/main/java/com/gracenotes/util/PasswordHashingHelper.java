package com.gracenotes.util;

import java.security.MessageDigest;
import java.util.Formatter;

/**
 * Created by adam on 4/8/15.
 */
public class PasswordHashingHelper {

    public static String toMD5(String password) {
        String md5 = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(password.getBytes("UTF-8"));
            md5 = byteToHex(md.digest());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String toSHA1(String password) {
        String sha1 = "";
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
            md.reset();
            md.update(password.getBytes("UTF-8"));
            sha1 = byteToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sha1;
    }

    private static String byteToHex(final byte[] hash)
    {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
