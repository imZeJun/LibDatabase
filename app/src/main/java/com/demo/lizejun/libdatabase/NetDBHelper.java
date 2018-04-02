package com.demo.lizejun.libdatabase;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NetDBHelper extends SQLiteOpenHelper {

    private static final String TAG = NetDBHelper.class.getSimpleName();

    private static final String DB_NAME = "table.db";
    private static final int DB_VERSION = 1;

    public static final class CacheTab {

        public static final String TABLE = "cache";

        public static final String _ID = "_id";
        public static final String URL = "url";
        public static final String BYTE_SIZE = "byte_size";
        public static final String LOCAL_UPDATE_TIME = "local_update_time";
        public static final String DATA = "data";
    }

    static final String CREATE_TAB_CACHE = "CREATE TABLE " + CacheTab.TABLE
            + "(" + CacheTab._ID + " integer primary key autoincrement, "
            + CacheTab.URL + " TEXT, "
            + CacheTab.BYTE_SIZE + " INTEGER, "
            + CacheTab.LOCAL_UPDATE_TIME + " INTEGER, "
            + CacheTab.DATA + " BLOB, "
            + " UNIQUE (" + CacheTab.URL
            + ") ON CONFLICT REPLACE" + ");";

    public NetDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate");
        db.execSQL(CREATE_TAB_CACHE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
