package com.model;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
    protected void onHandleIntent(@Nullable Intent intent) {
            registerReceiver(callBarring, intentF);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(callBarring);
        CallBarring.ACTION_STOP=true;
        callBarring = null;
        stopSelf();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }


}
