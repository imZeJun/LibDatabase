package com.lib.database;

import android.content.ContentProviderResult;

public class DbResponse<T> {

    private T value;
    private long insertResult;
    private int deleteResult;
    private int updateResult;
    private ContentProviderResult[] applyBatchResult;

    public T getValue() {
        return value;
    }

    public long getInsertResult() {
        return insertResult;
    }

    public int getDeleteResult() {
        return deleteResult;
    }

    public int getUpdateResult() {
        return updateResult;
    }

    public ContentProviderResult[] getApplyBatchResult() {
        return applyBatchResult;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setInsertResult(long insertResult) {
        this.insertResult = insertResult;
    }

    public void setDeleteResult(int deleteResult) {
        this.deleteResult = deleteResult;
    }

    public void setUpdateResult(int updateResult) {
        this.updateResult = updateResult;
    }

    public void setApplyBatchResult(ContentProviderResult[] applyBatchResult) {
        this.applyBatchResult = applyBatchResult;
    }
}
