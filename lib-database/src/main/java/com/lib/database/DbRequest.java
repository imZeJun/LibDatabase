package com.lib.database;


import android.content.ContentValues;
import java.util.ArrayList;
import java.util.List;

public class DbRequest {

    private String tableName;
    private @RequestType int requestType;
    private String selection;
    private List<String> selectionArgs = new ArrayList<>();
    private List<String> project = new ArrayList<>();
    private String groupBy;
    private String limit;
    private String sortOrder;
    private String having;
    private ContentValues values = new ContentValues();

    public DbRequest(Builder builder) {
        this.tableName = builder.tableName;
        this.requestType = builder.requestType;
        this.selection = builder.selection;
        this.selectionArgs = builder.selectionArgs;
        this.project = builder.project;
        this.groupBy = builder.groupBy;
        this.limit = builder.limit;
        this.sortOrder = builder.sortOrder;
        this.having = builder.having;
        this.values = builder.values;
    }

    public static class Builder {

        private String tableName;
        private @RequestType int requestType;
        private String selection;
        private List<String> selectionArgs = new ArrayList<>();
        private List<String> project = new ArrayList<>();
        private String groupBy;
        private String limit;
        private String sortOrder;
        private String having;
        private ContentValues values = new ContentValues();

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
            return new DbRequest(this);
        }

    }
}
