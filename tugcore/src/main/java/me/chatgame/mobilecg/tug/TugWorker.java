package me.chatgame.mobilecg.tug;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

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

        URL url = new URL(task.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);

        connection.setRequestProperty("Range", "bytes=" + downloadedSize + "-");

        InputStream is = null;
        RandomAccessFile fileOutput = null;
        try {
            is = new BufferedInputStream(connection.getInputStream());
            fileOutput = new RandomAccessFile(tmpFile, "rwd");
        } finally {
            connection.disconnect();
            if (is != null) {
                is.close();
                is = null;
            }
            if (fileOutput != null) {
                fileOutput.close();
                fileOutput = null;
            }
        }
    }
}
