package me.chatgame.mobilecg.tug;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import me.chatgame.mobilecg.tug.util.FileUtils;
import me.chatgame.mobilecg.tug.util.LogUtil;

/**
 * Created by star on 16/4/5.
 */
public class TugWorker implements Runnable {
    private static final int TIMEOUT = 10 * 1000;
    static final AtomicLong seq = new AtomicLong(0);
    private final long seqNum;

    private Tug tug;
    private TugTask currentTask;
    private boolean taskCancelled = false;
    public TugWorker(Tug tug) {
        seqNum = seq.getAndIncrement();
        this.tug = tug;
    }

    public synchronized TugTask getCurrentTask() {
        return currentTask;
    }

    private synchronized void setCurrentTask(TugTask currentTask) {
        this.currentTask = currentTask;
    }

    private synchronized void setTaskCancelled(boolean taskCancelled) {
        this.taskCancelled = taskCancelled;
    }

    private synchronized boolean isTaskCancelled() {
        return taskCancelled;
    }

    @Override
    public void run() {
        LogUtil.logD("[%s] running...", this);
        while (true) {
            try {
                TugTask task = tug.takeFromWaitingQueue();
                setCurrentTask(task);
                tug.downloadStart(task);
                download(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                TugTask task = getCurrentTask();
                if (task != null) {
                    if (task.getRetryCount() > 0) {
                        LogUtil.logD("[%s] retry download url: %s", this, task.getUrl());
                        task.decreaseRetryCount();
                        tug.addRetryTask(task);
                    } else {
                        LogUtil.logD("[%s] download failed url: %s", this, task.getUrl());
                        tug.downloadFail(task);
                    }
                    setCurrentTask(null);
                }
            }
        }
    }

    private void download(TugTask task) throws IOException {
        LogUtil.logD("[%s] Download task - url: %s", this, task.getUrl());
        setTaskCancelled(false);
        String tmpFilePath = task.getLocalPath() + ".tmp";
        File tmpFile = new File(tmpFilePath);
        long downloadedSize = 0;
        if (tmpFile.exists()) {
            // need continue previous download
            downloadedSize = tmpFile.length();
        } else {
            File parentDir = tmpFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            tmpFile.createNewFile();
        }

        task.setStatus(TugTask.Status.DOWNLOADING);
        HttpURLConnection connection = null;
        try {
            URL url = new URL(task.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setReadTimeout(TIMEOUT);

            LogUtil.logD("[%s] Download range from %d -", this, downloadedSize);
            connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            long totalSize = connection.getContentLength();
            LogUtil.logD("[%s] File total size: %d", this, totalSize);
            if (task.getFileTotalSize() > 0 && task.getFileTotalSize() != totalSize) {
                // file updated, need download from start
                task.setFileTotalSize(0);
                task.setDownloadedLength(0);
                FileUtils.deleteFile(tmpFile);
                download(task);
                return;
            } else {
                task.setFileTotalSize(totalSize);
            }

            InputStream is = null;
            RandomAccessFile fileOutput = null;
            try {
                is = new BufferedInputStream(connection.getInputStream());
                fileOutput = new RandomAccessFile(tmpFile, "rwd");
                fileOutput.seek(downloadedSize);

                byte[] buffer = new byte[2 * 1024];
                int readLength;
                while (!isTaskCancelled()
                        && (readLength = is.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, readLength);
                    downloadedSize += readLength;
                    int progress = (int) (downloadedSize / totalSize);
                    progress = Math.max(0, progress);
                    progress = Math.min(100, progress);
                    tug.onDownloadProgress(task, progress);
                }
                task.setDownloadedLength(downloadedSize);
                LogUtil.logD("[%s] downloaded size: %d", this, downloadedSize);
                if (isTaskCancelled()) {
                    LogUtil.logD("[%s] task cancelled", this);
                    setCurrentTask(null);
                    return;
                }
//                if (task.getDownloadedLength() == task.getFileTotalSize()) {
                    boolean ret = FileUtils.renameFile(tmpFile, task.getLocalPath());
                    if (ret) {
                        LogUtil.logD("[%s] download success url: %s", this, task.getUrl());
                        tug.onDownloadProgress(task, 100);
                        tug.downloadSuccess(task);
                    } else {
                        LogUtil.logW("[%s] rename file failed after download - set task as failed", this);
                        tug.downloadFail(task);
                    }
                    setCurrentTask(null);
//                }
            } finally {
                LogUtil.logD("[%s] close input stream and file output", this);
                if (is != null) {
                    is.close();
                    is = null;
                }
                if (fileOutput != null) {
                    fileOutput.close();
                    fileOutput = null;
                }
            }
        } finally {
            LogUtil.logD("[%s] close http connection", this);
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public void cancelCurrentTask() {
        setTaskCancelled(true);
    }

    @Override
    public String toString() {
        return "TugWorker-" + seqNum;
    }
}
