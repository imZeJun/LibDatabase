package com.lib.database.callback;

import android.database.Cursor;

public interface IConverter<T> {
    public T convert(Cursor cursor);
}
