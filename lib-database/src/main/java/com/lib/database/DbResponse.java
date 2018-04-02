package com.lib.database;

import android.net.Uri;

public class DbResponse<T> {

    private T value;
    private Uri insertResult;
    private int deleteResult;
    private int updateResult;

    public T getValue() {
        return value;
    }

    public Uri getInsertResult() {
        return insertResult;
    }

    public int getDeleteResult() {
        return deleteResult;
    }

    public int getUpdateResult() {
        return updateResult;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void setInsertResult(Uri insertResult) {
        this.insertResult = insertResult;
    }

    public void setDeleteResult(int deleteResult) {
        this.deleteResult = deleteResult;
    }

    public void setUpdateResult(int updateResult) {
        this.updateResult = updateResult;
    }
}
