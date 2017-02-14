package com.controller;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.model.SharedPreff;

public class LogActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private ImageButton delete_log;
    private CustomAdapter listAdapter;
    private SharedPreff sharedPreff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        context = LogActivity.this;
        delete_log = (ImageButton) findViewById(R.id.delete_log);
        listView = (ListView) findViewById(R.id.list_view_log);
        sharedPreff = new SharedPreff(context,"Log");

        listAdapter = new CustomAdapter(context, R.layout.spinner_list_items, sharedPreff.Retreive("Log"));
        listView.setAdapter(listAdapter);

        delete_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listAdapter.clear();
                sharedPreff.ClearAll("Log");
                finish();
            }
        });
    }
}
