package com.lib.database;

import android.support.annotation.IntDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({RequestType.INSERT, RequestType.QUERY, RequestType.UPDATE, RequestType.DELETE})
@Retention(RetentionPolicy.SOURCE)
public @interface RequestType {

    int INSERT = 1;
    int QUERY = 2;
    int UPDATE = 3;
    int DELETE = 4;
}
