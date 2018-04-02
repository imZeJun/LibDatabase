package com.demo.lizejun.libdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.lib.database.SimpleSQLiteProvider;

public class DemoProvider extends SimpleSQLiteProvider {

    public static final Uri AUTHORITY_URI = Uri.parse("content://" + "demo");

    @Override
    public SQLiteOpenHelper getDatabaseHelper(Context context) {
        return new NetDBHelper(context);
    }
}
