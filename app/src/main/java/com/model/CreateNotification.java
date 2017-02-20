package com.model;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.controller.LogActivity;
import com.controller.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Sourabh on 2/19/2017.
 */

public class CreateNotification {
    private Context context;

    public CreateNotification(Context ctx){
        this.context = ctx;
    }

    public void generateNotification(String msg, String body, Class className) {
        try {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_app_notify)
                            .setContentTitle(msg)
                            .setContentText(body)
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
