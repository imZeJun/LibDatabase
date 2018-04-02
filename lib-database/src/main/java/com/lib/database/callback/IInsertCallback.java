package com.lib.database.callback;

import android.net.Uri;

public abstract class IInsertCallback extends IBaseCallback {
    public abstract void onInsertComplete(long id);
}
