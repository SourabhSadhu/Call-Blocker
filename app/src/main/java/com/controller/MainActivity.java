package com.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.provider.ContactsContract;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {

    private Intent serviceIntent;
//    private CallBarring callBarring;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private boolean isGranted = false;

    private ImageButton stop_service,add_contact,add_group,view_log;
    private ListView listView;

    private CustomLogAdapter customAdapter;
    private SharedPreff sharedPreff;
    private Context context;
//    private SharedPreferences mPrefs;

    private Pojo pojo;
    private List<Pojo> pojoArrayList;

    private EditText et_name,et_number;
    private RadioButton block,silent;
    private Button btn_add;

    String phoneNo = null ;
    String name = null;

    private static final int RESULT_PICK_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        executeUserPermissionTree();

        if(isGranted) {
            startService(new Intent(this, CallBarringService.class));
            sharedPreff = new SharedPreff(context, "MyObject");
//            mPrefs = getSharedPreferences("MyObject", Context.MODE_PRIVATE);
            pojoArrayList = new ArrayList<>();
            pojo = new Pojo();

            initView();
            initListView();
            setListner();

        }

    }

    public void setListner() {
        stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });
        add_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContactGroup(1);
            }
        });
        add_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addContactGroup(2);
            }
        });
        view_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewLog = new Intent(context, LogActivity.class);
                startActivity(viewLog);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int pos = i;

                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                TextView number = (TextView) view.findViewById(R.id.number_number);
                builder1.setMessage("Delete number "+number.getText().toString()+"?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                sharedPreff.DeleteNumber(pos,"MyObject");
                                pojoArrayList = sharedPreff.Retreive("MyObject");
                                refreshListView();
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

            }
        });
    }

    private void initView(){
        stop_service = (ImageButton) findViewById(R.id.stop_service);
        add_contact = (ImageButton) findViewById(R.id.add_contact);
        add_group = (ImageButton) findViewById(R.id.add_group);
        view_log = (ImageButton) findViewById(R.id.view_log);
        listView = (ListView) findViewById(R.id.list_view);
    }

    private void initListView(){
        pojoArrayList = sharedPreff.Retreive("MyObject");
        customAdapter = new CustomLogAdapter(context,R.layout.activity_contact_list,pojoArrayList);
        listView.setAdapter(customAdapter);
    }

    private void refreshListView(){
        customAdapter.refreshAdapter(sharedPreff.Retreive("MyObject"));
    }

    private void addContactGroup(int type){



        final Dialog alertGroup = new Dialog(context,android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertGroup.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = alertGroup.getWindow();
        assert window != null;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        alertGroup.setCancelable(true);
        alertGroup.setContentView(R.layout.dialog_add_contact);
        alertGroup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        et_name = (EditText) alertGroup.findViewById(R.id.et_name);
        et_number = (EditText) alertGroup.findViewById(R.id.et_number);
        block = (RadioButton) alertGroup.findViewById(R.id.block);
        silent = (RadioButton) alertGroup.findViewById(R.id.silent);
        btn_add = (Button) alertGroup.findViewById(R.id.btn_add);

        et_name.setText("");
        et_number.setText("");

        if(type == 1){
            pickContact();
        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_number.getText() != null && et_number.getText().toString().length() > 0) {
                    if (et_name.getText() != null && et_name.getText().toString().length() > 0) {
                        if (block.isChecked() || silent.isChecked()) {
                            if (block.isChecked()) {
                                pojo.setAction("Block");
                            } else if (silent.isChecked()) {
                                pojo.setAction("Silent");
                            }
                            if (et_name != null && et_name.getText().toString().length() > 0) {
                                pojo.setName(et_name.getText().toString().trim());
                            }
                            pojo.setNumber(et_number.getText().toString());
                            sharedPreff.UpdateList(pojo, "MyObject");
                            refreshListView();
                            alertGroup.cancel();
                        } else
                            Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Select an Action", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "Enter a number", Toast.LENGTH_SHORT).show();
            }
        });

        alertGroup.show();
    }

    private void executeUserPermissionTree() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
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
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Access read contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Access write contacts");


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
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
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

//    public void startService() {
//        serviceIntent=new Intent(getApplicationContext(), CallBarringService.class);
//        startService(serviceIntent);
//    }

    public void stopService() {
        CallBarring.ACTION_STOP = true;
        serviceIntent=new Intent(getApplicationContext(), CallBarringService.class);
        stopService(serviceIntent);
    }

    public void pickContact()
    {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    contactPicked(data);
                    break;
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact");
        }
    }
    /**
     * Query the Uri and read contact details. Handle the picked contact data.
     * @param data
     */
    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int  nameIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
//            Uri photo = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Log.e("Contact", "Name-" + name + " Number-" + phoneNo + " Pic-" + phoneNo);

            phoneNo = phoneNo.replaceAll("-","");
            phoneNo = phoneNo.replaceAll(" ","");
            if(!phoneNo.substring(0,3).equalsIgnoreCase("+91"))
                phoneNo = "+91" + phoneNo;
            if(et_name != null && et_number != null) {
                et_name.setText(name);
                et_number.setText(phoneNo.replaceAll("-",""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}