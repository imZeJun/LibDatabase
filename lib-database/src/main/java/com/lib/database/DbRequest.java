package com.lib.database;


import android.content.ContentValues;

import com.lib.database.callback.IBaseCallback;
import com.lib.database.callback.IConverter;

import java.util.ArrayList;
import java.util.List;

public class DbRequest {

    private String tableName;
    private @RequestType int requestType;
    private String selection;
    private List<String> selectionArgs = new ArrayList<>();
    private List<String> projection = new ArrayList<>();
    private String groupBy;
    private String limit;
    private String sortOrder;
    private String having;
    private ContentValues values = new ContentValues();
    private IBaseCallback IBaseCallback;
    private com.lib.database.callback.IConverter IConverter;

    public DbRequest(Builder builder) {
        this.tableName = builder.tableName;
        this.requestType = builder.requestType;
        this.selection = builder.selection;
        this.selectionArgs = builder.selectionArgs;
        this.projection = builder.projection;
        this.groupBy = builder.groupBy;
        this.limit = builder.limit;
        this.sortOrder = builder.sortOrder;
        this.having = builder.having;
        this.values = builder.values;
        this.IBaseCallback = builder.baseCallback;
        this.IConverter = builder.converter;
    }

    public String getTableName() {
        return tableName;
    }

    public int getRequestType() {
        return requestType;
    }

    public String getSelection() {
        return selection;
    }

    public List<String> getSelectionArgs() {
        return selectionArgs;
    }

    public List<String> getProjection() {
        return projection;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public String getLimit() {
        return limit;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public String getHaving() {
        return having;
    }

    public ContentValues getValues() {
        return values;
    }

    public IBaseCallback getIBaseCallback() {
        return IBaseCallback;
    }

    public IConverter getIConverter() {
        return IConverter;
    }

    public static class Builder {

        private String tableName;
        private @RequestType int requestType;
        private String selection;
        private List<String> selectionArgs = new ArrayList<>();
        private List<String> projection = new ArrayList<>();
        private String groupBy;
        private String limit;
        private String sortOrder;
        private String having;
        private ContentValues values = new ContentValues();
        private IBaseCallback baseCallback;
        private IConverter converter;

        public Builder tableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        public Builder requestType(@RequestType int requestType) {
            this.requestType = requestType;
            return this;
        }

        public Builder selection(String selection) {
            this.selection = selection;
            return this;
        }

        public Builder groupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public Builder limit(String limit) {
            this.limit = limit;
            return this;
        }

        public Builder sortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
            return this;
        }

        public Builder having(String having) {
            this.having = having;
            return this;
        }

        public Builder IBaseCallback(IBaseCallback iBaseCallback) {
            this.baseCallback = iBaseCallback;
            return this;
        }

        public Builder IConverter(IConverter IConverter) {
            this.converter = IConverter;
            return this;
        }

        public Builder put(String key, String value) {
            this.values.put(key, value);
            return this;
        }

        public Builder putAll(ContentValues other) {
            this.values.putAll(other);
            return this;
        }

        public Builder put(String key, Byte value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Short value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Integer value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Long value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Float value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Double value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, Boolean value) {
            this.values.put(key, value);
            return this;
        }

        public Builder put(String key, byte[] value) {
            this.values.put(key, value);
            return this;
        }

        public Builder putNull(String key) {
            this.values.putNull(key);
            return this;
        }

        public DbRequest build() {
            if (converter == null) {
                converter = new CursorConverter();
            }
            return new DbRequest(this);
        }

    }
}
