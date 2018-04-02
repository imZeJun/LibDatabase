package com.lib.database;


import android.content.Context;
import android.net.Uri;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DbExecutor {

    private ConcurrentHashMap<Uri, DbWorker> workers = new ConcurrentHashMap<>();
    private ExecutorService executorService = Executors.newCachedThreadPool();
    private Context context;

    public DbResponse doSync(Uri uri, DbRequest request) {
        return getWorker(uri).doSync(request);
    }

    public Future doAsync(Uri uri, DbRequest request) {
        return getWorker(uri).doAsync(request);
    }

    private DbWorker getWorker(Uri uri) {
        DbWorker worker = workers.get(uri);
        if (worker == null) {
            worker = new DbWorker(context, uri, this);
            workers.put(uri, worker);
        }
        return worker;
    }
}
