package com.lib.database;


public abstract class IQueryCallback<T> extends BaseCallback {
    public abstract void onQueryCompleted(T result);
}
