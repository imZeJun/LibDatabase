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
     * @param dbUri ContentProvider 的 Uri。
     * @param request 请求体。
     * @return 请求结果。
     */
    public DbResponse doSync(Uri dbUri, DbRequest request) {
        return getWorker(dbUri).doSync(request);
    }

    /**
     * 同步任务。
     * @param dbUri ContentProvider 的 Uri。
     * @param request 请求体。
     * @return 控制任务执行。
     */
    public Future doAsync(Uri dbUri, DbRequest request) {
        return getWorker(dbUri).doAsync(request);
    }

    public <T> T doSyncQuery(Uri dbUri, String tableName, String[] projectionIn,
                             String selection, String[] selectionArgs, String groupBy,
                             String having, String sortOrder, String limit, IConverter<T> converter) {
        return getWorker(dbUri).doSyncQuery(tableName, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, converter);
    }

    public Future doAsyncQuery(Uri dbUri, String tableName, String[] projectionIn,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String sortOrder, String limit, IConverter converter, IQueryCallback callback, boolean dealOnUiThread) {
        return getWorker(dbUri).doAsyncQuery(tableName, projectionIn, selection, selectionArgs, groupBy, having, sortOrder, limit, converter, callback, dealOnUiThread);
    }

    public long doSyncInsert(Uri dbUri, String tableName, ContentValues values) {
        return getWorker(dbUri).doSyncInsert(tableName, values);
    }

    public Future doAsyncInsert(Uri dbUri, String tableName, ContentValues values, IInsertCallback callback, boolean dealOnUiThread) {
        return getWorker(dbUri).doAsyncInsert(tableName, values, callback, dealOnUiThread);
    }

    public int doSyncDelete(Uri dbUri, String tableName, String whereClause, String[] whereArgs) {
        return getWorker(dbUri).doSyncDelete(tableName, whereClause, whereArgs);
    }

    public Future doAsyncDelete(Uri dbUri, String tableName, String whereClause, String[] whereArgs, IDeleteCallback callback, boolean dealOnUiThread) {
        return getWorker(dbUri).doAsyncDelete(tableName, whereClause, whereArgs, callback, dealOnUiThread);
    }

    public int doSyncUpdate(Uri dbUri, String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        return getWorker(dbUri).doSyncUpdate(tableName, values, whereClause, whereArgs);
    }

    public Future doAsyncUpdate(Uri dbUri, String tableName, ContentValues values, String whereClause, String[] whereArgs, IUpdateCallback callback, boolean dealOnUiThread) {
        return getWorker(dbUri).doAsyncUpdate(tableName, values, whereClause, whereArgs, callback, dealOnUiThread);
    }

    public Future doAsyncApplyBatch(Uri dbUri, ArrayList<ContentProviderOperation> operations, IApplyBatchCallback callback) {
        return getWorker(dbUri).doAsyncApplyBatch(operations, callback);
    }

    public ContentProviderResult[] doSyncApplyBatch(Uri dbUri, ArrayList<ContentProviderOperation> operations) {
        return getWorker(dbUri).doSyncApplyBatch(operations);
    }

    /**
     * 根据表名返回在 ContentProvider 的 Uri。
     * @param dbUri ContentProvider 的 Uri。
     * @param tableName 表名。
     * @return 指向表的 Uri。
     */
    public Uri getTableUri(Uri dbUri, String tableName) {
        return getWorker(dbUri).getTableUri(tableName);
    }

    /**
     * 根据数据库的 Uri 和指向表的 Uri 返回表名。
     * @param dbUri 指向数据库的 Uri。
     * @param  tableUri 根据指向表的 Uri。
     * @return 表名。
     */
    public String parseTableName(Uri dbUri, Uri tableUri) {
        return getWorker(dbUri).parseTableName(tableUri);
    }

    public void registerContentObserver(Uri dbUri, String tableName, boolean notifyForDescendants, ContentObserver observer) {
        getWorker(dbUri).registerContentObserver(tableName, notifyForDescendants, observer);
    }

    public final void unregisterContentObserver(Uri dbUri, ContentObserver observer) {
        getWorker(dbUri).unregisterContentObserver(observer);
    }

    private DbWorker getWorker(Uri dbUri) {
        DbWorker worker = workers.get(dbUri);
        if (worker == null) {
            worker = new DbWorker(context, dbUri, this);
            workers.put(dbUri, worker);
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
