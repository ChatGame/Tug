package me.chatgame.mobilecg.tug;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import me.chatgame.mobilecg.tug.util.FileUtils;

/**
 * Created by star on 16/4/5.
 */
public class TugWorker implements Runnable {
    private static final int TIMEOUT = 10 * 1000;

    private Tug tug;
    private TugTask currentTask;

    public TugWorker(Tug tug) {
        this.tug = tug;
    }

    public synchronized TugTask getCurrentTask() {
        return currentTask;
    }

    private synchronized void setCurrentTask(TugTask currentTask) {
        this.currentTask = currentTask;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TugTask task = tug.taskQueue.take();
                setCurrentTask(task);
                // TODO: 16/4/6 download start
                download(task);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: 16/4/5 download fail
            }
        }
    }

    private void download(TugTask task) throws IOException {
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

            connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.connect();

            long totalSize = connection.getContentLength();
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
                while ((readLength = is.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, readLength);
                    downloadedSize += readLength;
                    // TODO: 16/4/6 progress update
                }
                task.setDownloadedLength(downloadedSize);
                if (task.getDownloadedLength() == task.getFileTotalSize()) {
                    task.setStatus(TugTask.Status.DOWNLOADED);
                    boolean ret = FileUtils.renameFile(tmpFile, task.getLocalPath());
                    if (ret) {
                        // TODO: 16/4/6 download success
                    }
                }
            } finally {
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
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
