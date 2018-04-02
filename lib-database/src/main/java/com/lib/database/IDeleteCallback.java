package com.lib.database;


public abstract class IDeleteCallback extends BaseCallback {
    public abstract void onDeleteComplete(int count);
}
