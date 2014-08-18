package com.yoneko.areyouthereyet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationClient.OnAddGeofencesResultListener;
import com.google.android.gms.location.LocationClient.OnRemoveGeofencesResultListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationStatusCodes;
import com.google.gson.Gson;
import com.yoneko.areyouthereyet.AddGeoFenceFragment.onDialogDismissed;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofenceList;
import com.yoneko.models.SimpleGeofenceStore;

public class MainActivity extends Activity implements  ConnectionCallbacks,
OnConnectionFailedListener,
OnAddGeofencesResultListener, LocationListener, onDialogDismissed, OnRemoveGeofencesResultListener {

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
	private GeofenceSampleReceiver mBroadcastReceiver;
	private Intent pendingIntent;
	private LocationRequest mLocationRequest;
	private SharedPreferences prefs;
	private ListView mainListView;
	private GeofenceAdapter adapter;
	private RelativeLayout loading_screen,main_screen;
	private List<SimpleGeofence> geoList;
	protected void onStop() {
		// Disconnecting the client invalidates it.
		Log.i(TAG,"Calling on Stop");
		if (mLocationClient.isConnected()) {
			Log.i(TAG,"stopping updates");
			/*
			 * Remove location updates for a listener.
			 * The current Activity is the listener, so
			 * the argument is "this".
			 */
			//dont need
			//			mLocationClient.removeLocationUpdates(this);
			//			mLocationClient.disconnect();
		} else {
			Log.i(TAG,"client is not connected()");
		}

		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mInProgress = false;
		mGeofencesToRemove = new ArrayList<String>();
		mBroadcastReceiver = new GeofenceSampleReceiver();
		mIntentFilter = new IntentFilter();
		setContentView(R.layout.activity_main);
		pendingIntent = new Intent(this,ReceiveTransitionsIntentService.class);	
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// Set the update interval to 5 seconds
		mLocationRequest.setInterval(5000);
		initViews();
		attachHandlers();
		geoList = getGeoFenceFromCache(getApplicationContext()).getGeoFences();
		adapter = new GeofenceAdapter(this, R.layout.list_geofence_row, geoList);
		mainListView.setAdapter(adapter);
		// Set the fastest update interval to 1 second
		//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		//				LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//		Location l = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		//		lm.addProximityAlert(34.054932 , -118.342929, 10, -1, getProximityPendingIntent("1"));
		//		lm.addProximityAlert(34.054932 , -118.342929, 10, -1, getProximityPendingIntent("2"));


		//				Intent svc = new Intent(this, ProximityService.class);
		//			    startService(svc);
		mGeofenceStorage = new SimpleGeofenceStore(this);

		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(TAG,
					"Google Play services is available.");
			// Continue
			addGeofences();

			//			prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			//			Map<String,?> keys = prefs.getAll();
			//
			//			for(Map.Entry<String,?> entry : keys.entrySet()){
			//				Log.d("map values",entry.getKey() + ": " + 
			//						entry.getValue().toString());            
			//			}
		}

		//show loading screen 2 seconds if its the initial launch 
		Handler handler = new Handler(); 
		handler.postDelayed(new Runnable() { 
			public void run() { 
				loading_screen.setVisibility(View.GONE);
				main_screen.setVisibility(View.VISIBLE); 
			} 
		}, 2000); 

	}

	private void attachHandlers() {
		mainListView.setLongClickable(true);
		mainListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView, View v,
					final int pos, long id) {
				
				final AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
				b.setIcon(android.R.drawable.ic_dialog_alert);
				b.setMessage("Are you sure?");
				//I want to use the right side button , which is the positive button apparently
				b.setPositiveButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
				b.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						final SimpleGeofence s = adapter.getItem(pos);
						mGeofencesToRemove.add(s.getId());
						adapter.remove(s);
						removeGeofences(mGeofencesToRemove);
						MainActivity.storeJSON(new SimpleGeofenceList(adapter.data), getApplicationContext());
						adapter.notifyDataSetChanged();
					}
				});

				b.show();
				return true;
			}

		});
		mainListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Launch maps activity to the location of the pin. pass the geofence data object so they can see it.  after the click the pin make them go back to the main activity
				Intent i = new Intent(MainActivity.this, MapActivity.class);
				i.putExtra("lon", geoList.get(position).getLongitude());
				i.putExtra("lat", geoList.get(position).getLatitude());
				i.putExtra("radius", geoList.get(position).getRadius());
				i.putExtra("editable", false);
				startActivity(i);
			}
		});		
	}

	private void initViews() {
		loading_screen = (RelativeLayout)findViewById(R.id.loading_screen);
		main_screen = (RelativeLayout)findViewById(R.id.main_screen);
		mainListView = (ListView)findViewById(R.id.mainListView);
	}

	public static String createGeoFenceId(double lat, double lon) {
		return lat + "|" + lon;
	}

	public static void storeJSON(SimpleGeofenceList list, Context context)
	{
		//clear out the stuff first
		SharedPreferences sp = context.getSharedPreferences(GEO_FENCES, MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.commit();
		Gson gson = new Gson();
		String jsonString = gson.toJson(list);
		spe.putString(GEO_FENCE_KEY_LIST, jsonString);
		spe.commit();
	}

	/*
	 * 
	 * TODO:
	 * 	Do we even need to show Long/lat editable fields? just hide it completely. 	 
	 * 	 need to create an alarm manager to be able to enable /disable when the geo fences are being sent (every friday at 4pm - 10 pm
	 */

	public static SimpleGeofenceList getGeoFenceFromCache(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences(GEO_FENCES, MODE_PRIVATE);
		String jsonString = sp.getString(GEO_FENCE_KEY_LIST, null);
		if (jsonString == null)
		{
			return new SimpleGeofenceList(new ArrayList<SimpleGeofence>());
		}
		Gson gson = new Gson();
		SimpleGeofenceList gfl = gson.fromJson(jsonString, SimpleGeofenceList.class);
		return gfl;
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode,data);
	}
	public void addGeoFenceClicked(View v) {
		FragmentManager manager = getFragmentManager();
		AddGeoFenceFragment agf = new AddGeoFenceFragment();
		agf.show(manager.beginTransaction(), "AddGeoFenceDialog");
	}

	public void clearGeoFenceClicked(View v) {
		SharedPreferences sp = this.getSharedPreferences(GEO_FENCES, MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.clear();
		spe.commit();
		Log.i("Reid", "clearing list");
		geoList = new ArrayList<SimpleGeofence>();
		adapter.clear();
		//remove geo fences
		removeGeofences(mTransitionPendingIntent);
		adapter.notifyDataSetChanged();
	}
	public void startMapsClicked(View v) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	}
	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private PendingIntent getProximityPendingIntent(String transitionType) {

		pendingIntent.putExtra("transitionType", transitionType);
		//		pendingIntent.putExtra("entering", LocationManager.KEY_PROXIMITY_ENTERING);
		return getTransitionPendingIntent();
	}
	//GEO fence is triggered
	private PendingIntent getTransitionPendingIntent() {
		// Create an explicit Intent

		/*
		 * Return the PendingIntent 
		 */


		//           If the PendingIntent already exists
		if (null != mTransitionPendingIntent) {

			Log.i(TAG, "Transition already exists");
			// Return the existing intent
			return mTransitionPendingIntent;

			// If no PendingIntent exists
		} else {

			// Create an Intent pointing to the IntentService

			//              Intent intent = new Intent(context, ReceiveTransitionsIntentService.class);
			/*
			 * Return a PendingIntent to start the IntentService.
			 * Always create a PendingIntent sent to Location Services
			 * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
			 * again updates the original. Otherwise, Location Services
			 * can't match the PendingIntent to requests made with it.
			 */
			Intent intent = new Intent("com.yoneko.areyouthereyet.ACTION_RECEIVE_GEOFENCE");
			return PendingIntent.getBroadcast(
					this,
					0,
					intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
		}

		//this currently works.. don't fuck i tup.
		//		return PendingIntent.getService(
		//				this,
		//				0,
		//				pendingIntent,
		//				PendingIntent.FLAG_CANCEL_CURRENT);
	}
	/**
	 * Start a request for geofence monitoring by calling
	 * LocationClient.connect().
	 */
	public void addGeofences() {
		// Start a request to add geofences
		mRequestType = REQUEST_TYPE.ADD;
		/*
		 * Test for Google Play services after setting the request type.
		 * If Google Play services isn't present, the proper request
		 * can be restarted.
		//        if (!servicesConnected()) {
		//            return;
		//        }
		/*
		 * Create a new location client object. Since the current
		 * activity class implements ConnectionCallbacks and
		 * OnConnectionFailedListener, pass the current activity object
		 * as the listener for both parameters
		 */
		mLocationClient = new LocationClient(this, this, this);


		if (!mInProgress) {
			// Indicate that a request is underway
			mInProgress = true;
			//If a request is not already underway        
			mLocationClient.connect();
			Log.v(TAG,"Add geo fence connected");

		} else {
			/*
			 * A request is already underway. You can handle
			 * this situation by disconnecting the client,
			 * re-setting the flag, and then re-trying the
			 * request.
			 */
			Log.v(TAG,"tryign to connect");
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// Request a connection from the client to Location Services
		Log.v(TAG,"connected");

		mLocationClient.requestLocationUpdates(mLocationRequest,getTransitionPendingIntent());
		Log.v(TAG,"Called get address task Request type: " + mRequestType);
		new GetAddressTask(this).execute(mLocationClient.getLastLocation());
		switch (mRequestType) {
		case ADD :
			
			// Get the PendingIntent for the request
			mTransitionPendingIntent =
					getTransitionPendingIntent();
			// Send a request to add the current geofences
			ArrayList<Geofence> geoFences = new ArrayList<Geofence>();
			SimpleGeofenceList geoFenceList = MainActivity.getGeoFenceFromCache(getApplicationContext());

			for(int i=0; i<geoFenceList.getGeoFences().size();i++) {
				//add items to geoFences;
				Log.v(TAG,"Added GEOFENCE");
				try {
				geoFences.add(geoFenceList.getGeoFences().get(i).toGeofence());
				} catch (IllegalArgumentException e) {
					Log.v(TAG,"illegal long/ lat combination not found...");	
				}
			}


			//****For crystals phone only for now
			//1 is enter 2 == exit
			//Crystal house = 33.885987,-118.310208
			//Reid house = 34.054840,-118.342908
			//			SimpleGeofence enter = new SimpleGeofence("1", 33.885987,-118.310208,RADIUS_METER,Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER, "","", "");
			//			SimpleGeofence exit = new SimpleGeofence("2", 33.885987,-118.310208,RADIUS_METER,Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_EXIT,"","", "");
			//			SimpleGeofence enterReid = new SimpleGeofence("3", 34.054840,-118.342908,RADIUS_METER,Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_ENTER,"","", "");
			//			SimpleGeofence exitReid = new SimpleGeofence("4", 34.054840,-118.342908,RADIUS_METER,Geofence.NEVER_EXPIRE, Geofence.GEOFENCE_TRANSITION_EXIT,"","", "");
			//			mGeofenceStorage.setGeofence("1",enter);
			//			mGeofenceStorage.setGeofence("2",exit);
			//			mGeofenceStorage.setGeofence("3",enterReid);
			//			mGeofenceStorage.setGeofence("4",exitReid);
			//			geoFences.add(enter.toGeofence());
			//			geoFences.add(exit.toGeofence());
			//			geoFences.add(enterReid.toGeofence());
			//			geoFences.add(exitReid.toGeofence());
			mLocationClient.addGeofences(
					geoFences, mTransitionPendingIntent, this);   

			//			Log.i(TAG,"Starting service");
			//			Intent svc = new Intent(this, ProximityService.class);
			//		    startService(svc);
			break;
		case REMOVE_INTENT :
			Log.i(TAG,"Removing all geo fences for reals on google");
			mLocationClient.removeGeofences(
					mTransitionPendingIntent, this);
			break;
		case REMOVE_LIST :
			Log.i(TAG,"Removing CERTAIN not all. geo fences for reals on google");
			mLocationClient.removeGeofences(
					mGeofencesToRemove, this);
			break;
		}
	}

	@Override
	public void onDisconnected() {
		mInProgress = false;
		// Destroy the current location client
		mLocationClient = null;
	}

	@Override
	public void onAddGeofencesResult(int statusCode, String[] geofenceRequestIds) {
		//  // If adding the geofences was successful

		if (LocationStatusCodes.SUCCESS == statusCode) {
			/*
			 * Handle successful addition of geofences here.
			 * You can send out a broadcast intent or update the UI.
			 * geofences into the Intent's extended data.
			 */
			Log.v(TAG,"GEO FENCE SUCCESS RADIUS is: " + String.valueOf(RADIUS_METER));
		} else {
			Log.v(TAG,"GEO FENCE FAILURE YOU SUCK ");
			// If adding the geofences failed
			/*
			 * Report errors here.
			 * You can log the error using Log.e() or update
			 * the UI.
			 */
		}
		// Turn off the in progress flag and disconnect the client

		mInProgress = false;

	}
	private boolean servicesConnected() {
		// Check that Google Play services is available
		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d("Geofence Detection",
					"Google Play services is available.");
			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			return false;
		}
	}

	public void removeGeofences(List<String> geofenceIds) {
		// If Google Play services is unavailable, exit
		// Record the type of removal request
		mRequestType = REQUEST_TYPE.REMOVE_LIST;
		/*
		 * Test for Google Play services after setting the request type.
		 * If Google Play services isn't present, the request can be
		 * restarted.
		 */
		if (!servicesConnected()) {
			return;
		}
		// Store the list of geofences to remove
		mGeofencesToRemove = geofenceIds;
		/*
		 * Create a new location client object. Since the current
		 * activity class implements ConnectionCallbacks and
		 * OnConnectionFailedListener, pass the current activity object
		 * as the listener for both parameters
		 */
		mLocationClient = new LocationClient(this, this, this);
		// If a request is not already underway
		if (!mInProgress) {
			// Indicate that a request is underway
			mInProgress = true;
			// Request a connection from the client to Location Services
			mLocationClient.connect();
		} else {
			/*
			 * A request is already underway. You can handle
			 * this situation by disconnecting the client,
			 * re-setting the flag, and then re-trying the
			 * request.
			 */
		}
	}
	public void removeGeofences(PendingIntent requestIntent) {
		// Record the type of removal request
		mRequestType = REQUEST_TYPE.REMOVE_INTENT;
		/*
		 * Test for Google Play services after setting the request type.
		 * If Google Play services isn't present, the request can be
		 * restarted.
		 */
		if (!servicesConnected()) {
			return;
		}
		// Store the PendingIntent
		mTransitionPendingIntent = requestIntent;
		/*
		 * Create a new location client object. Since the current
		 * activity class implements ConnectionCallbacks and
		 * OnConnectionFailedListener, pass the current activity object
		 * as the listener for both parameters
		 */
		mLocationClient = new LocationClient(this, this, this);
		// If a request is not already underway
		if (!mInProgress) {
			// Indicate that a request is underway
			mInProgress = true;
			// Request a connection from the client to Location Services
			mLocationClient.connect();
		} else {
			/*
			 * A request is already underway. You can handle
			 * this situation by disconnecting the client,
			 * re-setting the flag, and then re-trying the
			 * request.
			 */
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {

		// Turn off the request flag
		mInProgress = false;
		/*
		 * If the error has a resolution, start a Google Play services
		 * activity to resolve it.
		 */
		//TODO: finish this crap
		//        if (connectionResult.hasResolution()) {
		//            try {
		//                connectionResult.startResolutionForResult(
		//                        this,
		//                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
		//            } catch (SendIntentException e) {
		//                // Log the error
		//                e.printStackTrace();
		//            }
		//        // If no resolution is available, display an error dialog
		//        } else {
		//            // Get the error code
		//            int errorCode = connectionResult.getErrorCode();
		//            // Get the error dialog from Google Play services
		//            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
		//                    errorCode,
		//                    this,
		//                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
		//            // If Google Play services can provide an error dialog
		//            if (errorDialog != null) {
		//                // Create a new DialogFragment for the error dialog
		//                ErrorDialogFragment errorFragment =
		//                        new ErrorDialogFragment();
		//                // Set the dialog in the DialogFragment
		//                errorFragment.setDialog(errorDialog);
		//                // Show the error dialog in the DialogFragment
		//                errorFragment.show(
		//                        getSupportFragmentManager(),
		//                        "Geofence Detection");
		//            }
		//        }
	}
	public class GeofenceSampleReceiver extends BroadcastReceiver {
		/*
		 * Define the required method for broadcast receivers
		 * This method is invoked when a broadcast Intent triggers the receiver
		 */
		@Override
		public void onReceive(Context context, Intent intent) {

			// Check the action code and determine what to do
			String action = intent.getAction();

			Log.v(TAG,"received");
			// Intent contains information about errors in adding or removing geofences
			Toast.makeText(context, "something triggered" + action, Toast.LENGTH_LONG).show();
		}

		/**
		 * If you want to display a UI message about adding or removing geofences, put it here.
		 *
		 * @param context A Context for this component
		 * @param intent The received broadcast Intent
		 */
		private void handleGeofenceStatus(Context context, Intent intent) {

		}

		/**
		 * Report geofence transitions to the UI
		 *
		 * @param context A Context for this component
		 * @param intent The Intent containing the transition
		 */
		private void handleGeofenceTransition(Context context, Intent intent) {
			/*
			 * If you want to change the UI when a transition occurs, put the code
			 * here. The current design of the app uses a notification to inform the
			 * user that a transition has occurred.
			 */
			Log.v(TAG,"geofence transitioned");
			Toast.makeText(context, "something transitioned!!", Toast.LENGTH_LONG).show();
		}

		/**
		 * Report addition or removal errors to the UI, using a Toast
		 *
		 * @param intent A broadcast Intent sent by ReceiveTransitionsIntentService
		 */
		private void handleGeofenceError(Context context, Intent intent) {

			Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public void onLocationChanged(Location arg0) {
		Log.v(TAG,"Location CHanged: " + String.valueOf(arg0.getLongitude()) +","+String.valueOf(arg0.getLatitude())) 	;

	}

	private class GetAddressTask extends
	AsyncTask<Location, Void, String> {
		Context mContext;
		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}
		/**
		 * Get a Geocoder instance, get the latitude and longitude
		 * look up the address, and return it
		 *
		 * @params params One or more Location objects
		 * @return A string containing the address of the current
		 * location, or an empty string if no address can be found,
		 * or an error message
		 */
		@Override
		protected String doInBackground(Location... params) {
			Log.v(TAG,"Do in background");
			Geocoder geocoder =
					new Geocoder(mContext, Locale.getDefault());
			// Get the current location from the input parameter list
			Location loc = params[0];
			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				/*
				 * Return 1 address.
				 */
				if(loc != null) {
					addresses = geocoder.getFromLocation(loc.getLatitude(),
							loc.getLongitude(), 1);
				}
			} catch (IOException e1) {
				Log.e("LocationSampleActivity",
						"IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				// Error message to post in the log
				String errorString = "Illegal arguments " +
						Double.toString(loc.getLatitude()) +
						" , " +
						Double.toString(loc.getLongitude()) +
						" passed to address service";
				Log.e("LocationSampleActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}
			// If the reverse geocode returned an address
			if (addresses != null && addresses.size() > 0) {
				// Get the first address
				Address address = addresses.get(0);
				/*
				 * Format the first line of address (if available),
				 * city, and country name.
				 */
				String addressText = String.format(
						"%s, %s, %s",
						// If there's a street address, add it
						address.getMaxAddressLineIndex() > 0 ?
								address.getAddressLine(0) : "",
								// Locality is usually a city
								address.getLocality(),
								// The country of the address
								address.getCountryName());
				// Return the text
				return addressText;
			} else {
				return "No address found";
			}
		}
		@Override
		protected void onPostExecute(String address) {
			Log.v(TAG, " Address is: " + address);
			Toast.makeText(getApplicationContext(), address, Toast.LENGTH_LONG);
		}
	}
	@Override
	public void dialogDismissed() {
		Log.i(TAG,"Dialog dismissed called");
		geoList = getGeoFenceFromCache(this).getGeoFences();
		adapter.clear();
		adapter.addAll(geoList);
		adapter.notifyDataSetChanged();
		//		adapter.notifyDataSetChanged();
	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int statusCode,
			PendingIntent pendingIntent) {

		// If removing the geofences was successful
		if (statusCode == LocationStatusCodes.SUCCESS) {
			/*
			 * Handle successful removal of geofences here.
			 * You can send out a broadcast intent or update the UI.
			 * geofences into the Intent's extended data.
			 */
		} else {
			// If adding the geocodes failed
			/*
			 * Report errors here.
			 * You can log the error using Log.e() or update
			 * the UI.
			 */
		}
		/*
		 * Disconnect the location client regardless of the
		 * request status, and indicate that a request is no
		 * longer in progress
		 */
		mInProgress = false;
		mLocationClient.disconnect();
	}

	@Override
    public void onRemoveGeofencesByRequestIdsResult(
            int statusCode, String[] geofenceRequestIds) {
        // If removing the geocodes was successful
        if (LocationStatusCodes.SUCCESS == statusCode) {
            /*
             * Handle successful removal of geofences here.
             * You can send out a broadcast intent or update the UI.
             * geofences into the Intent's extended data.
             */
        } else {
        // If removing the geofences failed
            /*
             * Report errors here.
             * You can log the error using Log.e() or update
             * the UI.
             */
        }
        // Indicate that a request is no longer in progress
        mInProgress = false;
        // Disconnect the location client
        mLocationClient.disconnect();
    }
}
