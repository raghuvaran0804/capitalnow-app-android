package com.capitalnowapp.mobile.util;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CNEncryptDecrypt {
    static char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public String ENCRYPTION_KEY = "0123456789abcdef";
    private String iv = "fedcba9876543210";

    private IvParameterSpec ivSpec;
    private SecretKeySpec secretKeySpec;
    private Cipher cipher;

    public String ALGORITHM_NAME = "AES/CBC/NoPadding";

    public String CHARSET_NAME = "UTF-8";

    public CNEncryptDecrypt() {
        try {
            ivSpec = new IvParameterSpec(iv.getBytes());

            secretKeySpec = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "AES");

            cipher = Cipher.getInstance(ALGORITHM_NAME);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public String encryptString(String text) throws Exception {
        if (text == null || text.length() == 0)
            throw new Exception("Empty string");

        String encString = null;

        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);

            byte[] encryptedBytes = cipher.doFinal(padString(text).getBytes());

            encString = bytesToHex(encryptedBytes);

        } catch (Exception e) {
            throw new Exception("[encrypt] " + e.getMessage());
        }

        return encString;
    }

    public String decryptString(String encryptedString) throws Exception {
        if (encryptedString == null || encryptedString.length() == 0)
            throw new Exception("Empty string");

        String decString = null;

        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);

            byte[] decryptedBytes = cipher.doFinal(hexToBytes(encryptedString));

            //Remove trailing zeroes
            if (decryptedBytes.length > 0) {
                int trim = 0;
                for (int i = decryptedBytes.length - 1; i >= 0; i--)
                    if (decryptedBytes[i] == 0) trim++;

                if (trim > 0) {
                    byte[] newArray = new byte[decryptedBytes.length - trim];
                    System.arraycopy(decryptedBytes, 0, newArray, 0, decryptedBytes.length - trim);
                    decryptedBytes = newArray;
                }
            }

            // Decode using UTF-8
            decString = new String(decryptedBytes);

        } catch (Exception e) {
            throw new Exception("[decrypt] " + e.getMessage());
        }

        return decString;
    }

    public static String bytesToHex(byte[] buf) {
        char[] chars = new char[2 * buf.length];
        for (int i = 0; i < buf.length; ++i) {
            chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
            chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
        }
        return new String(chars);
    }

    public static byte[] hexToBytes(String str) {
        if (str == null) {
            return null;
        } else if (str.length() < 2) {
            return null;
        } else {
            int len = str.length() / 2;
            byte[] buffer = new byte[len];
            for (int i = 0; i < len; i++) {
                buffer[i] = (byte) Integer.parseInt(str.substring(i * 2, i * 2 + 2), 16);
            }
            return buffer;
        }
    }

    private static String padString(String source) {
        char paddingChar = 0;
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }

    /*public static void main(String[] args) throws Exception {
        QCEncryptDecrypt encryptDecrypt = new QCEncryptDecrypt();
        String encryptedString = encryptDecrypt.encryptString("7");
        String decryptedValue = encryptDecrypt.decryptString("bd2fe9be2d596716057ca07d177fe99d");
        Log.d("QCEncryptDecrypt","****** encryptedString = " + encryptedString + " && decryptedValue : " + decryptedValue);
    }*/
}
