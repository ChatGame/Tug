package me.chatgame.mobilecg.tug.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by star on 16/4/8.
 */
public class TugDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;

    private static TugDbHelper instance;

    public synchronized static void setInstance(TugDbHelper tugDbHelper) {
        if (instance != null) {
            instance.close();
            instance = null;
        }
        instance = tugDbHelper;
    }

    public synchronized static TugDbHelper getInstance() {
        return instance;
    }

    public TugDbHelper(Context context) {
        super(context, TugDbConstant.DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TugDbConstant.TableCreate.CREATE_TUG_TASK);
            db.execSQL(TugDbConstant.TableCreate.CREATE_TUG_TASK_INDEX);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
