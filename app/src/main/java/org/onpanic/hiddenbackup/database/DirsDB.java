package org.onpanic.hiddenbackup.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DirsDB extends SQLiteOpenHelper {

    public static final String DIRS_TABLE_NAME = "paths";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "backup_dirs";

    private static final String DIRS_TABLE_CREATE =
            "CREATE TABLE " + DIRS_TABLE_NAME + " (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "enabled INTEGER DEFAULT 1, " +
                    "path TEXT );";

    public DirsDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DIRS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}

