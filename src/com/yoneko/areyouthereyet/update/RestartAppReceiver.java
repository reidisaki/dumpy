package com.yoneko.areyouthereyet.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartAppReceiver extends BroadcastReceiver {
	public RestartAppReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent i) {
		if(i != null && i.getAction() != null) {
			Log.i("ty", "Restarting the app service if it were killed: " + i.getAction());
			if(!i.getAction().equals("YouWillNeverKillMe")) {
				Intent intent = new Intent(context.getApplicationContext(), SafetyService.class);
				if(i.getAction().equals("android.location.PROVIDERS_CHANGED")) {
					intent.setAction(Intent.ACTION_PROVIDER_CHANGED);
				} else if(i.getAction().equals("android.intent.action.BOOT_COMPLETED")){
					
					intent.setAction(Intent.ACTION_BOOT_COMPLETED);
				}
				context.startService(intent);		
			}			
		}
		
		
		
//		if(intent.getAction()!= null && intent.getAction().equalsIgnoreCase("YouWillNeverKillMe")) {
//			context.startService(new Intent(context.getApplicationContext(), SafetyService.class));
//		}

	}
}
