package me.chatgame.mobilecg.tug;

import android.content.Context;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

import me.chatgame.mobilecg.tug.util.FileUtils;
import me.chatgame.mobilecg.tug.util.LogUtil;

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
    BlockingQueue<TugTask> waitingQueue = new PriorityBlockingQueue<>();
    Queue<TugTask> workingQueue = new ConcurrentLinkedQueue<>();

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

    private synchronized void removeListeners(String url) {
        listenerMap.remove(url);
    }

    private TugTask findTaskFromQueue(Queue<TugTask> queue, TugTask dstTask) {
        for (TugTask task : queue) {
            if (task.equals(dstTask)) {
                return task;
            }
        }
        return null;
    }

    void addRetryTask(TugTask task) {
        workingQueue.remove(task);
        task.setStatus(TugTask.Status.WAITING);
        waitingQueue.offer(task);
        // TODO: 16/4/6 update in db
    }

    public void addTask(TugTask task, DownloadListener listener) {
        if (!waitingQueue.contains(task) && !workingQueue.contains(task)) {
            task.setStatus(TugTask.Status.WAITING);
            waitingQueue.offer(task);
            // TODO: 16/4/6 update in db
        } else {
            TugTask foundTask = findTaskFromQueue(waitingQueue, task);
            if (foundTask == null) {
                foundTask = findTaskFromQueue(workingQueue, task);
            }
            if (foundTask != null) {
                foundTask.increaseRetryCount();
            }
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
                    listener.onDownloadProgress(url, 100);
                    listener.downloadSuccess(url, localPath);
                }
            } else {
                TugTask task = new TugTask();
                task.setFileType(fileType);
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
        removeListeners(url);
        cancelWorkingTask(url);
        removeTaskFromQueue(url);
    }

    private void cancelWorkingTask(String url) {
        TugTask task = new TugTask();
        task.setUrl(url);
        for (TugWorker worker : workers) {
            if (task.equals(worker.getCurrentTask())) {
                worker.cancelCurrentTask();
            }
        }
    }

    private void removeTaskFromQueue(String url) {
        TugTask task = new TugTask();
        task.setUrl(url);
        waitingQueue.remove(task);
        workingQueue.remove(task);
    }

    public void start() {
        executor = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < threads; i++) {
            TugWorker worker = new TugWorker(this);
            workers.add(worker);
            executor.execute(worker);
        }
    }

    synchronized void downloadStart(TugTask task) {
        task.setStatus(TugTask.Status.DOWNLOADING);
        workingQueue.offer(task);
        // TODO: 16/4/6 update in db
        Set<DownloadListener> listeners = listenerMap.get(task.getUrl());
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadStart(task.getUrl());
            }
        }
    }

    synchronized void onDownloadProgress(TugTask task, int progress) {
        Set<DownloadListener> listeners = listenerMap.get(task.getUrl());
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.onDownloadProgress(task.getUrl(), progress);
            }
        }
    }

    synchronized void downloadSuccess(TugTask task, String localPath) {
        task.setStatus(TugTask.Status.DOWNLOADED);
        workingQueue.remove(task);
        // TODO: 16/4/6 update in db
        Set<DownloadListener> listeners = listenerMap.get(task.getUrl());
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadSuccess(task.getUrl(), localPath);
            }
        }
        removeListeners(task.getUrl());
    }

    synchronized void downloadFail(TugTask task) {
        task.setStatus(TugTask.Status.FAILED);
        workingQueue.remove(task);
        // TODO: 16/4/6 update in db
        Set<DownloadListener> listeners = listenerMap.get(task.getUrl());
        if (listeners != null) {
            for (DownloadListener listener : listeners) {
                listener.downloadFail(task.getUrl());
            }
        }
        removeListeners(task.getUrl());
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
        private boolean needLog = true;
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

        public Builder setNeedLog(boolean needLog) {
            this.needLog = needLog;
            return this;
        }

        public Tug build() {
            Tug tug = new Tug();
            tug.threads = Builder.this.threads;
            tug.rootPath = Builder.this.rootPath;
            if (TextUtils.isEmpty(tug.rootPath)) {
                tug.rootPath = FileUtils.getCacheDir(context);
            }
            LogUtil.NEED_LOG = needLog;
            return tug;
        }
    }
}
