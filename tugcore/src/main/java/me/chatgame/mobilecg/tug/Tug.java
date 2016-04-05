package me.chatgame.mobilecg.tug;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import me.chatgame.mobilecg.tug.util.FileUtils;

/**
 * Created by star on 16/4/5.
 */
public class Tug {
    private static Tug instance;
    private int threads;
    private Executor executor;
    private String rootPath;
    private Map<String, Set<DownloadListener>> listenerMap = new HashMap<>();
    private List<TugWorker> workers = new ArrayList<>();
    BlockingQueue<TugTask> taskQueue = new PriorityBlockingQueue<>();

    private Tug() {

    }

    public synchronized static Tug getInstance() {
        if (instance == null) {
            throw new RuntimeException("Tug instance is not set!");
        }
        return instance;
    }

    public synchronized static void setInstance(Tug tug) {
        instance = tug;
    }

    private synchronized void addListener(String url, DownloadListener listener) {
        if (listener == null) {
            return;
        }
        Set<DownloadListener> list = listenerMap.get(url);
        if (list == null) {
            list = new HashSet<>();
            listenerMap.put(url, list);
        }
        list.add(listener);
    }

    /**
     * Just remove download listener without deleting task
     * @param url
     * @param listener
     */
    public synchronized void removeListener(String url, DownloadListener listener) {
        if (listener == null) {
            return;
        }
        Set<DownloadListener> list = listenerMap.get(url);
        if (list != null) {
            list.remove(listener);
        }
    }

    public void addTask(TugTask task, DownloadListener listener) {
        if (!taskQueue.contains(task)) {
            taskQueue.offer(task);
        }
        if (task != null) {
            addListener(task.getUrl(), listener);
        }
    }

    /**
     * Add download task
     * @param url
     * @param fileType see {@link TugTask.FileType}
     * @param destLocalPath local path to save the downloaded file
     * @param listener callback listener
     */
    public void addTask(String url, int fileType, String destLocalPath, DownloadListener listener) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (url.startsWith("http")) {
            String localPath = destLocalPath;
            if (TextUtils.isEmpty(destLocalPath)) {
                localPath = FileUtils.getLocalFilePath(url, fileType, rootPath);
            }
            if (FileUtils.isFileExist(localPath)) {
                if (listener != null) {
                    listener.downloadSuccess(url, localPath);
                }
            } else {
                TugTask task = new TugTask();
                task.setStatus(TugTask.Status.WAITING);
                task.setUrl(url);
                task.setLocalPath(localPath);
                addTask(task, listener);
            }
        }
    }

    /**
     * Remove and delete task with specified url, all listeners will be removed too
     * @param url
     */
    public void deleteTask(String url) {
        downloadDeleted(url);
        listenerMap.remove(url);
        removeTaskFromQueue(url);
        // TODO: 16/4/5 check worker thread
    }

    private void removeTaskFromQueue(String url) {
        TugTask task = new TugTask();
        task.setUrl(url);
        taskQueue.remove(task);
    }

    public void start() {
        executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            TugWorker worker = new TugWorker(this);
            workers.add(worker);
            executor.execute(worker);
        }
    }

    synchronized void downloadStart(String url) {
        Set<DownloadListener> listeners = listenerMap.get(url);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadStart(url);
            }
        }
    }

    synchronized void onDownloadPrgress(String url, int progress) {
        Set<DownloadListener> listeners = listenerMap.get(url);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.onDownloadPrgress(url, progress);
            }
        }
    }

    synchronized void downloadSuccess(String url, String localPath) {
        Set<DownloadListener> listeners = listenerMap.get(url);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadSuccess(url, localPath);
            }
        }
    }

    synchronized void downloadFail(String url) {
        Set<DownloadListener> listeners = listenerMap.get(url);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadFail(url);
            }
        }
    }

    synchronized void downloadDeleted(String url) {
        Set<DownloadListener> listeners = listenerMap.get(url);
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadDeleted(url);
            }
        }
    }

    public class Builder {
        private int threads = 2;
        private String rootPath;
        private Context context;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setThreads(int threads) {
            this.threads = threads;
            return this;
        }

        public Builder setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Tug build() {
            Tug tug = new Tug();
            tug.threads = Builder.this.threads;
            tug.rootPath = Builder.this.rootPath;
            if (TextUtils.isEmpty(tug.rootPath)) {
                tug.rootPath = FileUtils.getCacheDir(context);
            }
            return tug;
        }
    }
}
