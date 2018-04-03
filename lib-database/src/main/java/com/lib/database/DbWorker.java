package com.lib.database;


import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.lib.database.annotation.RequestType;
import com.lib.database.callback.IApplyBatchCallback;
import com.lib.database.callback.IBaseCallback;
import com.lib.database.callback.IConverter;
import com.lib.database.callback.IDeleteCallback;
import com.lib.database.callback.IInsertCallback;
import com.lib.database.callback.IQueryCallback;
import com.lib.database.callback.IUpdateCallback;

import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * 用于处理单个数据库中的所有表。
 */
public class DbWorker {

    private Context context;

    /**
     * 该数据库关联的 ContentProvider 所对应的 Uri。
     */
    private Uri dbUri;
    private DbExecutor dbExecutor;

    public DbWorker(Context context, Uri dbUri, DbExecutor dbExecutor) {
        this.dbUri = dbUri;
        this.context = context;
        this.dbExecutor = dbExecutor;
    }

    public <T> DbResponse<T> doSync(DbRequest dbRequest) {
        return doRealWork(true, dbRequest);
    }

    public Future doAsync(final DbRequest request) {
        return dbExecutor.getExecutorService().submit(new Runnable() {

            @Override
            public void run() {
                doRealWork(false, request);
            }
        });
    }

    public <T> T doSyncQuery(String tableName, String[] projectionIn,
                             String selection, String[] selectionArgs, String groupBy,
                             String having, String sortOrder, String limit, IConverter<T> converter) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.QUERY)
                .projection(projectionIn)
                .selection(selection)
                .selectionArgs(selectionArgs)
                .groupBy(groupBy)
                .having(having)
                .sortOrder(sortOrder)
                .limit(limit)
                .addConverter(converter)
                .build();
        DbResponse<T> dbResponse = doSync(dbRequest);
        return dbResponse.getValue();
    }

    public Future doAsyncQuery(String tableName, String[] projectionIn,
                               String selection, String[] selectionArgs, String groupBy,
                               String having, String sortOrder, String limit, IConverter converter, IQueryCallback callback, boolean dealOnUiThread) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.QUERY)
                .projection(projectionIn)
                .selection(selection)
                .selectionArgs(selectionArgs)
                .groupBy(groupBy)
                .having(having)
                .sortOrder(sortOrder)
                .limit(limit)
                .addConverter(converter)
                .addCallback(callback)
                .dealOnUiThread(dealOnUiThread)
                .build();
        return doAsync(dbRequest);
    }

    public long doSyncInsert(String tableName, ContentValues values) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.INSERT)
                .putAll(values)
                .build();
        DbResponse dbResponse = doSync(dbRequest);
        return dbResponse.getInsertResult();
    }

    public Future doAsyncInsert(String tableName, ContentValues values, IInsertCallback callback, boolean dealOnUiThread) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.INSERT)
                .putAll(values)
                .addCallback(callback)
                .dealOnUiThread(dealOnUiThread)
                .build();
        return doAsync(dbRequest);
    }

    public int doSyncDelete(String tableName, String whereClause, String[] whereArgs) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.DELETE)
                .selection(whereClause)
                .selectionArgs(whereArgs)
                .build();
        DbResponse dbResponse = doSync(dbRequest);
        return dbResponse.getDeleteResult();
    }

    public Future doAsyncDelete(String tableName, String whereClause, String[] whereArgs, IDeleteCallback callback, boolean dealOnUiThread) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.DELETE)
                .selection(whereClause)
                .selectionArgs(whereArgs)
                .addCallback(callback)
                .dealOnUiThread(dealOnUiThread)
                .build();
        return doAsync(dbRequest);
    }

    public int doSyncUpdate(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.UPDATE)
                .selection(whereClause)
                .selectionArgs(whereArgs)
                .putAll(values).build();
        DbResponse dbResponse = doSync(dbRequest);
        return dbResponse.getUpdateResult();
    }

    public Future doAsyncUpdate(String tableName, ContentValues values, String whereClause, String[] whereArgs, IUpdateCallback callback, boolean dealOnUiThread) {
        DbRequest dbRequest = new DbRequest.Builder()
                .tableName(tableName)
                .requestType(RequestType.UPDATE)
                .selection(whereClause)
                .selectionArgs(whereArgs)
                .putAll(values)
                .addCallback(callback)
                .dealOnUiThread(dealOnUiThread)
                .build();
        return doAsync(dbRequest);
    }

    public Future doAsyncApplyBatch(ArrayList<ContentProviderOperation> operations, IApplyBatchCallback callback) {
        DbRequest dbRequest = new DbRequest.Builder()
                .operations(operations)
                .build();
        return doAsync(dbRequest);
    }

    public ContentProviderResult[] doSyncApplyBatch(ArrayList<ContentProviderOperation> operations) {
        DbRequest dbRequest = new DbRequest.Builder()
                .operations(operations)
                .build();
        DbResponse dbResponse = doSync(dbRequest);
        return dbResponse.getApplyBatchResult();
    }

    public void registerContentObserver(String tableName, boolean notifyForDescendants, ContentObserver observer) {
        context.getContentResolver().registerContentObserver(getTableUri(tableName), notifyForDescendants, observer);
    }

    public final void unregisterContentObserver(ContentObserver observer) {
        context.getContentResolver().unregisterContentObserver(observer);
    }

    public Uri getTableUri(String tableName) {
        return dbUri.buildUpon().appendQueryParameter(Constant.TABLE_NAME, tableName).build();
    }

    public String parseTableName(Uri uri) {
        return uri.getQueryParameter(Constant.TABLE_NAME);
    }

    private <T> DbResponse<T> doRealWork(boolean sync, DbRequest dbRequest) {
        DbResponse<T> response = null;
        @RequestType int requestType = dbRequest.getRequestType();
        switch (requestType) {
            case RequestType.QUERY: {
                String[] projection = null;
                String selection = null;
                String[] selectionArgs = null;
                if (dbRequest.getProjection() != null) {
                    projection = dbRequest.getProjection().toArray(new String[]{});
                }
                if (dbRequest.getSelection() != null && dbRequest.getSelectionArgs() != null) {
                    selection = dbRequest.getSelection();
                    selectionArgs = dbRequest.getSelectionArgs().toArray(new String[]{});
                }
                Uri.Builder builder = getTableUri(dbRequest.getTableName()).buildUpon();
                if (dbRequest.getGroupBy() != null) {
                    builder.appendQueryParameter(Constant.GROUP_BY, dbRequest.getGroupBy());
                }
                if (dbRequest.getHaving() != null) {
                    builder.appendQueryParameter(Constant.HAVING, dbRequest.getHaving());
                }
                if (dbRequest.getLimit() != null) {
                    builder.appendQueryParameter(Constant.LIMIT, dbRequest.getLimit());
                }
                if (dbRequest.isRawQuery()) {
                    builder.appendQueryParameter(Constant.RAW_QUERY, "true");
                }
                Cursor cursor = context.getContentResolver().query(builder.build(), projection, selection, selectionArgs, dbRequest.getSortOrder());
                IConverter<T> IConverter = dbRequest.getIConverter();
                T value = IConverter.convert(cursor);
                response = new DbResponse<>();
                response.setValue(value);
                break;
            }
            case RequestType.INSERT: {
                Uri uri = context.getContentResolver().insert(getTableUri(dbRequest.getTableName()), dbRequest.getValues());
                response = new DbResponse<>();
                response.setInsertResult(ContentUris.parseId(uri));
                break;
            }
            case RequestType.DELETE: {
                String selection = null;
                String[] selectionArgs = null;
                if (dbRequest.getSelection() != null && dbRequest.getSelectionArgs() != null) {
                    selection = dbRequest.getSelection();
                    selectionArgs = dbRequest.getSelectionArgs().toArray(new String[]{});
                }
                int count = context.getContentResolver().delete(getTableUri(dbRequest.getTableName()), selection, selectionArgs);
                response = new DbResponse<>();
                response.setDeleteResult(count);
                break;
            }
            case RequestType.UPDATE: {
                String selection = null;
                String[] selectionArgs = null;
                if (dbRequest.getSelection() != null && dbRequest.getSelectionArgs() != null) {
                    selection = dbRequest.getSelection();
                    selectionArgs = dbRequest.getSelectionArgs().toArray(new String[]{});
                }
                int count = context.getContentResolver().update(getTableUri(dbRequest.getTableName()), dbRequest.getValues(), selection, selectionArgs);
                response = new DbResponse<>();
                response.setUpdateResult(count);
                break;
            }
            case RequestType.APPLY_BATCH: {
                ContentProviderResult[] results = new ContentProviderResult[0];
                try {
                    results = context.getContentResolver().applyBatch(dbUri.getAuthority(), dbRequest.getOperations());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                response = new DbResponse<>();
                response.setApplyBatchResult(results);
            }
            default:
                break;
        }
        if (!sync) {
            postResponse(dbRequest, response);
        }
        return response;
    }

    private  <T> void postResponse(final DbRequest dbRequest, final DbResponse<T> dbResponse) {
        if (dbRequest.isDealOnUiThread()) {
            dbExecutor.getMainHandler().post(new Runnable() {

                @Override
                public void run() {
                    realPostResponse(dbRequest, dbResponse);
                }

            });
        } else {
            realPostResponse(dbRequest, dbResponse);
        }
    }

    private  <T> void realPostResponse(DbRequest dbRequest, DbResponse<T> dbResponse) {
        IBaseCallback callback = dbRequest.getCallback();
        if (callback != null) {
            switch (dbRequest.getRequestType()) {
                case RequestType.QUERY:
                    ((IQueryCallback<T>) callback).onQueryCompleted(dbResponse.getValue());
                    break;
                case RequestType.DELETE:
                    ((IDeleteCallback) callback).onDeleteComplete(dbResponse.getDeleteResult());
                    break;
                case RequestType.INSERT:
                    ((IInsertCallback) callback).onInsertComplete(dbResponse.getInsertResult());
                    break;
                case RequestType.UPDATE:
                    ((IUpdateCallback) callback).onUpdateComplete(dbResponse.getUpdateResult());
                    break;
                case RequestType.APPLY_BATCH:
                    ((IApplyBatchCallback) callback).onApplyBatchComplete(dbResponse.getApplyBatchResult());
                    break;
                default:
                    break;
            }
        }
    }

}
