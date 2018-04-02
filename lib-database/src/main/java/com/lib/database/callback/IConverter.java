package com.lib.database.callback;

import android.database.Cursor;

public interface IConverter<T> {
    T convert(Cursor cursor);
}
