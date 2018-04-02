package com.lib.database;


public abstract class IUpdateCallback extends BaseCallback {
    public abstract void onUpdateComplete(int count);
}
