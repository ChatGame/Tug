package me.chatgame.mobilecg.tug.util;

/**
 * Created by star on 16/4/5.
 */
public enum CacheDir {
    IMAGE("/.image/"), AUDIO("/.audio/"), VIDEO("/.video/"), FILE("/.file/");

    private String dir;

    CacheDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return String.valueOf(this.dir);
    }

    public String getDir() {
        return dir;
    }
}
