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
     * @param context context instance
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
        String appendix;
        switch (fileType) {
            case TugTask.FileType.IMAGE:
                appendix = ".image";
                break;
            case TugTask.FileType.VIDEO:
                appendix = ".video";
                break;
            case TugTask.FileType.AUDIO:
                appendix = ".audio";
                break;
            default:
                appendix = ".file";
                break;
        }
        return getLocalFilePathBySpecifiedName(StringUtils.getMD5(fileUrl) + appendix, fileType, rootPath);
    }

    public static String getLocalFilePathBySpecifiedName(String fileName, int fileType, String rootPath) {
        CacheDir dir;
        switch (fileType) {
            case TugTask.FileType.IMAGE:
                dir = CacheDir.IMAGE;
                break;
            case TugTask.FileType.VIDEO:
                dir = CacheDir.VIDEO;
                break;
            case TugTask.FileType.AUDIO:
                dir = CacheDir.AUDIO;
                break;
            default:
                dir = CacheDir.FILE;
                break;
        }
        return getCacheDirByType(dir, rootPath) + fileName;
    }

    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists();
    }

    public static void deleteFile(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isFile()) {
            File tmpFile = new File(file.getAbsolutePath() + "_delete");
            file.renameTo(tmpFile);
            tmpFile.delete();
        } else {
            file.delete();
        }
    }

    public static boolean renameFile(File src, File dst) {
        deleteFile(dst);
        return src.renameTo(dst);
    }

    public static boolean renameFile(File src, String dstPath) {
        File dst = new File(dstPath);
        return renameFile(src, dst);
    }
}
