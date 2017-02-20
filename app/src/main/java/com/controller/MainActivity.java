package com.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
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
import android.widget.RadioGroup;
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
    private boolean numberCheck;

    private ImageButton stop_service, add_contact, add_group, view_log;
    private ListView listView;

    private CustomLogAdapter customAdapter;
    private CreateNotification createNotification;
    private SharedPreff sharedPreff;
    private Context context;
//    private SharedPreferences mPrefs;

    private Pojo pojo;
    private List<Pojo> pojoArrayList;

    private EditText et_name, et_number;
    private RadioGroup radio_grp;
    private RadioButton block, silent;
    private Button btn_add;

    String phoneNo = "";
    String name = "";

    private static final int RESULT_PICK_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        executeUserPermissionTree();

//        if(isGranted) {
        //TODO isGranted is coming false for first time
        startService();
        sharedPreff = new SharedPreff(context, "MyObject");
//            mPrefs = getSharedPreferences("MyObject", Context.MODE_PRIVATE);
        pojoArrayList = new ArrayList<>();
        pojo = new Pojo();

        initView();
        initListView();
        setListner();
        createNotification = new CreateNotification(context);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
    }

    public void setListner() {
        try {
            stop_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopService();
                }
            });
            add_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEditContactGroup(1, null, 0);
                }
            });
            add_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addEditContactGroup(2, null, 0);
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
                    TextView name = (TextView) view.findViewById(R.id.contact_name);
                    builder1.setMessage("Take action for " + name.getText().toString() + "?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Edit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    addEditContactGroup(3, sharedPreff.Retreive(pos, "MyObject"), pos);
                                }
                            });

                    builder1.setNegativeButton(
                            "Remove",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    sharedPreff.DeleteNumber(pos, "MyObject");
//                                    pojoArrayList = sharedPreff.Retreive("MyObject");
                                    refreshListView();
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initView() {
        stop_service = (ImageButton) findViewById(R.id.stop_service);
        add_contact = (ImageButton) findViewById(R.id.add_contact);
        add_group = (ImageButton) findViewById(R.id.add_group);
        view_log = (ImageButton) findViewById(R.id.view_log);
        listView = (ListView) findViewById(R.id.list_view);
    }

    private void initListView() {
        if (null != sharedPreff.Retreive("MyObject"))
            pojoArrayList = sharedPreff.Retreive("MyObject");
        customAdapter = new CustomLogAdapter(context, R.layout.activity_contact_list, pojoArrayList);
        listView.setAdapter(customAdapter);
    }

    private void refreshListView() {
        customAdapter.refreshAdapter(sharedPreff.Retreive("MyObject"));
    }

    private void addEditContactGroup(final int type, Pojo p, final int position) {

        if (type == 1) {
            pickContact();
        }

        final Dialog alertGroup = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
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
        radio_grp = (RadioGroup) alertGroup.findViewById(R.id.radio_grp);
        block = (RadioButton) alertGroup.findViewById(R.id.block);
        silent = (RadioButton) alertGroup.findViewById(R.id.silent);
        btn_add = (Button) alertGroup.findViewById(R.id.btn_add);

        if (null == p) {
            et_name.setText("");
            et_number.setText("");
        } else {
            et_name.setText(p.getName());
            et_number.setText(p.getNumber());
            if (p.getAction().equals("Block")) {
                block.isChecked();
            } else
                silent.isChecked();
        }

        if(!name.equals("") || !phoneNo.equals("")){
            et_name.setText(name);
            et_number.setText(phoneNo);
        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText() != null && et_name.getText().toString().length() > 0) {
                    if (et_number.getText() != null && et_number.getText().toString().length() >= 3) {
                        if (block.isChecked() || silent.isChecked()) {
                            populateCountryCode();
                            if (block.isChecked()) {
                                pojo.setAction("Block");
                            } else if (silent.isChecked()) {
                                pojo.setAction("Silent");
                            }
                            pojo.setName(et_name.getText().toString().trim());
                            pojo.setNumber(et_number.getText().toString());
                            if (type == 3) {
                                sharedPreff.EditList("MyObject", position, pojo);
                            } else {
                                sharedPreff.UpdateList(pojo, "MyObject");
                            }
                            refreshListView();
                            alertGroup.cancel();
                            createNotification.generateNotification("Contact Added", phoneNo, MainActivity.class);
                        } else
                            Toast.makeText(context, "Select an Action", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Enter a valid number", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        alertGroup.show();

        phoneNo = "";
        name = "";
    }

    private void populateCountryCode() {
        phoneNo = et_number.getText().toString().trim();
        if (phoneNo.length() >= 3 && !phoneNo.substring(0, 3).equalsIgnoreCase("+91")) {
            phoneNo = "+91" + phoneNo;
            Toast.makeText(context, "Default country code added", Toast.LENGTH_SHORT).show();
        } /*else if (phoneNo.length() < 3) {
//            phoneNo = "+91";
            Toast.makeText(context, "Enter a valid number", Toast.LENGTH_SHORT).show();
            finish();
        }*/ else {
            Toast.makeText(context, "Verified", Toast.LENGTH_SHORT).show();
        }
        et_number.setText(phoneNo);
        numberCheck = true;
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

    public void startService() {
        serviceIntent = new Intent(context, CallBarringService.class);
        startService(serviceIntent);
//        Intent restartService = serviceIntent; //new Intent(getApplicationContext(),this.getClass());
//        restartService.setPackage(getPackageName());
//        PendingIntent restartServicePI = PendingIntent.getService(this, 1, restartService, PendingIntent.FLAG_ONE_SHOT);
//        AlarmManager alarmService = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, 1000, restartServicePI);
//        alarmService.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, 3000, restartServicePI);
    }

    public void stopService() {
        CallBarring.ACTION_STOP = true;
        serviceIntent = new Intent(getApplicationContext(), CallBarringService.class);
        stopService(serviceIntent);
    }

    public void pickContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
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

    private void contactPicked(Intent data) {
        Cursor cursor = null;
        try {
            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            // column index of the phone number
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            // column index of the contact name
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
//            Uri photo = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Log.e("Contact", "Name-" + name + " Number-" + phoneNo + " Pic-" + phoneNo);

           /* if (et_name != null && et_number != null) {
                if (null != phoneNo) {
                    phoneNo = phoneNo.replaceAll("-", "");
                    phoneNo = phoneNo.replaceAll(" ", "");
                    *//*et_name.setText(name);
                    et_number.setText(phoneNo.replaceAll("-", ""));*//*
                }
            }*/
            phoneNo = phoneNo.replaceAll("-", "");
            phoneNo = phoneNo.replaceAll(" ", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}