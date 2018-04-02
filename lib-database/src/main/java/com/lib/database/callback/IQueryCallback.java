package com.lib.database.callback;


public abstract class IQueryCallback<T> extends IBaseCallback {
    public abstract void onQueryCompleted(T result);
}
