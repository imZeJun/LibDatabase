package com.lib.database.callback;


import android.content.ContentProviderResult;

public abstract class IApplyBatchCallback extends IBaseCallback {
    public abstract void onApplyBatchComplete(ContentProviderResult[] results);
}
