package me.chatgame.mobilecg;

/**
 * Created by star on 16/4/7.
 */
public class Util {

    public static String getMimeType(String fileName) {
        if (fileName == null) return "*/*";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".image")) {
            return "image/jpeg";
        } else if (fileName.endsWith(".png")) {
            return "mage/png";
        } else if (fileName.endsWith(".video")) {
            return "video/mp4";
        } else if (fileName.endsWith(".audio")) {
            return "audio/x-mpeg";
        } else {
            return "*/*";
        }
    }
}
