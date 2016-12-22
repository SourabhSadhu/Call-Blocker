package com.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.model.*;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class CallBarring extends BroadcastReceiver{

    private String number;
    private static List<Pojo> list = new ArrayList<>();
    public static Boolean ACTION_STOP = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;
        else {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(number != null ) {
                String data1 = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                int data2 = TelephonyManager.CALL_STATE_RINGING;
                Log.d("Call", "State " + data1 + ":Incoming " + number + ":Call State" + data2);
                if(ACTION_STOP == false) {
                    for(int iter = 0; iter<list.size();iter++){
                        String mobNumber,action;
                        int length;
                        mobNumber = list.get(iter).getNumber();
                        action = list.get(iter).getAction();
                        length = mobNumber.length();
                        Log.d("Call Barring","Mobile Number "+mobNumber+":Action "+action+":Number length "+length);
                        Log.d("Call Barring", "Received Number "+number);
                        if (number.substring(0, length).equals(mobNumber)) {
                            Log.d("Call Barring","Taking Action");
                            disconnectPhoneItelephony(context,action);
                            return;
                        }
                    }

                }else{
                    Log.d("CallBarring","Broadcast action stopped");
                }
            }
        }
    }

    public void UpdateRecord(Context context){
        list = new SharedPreff(context).Retreive();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context, String action) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            if(action.equals("Block")){
                telephonyService.endCall();
                Log.d("Telephony Service","Call Dropped");
            } else if(action.equals("Slient")){
                telephonyService.silenceRinger();
                Log.d("Telephony Service","Call Silent");
            } else if(action.equals("Answer")){
                telephonyService.answerRingingCall();
                Log.d("Telephony Service","Call Answered");
            }
            Log.d("Call", "Rejected RS");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
