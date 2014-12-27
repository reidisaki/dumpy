package com.yoneko.areyouthereyet.update;

import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofenceStore;

public class MainActivity extends Activity  {

	private static final long SECONDS_PER_HOUR = 60;
	private static final long MILLISECONDS_PER_SECOND = 1000;
	private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
	public static  String GEO_FENCES = "geofences";
	public static final int DIALOG_FRAGMENT = 100;
	public static String GEO_FENCE_KEY_LIST = "geoFenceList";
	private IntentFilter mIntentFilter;
	public static final float RADIUS_METER = 130;
	private static String TAG = "Reid";
	private static final long GEOFENCE_EXPIRATION_TIME =
			GEOFENCE_EXPIRATION_IN_HOURS *
			SECONDS_PER_HOUR *
			MILLISECONDS_PER_SECOND;

	// Holds the location client
	private LocationClient mLocationClient;
	// Stores the PendingIntent used to request geofence monitoring
	private PendingIntent mTransitionPendingIntent;

	// Defines the allowable request types.
	public enum REQUEST_TYPE {ADD, REMOVE_INTENT, REMOVE_LIST };
	public enum TRANSIENT_TYPE { TRANSIENT_ENTER, TRANSIENT_EXIT};
	public List<String> mGeofencesToRemove;
	private REQUEST_TYPE mRequestType;
	// Flag that indicates if a request is underway.
	private boolean mInProgress;
	private SimpleGeofenceStore mGeofenceStorage;
	private Intent pendingIntent;
	private LocationRequest mLocationRequest;
	private SharedPreferences prefs;
	private ListView mainListView;
	private GeofenceAdapter adapter;
	private RelativeLayout loading_screen,main_screen;
	private List<SimpleGeofence> geoList;
	private SlidingUpPanelLayout slide;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		}

}
