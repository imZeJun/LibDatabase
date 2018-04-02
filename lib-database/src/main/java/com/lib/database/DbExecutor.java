package com.lib.database;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.lib.database.callback.IApplyBatchCallback;
import com.lib.database.callback.IConverter;
import com.lib.database.callback.IDeleteCallback;
import com.lib.database.callback.IInsertCallback;
import com.lib.database.callback.IQueryCallback;
import com.lib.database.callback.IUpdateCallback;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DbExecutor {

    private ConcurrentHashMap<Uri, DbWorker> workers = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Context context;
    private Handler mainHandler;

    public DbExecutor(Context context) {
        this.context = context;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 同步任务。
     * @param uri ContentProvider 的 Uri。
     * @param request 请求体。
     * @return 请求结果。
     */
    public DbResponse doSync(Uri uri, DbRequest request) {
        return getWorker(uri).doSync(request);
    }

    /**
     * 同步任务。
     * @param uri ContentProvider 的 Uri。
     * @param request 请求体。
     * @return 控制任务执行。
     */
    public Future doAsync(Uri uri, DbRequest request) {
        return getWorker(uri).doAsync(request);
    }

    public <T> T doSyncQuery(Uri uri, String tableName, String[] projectionIn,
                             String selection, String[] selectionArgs, String groupBy,
                             String having, String sortOrder, String limit, IConverter<T> converter) {
        return getWorker(uri).doSyncQuery(tableName, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, converter);
    }

    public Future doAsyncQuery(Uri uri, String tableName, String[] projectionIn,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String sortOrder, String limit, IConverter converter, IQueryCallback callback, boolean dealOnUiThread) {
        return getWorker(uri).doAsyncQuery(tableName, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, converter, callback, dealOnUiThread);
    }

    public long doSyncInsert(Uri uri, String tableName, ContentValues values) {
        return getWorker(uri).doSyncInsert(tableName, values);
    }

    public Future doAsyncInsert(Uri uri, String tableName, ContentValues values, IInsertCallback callback, boolean dealOnUiThread) {
        return getWorker(uri).doAsyncInsert(tableName, values, callback, dealOnUiThread);
    }

    public int doSyncDelete(Uri uri, String tableName, String whereClause, String[] whereArgs) {
        return getWorker(uri).doSyncDelete(tableName, whereClause, whereArgs);
    }

    public Future doAsyncDelete(Uri uri, String tableName, String whereClause, String[] whereArgs, IDeleteCallback callback, boolean dealOnUiThread) {
        return getWorker(uri).doAsyncDelete(tableName, whereClause, whereArgs, callback, dealOnUiThread);
    }

    public int doSyncUpdate(Uri uri, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return getWorker(uri).doSyncUpdate(tableName, values, whereClause, whereArgs);
    }

    public Future doAsyncUpdate(Uri uri, String tableName, ContentValues values, String whereClause, String[] whereArgs, IUpdateCallback callback, boolean dealOnUiThread) {
        return getWorker(uri).doAsyncUpdate(tableName, values, whereClause, whereArgs, callback, dealOnUiThread);
    }

    public Future doAsyncApplyBatch(Uri uri, ArrayList<ContentProviderOperation> operations, IApplyBatchCallback callback) {
        return getWorker(uri).doAsyncApplyBatch(operations, callback);
    }

    public ContentProviderResult[] doSyncApplyBatch(Uri uri, ArrayList<ContentProviderOperation> operations) {
        return getWorker(uri).doSyncApplyBatch(operations);
    }

    /**
     * 根据表名返回在 ContentProvider 的 Uri。
     * @param uri ContentProvider 的 Uri。
     * @param tableName 表名。
     * @return 指向表的 Uri。
     */
    public Uri getTableUri(Uri uri, String tableName) {
        return getWorker(uri).getTableUri(tableName);
    }

    /**
     * 根据指向表的 Uri 返回表名。
     * @param uri 根据指向表的 Uri 返回表名。
     * @return 表名。
     */
    public String parseTableName(Uri uri) {
        return getWorker(uri).parseTableName(uri);
    }

    public void registerContentObserver(Uri uri, String tableName, boolean notifyForDescendants, ContentObserver observer) {
        getWorker(uri).registerContentObserver(tableName, notifyForDescendants, observer);
    }

    public final void unregisterContentObserver(Uri uri, ContentObserver observer) {
        getWorker(uri).unregisterContentObserver(observer);
    }

    private DbWorker getWorker(Uri uri) {
        DbWorker worker = workers.get(uri);
        if (worker == null) {
            worker = new DbWorker(context, uri, this);
            workers.put(uri, worker);
        }
        return worker;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public Handler getMainHandler() {
        return mainHandler;
    }
}
