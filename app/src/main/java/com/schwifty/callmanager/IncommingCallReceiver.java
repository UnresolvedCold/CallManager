package com.schwifty.callmanager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import androidx.core.content.ContextCompat;

public class IncommingCallReceiver extends BroadcastReceiver {

    static boolean ring=false;
    static boolean callReceived=false;
    private static String mLastState;

    @Override
    public void onReceive(Context mContext, Intent intent) {

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Bundle bundle = intent.getExtras();
        String number = bundle.getString("incoming_number");

        if (!state.equals(mLastState) && number!=null) {
            mLastState = state;

            try {
                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    Log.d("CallManager", "Phone is ringing");
                    ring = true;
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    Log.d("CallManager", "Call picked up");
                    callReceived = true;
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    Log.d("CallManager", "Phone is Idle");

                    //Check if it was a missed call
                    if (ring && !callReceived) {
                        Log.d("CallManager", "Missed Call from " + number);

                        //Get Personalised Settings
                        JSONObject unknown = new JSONObject( PreferenceManager.getDefaultSharedPreferences(mContext).getString("CallManager_Unknown",""));
                        JSONObject known = new JSONObject( PreferenceManager.getDefaultSharedPreferences(mContext).getString("CallManager_known",""));

                        //is a known number
                        if(isNumberSaved(number,mContext))
                        {
                            sendSMS(number, unknown.get("message").toString());
                            /*sendSMS(number,
                                    "Your call has been received."+
                                    "\n I'll call you ASAP."+
                                    "\n Kindly wait for sometime. \n\n"+
                                    "If it's urgent, kindly reply to this message with the purpose of your call.");
                       */ }
                        else
                        {
                            sendSMS(number,unknown.get("message").toString());
                           /* sendSMS(number,"This is an automated message.\n\n"+
                                    "Your phone number is unknown to me. Kindly reply to this number with the purpose of your call for further communication.\n");
                       */ }
                    }
                }
            } catch (Exception e) {

            }
        }

    }

    public Boolean isNumberSaved(String number, Context mContext) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor contact = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        return contact.getCount()>0;
    }

    public String getContactName(String number, Context mContext)
    {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = mContext.getContentResolver();
        Cursor contact = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contact != null && contact.getCount() > 0) {
                contact.moveToNext();
                name = contact.getString(contact.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (contact != null) {
                contact.close();
            }
        }

        return name;

    }

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
