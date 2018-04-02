package com.demo.lizejun.libdatabase;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lib.database.DbExecutor;
import com.lib.database.DbRequest;
import com.lib.database.RequestType;
import com.lib.database.callback.IConverter;
import com.lib.database.callback.IQueryCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbExecutor executor = new DbExecutor(getApplicationContext());
        executor.doAsync(DemoProvider.AUTHORITY_URI,
                new DbRequest.Builder()
                        .tableName(NetDBHelper.CacheTab.TABLE)
                        .requestType(RequestType.INSERT)
                        .put(NetDBHelper.CacheTab.URL, "ccc")
                        .put(NetDBHelper.CacheTab.LOCAL_UPDATE_TIME, System.currentTimeMillis()).build());
        executor.doAsync(DemoProvider.AUTHORITY_URI,
                new DbRequest.Builder()
                        .tableName(NetDBHelper.CacheTab.TABLE)
                        .requestType(RequestType.QUERY)
                        .projection(NetDBHelper.CacheTab.URL)
                        .projection(NetDBHelper.CacheTab.LOCAL_UPDATE_TIME)
                        .IConverter(new IConverter<List<CacheBean>>() {

                            @Override
                            public List<CacheBean> convert(Cursor cursor) {
                                List<CacheBean> list = new ArrayList<>();
                                while (cursor != null && cursor.moveToNext()) {
                                    String url = cursor.getString(cursor.getColumnIndex(NetDBHelper.CacheTab.URL));
                                    long time = cursor.getLong(cursor.getColumnIndex(NetDBHelper.CacheTab.LOCAL_UPDATE_TIME));
                                    CacheBean cacheBean = new CacheBean();
                                    cacheBean.setUrl(url);
                                    cacheBean.setTime(time);
                                    list.add(cacheBean);
                                }
                                Log.d(MainActivity.class.getSimpleName(), "main=" + Thread.currentThread().getId());
                                return list;
                            }
                        })
                        .dealOnUiThread(true)
                        .addCallback(new IQueryCallback<List<CacheBean>>() {

                            @Override
                            public void onQueryCompleted(List<CacheBean> result) {
                                for (CacheBean cacheBean : result) {
                                    Log.d(MainActivity.class.getSimpleName(), "cacheBean=" + cacheBean);
                                }
                                Log.d(MainActivity.class.getSimpleName(), "main=" + Thread.currentThread().getId());
                            }

                        }).build());
        executor.doAsync(DemoProvider.AUTHORITY_URI,
                new DbRequest.Builder()
                        .tableName(NetDBHelper.CacheTab.TABLE)
                        .requestType(RequestType.UPDATE)
                        .selection(NetDBHelper.CacheTab.URL + " = ?")
                        .selectionArgs("aaa")
                        .put(NetDBHelper.CacheTab.LOCAL_UPDATE_TIME, System.currentTimeMillis()).build());
        executor.doAsync(DemoProvider.AUTHORITY_URI,
                new DbRequest.Builder()
                        .tableName(NetDBHelper.CacheTab.TABLE)
                        .requestType(RequestType.DELETE)
                        .selection(NetDBHelper.CacheTab.URL + " = ?")
                        .selectionArgs("bbb").build());

    }

}
