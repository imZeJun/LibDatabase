package com.lib.database;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DbWorker {

    private ExecutorService executorService;
    private Context context;
    private Uri authority;
    private DbExecutor dbExecutor;

    public DbWorker(Context context, Uri authority, DbExecutor dbExecutor) {
        this.authority = authority;
        this.context = context;
        this.dbExecutor = dbExecutor;
        executorService = Executors.newCachedThreadPool();
    }

    public Future doAsync(final DbRequest request) {
        return executorService.submit(new Runnable() {

            @Override
            public void run() {
                doRealWork(false, request);
            }
        });
    }

    public <T> DbResponse<T> doSync(DbRequest dbRequest) {
        return doRealWork(true, dbRequest);
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
                Cursor cursor = context.getContentResolver().query(Uri.withAppendedPath(authority, dbRequest.getTableName()), projection, selection, selectionArgs, dbRequest.getSortOrder());
                Converter<T> converter = dbRequest.getConverter();
                T value = converter.convert(cursor);
                response = new DbResponse<>();
                response.setValue(value);
                break;
            }
            case RequestType.INSERT: {
                Uri uri = context.getContentResolver().insert(Uri.withAppendedPath(authority, dbRequest.getTableName()), dbRequest.getValues());
                response = new DbResponse<>();
                response.setInsertResult(uri);
                break;
            }
            case RequestType.DELETE: {
                String selection = null;
                String[] selectionArgs = null;
                if (dbRequest.getSelection() != null && dbRequest.getSelectionArgs() != null) {
                    selection = dbRequest.getSelection();
                    selectionArgs = dbRequest.getSelectionArgs().toArray(new String[]{});
                }
                int count = context.getContentResolver().delete(Uri.withAppendedPath(authority, dbRequest.getTableName()), selection, selectionArgs);
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
                int count = context.getContentResolver().update(Uri.withAppendedPath(authority, dbRequest.getTableName()), dbRequest.getValues(), selection, selectionArgs);
                response = new DbResponse<>();
                response.setUpdateResult(count);
                break;
            }
            default:
                break;
        }
        if (!sync) {
            postResponse(dbRequest, response);
        }
        return response;
    }

    private  <T> void postResponse(DbRequest dbRequest, DbResponse<T> dbResponse) {
        BaseCallback callback = dbRequest.getBaseCallback();
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
                default:
                    break;
            }
        }

    }

}
