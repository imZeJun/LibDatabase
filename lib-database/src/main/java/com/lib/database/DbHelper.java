package com.lib.database;


import android.database.Cursor;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DbHelper {

    private ExecutorService executorService;

    public DbHelper() {
        executorService = Executors.newCachedThreadPool();
    }
    
    public Future doAsync(final DbRequest request) {
        return executorService.submit(new Runnable() {

            @Override
            public void run() {
                doRealWork(false, request);
            }
        });
    }

    public DbResponse doSync(DbRequest dbRequest) {
        return doRealWork(true, dbRequest);
    }

    private DbResponse doRealWork(boolean sync, DbRequest dbRequest) {
        return new DbResponse();
    }
}
