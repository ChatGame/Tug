package me.chatgame.mobilecg.tug.util;

import android.util.Log;

/**
 * Created by star on 16/4/6.
 */
public class LogUtil {
    private static final String TAG = "Tug";
    public static boolean NEED_LOG = true;
    public static void logV(String message, Object... args) {
        if (NEED_LOG) {
            Log.v(TAG, String.format(message, args));
        }
    }
    public static void logD(String message, Object... args) {
        if (NEED_LOG) {
            Log.d(TAG, String.format(message, args));
        }
    }
    public static void logI(String message, Object... args) {
        if (NEED_LOG) {
            Log.i(TAG, String.format(message, args));
        }
    }
    public static void logW(String message, Object... args) {
        if (NEED_LOG) {
            Log.w(TAG, String.format(message, args));
        }
    }
    public static void logE(String message, Object... args) {
        if (NEED_LOG) {
            Log.e(TAG, String.format(message, args));
        }
    }
}
