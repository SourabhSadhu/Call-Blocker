package com.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;


import android.util.Log;
import com.controller.R;

import static android.content.Context.NOTIFICATION_SERVICE;

import com.model.SharedPreff;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sourabh on 2/19/2017.
 */

public class CreateNotification {
    private Context context;
    SharedPreff sharedPreff;
    int count;
    String customMessage;

    public CreateNotification(Context ctx) {
        this.context = ctx;
        sharedPreff = new SharedPreff(context, context.getResources().getString(R.string.notification));
    }

    public void generateNotification(String msg, String body, Class className) {
        try {
            /*if (onResumeCheck()) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_app_notify)
                                .setContentTitle(msg)
                                .setContentText(body)
                                .setContentText("Multi Line")
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

                int mNotificationId = 001;
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            }else{
                *//**
                 * if classname log activity
                 *
                 *//*
            */
                lastNotificationCount();
                lastNotificationMsg();

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

            int mNotificationId = 001;
            NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean onResumeCheck() {
        String data;
        data = sharedPreff.getString("notification", "");
        return data.equals("resume") ? true : false;
    }

    private void lastNotificationCount(){
        count = Integer.parseInt(sharedPreff.getString("nCount",""));
        Log.e("Notification",Integer.toString(count));
        sharedPreff.SaveSerialize(null,"nCount",Integer.toString(count + 1));
    }

    private void lastNotificationMsg(){
        Pojo pojo = new Pojo();
        List<Pojo> pojoList = new ArrayList<>();
        List<String> namesAll = new ArrayList<>();
        for(int iter = 0; iter < count; iter++){
//            pojoList.add(sharedPreff.Retreive(iter,"Log"));
            if(null != sharedPreff.Retreive(iter,"Log"))
            namesAll.add(sharedPreff.Retreive(iter,"Log").getName());
            else
                Log.e("NULL","Invalid index");
        }
        Set<String> namesUnq = new HashSet<>(namesAll);
        Log.e("Notification",Integer.toString(namesUnq.size()));

        customMessage = "Processed " + (count + 1) + " calls for " + Integer.toString(namesUnq.size()) + " numbers";
    }
}
