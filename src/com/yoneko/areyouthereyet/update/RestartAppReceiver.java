package com.yoneko.areyouthereyet.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RestartAppReceiver extends BroadcastReceiver {
	public RestartAppReceiver() {
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO: This method is called when the BroadcastReceiver is receiving
		// an Intent broadcast.
		Log.i("Reid1", "Restarting the app service if it were killed");
		if(intent.getAction()!= null && intent.getAction().equalsIgnoreCase("YouWillNeverKillMe")) {
			context.startService(new Intent(context.getApplicationContext(), SafetyService.class));
		}

	}
}
