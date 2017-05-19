package com.block.callblocker.blocksilent.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class BootCompletion extends BroadcastReceiver {
    public BootCompletion() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
        {
            Intent serviceIntent = new Intent(context, CallBarringService.class);
            context.startService(serviceIntent);
        }
    }

}
