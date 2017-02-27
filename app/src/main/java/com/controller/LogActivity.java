package com.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.model.Pojo;
import com.model.SharedPreff;

import java.util.ArrayList;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private ImageButton delete_log;
    private CustomLogAdapter listAdapter;
    private SharedPreff sharedPreff;
    private List<Pojo> pojolist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        context = LogActivity.this;
        delete_log = (ImageButton) findViewById(R.id.delete_log);
        listView = (ListView) findViewById(R.id.list_view_log);
        sharedPreff = new SharedPreff(context);
        if(null != sharedPreff.Retreive("Log")){
            pojolist = sharedPreff.Retreive("Log");
        }else
        pojolist = new ArrayList<>();

        listAdapter = new CustomLogAdapter(context, R.layout.activity_contact_list, pojolist);
        listView.setAdapter(listAdapter);

        delete_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listAdapter.clear();
                if(pojolist != null && pojolist.size()>0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                    builder1.setMessage("Delete Log ?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sharedPreff.ClearAll("Log");
                                    finish();
                                    dialog.cancel();
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else{
                    Toast.makeText(context,"Log is empty!",Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.refreshAdapter(sharedPreff.Retreive("Log"));
        sharedPreff.SaveSerialize(null, context.getResources().getString(R.string.notification),
                context.getResources().getString(R.string.onResume));
        sharedPreff.SaveSerialize(null,"nCount",Integer.toString(0));
    }


    @Override
    protected void onPause() {
        super.onPause();
        sharedPreff.SaveSerialize(null,context.getResources().getString(R.string.notification),context.getResources().getString(R.string.onPause));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
