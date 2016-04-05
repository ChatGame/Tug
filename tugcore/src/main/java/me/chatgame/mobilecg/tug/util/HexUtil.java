package me.chatgame.mobilecg.tug.util;

/**
 * Created by star on 15/10/15.
 */
public class HexUtil {
    
    private static final char[] HexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String charArrayToHexString(char[] chars) {
        if (chars == null) {
            return null;
        }

        byte[] bytes = new byte[chars.length * 2];
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            byte high = (byte) ((c >> 8) & 0x00ff);
            byte low = (byte) (c & 0x00ff);
            bytes[i * 2] = high;
            bytes[i * 2 + 1] = low;
        }
        return byteArrayToHexString(bytes);
    }

    public static String byteArrayToHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(HexDigits[(b >> 4) & 0x0f]);
            sb.append(HexDigits[b & 0x0f]);
        }
        return sb.toString();
    }
}
