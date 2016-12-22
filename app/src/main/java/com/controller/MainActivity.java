package com.controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.model.Pojo;
import com.model.*;
import com.model.SharedPreff;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Spinner spinner;
    private TextView textView;
    private Button add, delete, log;
    private String spinnerData;
    private List<Pojo> pojoList;
    private CustomAdapter listAdapter;
    private int id = 1;
    public String item = "";
    SharedPreferences mPrefs;
    private Intent serviceIntent;
    private CallBarring callBarring;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        final SharedPreff sharedPreff = new SharedPreff(this);
        callBarring = new CallBarring();
        callBarring.UpdateRecord(getApplicationContext());
        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.new_contacts);
        add = (Button) findViewById(R.id.add);
        delete = (Button) findViewById(R.id.delete);
        log = (Button) findViewById(R.id.log);
        spinner = (Spinner) findViewById(R.id.spinner);
//        listAdapter = new CustomAdapter(this, R.layout.spinner_list_items, new ArrayList<Pojo>());
        listAdapter = new CustomAdapter(this, R.layout.spinner_list_items, sharedPreff.Retreive());
        listView.setAdapter(listAdapter);
        pojoList  = new ArrayList<Pojo>();

        mPrefs = getSharedPreferences("MyObject", Context.MODE_PRIVATE);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                Log.d("Spinner","Input "+i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("Spinner","Nothing Selected");
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.length() > 0 && !item.equals("Action")) {
                    Pojo pojo = new Pojo();
                    pojo.setNumber(textView.getText().toString());
                    pojo.setAction(item);
                    pojo.setId(id);
                    pojoList = sharedPreff.Retreive();
                    if(pojoList.size() == 0) {
                        pojoList = new ArrayList<Pojo>();
                        pojoList.add(pojo);
                        sharedPreff.SaveSerialize(pojoList);
                        listAdapter = new CustomAdapter(view.getContext(), R.layout.spinner_list_items, sharedPreff.Retreive());
                        listView.setAdapter(listAdapter);
                    }else{
                        listAdapter.refreshAdapter(pojoList);
                        sharedPreff.UpdateList(pojo);
                        sharedPreff.SaveSerialize(pojoList);
                    }
                    textView.setText("+91");
                    callBarring.UpdateRecord(getApplicationContext());
                } else {
                    Toast.makeText(view.getContext(), "Select an Action", Toast.LENGTH_SHORT).show();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.d("mainactivity","Clicked itam at position "+i+":long "+l);
                final View selectedView  = view ;
                final long itemPosition = l;

                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                TextView number = (TextView) selectedView.findViewById(R.id.number_number);
                builder1.setMessage("Delete number "+number.getText().toString()+"?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                String Data = listView.getItemAtPosition(i).toString();
                                Log.d("mainactivity","Data"+id);
                                //Delete the entry and update list and update adapter
                                sharedPreff.DeleteNumber(i);
                                List<Pojo> list = sharedPreff.Retreive();
                                listAdapter.refreshAdapter(list);
                                Log.d("mainactivity",":Id"+i);
                                dialog.cancel();
                                callBarring.UpdateRecord(getApplicationContext());
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


            }
        });
    }

    public void startService() {
        serviceIntent=new Intent(getApplicationContext(), CallBarringService.class);
        startService(serviceIntent);
    }

    // Method to stop the service
    public void stopService() {
       // if(CallBarringService.callBarring!=null)
        //unregisterReceiver(CallBarringService.callBarring);
        //new Intent(getApplicationContext(), CallBarringService.class)
        stopService(serviceIntent);
    }



}
