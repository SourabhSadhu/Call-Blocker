package com.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;


import com.model.Log;
import com.controller.R;
import static android.content.Context.NOTIFICATION_SERVICE;
import com.model.SharedPreff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateNotification {
    private Context context;
    private SharedPreff sharedPreff;
    private int count;
    private String customMessage;

    public CreateNotification(Context ctx) {
        this.context = ctx;
        sharedPreff = new SharedPreff(context);
    }

    public void generateNotification(String msg, String body, Class className) {
        try {
            if (onResumeCheck())
                customMessage = body;
            else{
                lastNotificationCount();
                lastNotificationMsg();
            }
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_app_notify)
                            .setContentTitle(msg)
                            .setContentText(customMessage)
                            .setAutoCancel(true);
            mBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

            Intent resultIntent = new Intent(context, className);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);

            int mNotificationId = 1;
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean onResumeCheck() {
        String data;
        data = sharedPreff.getString("notification", "");
        return data.equals("resume");
    }

    private void lastNotificationCount(){
        count = Integer.parseInt(sharedPreff.getString("nCount","")) + 1;
        Log.e("Notification",Integer.toString(count));
        sharedPreff.SaveSerialize(null,"nCount",Integer.toString(count));
    }

    private void lastNotificationMsg(){
        List<Pojo> pojoList;
        List<String> namesAll = new ArrayList<>();
        String name;

        pojoList = sharedPreff.Retreive("Log");
        for(int iter = 0; iter < count; iter++){
            if(null != pojoList) {
                name = pojoList.get(iter).getName();
                namesAll.add(name);
                Log.e("lastNotificationName" , name);
            }
            else
                Log.e("NULL","Invalid index for " + iter);
        }
        Set<String> namesUnq = new HashSet<>(namesAll);
        Log.e("Notification",Integer.toString(namesUnq.size()));

        customMessage = "Processed " + (count) + " calls for ";
        if(1 == namesUnq.size() )
            customMessage += namesAll.get(0);
        else
         customMessage += Integer.toString(namesUnq.size()) + " numbers";
    }
}
