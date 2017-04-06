package com.call.block.group.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.call.block.group.CustomContentObserver;
//import com.call.block.group.model.AlertDialogWithImage;
import com.call.block.group.model.CallBarring;
import com.call.block.group.model.CallBarringService;
import com.call.block.group.model.CallBlockNumberType;
import com.call.block.group.model.CommonUtils;
import com.call.block.group.model.CreateNotification;
import com.call.block.group.model.Log;
import com.call.block.group.model.Pojo;
import com.call.block.group.model.PojoCallList;
import com.call.block.group.model.PojoCallLogData;
import com.call.block.group.model.SharedPreff;
import com.controller.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Intent serviceIntent;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private boolean isGranted = false;
    private boolean numberCheck;

    private ImageButton stop_service, add_contact, add_group, view_log;
    private ImageButton btn_call_log,btn_contact;
    private TextView stop_service_text;
    private ListView listView,listViewContact;

    private CustomLogAdapter customAdapter;
    private CustomCallLogAdapter customCallLogAdapter;
    private CreateNotification createNotification;
    private SharedPreff sharedPreff;
    private Context context;
    private Pojo pojo;
    private List<Pojo> pojoArrayList;
    private int block_type_indicator = 0;

    private EditText et_name, et_number;
    private RadioGroup radio_grp;
    private RadioButton block, silent;
    private Button btn_add;
    private Spinner block_type;
    private boolean service_stopped;
    String phoneNo = "";
    String name = "";

    private static final int RESULT_PICK_CONTACT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;
        executeUserPermissionTree();
        startService();
        sharedPreff = new SharedPreff(context);
        pojoArrayList = new ArrayList<>();
        pojo = new Pojo();

        initView();
        initListView();
        setListner();
        createNotification = new CreateNotification(context);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshListView();
        sharedPreff.SaveSerialize(null, context.getResources().getString(R.string.notification),context.getResources().getString(R.string.onResume));
        sharedPreff.putString("nCount",Integer.toString(0));
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreff.SaveSerialize(null, context.getResources().getString(R.string.notification),context.getResources().getString(R.string.onPause));
    }

    public void setListner() {
        try {
            stop_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!service_stopped){
                        stopService();
                        stop_service.setImageResource(R.mipmap.ic_start_blocking);
                        stop_service_text.setText("Start Service");
                        service_stopped = true;
                    }else{
                        startService();
                        stop_service.setImageResource(R.mipmap.ic_stop_blocking);
                        stop_service_text.setText("Stop Service");
                        service_stopped = false;
                    }
                }
            });
            add_contact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contactPickSelection();
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
            /*listViewContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initView() {
        stop_service = (ImageButton) findViewById(R.id.stop_service);
        stop_service_text = (TextView) findViewById(R.id.stop_service_text);
        add_contact = (ImageButton) findViewById(R.id.add_contact);
        add_group = (ImageButton) findViewById(R.id.add_group);
        view_log = (ImageButton) findViewById(R.id.view_log);
        listView = (ListView) findViewById(R.id.list_view);
        listViewContact = (ListView) findViewById(R.id.list_view);
        service_stopped = false;
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

        /*if (type == 1) {
            pickContact();
        }*/

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
        block_type = (Spinner) alertGroup.findViewById(R.id.block_type);

        block_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Spinner", "Position: " + position);
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        pojo.setBlock_action(CallBlockNumberType.STARTS_WITH.value());
                        break;
                    case 2:
                        pojo.setBlock_action(CallBlockNumberType.CONTAINS.value());
                        break;
                    case 3:
                        pojo.setBlock_action(CallBlockNumberType.ENDS_WITH.value());
                        break;
                }
                block_type_indicator = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context,"Select an Action", Toast.LENGTH_SHORT).show();
            }
        });
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

        if(type == 3 && !name.equals("") || !phoneNo.equals("")){
            et_name.setText(name);
            et_number.setText(phoneNo);
        }


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_name.getText() != null && et_name.getText().toString().length() > 0) {
                    if (et_number.getText() != null && et_number.getText().toString().length() >= 3) {
                        if (block.isChecked() || silent.isChecked()) {
                            if(block_type_indicator > 0) {
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
//                            createNotification.generateNotification("Contact Added", name, MainActivity.class);
                                phoneNo = "";
                                name = "";
                            }else
                                Toast.makeText(context, "Select an Action Type", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(context, "Select an Action", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Enter a valid number", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(context, "Enter a name", Toast.LENGTH_SHORT).show();
            }
        });

        alertGroup.show();


    }

    private void contactPickSelection(){
        final Dialog dialogContactSelector = new Dialog(context, android.R.style.Theme_DeviceDefault_Light_Dialog);
//        dialogContactSelector.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        Window window = dialogContactSelector.getWindow();
        assert window != null;
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        dialogContactSelector.setCancelable(true);
        dialogContactSelector.setContentView(R.layout.dialog_contact_selection);
        dialogContactSelector.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        btn_contact = (ImageButton) dialogContactSelector.findViewById(R.id.btn_from_contact);
        btn_call_log = (ImageButton) dialogContactSelector.findViewById(R.id.btn_from_log);

        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogContactSelector.cancel();
                pickContact();
            }
        });

        btn_call_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogContactSelector.cancel();
                List<PojoCallLogData> pojoCallLogDatas = getCallDetails(context);
                customCallLogAdapter = new CustomCallLogAdapter(context, R.layout.activity_contact_list, pojoCallLogDatas);
//                listViewContact.setAdapter(customCallLogAdapter);

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                builderSingle.setIcon(R.mipmap.ic_call_log);
                builderSingle.setTitle("Select One Contact");
                builderSingle.setAdapter(customCallLogAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context,"Clicked at " + which,Toast.LENGTH_SHORT).show();
//                        try {
                            PojoCallLogData cd = (PojoCallLogData) customCallLogAdapter.getItem(which);
                            Pojo p = new Pojo();
                            p.setName(cd.getName());
                            p.setNumber(cd.getNumber());
                            p.setAction("Block");
                            addEditContactGroup(2,p,0);
//                        }catch(Exception e){
//                            Log.e("Error",e.getMessage());
//                        }
                        dialog.cancel();
                    }
                });
                builderSingle.show();
            }
        });

        dialogContactSelector.show();
    }

    private List getCallDetails(Context context) {
        Log.d("GetCall","Before Called");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"Permission denied for Reading Call Log", Toast.LENGTH_SHORT).show();
            return null;
        }
        String[] callLogFields = {Calls._ID,
                Calls.CACHED_NAME,
                Calls.NUMBER,
                Calls.TYPE,
                Calls.DATE,
                Calls.DURATION,
                Calls.GEOCODED_LOCATION};
        String WHERE = Calls.NUMBER + " >0";
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                callLogFields, WHERE, null, CallLog.Calls.DATE + " DESC");
        int name = cursor.getColumnIndex(Calls.CACHED_NAME);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
        int location = cursor.getColumnIndex(Calls.GEOCODED_LOCATION);
        List<PojoCallLogData> pojoCallLogDatas = new ArrayList<>();
        while (cursor.moveToNext()) {
            String phName = cursor.getString(name);
            String phNumber = cursor.getString(number);
            String callType = cursor.getString(type);
            String callDate = cursor.getString(date);
            SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
            Date callDayTime = new Date(Long.valueOf(callDate));
            try {
                callDate = df.format(callDayTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String callDuration = cursor.getString(duration);
            String callLocation = cursor.getString(location);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUT";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "IN";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISS";
                    break;
            }
            PojoCallLogData p = new PojoCallLogData();
            p.setName(phName);
            p.setNumber(phNumber.trim());
            p.setType(dir);
            p.setDate_time(callDate);
            p.setCall_date(callDayTime);
            p.setDuration(callDuration);
            p.setLocation(callLocation);

            if (CommonUtils.checkDuplicate(pojoCallLogDatas,p)) pojoCallLogDatas.add(p);

            p = null;
        }
        for(PojoCallLogData pData : pojoCallLogDatas) {
            Log.d("Unique List", "Number: " + pData.getNumber() + " Name: " + pData.getName() + " Type: " + pData.getType());
        }

        cursor.close();
        return pojoCallLogDatas;
    }

    public void getCallLog() {

        String[] callLogFields = {Calls._ID,
                Calls.CACHED_NAME,
                Calls.NUMBER,
                Calls.TYPE,
                Calls.DATE,
                Calls.DURATION,
                Calls.GEOCODED_LOCATION};
        String viaOrder = Calls.DATE + " DESC";
        String WHERE = Calls.NUMBER + " >0";

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        final Cursor callLog_cursor = context.getContentResolver().query(Calls.CONTENT_URI, callLogFields,
                WHERE, null, viaOrder);
        List<PojoCallLogData> pojoCallLogDatas = new ArrayList<>();
//        for(int i = 0; i < callLog_cursor.getCount(); i++){
        while (callLog_cursor.moveToNext()) {
//            callLog_cursor.moveToPosition(i);

            PojoCallLogData p = new PojoCallLogData();
            p.setName(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.CACHED_NAME)));
            p.setNumber(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.NUMBER)).trim());
            p.setType(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.TYPE)));
            p.setDate_time(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.DATE)));
            p.setDuration(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.DURATION)));
            p.setLocation(callLog_cursor.getString(callLog_cursor
                    .getColumnIndex(Calls.GEOCODED_LOCATION)));

            if (CommonUtils.checkDuplicate(pojoCallLogDatas,p)) pojoCallLogDatas.add(p);

            p = null;
        }
        for(PojoCallLogData pData : pojoCallLogDatas) {
            Log.d("Unique List", "Number: " + pData.getNumber() + "Name: " + pData.getName() + "Type:");
        }
//        AlertDialog.Builder myversionOfCallLog = new AlertDialog.Builder(
//                context);
//
//        android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialogInterface, int item) {
//                callLog_cursor.moveToPosition(item);
//
//                Log.v("number", callLog_cursor.getString(callLog_cursor
//                        .getColumnIndex(Calls.NUMBER)));
//
//                callLog_cursor.close();
//
//            }
//        };
//        myversionOfCallLog.setCursor(callLog_cursor, listener,
//                Calls.CACHED_NAME
            /*+ "(" + Calls.NUMBER +")"*/
//        );

//        myversionOfCallLog.setTitle("Choose from Call Log");
//        myversionOfCallLog.create().show();
    }

    private void populateCountryCode() {
        if(block_type_indicator == 1) {
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
    }

    private void executeUserPermissionTree() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
//        if (!addPermission(permissionsList, Manifest.permission.RECEIVE_BOOT_COMPLETED))
//            permissionsNeeded.add("BOOT");
//        if (!addPermission(permissionsList, Manifest.permission.ACCESS_NOTIFICATION_POLICY))
//            permissionsNeeded.add("notification");
//        if (!addPermission(permissionsList, Manifest.permission.MODIFY_AUDIO_SETTINGS))
//            permissionsNeeded.add("audio settings");
        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("phone state");
        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("call phone");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS) || !addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Manage contacts");
        if (!addPermission(permissionsList, Manifest.permission.READ_CALL_LOG) || !addPermission(permissionsList, Manifest.permission.WRITE_CALL_LOG))
            permissionsNeeded.add("Call Log");


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
                perms.put(Manifest.permission.WAKE_LOCK, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CALL_LOG,PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CALL_LOG,PackageManager.PERMISSION_GRANTED);
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

    public void pickNumberFromLog(){
        /*Intent showCallLog = new Intent();
        showCallLog.setAction(Intent.ACTION_VIEW);
        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
        startActivityForResult(showCallLog,11);*/
        Handler handler = new Handler();
        Uri mediaUri = android.provider.CallLog.Calls.CONTENT_URI;
        Log.d("PhoneService", "The Encoded path of the media Uri is "
                + mediaUri.getEncodedPath());
        CustomContentObserver custObser = new CustomContentObserver(handler,context);
        context.getContentResolver().registerContentObserver(mediaUri, false, custObser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
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
            Uri uri = data.getData();
            cursor = getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            phoneNo = cursor.getString(phoneIndex);
            name = cursor.getString(nameIndex);
//            Uri photo = Uri.withAppendedPath(uri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
            Log.e("Contact", "Name-" + name + " Number-" + phoneNo + " Pic-" + phoneNo);
            phoneNo = phoneNo.replaceAll("-", "");
            phoneNo = phoneNo.replaceAll(" ", "");

            addEditContactGroup(1, null, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
