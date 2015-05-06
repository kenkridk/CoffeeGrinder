package com.example.kenneth.coffeegrinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kenneth on 04-05-2015.
 */
public class DatabaseImpl extends SQLiteOpenHelper {

    public static final String TABLE_LISTVIEWCLASS = "listViewClass";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LISTVIEWCLASS = "listViewClass";

    private static final String DATABASE_NAME = "listViewClass.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_LISTVIEWCLASS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_LISTVIEWCLASS
            + " text not null);";

    public DatabaseImpl(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseImpl.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTVIEWCLASS);
        onCreate(db);
    }
}