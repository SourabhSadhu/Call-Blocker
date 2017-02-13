package com.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private ListView listView;
    private Spinner spinner;
    private TextView textView;
    private ImageButton  delete, log; //add,
    private ImageButton add;
    private String spinnerData;
    private List<Pojo> pojoList;
    private CustomAdapter listAdapter;
    private int id = 1;
    public String item = "";
    SharedPreferences mPrefs;
    private Intent serviceIntent;
    private CallBarring callBarring;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private boolean isGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        context = MainActivity.this;
        executeUserPermissionTree();
        startService(new Intent(this, CallBarringService.class));

//        startService();


        final SharedPreff sharedPreff = new SharedPreff(context, "MyObject");
        callBarring = new CallBarring();
//        callBarring.UpdateRecord(context);
        listView = (ListView) findViewById(R.id.list_view);
        textView = (TextView) findViewById(R.id.new_contacts);
        add = (ImageButton) findViewById(R.id.add);
        delete = (ImageButton) findViewById(R.id.delete);
        log = (ImageButton) findViewById(R.id.log);
        spinner = (Spinner) findViewById(R.id.spinner);
//        listAdapter = new CustomAdapter(this, R.layout.spinner_list_items, new ArrayList<Pojo>());
        listAdapter = new CustomAdapter(this, R.layout.spinner_list_items, sharedPreff.Retreive("MyObject"));
        listView.setAdapter(listAdapter);
        pojoList  = new ArrayList<Pojo>();

        mPrefs = getSharedPreferences("MyObject", Context.MODE_PRIVATE);

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

                /*
                Making action hardcoded as block due to unable to silence
                 */
//                item = "Slient";
//                item = "Block";

                if (item.length() > 0 && !item.equals("Action")) {
                    Pojo pojo = new Pojo();
                    pojo.setNumber(textView.getText().toString().trim());
                    pojo.setAction(item);
                    pojo.setId(id);
                    pojoList = sharedPreff.Retreive("MyObject");
                    if(pojoList == null || pojoList.size() == 0) {
                        pojoList = new ArrayList<Pojo>();
                        pojoList.add(pojo);
                        sharedPreff.SaveSerialize(pojoList);
                        listAdapter = new CustomAdapter(context, R.layout.spinner_list_items, sharedPreff.Retreive("MyObject"));
                        listView.setAdapter(listAdapter);
                    }else{
                        sharedPreff.UpdateList(pojo,"MyObject");
                        pojoList = sharedPreff.Retreive("MyObject");
                        listAdapter.refreshAdapter(pojoList);
                    }
                    textView.setText("+91");
//                    callBarring.UpdateRecord(context);
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

        log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LogActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.d("mainactivity","Clicked itam at position "+i+":long "+l);
                final View selectedView  = view ;
                final long itemPosition = l;

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
                                sharedPreff.DeleteNumber(i,"MyObject");
                                List<Pojo> list = sharedPreff.Retreive("MyObject");
                                listAdapter.refreshAdapter(list);
                                Log.d("mainactivity",":Id"+i);
                                dialog.cancel();
//                                callBarring.UpdateRecord(context);
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

    private void executeUserPermissionTree() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_BOOT_COMPLETED))
            permissionsNeeded.add("BOOT Completion");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NOTIFICATION_POLICY))
            permissionsNeeded.add("Access notification");
        if (!addPermission(permissionsList, Manifest.permission.MODIFY_AUDIO_SETTINGS))
            permissionsNeeded.add("Access network state");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("Access phone state");
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("Access phone state");


        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            }
                        });
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            }
            return;
        }
        isGranted = true;

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                if (!shouldShowRequestPermissionRationale(permission))
                    return false;
            }
            return true;
        }
        return false;
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.RECEIVE_BOOT_COMPLETED, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NOTIFICATION_POLICY, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.MODIFY_AUDIO_SETTINGS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (/*perms.get(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && */
                        perms.get(Manifest.permission.MODIFY_AUDIO_SETTINGS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                        ) {
                    // All Permissions Granted
                    isGranted = true;
                } else {
                    // Permission Denied
                    finish();

                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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
        CallBarring.ACTION_STOP = true;
        serviceIntent=new Intent(getApplicationContext(), CallBarringService.class);
        stopService(serviceIntent);
    }



}
