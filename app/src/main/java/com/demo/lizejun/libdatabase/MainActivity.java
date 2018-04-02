package com.demo.lizejun.libdatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lib.database.DbWorker;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DbWorker dbWorker = new DbWorker();
    }
}
