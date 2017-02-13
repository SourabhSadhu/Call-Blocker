package com.model;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by SourabhSadhu on 14-12-2016.
 */

public class CallBarringService extends IntentService {

    boolean run = true;
    public static  CallBarring callBarring = new CallBarring();
    public static IntentFilter intentF = new IntentFilter("CallBarring");

    public CallBarringService() {
        super("CallBarringService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(callBarring, filter);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);
//        filter.addAction("your_action_strings"); //further more
//        filter.addAction("your_action_strings"); //further more
        registerReceiver(callBarring, filter);
        Log.e("Service","Handle Intent");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
        Log.e("Service","Start");
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("Service","Task Removed");
        Intent restartService = rootIntent; //new Intent(getApplicationContext(),this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(this, 1, restartService,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)this.getSystemService(this.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, 1000, restartServicePI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Service","Destroy");
//        unregisterReceiver(callBarring);
        CallBarring.ACTION_STOP=true;
//        callBarring = null;
        stopSelf();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}
