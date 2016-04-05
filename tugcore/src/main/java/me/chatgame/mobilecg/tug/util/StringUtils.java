package me.chatgame.mobilecg.tug.util;

import java.security.MessageDigest;

/**
 * Created by star on 16/4/5.
 */
public class StringUtils {
    public static String getMD5(String str) {
        String resultString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = HexUtil.byteArrayToHexString(md.digest(str
                    .getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultString;
    }
}
