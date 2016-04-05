package me.chatgame.mobilecg.tug.util;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

import me.chatgame.mobilecg.tug.TugTask;

/**
 * Created by star on 16/4/5.
 */
public class FileUtils {
    /**
     * 获取可用的缓存目录 优先/sdcard/Android/data/mypacketname/cache
     * 不可用时返回/data/data/mypacketname/file
     *
     * @return path
     */
    public static String getCacheDir(Context context) {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File file = context.getExternalCacheDir();

            if (file != null) {
                String fileDir = file.getAbsolutePath();
                return fileDir;
            }
        }
        String fileDir = context.getCacheDir().getAbsolutePath();
        return fileDir;
    }

    public static String getCacheDirByType(CacheDir dir, String rootPath) {
        String path = rootPath + dir;

        File file = new File(path);

        if (!file.exists()) {
            file.mkdirs();
        }

        return path;
    }

    public static String getLocalFilePath(String fileUrl, int fileType, String rootPath) {
        CacheDir dir;
        String appendix;
        switch (fileType) {
            case TugTask.FileType.IMAGE:
                dir = CacheDir.IMAGE;
                appendix = ".image";
                break;
            case TugTask.FileType.VIDEO:
                dir = CacheDir.VIDEO;
                appendix = ".video";
                break;
            case TugTask.FileType.AUDIO:
                dir = CacheDir.AUDIO;
                appendix = ".audio";
                break;
            default:
                dir = CacheDir.FILE;
                appendix = ".file";
                break;
        }
        return getCacheDirByType(dir, rootPath) + StringUtils.getMD5(fileUrl) + appendix;
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }
}
