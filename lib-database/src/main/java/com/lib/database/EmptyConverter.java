package com.lib.database;


import android.database.Cursor;

public class EmptyConverter implements Converter<Cursor> {

    @Override
    public Cursor convert(Cursor cursor) {
        return cursor;
    }
}
