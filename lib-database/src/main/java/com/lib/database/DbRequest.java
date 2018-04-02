package com.lib.database;


import android.content.ContentValues;
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
    private BaseCallback baseCallback;
    private Converter converter;

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
        this.baseCallback = builder.baseCallback;
        this.converter = builder.converter;
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

    public BaseCallback getBaseCallback() {
        return baseCallback;
    }

    public Converter getConverter() {
        return converter;
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
        private BaseCallback baseCallback;
        private Converter converter;

        public Builder tableName(String tableName) {
            this.tableName = tableName;
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
                converter = new EmptyConverter();
            }
            return new DbRequest(this);
        }

    }
}
