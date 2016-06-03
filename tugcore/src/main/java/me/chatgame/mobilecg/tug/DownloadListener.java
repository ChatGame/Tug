package me.chatgame.mobilecg.tug;

/**
 * Created by star on 16/4/5.
 */
public interface DownloadListener {
    /**
     * Called on download start
     * @param task tug task
     */
    void onDownloadStart(TugTask task);

    /**
     * When downloading is going, progress will be contained in task
     * @param task tug task
     */
    void onDownloadProgress(TugTask task);

    /**
     * Called on download success
     * @param task tug task
     */
    void onDownloadSuccess(TugTask task);

    /**
     * Called on download failed
     * @param task tug task
     */
    void onDownloadFail(TugTask task);

    /**
     * Called on download task is deleted
     * @param task tug task
     */
    void onDownloadDeleted(TugTask task);

    /**
     * Called on download task is paused
     * @param task tug task
     */
    void onDownloadPaused(TugTask task);

    /**
     * Called on download task is resumed
     * @param task tug task
     */
    void onDownloadResumed(TugTask task);
}
