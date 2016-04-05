package me.chatgame.mobilecg.tug;

import android.text.TextUtils;

/**
 * Created by star on 16/4/5.
 */
public class TugTask {
    public interface Status {
        int WAITING = 0;
        int DOWNLOADING = 1;
        int DOWNLOADED = 2;
        int FAILED = 3;
    }

    public interface FileType {
        int FILE = 0;
        int IMAGE = 1;
        int VIDEO = 2;
        int AUDIO = 3;
    }

    private String url;
    private String localPath;
    private int fileTotalSize;
    private int downloadedLength;
    private int fileType;
    private int status = Status.WAITING;

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

    public int getFileTotalSize() {
        return fileTotalSize;
    }

    public void setFileTotalSize(int fileTotalSize) {
        this.fileTotalSize = fileTotalSize;
    }

    public int getDownloadedLength() {
        return downloadedLength;
    }

    public void setDownloadedLength(int downloadedLength) {
        this.downloadedLength = downloadedLength;
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
}
