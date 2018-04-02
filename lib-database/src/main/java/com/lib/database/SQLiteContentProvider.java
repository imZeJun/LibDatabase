package com.lib.database;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class SQLiteContentProvider extends ContentProvider {

    private static final String TAG = "SQLiteContentProvider";

    private SQLiteOpenHelper mOpenHelper;
    private Set<Uri> mChangedUris;
    protected SQLiteDatabase mDb;
    private final ThreadLocal<Boolean> mApplyingBatch = new ThreadLocal<>();
    private static final int SLEEP_AFTER_YIELD_DELAY = 4000;
    private static final int MAX_OPERATIONS_PER_YIELD_POINT = 2000;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mOpenHelper = getDatabaseHelper(context);
        mChangedUris = new HashSet<>();
        return true;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri result = null;
        if (!invalidSQLiteDatabase()) {
            return null;
        }
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            mDb.beginTransaction();
            try {
                result = onInsert(mDb, uri, values);
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }
            onEndTransaction();
        } else {
            result = onInsert(mDb, uri, values);
        }
        return result;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        if (!invalidSQLiteDatabase()) {
            return 0;
        }
        int numValues = values.length;
        mDb.beginTransaction();
        try {
            for (int i = 0; i < numValues; i++) {
                Uri result = onInsert(mDb, uri, values[i]);
                mDb.yieldIfContendedSafely();
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
        }
        onEndTransaction();
        return numValues;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (!invalidSQLiteDatabase()) {
            return 0;
        }
        int count = 0;
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            mDb.beginTransaction();
            try {
                count = onUpdate(mDb, uri, values, selection, selectionArgs);
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }

            onEndTransaction();
        } else {
            count = onUpdate(mDb, uri, values, selection, selectionArgs);
        }
        return count;
    }


    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        if (!invalidSQLiteDatabase()) {
            return count;
        }
        boolean applyingBatch = applyingBatch();
        if (!applyingBatch) {
            mDb.beginTransaction();
            try {
                count = onDeleted(mDb, uri, selection, selectionArgs);
                mDb.setTransactionSuccessful();
            } finally {
                mDb.endTransaction();
            }

            onEndTransaction();
        } else {
            count = onDeleted(mDb, uri, selection, selectionArgs);
        }
        return count;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (!invalidSQLiteDatabase(true)) {
            return null;
        }
        return onQuery(mDb, uri, projection, selection, selectionArgs, sortOrder);
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        int ypCount = 0;
        int opCount = 0;
        if (!invalidSQLiteDatabase()) {
            return new ContentProviderResult[0];
        }
        mDb.beginTransaction();
        try {
            mApplyingBatch.set(true);
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                if (++opCount >= MAX_OPERATIONS_PER_YIELD_POINT) {
                    throw new OperationApplicationException(
                            "Too many content provider operations between yield points. "
                                    + "The maximum number of operations per yield point is "
                                    + MAX_OPERATIONS_PER_YIELD_POINT, ypCount);
                }
                final ContentProviderOperation operation = operations.get(i);
                if (i > 0 && operation.isYieldAllowed()) {
                    opCount = 0;
                    if (mDb.yieldIfContendedSafely(SLEEP_AFTER_YIELD_DELAY)) {
                        ypCount++;
                    }
                }
                results[i] = operation.apply(this, results, i);
            }
            mDb.setTransactionSuccessful();
            return results;
        } finally {
            mApplyingBatch.set(false);
            mDb.endTransaction();
            onEndTransaction();
        }
    }

    protected void onEndTransaction() {
        Set<Uri> changed;
        synchronized (mChangedUris) {
            changed = new HashSet<>(mChangedUris);
            mChangedUris.clear();
        }
        ContentResolver resolver = getContext().getContentResolver();
        for (Object uriObject : changed) {
            if (!(uriObject instanceof Uri)) {
                continue;
            }
            Uri uri = (Uri) uriObject;
            resolver.notifyChange(uri, null, false);
        }
    }

    private boolean invalidSQLiteDatabase() {
        return invalidSQLiteDatabase(false);
    }

    private boolean invalidSQLiteDatabase(boolean readOnly) {
        boolean invalidOk = true;
        try {
            if (readOnly) {
                mDb = mOpenHelper.getReadableDatabase();
            } else {
                mDb = mOpenHelper.getWritableDatabase();
            }
        } catch (Exception e) {
            invalidOk = false;
        }
        return invalidOk;
    }

    protected void postNotifyUri(Uri uri) {
        synchronized (mChangedUris) {
            mChangedUris.add(uri);
        }
    }

    private boolean applyingBatch() {
        return mApplyingBatch.get() != null && mApplyingBatch.get();
    }

    public abstract SQLiteOpenHelper getDatabaseHelper(Context context);

    public abstract Uri onInsert(SQLiteDatabase db, Uri uri, ContentValues values);

    public abstract int onUpdate(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs);

    public abstract int onDeleted(SQLiteDatabase db, Uri uri, String selection, String[] selectionArgs);

    public abstract Cursor onQuery(SQLiteDatabase db, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

}
