package com.lib.database;

import android.net.Uri;

public abstract class IInsertCallback extends BaseCallback {
    public abstract void onInsertComplete(Uri uri);
}
