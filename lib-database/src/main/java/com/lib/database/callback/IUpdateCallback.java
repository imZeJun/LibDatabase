package com.lib.database.callback;


public abstract class IUpdateCallback extends IBaseCallback {
    public abstract void onUpdateComplete(int count);
}
