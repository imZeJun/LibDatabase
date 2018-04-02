package com.lib.database.callback;


public abstract class IDeleteCallback extends IBaseCallback {
    public abstract void onDeleteComplete(int count);
}
