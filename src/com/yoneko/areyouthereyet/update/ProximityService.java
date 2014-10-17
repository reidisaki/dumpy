package com.yoneko.areyouthereyet.update;
import com.yoneko.areyouthereyet.update.R;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;


public class ProximityService extends Service{
	String proximitysd = "com.yoneko.areyouthereyet.ProximityService";
	int n = 0;
	private BroadcastReceiver mybroadcast;
	private LocationManager locationManager;
	public static String TAG = "yoneko";
	public static String SMS_SENT = "ConfirmSentActivity";
	public static String SMS_DELIVERED = "DevliveredActivty";
	public static int MAX_SMS_MESSAGE_LENGTH = 160;
	public static int SMS_PORT = 21;
	public static String SMS_MESSAGE_TEXT = "Hi Baby, I made it home safely! " + String.valueOf(MainActivity.RADIUS_METER);
	public static String SMS_MESSAGE_OUT_TEXT = "Hi Baby, I'm leaving my house now!!!! " + String.valueOf(MainActivity.RADIUS_METER);
	MyLocationListener locationListenerp;
	public ProximityService() {

		Log.v(TAG,"prox service");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onCreate() {
		mybroadcast = new ProximityIntentReceiver();
		locationManager = (LocationManager) 
				getSystemService(Context.LOCATION_SERVICE);
		Log.v(TAG,"prox service created");
		
		IntentFilter filter = new IntentFilter("com.yoneko.ProximityService");
        registerReceiver(mybroadcast, filter );

        Intent intent = new Intent("com.yoneko.ProximityService");        
//        intent.putExtra("alert", what);
//        intent.putExtra("type", how);
        PendingIntent proximityIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        locationManager.addProximityAlert(34.054932 , -118.342929, MainActivity.RADIUS_METER, -1, proximityIntent);
//        sendBroadcast(new Intent(intent));
        
	}

	@Override
	public void onDestroy() {
		//Toast.makeText(this, "Proximity Service Stopped", Toast.LENGTH_LONG).show();
		try{
			unregisterReceiver(mybroadcast);
		}catch(IllegalArgumentException e){
			Log.d("reciever",e.toString());
		}


	}
	@Override
	public void onStart(Intent intent, int startid) {
		//Toast.makeText(this, "Proximity Service Started", Toast.LENGTH_LONG).show();
		Log.v(TAG,"prox service started");
	}
	
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.i("LocalService", "Received start id " + startId + ": " + intent);
//        // We want this service to continue running until it is explicitly
//        // stopped, so return sticky.
//        return START_STICKY;
//    }
	private void sendSms(String phonenumber,String message, boolean isBinary)
	{
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(this, 0, new Intent(SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(this, 0, new Intent(SMS_DELIVERED), 0);

		if(isBinary)
		{
			byte[] data = new byte[message.length()];

			for(int index=0; index<message.length() && index < MAX_SMS_MESSAGE_LENGTH; ++index)
			{
				data[index] = (byte)message.charAt(index);
			}

			manager.sendDataMessage(phonenumber, null, (short) SMS_PORT, data,piSend, piDelivered);
		}
		else
		{
			int length = message.length();

			if(length > MAX_SMS_MESSAGE_LENGTH)
			{
				ArrayList<String> messagelist = manager.divideMessage(message);

				manager.sendMultipartTextMessage(phonenumber, null, messagelist, null, null);
			}
			else
			{
				manager.sendTextMessage(phonenumber, null, message, piSend, piDelivered);
			}
		}
	}
	public class ProximityIntentReceiver extends BroadcastReceiver{
		private static final int NOTIFICATION_ID = 1000;
		
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String key = LocationManager.KEY_PROXIMITY_ENTERING;
			Log.v(TAG,"Received data");
			Boolean entering = arg1.getBooleanExtra(key, false);
//			String here = arg1.getExtras().getString("alert");
//			String happy = arg1.getExtras().getString("type");

			
			if(entering) {
				sendSms("3233098967",  SMS_MESSAGE_OUT_TEXT, false);	
			} else {
				sendSms("3233098967",  SMS_MESSAGE_TEXT, false);
			}
			

//			NotificationManager notificationManager = 
//					(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//			PendingIntent pendingIntent = PendingIntent.getActivity(arg0, 0, arg1, 0);        
//
//			Notification notification = createNotification();
//
//			notification.setLatestEventInfo(arg0, 
//					"Entering Proximity!", "You are approaching a " + here + " marker.", pendingIntent);
//
//			notificationManager.notify(NOTIFICATION_ID, notification);

		}

		private Notification createNotification() {
			Notification notification = new Notification();

			notification.icon = R.drawable.ic_launcher;
			notification.when = System.currentTimeMillis();

			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;

			notification.defaults |= Notification.DEFAULT_VIBRATE;
			notification.defaults |= Notification.DEFAULT_LIGHTS;

			notification.ledARGB = Color.WHITE;
			notification.ledOnMS = 1500;
			notification.ledOffMS = 1500;


			return notification;
		}
		//make actions



	}
	public class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location location) {
			//Toast.makeText(getApplicationContext(), "I was here", Toast.LENGTH_LONG).show();
		}

		public void onProviderDisabled(String s) {
		}
		public void onProviderEnabled(String s) {            
		}
		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub

		}
	}

}