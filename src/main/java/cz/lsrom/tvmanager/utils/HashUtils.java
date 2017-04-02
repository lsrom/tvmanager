package cz.lsrom.tvmanager.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lsrom on 1.4.2017.
 */
public abstract class HashUtils {
    final private static char[] hexArray = "0123456789abcdef".toCharArray();

    public static byte[] sha256 (byte[] bytes){
        if (bytes == null){return null;}

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        assert digest != null;
        byte[] result = digest.digest(bytes);

        return result;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static boolean byteArraysEqual (byte[] array1, byte[] array2){
        if (array1 == null && array2 == null){return true;}
        if (array1 == null || array2 == null){return false;}

        if (array1.length != array2.length){return false;}

        for (int i = 0; i < array1.length; i++){
            if (array1[i] != array2[i]){return false;}
        }

        return true;
    }
}
