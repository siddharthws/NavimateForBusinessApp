package com.biz.navimate.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.biz.navimate.debug.Dbg;

/**
 * Created by Siddharth on 19-04-2017.
 */

public class SmsReceiver extends BroadcastReceiver
{
    // ----------------------- Constants ----------------------- //
    private static final String TAG = "SMS_RECEIVER";

    // SMS Receive Intent Action
    private static final String RECEIVE_ACTION     = "android.provider.Telephony.SMS_RECEIVED";

    // ----------------------- Classes ---------------------------//
    // ----------------------- Interfaces ----------------------- //
    public interface SmsReceiverInterface
    {
        void onSmsReceived(String text);
    }
    private SmsReceiverInterface listener = null;
    public void SetSmsReceiverInterface(SmsReceiverInterface listener)
    {
        this.listener = listener;
    }

    // ----------------------- Globals ----------------------- //
    // ----------------------- Constructor ----------------------- //
    public SmsReceiver() {}

    // ----------------------- Overrides ----------------------- //
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Dbg.info(TAG, "SMS Received");
        String action = intent.getAction();

        if (action.equals(RECEIVE_ACTION))
        {
            // Extract Message
            Bundle extras = intent.getExtras();
            String smsText = ParseReceivedSms(extras);

            // Call listener
            if (listener != null)
            {
                listener.onSmsReceived(smsText);
            }
        }
    }

    // ----------------------- Public APIs ----------------------- //
    public void Register(Context context)
    {
        try
        {
            context.registerReceiver(this, new IntentFilter(RECEIVE_ACTION));
        }
        catch (Exception e)
        {
            Dbg.error(TAG, "Exception while registering receiver " + e.getMessage());
            Dbg.stack(e);
        }
    }

    public void UnregisterReceiver (Context context)
    {
        try
        {
            context.unregisterReceiver(this);
        }
        catch (Exception e)
        {
            Dbg.error(TAG, "Exception while unregistering receiver " + e.getMessage());
            Dbg.stack(e);
        }
    }

    // ----------------------- Private APIs ----------------------- //
    private String ParseReceivedSms(Bundle extras)
    {
        String smsText = "";

        // Extract SMS PDU from Intent
        SmsMessage message = null;
        String smsFormatString = extras.getString("format");
        Object[] pdus = (Object[]) extras.get("pdus");

        // Extract SMS message from PDU
        for (int i = 0; i < pdus.length; i++)
        {
            byte[] pdu = (byte[]) pdus[i];

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                message = SmsMessage.createFromPdu(pdu, smsFormatString);
            }
            else
            {
                message = SmsMessage.createFromPdu(pdu);
            }
        }

        smsText = message.getDisplayMessageBody();

        return smsText;
    }
}
