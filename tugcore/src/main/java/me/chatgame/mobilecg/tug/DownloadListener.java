package me.chatgame.mobilecg.tug;

/**
 * Created by star on 16/4/5.
 */
public interface DownloadListener {
    /**
     * Called on download start
     * @param url
     */
    void downloadStart(String url);

    /**
     * When downloading is going, progress will be passed here
     * @param url downloading url
     * @param progress [0 ~ 100]
     */
    void onDownloadProgress(String url, int progress);

    /**
     * Called on download success
     * @param url
     * @param localPath local file path of downloaded file
     */
    void downloadSuccess(String url, String localPath);

    /**
     * Called on download failed
     * @param url
     */
    void downloadFail(String url);

    /**
     * Called on download task is deleted
     * @param url
     */
    void downloadDeleted(String url);
}
