package com.call.block.group.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.call.block.group.R;
import com.call.block.group.model.CommonUtils;
import com.call.block.group.model.Log;
import com.call.block.group.model.PojoCallLogData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogPickerActivity extends AppCompatActivity {

    private CustomCallLogAdapter customCallLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_picker);
        Context context = this;
        ListView log_picker_list_view = (ListView) findViewById(R.id.log_picker_list_view);
        customCallLogAdapter = new CustomCallLogAdapter(context,R.layout.activity_contact_list_with_image, getCallDetails(context));

        log_picker_list_view.setAdapter(customCallLogAdapter);

        log_picker_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("DEBUG","List Click working");
                PojoCallLogData pojoCallLogData;
                pojoCallLogData = (PojoCallLogData) customCallLogAdapter.getItem(i);
                assert pojoCallLogData != null;
                Log.d("DEBUG","" + pojoCallLogData.getName());
                Intent result = new Intent();
                result.putExtra("NAME",pojoCallLogData.getName());
                setResult(3,result);
                finish();
            }
        });
    }

    private List<PojoCallLogData> getCallDetails(Context context) {
        Log.d("GetCall","Before Called");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context,"Permission denied for Reading Call Log", Toast.LENGTH_SHORT).show();
            return null;
        }
        String[] callLogFields = {CallLog.Calls._ID,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.NUMBER,
                CallLog.Calls.TYPE,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION};
        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
                callLogFields, null, null, CallLog.Calls.DATE + " DESC");
        assert cursor != null;
        int name = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
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
                case CallLog.Calls.REJECTED_TYPE:
                    dir = "Reject";
                    break;
            }
            PojoCallLogData p = new PojoCallLogData();
            p.setName(phName);
            p.setNumber(phNumber.trim());
            p.setType(dir);
            p.setTypeInt(dircode);
            p.setDate_time(callDate);
            p.setCall_date(callDayTime);
            p.setDuration(Integer.parseInt(callDuration));
            if (CommonUtils.checkDuplicate(pojoCallLogDatas,p)) pojoCallLogDatas.add(p);
        }
        cursor.close();
        return pojoCallLogDatas;
    }
}
