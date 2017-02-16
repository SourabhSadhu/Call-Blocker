package com.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telecom.Call;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.model.*;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CallBarring extends BroadcastReceiver{

    private String number;
    private static List<Pojo> list = new ArrayList<>();
    public static Boolean ACTION_STOP = false;
    private Context context;
    AudioManager audioManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;
        else {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if(number != null ) {
                list = new SharedPreff(context, "MyObject").Retreive("MyObject");
                String data1 = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                int data2 = TelephonyManager.CALL_STATE_RINGING;
                Log.d("Call", "State " + data1 + ":Incoming " + number + "list size" + list.size());
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
                            audioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            disconnectPhoneItelephony(context,action,number);
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

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context, String action, String numberAction) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        Pojo pojo = new Pojo("",numberAction+"("+action+")",action,1,getCurrentDate());
        SharedPreff putLog = new SharedPreff(context,"Log");
        List<Pojo> lastData = new ArrayList<>();
        lastData = putLog.Retreive("Log");
        String lastDateTime = "";
        if(lastData != null && lastData.size()>0) {
            lastDateTime = lastData.get(lastData.size() - 1).getDateTime();
        }

        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            if(action.equals("Block")){
                telephonyService.endCall();
                Log.d("Telephony Service","Call Dropped");
            } else if(action.equals("Silent")){
                telephonyService.silenceRinger();
                if(audioManager.getRingerMode()==AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            backToNormal();
                        }
                    }, 45000);
                }
                Log.d("Telephony Service","Call Silent");
            } else if(action.equals("Answer")){
                telephonyService.answerRingingCall();
                Log.d("Telephony Service","Call Answered");
            }

            if(timeCheck(lastDateTime))
                putLog.UpdateList(pojo,"Log");
            Log.d("Call", "Rejected");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToNormal(){
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public String getCurrentDate() {
        SimpleDateFormat currentDate = new SimpleDateFormat("MM/dd HH:mm");
        String thisDate = currentDate.format(new Date());
        return thisDate;
    }

    public boolean timeCheck(String lastDateTime) {
        if (lastDateTime.equals("")) {
            return true;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
            Date sysTime = null;
            Date lastTime = null;
            try {
                sysTime = format.parse(getCurrentDate());
                lastTime = format.parse(lastDateTime);
                if (sysTime.after(lastTime)) {
                    Log.d("Date Parse", "Sys-" + sysTime + ":Last" + lastTime);
                    return true;
                }
            } catch (ParseException e) {
                Log.e("Date Parse", e.toString());
            }


        }
        return false;
    }
}
