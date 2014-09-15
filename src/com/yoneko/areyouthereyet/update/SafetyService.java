package com.yoneko.areyouthereyet.update;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SafetyService extends Service {
	public SafetyService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		Log.i("Reid1","Service on Bind");
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
    	SafetyService getService() {
            return SafetyService.this;
        }
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("Reid1","OnStartCommand");
		if(!MapActivity.isActive) {
			Intent i = new Intent(getApplicationContext(), MapActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle b = new Bundle();
			b.putBoolean("moveToBack", true);
			i.putExtras(b);
			startActivity(i);
		}
		return Service.START_STICKY;
	}
	@Override
	public void onDestroy() {
		Log.i("Reid1","Killing safety service");
		sendBroadcast(new Intent("YouWillNeverKillMe"));
		super.onDestroy();
		
	}
}