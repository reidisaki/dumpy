package com.yoneko.areyouthereyet.update;

import java.util.ArrayList;
import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;

public class ReceiveTransitionsIntentService extends IntentService {
	public static String TAG = "Reid";
	public static String SMS_SENT = "ConfirmSentActivity";
	public static String SMS_DELIVERED = "DevliveredActivty";
	public static int MAX_SMS_MESSAGE_LENGTH = 160;
	public static int SMS_PORT = 21;
	public static String SMS_NUMBER = "3233098967";
	//Crystals - public static String SMS_NUMBER = "3104647957";
	public static String SMS_MESSAGE_TEXT = "Hi Baby, I made it home safely! ";// + String.valueOf(MainActivity.RADIUS_METER);
	public static String SMS_MESSAGE_OUT_TEXT = "Hi Baby, I'm leaving my house now!!!! ";// + String.valueOf(MainActivity.RADIUS_METER);
	/**
	 * Sets an identifier for the service
	 */
	public ReceiveTransitionsIntentService() {		
		super("ReceiveTransitionsIntentService");
//		Log.v(TAG,"Receive Transion service is running");		
	}
	/**
	 * Handles incoming intents
	 *@param intent The Intent sent by Location Services. This
	 * Intent is provided
	 * to Location Services (inside a PendingIntent) when you call
	 * addGeofences()
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		// First check for errors
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		String locationString =  "http://maps.google.com/?q=" + String.valueOf(latitude) + "," +String.valueOf(longitude) ;

//		Log.v(TAG,"handling intent");
		//		if(intent.getExtras().getString("transitionType").equals("1")) {
		//			sendSms("3233098967",SMS_MESSAGE_TEXT + locationString + "  http://maps.google.com/?q=34.054932,-118.342929", false);
		//				
		//		} 
		//		if(intent.getExtras().getString("transitionType").equals("2")) {
		//			sendSms("3233098967",SMS_MESSAGE_OUT_TEXT + locationString , false);	
		//		}
		if (LocationClient.hasError(intent)) {
			Log.v(TAG,"onHandleIntent Error");
			// Get the error code with a static method
			int errorCode = LocationClient.getErrorCode(intent);
			// Log the error
			Log.e("ReceiveTransitionsIntentService",
					"Location Services error: " +
							Integer.toString(errorCode));
			/*
			 * You can also send the error code to an Activity or
			 * Fragment with a broadcast Intent
			 */
			/*
			 * If there's no error, get the transition type and the IDs
			 * of the geofence or geofences that triggered the transition
			 */
		} else {
//			Log.v(TAG,"on handle intent");
			// Get the type of transition (entry or exit)
			int transitionType =
					LocationClient.getGeofenceTransition(intent);
//			Log.v(TAG,"Transition type = " + String.valueOf(transitionType));
			// Test that a valid transition was reported
			if (
					(transitionType == Geofence.GEOFENCE_TRANSITION_ENTER)
					||
					(transitionType == Geofence.GEOFENCE_TRANSITION_EXIT)
					) {
				Log.i(TAG,"Inside if statement");
				//getListOfGeoFences here
				List <Geofence> triggerList = //new ArrayList<Geofence>(); 
						LocationClient.getTriggeringGeofences(intent);

				String[] triggerIds = new String[triggerList.size()];

				for (int i = 0; i < triggerIds.length; i++) {
					// Store the Id of each geofence
					Log.i(TAG,"on trigger Ids");
					if(triggerList.get(i).getRequestId().equals("1")) {
						sendSms(SMS_NUMBER,SMS_MESSAGE_TEXT, false);
						Log.v(TAG,"Success sending in");		
					} 
					if(triggerList.get(i).getRequestId().equals("2")) {
						sendSms(SMS_NUMBER,SMS_MESSAGE_OUT_TEXT , false);
						Log.v(TAG,"Success sending out");		
					}
					if(triggerList.get(i).getRequestId().equals("3")) {
						sendSms(SMS_NUMBER,"Hi baby I'm at your house, finding parking!!!", false);
						Log.v(TAG,"Success sending out Triggered entered at Reid's house");		
					}				
					if(triggerList.get(i).getRequestId().equals("4")) {
						sendSms(SMS_NUMBER,"Leaving your house!! :( ", false);
						Log.v(TAG,"Success sending out leaving your house");		
					}				
					triggerIds[i] = triggerList.get(i).getRequestId();
				}



				/*
				 * At this point, you can store the IDs for further use
				 * display them, or display the details associated with
				 * them.
				 */
			}
			// An invalid transition was reported
		} 
	}

//	@Override
//	public int onStartCommand(Intent intent, int flags, int startId) {
//		// We want this service to continue running until it is explicitly
//		// stopped, so return sticky.
//		return START_STICKY;
//	}

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
				Log.i(TAG,"Sending text message: "  + phonenumber);
				manager.sendTextMessage(phonenumber, null, message, null, null);
			}
		}
	}
}