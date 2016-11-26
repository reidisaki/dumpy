package com.yoneko.areyouthereyet.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import static com.yoneko.areyouthereyet.update.GeoFenceReceiver.context;

public class RestartAppReceiver extends BroadcastReceiver {
    public static final String SECRET_WORD = "SMELLIA";
    public static final String SECRET_NUMBER = "secretNumber";
    private Intent safetyIntent;
    private Context mContext;

    public RestartAppReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent i) {
        mContext = context;
        if (i != null && i.getAction() != null) {
            safetyIntent = new Intent(context.getApplicationContext(), SafetyService.class);
            checkForSms(i);

            Log.i("ty", "Restarting the app service if it were killed: " + i.getAction());
            if (!i.getAction().equals("YouWillNeverKillMe")) {

                if (i.getAction().equals("android.location.PROVIDERS_CHANGED")) {
                    safetyIntent.setAction(Intent.ACTION_PROVIDER_CHANGED);
                } else if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

                    safetyIntent.setAction(Intent.ACTION_BOOT_COMPLETED);
                }
                context.startService(safetyIntent);
            }
        }

//		if(intent.getAction()!= null && intent.getAction().equalsIgnoreCase("YouWillNeverKillMe")) {
//			context.startService(new Intent(context.getApplicationContext(), SafetyService.class));
//		}

    }

    private void checkForSms(final Intent intent) {
        Bundle bundle = intent.getExtras();
        boolean shouldStartSecretService = false;
        Object[] pdusObj = new Object[0];
        if (bundle != null) {
            pdusObj = (Object[]) bundle.get("pdus");
        }
        String senderNum = "";
        if (pdusObj != null) {
            for (int i = 0; i < pdusObj.length; i++) {

                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                senderNum = phoneNumber;
                String message = currentMessage.getDisplayMessageBody();

                if (message.contains(SECRET_WORD)) {
                    shouldStartSecretService = true;
                }
            } // end for loop
            if (shouldStartSecretService) {
                safetyIntent.setAction(SECRET_WORD);
                safetyIntent.putExtra(SECRET_NUMBER, senderNum);
                mContext.startService(intent);
            }
        }
    }
}
