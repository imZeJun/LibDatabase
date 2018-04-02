package com.lib.database;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.lib.database.annotation.RequestType;
import com.lib.database.callback.IBaseCallback;
import com.lib.database.callback.IConverter;
import com.lib.database.callback.IDeleteCallback;
import com.lib.database.callback.IInsertCallback;
import com.lib.database.callback.IQueryCallback;
import com.lib.database.callback.IUpdateCallback;

import java.util.concurrent.Future;

public class DbWorker {

    private Context context;
    private Uri authority;
    private DbExecutor dbExecutor;

    public DbWorker(Context context, Uri authority, DbExecutor dbExecutor) {
        this.authority = authority;
        this.context = context;
        this.dbExecutor = dbExecutor;
    }

    public Future doAsync(final DbRequest request) {
        return dbExecutor.getExecutorService().submit(new Runnable() {

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
                Uri.Builder builder = authority.buildUpon();
                builder.appendQueryParameter(Constant.TABLE_NAME, dbRequest.getTableName());
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
                Uri uri = context.getContentResolver().insert(authority.buildUpon().appendQueryParameter(Constant.TABLE_NAME, dbRequest.getTableName()).build(), dbRequest.getValues());
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
                int count = context.getContentResolver().delete(authority.buildUpon().appendQueryParameter(Constant.TABLE_NAME, dbRequest.getTableName()).build(), selection, selectionArgs);
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
                int count = context.getContentResolver().update(authority.buildUpon().appendQueryParameter(Constant.TABLE_NAME, dbRequest.getTableName()).build(), dbRequest.getValues(), selection, selectionArgs);
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
                default:
                    break;
            }
        }
    }

}
