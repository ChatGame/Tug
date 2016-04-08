package me.chatgame.mobilecg.tug;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import java.util.concurrent.atomic.AtomicLong;

import me.chatgame.mobilecg.tug.db.TugDbConstant;

/**
 * Created by star on 16/4/5.
 */
public class TugTask implements Comparable<TugTask> {

    public interface Status {
        int IDLE = 0;
        int WAITING = 1;
        int DOWNLOADING = 2;
        int DOWNLOADED = 3;
        int FAILED = 4;
    }

    public interface FileType {
        int FILE = 0;
        int IMAGE = 1;
        int VIDEO = 2;
        int AUDIO = 3;
    }

    public interface Priority {
        int LOW = -1;
        int NORMAL = 0;
        int HIGH = 1;
    }

    static final AtomicLong seq = new AtomicLong(0);
    private final long seqNum;

    private int id;
    private String url;
    private String localPath;
    private long fileTotalSize;
    private long downloadedSize;
    private int fileType;
    private int status = Status.WAITING;
    private int priority;

    private int retryCount = 1;
    private int progress;

    public TugTask() {
        seqNum = seq.getAndIncrement();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getFileTotalSize() {
        return fileTotalSize;
    }

    public void setFileTotalSize(long fileTotalSize) {
        this.fileTotalSize = fileTotalSize;
    }

    public long getDownloadedSize() {
        return downloadedSize;
    }

    public void setDownloadedSize(long downloadedSize) {
        this.downloadedSize = downloadedSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getFileType() {
        return fileType;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public synchronized int getRetryCount() {
        return retryCount;
    }

    public synchronized void increaseRetryCount() {
        retryCount++;
        retryCount = Math.min(retryCount, 4);
    }

    public synchronized void decreaseRetryCount() {
        retryCount--;
        retryCount = Math.max(retryCount, 0);
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public String getStatusText() {
        switch (status) {
            case Status.IDLE:
                return "Idle";
            case Status.WAITING:
                return "Waiting";
            case Status.DOWNLOADING:
                return "Downloading";
            case Status.DOWNLOADED:
                return "Downloaded";
            case Status.FAILED:
                return "Failed";
        }
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (o instanceof TugTask && TextUtils.equals(url, ((TugTask) o).getUrl())) {
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(TugTask another) {
        if (another == null) {
            return 1;
        }
        int ret = priority - another.priority;
        if (ret == 0) {
            ret = (seqNum < another.seqNum ? -1 : 1);
        }
        return ret;
    }

    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        if (url != null) {
            contentValues.put(TugDbConstant.TugTaskField.URL, url);
        }
        if (localPath != null) {
            contentValues.put(TugDbConstant.TugTaskField.LOCAL_PATH, localPath);
        }
        contentValues.put(TugDbConstant.TugTaskField.FILE_TOTAL_SIZE, fileTotalSize);
        contentValues.put(TugDbConstant.TugTaskField.DOWNLOADED_SIZE, downloadedSize);
        contentValues.put(TugDbConstant.TugTaskField.FILE_TYPE, fileType);
        contentValues.put(TugDbConstant.TugTaskField.STATUS, status);
        contentValues.put(TugDbConstant.TugTaskField.PRIORITY, priority);
        contentValues.put(TugDbConstant.TugTaskField.PROGRESS, progress);
        return contentValues;
    }

    public void fromCursor(Cursor cursor) {
        if (cursor != null) {
            try {
                id = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.ID));
                url = cursor.getString(cursor.getColumnIndex(TugDbConstant.TugTaskField.URL));
                localPath = cursor.getString(cursor.getColumnIndex(TugDbConstant.TugTaskField.LOCAL_PATH));
                fileTotalSize = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.FILE_TOTAL_SIZE));
                downloadedSize = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.DOWNLOADED_SIZE));
                fileType = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.FILE_TYPE));
                status = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.STATUS));
                priority = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.PRIORITY));
                progress = cursor.getInt(cursor.getColumnIndex(TugDbConstant.TugTaskField.PROGRESS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
