package com.yoneko.areyouthereyet.update;
import com.yoneko.areyouthereyet.update.debug.R;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.yoneko.models.PhoneContact;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofence.fencetype;
import com.yoneko.models.SimpleGeofenceList;
public class GeoFenceReceiver extends BroadcastReceiver {
	Context context;
	Intent broadcastIntent = new Intent();
	public static String TAG = "Reid";
	static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs
	static final long TIME_THRESHOLD_TO_SEND_MESSAGE=15;//Time threshold to send the same alert in inutes
	private SimpleGeofenceList geoFenceList;
	private List<SimpleGeofence> simpleList;
	private float MAX_ACCURACY_ERROR = 250f;
	public static String SMS_SENT = "ConfirmSentActivity";
	public static String SMS_DELIVERED = "DevliveredActivty";
	public static int MAX_SMS_MESSAGE_LENGTH = 160;
	public static int SMS_PORT = 21;
	public static int ACCURACY_METER_THRESHOLD = 150;
	public static String SMS_NUMBER = "3233098967";
	
	//Crystals - public static String SMS_NUMBER = "3104647957";
	public static String SMS_MESSAGE_TEXT = "Hi Baby, I made it home safely! ";// + String.valueOf(MainActivity.RADIUS_METER);
	public static String SMS_MESSAGE_OUT_TEXT = "Hi Baby, I'm leaving my house now!!!! ";// + String.valueOf(MainActivity.RADIUS_METER);
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		Log.i("yoneko","in on Receive");
		//start on boot
		Log.i("Reid", "device restart: " + intent.getAction());
		if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equalsIgnoreCase(Intent.ACTION_REBOOT)){
            
			Intent i = new Intent(context, MapActivity.class);
            i.putExtra(MapActivity.REREGISTER_GEOFENCE, true); 
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
		//		broadcastIntent.addCategory(GeofenceUtils.CATEGORY_LOCATION_SERVICES);

				if (LocationClient.hasError(intent)) {
//					handleError(intent);
					 int errorCode = LocationClient.getErrorCode(intent);

				        // Get the error message
				        

				        // Log the error
				        Log.i("Reid", "FAILED!! Error code: " + errorCode);
				} else {
//					handleEnterExit(intent);
				}
		//	}

		LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(location == null) {
			return;
			//disabled gps.
		}
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
			
		    Map<String, String> errorParams = new HashMap<String, String>();
		    errorParams.put("ErrorCode", ""+errorCode); 
	        FlurryAgent.logEvent("Error occured in onReceive GeoFence Receiver", errorParams);
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
//				Log.i(TAG,"Inside if statement");
				//getListOfGeoFences here
				List <Geofence> triggerList = //new ArrayList<Geofence>(); 
						LocationClient.getTriggeringGeofences(intent);

				String[] triggerIds = new String[triggerList.size()];
				Log.i("Reid","all good trying to send message");
				geoFenceList = MapActivity.getGeoFenceFromCache(context);
				simpleList = geoFenceList.getGeoFences();
				String debugMessage = "acc: " + location.getAccuracy() + "lat: " + location.getLatitude() + " lon: " 
				+ location.getLongitude() + "  http://maps.google.com/?q=" + location.getLatitude() + "," + location.getLongitude();
				
				for (int i = 0; i < triggerIds.length; i++) {
					SimpleGeofence g  = getSimpleGeofence(simpleList,triggerList.get(i));
					Log.i("Reid","is geofence null? : " + (g == null));
					if(g == null) {
						return;
					}
					Log.i("Reid","geofecne is active?" + g.isActive());
					String realCoordinates = "  real lat:" + g.getLatitude() + "," + g.getLongitude(); 
					if(g.isShouldSend() && location.getAccuracy() <= MAX_ACCURACY_ERROR  && g.isActive()) {
						Log.i("Reid","should send");
						if(g.getPhoneContacts() != null) {
							for(PhoneContact p : g.getPhoneContacts()) {
								sendSms(p.getNumber(),g.getMessage(), false);
								
								//if the message is a one time send then set it to inactive after you've sent it
								if(g.getFenceType() == fencetype.ONE_TIME) {
									g.setActive(false);
								}
								simpleList.set(i, g);
							}
						} else {
							//old version using just one contact.. don't want to break it.
							sendSms(g.getEmailPhone(),g.getMessage(), false);
						}
						//DEBUG STATEMENT - Reid Isaki
//						sendSms("3233098967",g.getMessage() + realCoordinates + debugMessage, false);	
//					sendSms("4152601156",g.getMessage() + debugMessage, false);
					}

					// Store the Id of each geofence
//					Old way of hard coded sending to Crystal
//					if(triggerList.get(i).getRequestId().equals("1")) {
//						sendSms(SMS_NUMBER,SMS_MESSAGE_TEXT, false);
//						Log.v(TAG,"Success sending in");		
//					} 
//					if(triggerList.get(i).getRequestId().equals("2")) {
//						sendSms(SMS_NUMBER,SMS_MESSAGE_OUT_TEXT , false);
//						Log.v(TAG,"Success sending out");		
//					}
//					if(triggerList.get(i).getRequestId().equals("3")) {
//						sendSms(SMS_NUMBER,"Hi baby I'm at your house, finding parking!!!", false);
//
					/*I've been thinking about how this whole location thing should work.
					 * Before I send a text message I check the time threshold, if less than 5 mintues have passed, then I should check my current location
					 *  if the distance between the center of the geo fence and my location is greater than 250m + the radius of the geofence itself then don't send it. 
					 *  if more than 5 minutes passes, I will set the time interval to empty? 
					 *  We need to see how often the gps bounces around, and by how much does it bounce. You should be able to find that out tonight.
					 *  
					 * */ 
//						Log.v(TAG,"Success sending out Triggered entered at Reid's house -- Receiver");		
//					}				
//					if(triggerList.get(i).getRequestId().equals("4")) {
//						sendSms(SMS_NUMBER,"Leaving your house!! :( ", false);
//						Log.v(TAG,"Success sending out leaving your house -- Receiver");		
//					}				
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

	private void sendSms(String phonenumber,String message, boolean isBinary)
	{
		SmsManager manager = SmsManager.getDefault();

		PendingIntent piSend = PendingIntent.getBroadcast(context, 0, new Intent(SMS_SENT), 0);
		PendingIntent piDelivered = PendingIntent.getBroadcast(context, 0, new Intent(SMS_DELIVERED), 0);
		Log.i("Reid","sending a text to : " + phonenumber);
		Toast.makeText(context, "SENDING A TEXT " + message + " phone number: " + phonenumber,
				Toast.LENGTH_LONG).show();
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
//				Log.i(TAG,"Sending texts are CURRENTLY DISABLED Sending text message: "  + phonenumber);
				Log.i(TAG,"Sending texts are SSENDING!!:  "  + phonenumber);
				if(phonenumber != null && message != null && phonenumber != "" && message != "") {
					manager.sendTextMessage(phonenumber, null, message + context.getResources().getString(R.string.short_there_yet_link), null, null);
				}
			}
		}
	}
	private SimpleGeofence getSimpleGeofence (List<SimpleGeofence> list, Geofence g) {
		SimpleGeofence retFence = null;
		
		for(SimpleGeofence geo : list) {
			geo.setShouldSend(false);
			long currentTime = new Date().getTime();
			if(geo.getId().equals(g.getRequestId())) {
				Log.i("Reid","Found geofence line 246");
				//if the dateLastSent is -1 then you set it to current date
				//if the dateLastSent is > 0 , add 15 minutes to the date then compare the times to dateTime.now. 
				//if the date lastsent + threshold minutes is > dateTime now then you should send it
				if(geo.getLastSent() == -1 || geo.getLastSent() + (TIME_THRESHOLD_TO_SEND_MESSAGE * ONE_MINUTE_IN_MILLIS) <= currentTime) {
					geo.setLastSent(currentTime);
					geo.setShouldSend(true);
				} 
				retFence = geo;
			}
		}
		geoFenceList.setGeofences(list);
		MapActivity.storeJSON(geoFenceList, context);
		return retFence;		
	}
	
}