package me.chatgame.mobilecg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.chatgame.mobilecg.tug.Tug;
import me.chatgame.mobilecg.tug.TugTask;

/**
 * Created by star on 16/4/7.
 */
public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ViewHolder> {

    private List<TugTask> tasks = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    public DownloadListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setTasks(List<TugTask> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void addTask(TugTask task) {
        if (tasks.contains(task)) {
            return;
        }
        tasks.add(task);
        notifyItemInserted(tasks.size() - 1);
    }

    public void updateTask(TugTask task) {
        int pos = tasks.indexOf(task);
        if (pos >= 0) {
            tasks.set(pos, task);
            notifyItemChanged(pos);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.view_download_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TugTask task = tasks.get(position);
        holder.bind(task);
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView seqNumView;
        private TextView urlView;
        private TextView localPathView;
        private TextView statusTv;
        private Button startButton;
        private TextView totalSizeView;
        private TextView downloadedSizeView;

        private TugTask task;
        public ViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.id_progress);
            seqNumView = (TextView) itemView.findViewById(R.id.id_seq_num);
            urlView = (TextView) itemView.findViewById(R.id.id_url);
            localPathView = (TextView) itemView.findViewById(R.id.id_local_path);
            statusTv = (TextView) itemView.findViewById(R.id.status_tv);
            startButton = (Button) itemView.findViewById(R.id.start_stop_button);
            totalSizeView = (TextView) itemView.findViewById(R.id.total_size);
            downloadedSizeView = (TextView) itemView.findViewById(R.id.downloaded_size);

            View item = itemView.findViewById(R.id.id_item_region);
            item.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (task == null || task.getStatus() != TugTask.Status.DOWNLOADED) {
                        return false;
                    }
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    String type = Util.getMimeType(task.getLocalPath());
                    intent.setDataAndType(Uri.fromFile(new File(task.getLocalPath())), type);
                    try {
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });

            startButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (task.getStatus()) {
                        case TugTask.Status.IDLE:
                            Tug.getInstance().resumeTask(task.getUrl());
                            break;
                        case TugTask.Status.DOWNLOADING:
                        case TugTask.Status.WAITING:
                            Tug.getInstance().pauseTask(task.getUrl());
                            break;
                    }
                }
            });
        }

        public void bind(TugTask task) {
            this.task = task;
            seqNumView.setText("" + task.getSeqNum());
            progressBar.setProgress(task.getProgress());
            urlView.setText(task.getUrl());
            localPathView.setText(task.getLocalPath());
            statusTv.setText(task.getStatusText());
            totalSizeView.setText("Total: " + task.getFileTotalSize());
            downloadedSizeView.setText("Down: " + task.getDownloadedSize());
            startButton.setVisibility(View.VISIBLE);
            switch (task.getStatus()) {
                case TugTask.Status.IDLE:
                    startButton.setText("Start");
                    break;
                case TugTask.Status.DOWNLOADED:
                case TugTask.Status.FAILED:
                    startButton.setVisibility(View.GONE);
                    break;
                case TugTask.Status.DOWNLOADING:
                case TugTask.Status.WAITING:
                    startButton.setText("Stop");
                    break;
            }
        }
    }
}
