package com.lib.database;


import android.database.Cursor;

import com.lib.database.callback.IConverter;

public class CursorConverter implements IConverter<Cursor> {

    @Override
    public Cursor convert(Cursor cursor) {
        return cursor;
    }
}
