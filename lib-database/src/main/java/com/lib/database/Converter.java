package com.lib.database;

import android.database.Cursor;

public interface Converter<T> {
    public T convert(Cursor cursor);
}
