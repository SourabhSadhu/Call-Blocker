package com.call.block.group;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.CallLog;

import com.call.block.group.model.Log;

import java.util.Date;

/**
 * Created by sourabh on 14/3/17.
 */

public class CustomContentObserver extends ContentObserver {

    Context context;
    public CustomContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;

    }

    @Override public boolean deliverSelfNotifications() {
        return false;
    }

    public void logCallLog() {
        long dialed;
        Log.i("Call Log","log Called");
        String columns[]=new String[] {
                CallLog.Calls._ID,
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.DURATION,
                CallLog.Calls.TYPE};
        Cursor c;
        c = context.getContentResolver().query(Uri.parse("content://call_log/calls"),
                columns, null, null, "Calls._ID DESC"); //last record first
        while (c.moveToNext()) {
            dialed=c.getLong(c.getColumnIndex(CallLog.Calls.DATE));
            Log.i("CallLog","type: " + c.getString(4) + "Call to number: "+c.getString(0)+", registered at: "+new Date(dialed).toString());
        }
    }

    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d("PhoneService", "StringsContentObserver.onChange( " + selfChange + ")");
        logCallLog();
    }

}
