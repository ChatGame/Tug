package me.chatgame.mobilecg.tug.db;

/**
 * Created by star on 16/4/8.
 */
public interface TugDbConstant {
    String DB_NAME = "tug.db";

    interface Table {
        String TUG_TASK = "tug_task";
    }

    interface TugTaskField {
        String ID = "_id";
        String URL = "_url";
        String LOCAL_PATH = "_local_path";
        String FILE_TOTAL_SIZE = "_file_total_size";
        String DOWNLOADED_SIZE = "_downloaded_size";
        String FILE_TYPE = "_file_type";
        String STATUS = "_status";
        String PRIORITY = "_priority";
        String PROGRESS = "_progress";
    }

    interface TableCreate {
        String CREATE_TUG_TASK = "CREATE TABLE " + Table.TUG_TASK
                + " ( " + TugTaskField.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TugTaskField.URL + " VARCHAR, "
                + TugTaskField.LOCAL_PATH + " VARCHAR, "
                + TugTaskField.FILE_TOTAL_SIZE + " BIGINT DEFAULT 0, "
                + TugTaskField.DOWNLOADED_SIZE + " BIGINT DEFAULT 0, "
                + TugTaskField.FILE_TYPE + " SMALLINT DEFAULT 0, "
                + TugTaskField.STATUS + " SMALLINT DEFAULT 0, "
                + TugTaskField.PRIORITY + " SMALLINT DEFAULT 0, "
                + TugTaskField.PROGRESS + " SMALLINT DEFAULT 0 "
                + ")";
        String CREATE_TUG_TASK_INDEX = "CREATE INDEX tug_task_index ON " + Table.TUG_TASK
                + " (" + TugTaskField.URL + ", "
                + TugTaskField.STATUS + ")";
    }
}
