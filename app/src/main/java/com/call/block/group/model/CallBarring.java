package com.call.block.group.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.telephony.TelephonyManager;

import com.call.block.group.controller.LogActivity;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CallBarring extends BroadcastReceiver {

    private String number;
    String mobNumber, action, name;
    int length;
    String block_type;
    private static List<Pojo> list = new ArrayList<>();
    public static Boolean ACTION_STOP = false;
    private Context context;
    private AudioManager audioManager;
    private CreateNotification createNotification;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        if (!intent.getAction().equals("android.intent.action.PHONE_STATE"))
            return;
        else {
            number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number != null) {
                createNotification = new CreateNotification(context);
                list = new SharedPreff(context).Retreive("MyObject");
                String data1 = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                int data2 = TelephonyManager.CALL_STATE_RINGING;
                Log.d("Call", "State " + data1 + ":Incoming " + number + "list size" + list.size());
                if (ACTION_STOP == false) {
                    for (int iter = 0; iter < list.size(); iter++) {

                        mobNumber = list.get(iter).getNumber();
                        action = list.get(iter).getAction();
                        name = list.get(iter).getName();
                        block_type = list.get(iter).getBlock_action();
                        length = mobNumber.length();
                        Log.d("Call Barring", "Mobile Number " + mobNumber + ":Action " + action + ":Number length " + length);
                        Log.d("Call Barring", "Received Number " + number);
                        if(CallBlockNumberType.STARTS_WITH.value().equals(block_type)) {
                            if (number.length() >= length && number.substring(0, length).equals(mobNumber)) {
                                Log.d("Call Barring", "Taking Action for Starts with");
                                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                disconnectPhoneItelephony(context, name, action, number, block_type);
                                return;
                            }
                        } else if(CallBlockNumberType.CONTAINS.value().equals(block_type)){
                            if (number.length() >= length && number.contains(mobNumber)) {
                                Log.d("Call Barring", "Taking Action for contains");
                                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                disconnectPhoneItelephony(context, name, action, number, block_type);
                                return;
                            }
                        }else if(CallBlockNumberType.ENDS_WITH.value().equals(block_type)){
                            Log.d("Ends with","stored number: "+ mobNumber + " length: " + length + "received number check:" +
                                    number.substring(number.length()-length, number.length()));
                            if (number.length() >= length && number.substring(number.length()-length, number.length()).equals(mobNumber)) {
                                Log.d("Call Barring", "Taking Action for contains");
                                audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                disconnectPhoneItelephony(context, name, action, number, block_type);
                                return;
                            }
                        }
                    }

                } else {
                    Log.d("CallBarring", "Broadcast action stopped");
                }
            }
        }
    }

    public void UpdateRecord(Context context) {

    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void disconnectPhoneItelephony(Context context, String name, String action, String numberAction, String block_type) {
        ITelephony telephonyService;
        TelephonyManager telephony = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);

        Pojo pojo = new Pojo(name, numberAction, action, 1, getCurrentDate(), block_type);
        SharedPreff putLog = new SharedPreff(context);
        List<Pojo> lastData = new ArrayList<>();
        lastData = putLog.Retreive("Log");
        String lastDateTime = "";
        final String handlerName = name;

        if (lastData != null && lastData.size() > 0) {
            lastDateTime = lastData.get(0).getDateTime();
        }

        try {
            Class c = Class.forName(telephony.getClass().getName());
            Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            telephonyService = (ITelephony) m.invoke(telephony);
            if (action.equals("Block")) {
                telephonyService.endCall();
            } else if (action.equals("Silent")) {
                telephonyService.silenceRinger();
                if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            backToNormal();
                        }
                    }, 45000);
                }
            } else if (action.equals("Answer")) {
                telephonyService.answerRingingCall();
            }

            if (timeCheck(lastDateTime)) {
                putLog.UpdateList(pojo, "Log");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        createNotification.generateNotification("Action taken for " + handlerName, "View Details", LogActivity.class);
                    }
                }, 1000);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void backToNormal() {
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    public String getCurrentDate() {
        SimpleDateFormat currentDate = new SimpleDateFormat("dd/MM HH:mm:ss");
        String thisDate = currentDate.format(new Date());
        return thisDate;
    }

    public boolean timeCheck(String lastDateTime) {
        if (lastDateTime.equals("")) {
            return true;
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm:ss");
            Date sysTime;
            Date lastTime;
            try {
                sysTime = format.parse(getCurrentDate());
                lastTime = format.parse(lastDateTime);
                if (sysTime.getTime() - lastTime.getTime() >= 5000) {
                    return true;
                }
            } catch (ParseException e) {
                Log.e("Date Parse", e.toString());
            }


        }
        return false;
    }
}
