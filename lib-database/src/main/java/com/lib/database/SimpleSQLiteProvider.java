package com.lib.database;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public abstract class SimpleSQLiteProvider extends SQLiteContentProvider {

    private static final String TAG = SimpleSQLiteProvider.class.getSimpleName();

    @Override
    public Uri onInsert(SQLiteDatabase db, Uri uri, ContentValues values) {
        long id = -1;
        String tableName = getDatabaseTableName(db, uri);
        if (tableName != null) {
            id = db.insert(tableName, null, values);
        } else {
            Log.e(TAG, "onInsert, Unknown insert URI " + uri);
        }
        if (id >= 0) {
            postNotifyUri(uri);
            return ContentUris.withAppendedId(uri, id);
        }
        return null;
    }

    @Override
    public int onUpdate(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int id = -1;
        String tableName = getDatabaseTableName(db, uri);
        if (tableName != null) {
            id = db.update(tableName, values, selection, selectionArgs);
        } else {
            Log.e(TAG, "onUpdate, Unknown insert URI " + uri);
        }
        if (id > 0) {
            postNotifyUri(uri);
        }
        return 0;
    }

    @Override
    public int onDeleted(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs) {
        String tableName = getDatabaseTableName(db, uri);
        int id = -1;
        if (tableName != null) {
            id = db.delete(tableName, selection, selectionArgs);
        } else {
            Log.e(TAG, "onDeleted, Unknown insert URI " + uri);
        }
        if (id > 0) {
            postNotifyUri(uri);
        }
        return 0;
    }

    @Override
    public Cursor onQuery(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        boolean rawQuery = uri.getBooleanQueryParameter(Constant.RAW_QUERY, false);
        if (rawQuery) {
            return db.rawQuery(selection, selectionArgs);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String limit = uri.getQueryParameter(Constant.LIMIT);
        String groupBy = uri.getQueryParameter(Constant.GROUP_BY);
        String having = uri.getQueryParameter(Constant.HAVING);
        String tableName = getDatabaseTableName(db, uri);
        if (tableName != null) {
            qb.setTables(tableName);
        } else {
            Log.e(TAG, "onQuery, Unknown insert URI " + uri);
        }
        return qb.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder, limit);
    }

    protected String getDatabaseTableName(SQLiteDatabase database, Uri uri) {
        if (database == null) {
            return null;
        }
        return uri.getQueryParameter(Constant.TABLE_NAME);
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }
}
