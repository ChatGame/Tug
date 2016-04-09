package me.chatgame.mobilecg.tug.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import me.chatgame.mobilecg.tug.TugTask;

/**
 * Created by star on 16/4/8.
 */
public class TugTaskDao {
    private SQLiteDatabase db;
    public TugTaskDao() {
        db = TugDbHelper.getInstance().getWritableDatabase();
    }

    public void addTask(TugTask task) {
        if (task == null) {
            return;
        }
        insertOrUpdate(task);
    }

    public void updateTask(TugTask task) {
        if (task == null) {
            return;
        }
        insertOrUpdate(task);
    }

    public void deleteTask(TugTask task) {
        if (task == null) {
            return;
        }
        db.delete(TugDbConstant.Table.TUG_TASK, TugDbConstant.TugTaskField.URL + "=?", new String[]{task.getUrl()});
    }

    public List<TugTask> getAllTasks() {
        List<TugTask> tasks = new ArrayList<>();
        Cursor cursor = db.query(TugDbConstant.Table.TUG_TASK, null, null, null, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TugTask task = new TugTask();
                    task.fromCursor(cursor);
                    tasks.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }

    public List<TugTask> getUnFinishedTasks() {
        List<TugTask> tasks = new ArrayList<>();
        Cursor cursor = db.query(TugDbConstant.Table.TUG_TASK, null,
                TugDbConstant.TugTaskField.STATUS + " IN (?, ?, ?)",
                new String[]{String.valueOf(TugTask.Status.IDLE), String.valueOf(TugTask.Status.WAITING),
                        String.valueOf(TugTask.Status.DOWNLOADING)}, null, null, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    TugTask task = new TugTask();
                    task.fromCursor(cursor);
                    tasks.add(task);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return tasks;
    }

    private synchronized void insertOrUpdate(TugTask task) {
        Cursor cursor = db.query(TugDbConstant.Table.TUG_TASK, null, TugDbConstant.TugTaskField.URL + "=?", new String[]{task.getUrl()}, null, null, null);
        boolean needInsert = true;
        ContentValues contentValues = task.getContentValues();
        try {
            if (cursor != null && cursor.getCount() > 0) {
                // update task
                int rows = db.update(TugDbConstant.Table.TUG_TASK, contentValues, TugDbConstant.TugTaskField.URL + "=?", new String[]{task.getUrl()});
                if (rows > 0) {
                    needInsert = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        if (needInsert) {
            db.insert(TugDbConstant.Table.TUG_TASK, null, contentValues);
        }
    }
}
