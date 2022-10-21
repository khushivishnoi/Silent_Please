package com.vishnoikhushi.silentplease;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.vishnoikhushi.silentplease.data.DbHandler;

import java.util.ArrayList;

public class ServiceReceiver extends BroadcastReceiver {
    private AudioManager am;
    DbHandler mydb;
    ArrayList<String> numbersList;
    static int initialState;
    @Override
    public void onReceive(Context context, Intent intent) {
        mydb = new DbHandler(context);
        numbersList = new ArrayList<>();
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Cursor res = mydb.getAllData();
        while (res.moveToNext()) {
            numbersList.add(res.getString(2));
        }
        if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            initialState = am.getRingerMode();
            TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context
                    .TELEPHONY_SERVICE);
            telephony.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String phoneNumber) {
                    super.onCallStateChanged(state, phoneNumber);
                    System.out.println("incomingNumber : " + phoneNumber);
                    //if the incoming number is present inside the database, it will change the state of phone to silent.
                    if (numbersList != null) {
                        boolean flag=false;
                        for (int i = 0; i < numbersList.size(); i++) {
                            String no = numbersList.get(i);
                            
                            if (PhoneNumberUtils.compare(no, phoneNumber)) {
//                                 am.setRingerMode(1);
//                                 break;
                                flag=true;
                            }
                        }
                        if(flag== false){
                            am.setRingerMode(1);
                            break;
                        }
                    }
                    sendMessage(phoneNumber);
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
        }
        //Restore the state of phone back to ringing
        else if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            if (initialState == 1) {
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            }
        }
    }

    /*
     This metthod used to send message to the incoming number.
     */
    private void sendMessage(String number) {
        String msg = "Will reach you soon!!";
        if (!number.equals("") && !msg.equals("")) {
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage(number, null, msg, null, null);
            System.out.println(msg + " is send to " + number);
        }
    }
}
