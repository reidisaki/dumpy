package com.yoneko.areyouthereyet.update;

import java.util.ArrayList;
import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.yoneko.models.SimpleGeofence;

public class SafetyService extends Service implements ConnectionCallbacks, OnConnectionFailedListener {

	LocationRequest mLocationRequest;
	GoogleApiClient mGoogleApiClient;
	boolean  mReRegister = false;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		Log.i("ty1","Service on Bind");
		return mBinder;
	}

	private final IBinder mBinder = new LocalBinder();
	private PendingIntent mTransitionPendingIntent;
	private List<SimpleGeofence> mSimpleGeoFenceList;

    public class LocalBinder extends Binder {
    	SafetyService getService() {
            return SafetyService.this;
        }
    }
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("ty","OnStartCommand action: " + (intent != null ? intent.getAction() : "intent is null"));
		if ((intent != null && intent.getAction() != null) && (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
				|| intent.getAction().equalsIgnoreCase(Intent.ACTION_REBOOT))){
			Log.i("ty","action reboot or boot completed");
		 mReRegister = true;
		}
		
		if(intent != null && intent.getExtras() != null) {
		Log.i("ty", intent.getExtras().getString("test", "default"));
		}
		//do code in here
		init();
		return Service.START_STICKY;
	}
	private void init() {
		mSimpleGeoFenceList = MapActivity.getGeoFenceFromCache(this).getGeoFences();
		mLocationRequest = LocationRequest.create();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addApi(LocationServices.API).addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).build();
		mGoogleApiClient.connect();
		
		
	}

	@Override
	public void onDestroy() {
		Log.i("ty","Killing safety service sending broadcast");
		//todo: remove this to restart the app if you dont want it ever to be killed
		Intent intent = new Intent("YouWillNeverKillMe");
		intent.putExtra("test", "testing value");
		sendBroadcast(intent);
		super.onDestroy();	
	}	
	
	public void removeGeofences( PendingIntent requestIntent) {
		PendingResult<Status> result = LocationServices.GeofencingApi.removeGeofences(mGoogleApiClient,
				requestIntent);
		result.setResultCallback(new ResultCallback<Status>() {
			@Override
			public void onResult(Status result) {
				if(result.isSuccess()) {
						addGeofences();
					Log.i("ty","removed fences successfully");
				} else {
					Log.i("ty","removed fences UNsuccessfully");
				}

			}
		});
	}
	public void addGeofences(){
		ArrayList<Geofence> geoFences = new ArrayList<Geofence>();
		mTransitionPendingIntent = getTransitionPendingIntent();
		// for(int i=0; i<mSimpleGeoFenceList.size();i++) {
		// add items to geoFences;
		try {
			
				for (SimpleGeofence g : mSimpleGeoFenceList) {
					Log.i("ty",
							"geofence number registerd:"
									+ g.getEmailPhone() + " name: " + g.getId() +  " title: " + g.getTitle());
					geoFences.add(g.toGeofence());
				}
				//TODO: check if this is valid, we want to restart geo fences frequently since they get dropped serverside frequently
//				reRegisterGeoFences = false;
			
		} catch (IllegalArgumentException e) {
			
		}
		//			mInProgress = true;
		if(geoFences.size() > 0) {
			GeofencingRequest fenceRequest = new GeofencingRequest.Builder().addGeofences(geoFences).build();
			PendingResult<Status> result = LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,fenceRequest,mTransitionPendingIntent);

			result.setResultCallback(new ResultCallback<Status>() {
				@Override
				public void onResult(Status result) {
					if(result.isSuccess()) {
						//TODO: this isnt really doing anything.
						Log.i("ty","added fences successfully");
						Toast.makeText(getApplicationContext(), "SUCCESS, added fence on Restart",Toast.LENGTH_SHORT).show();
					} else {
						//						TODO:
						//there was an error relay this to the user..
						Toast.makeText(getApplicationContext(), "ERROR, please try again:" + result.getStatusMessage(),Toast.LENGTH_SHORT).show();
					}

				}
			});
		}
	}

	
		private PendingIntent getTransitionPendingIntent() {

			// Create an Intent pointing to the IntentService

			// Intent intent = new Intent(context,
			// ReceiveTransitionsIntentService.class);
			/*
			 * Return a PendingIntent to start the IntentService. Always create a
			 * PendingIntent sent to Location Services with FLAG_UPDATE_CURRENT, so
			 * that sending the PendingIntent again updates the original. Otherwise,
			 * Location Services can't match the PendingIntent to requests made with
			 * it.
			 */

			// If the PendingIntent already exists
			if (null != mTransitionPendingIntent) {

				// Return the existing intent
				return mTransitionPendingIntent;

				// If no PendingIntent exists
			} else {
				Intent intent = new Intent(
						"com.yoneko.areyouthereyet.ACTION_RECEIVE_GEOFENCE");
				return PendingIntent.getBroadcast(this, 0, intent,
						PendingIntent.FLAG_UPDATE_CURRENT);
			}
	}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			Log.i("ty","onconnectionFailed");
		}

		@Override
		public void onConnected(Bundle connectionHint) {
			if(mReRegister) {
				Log.i("ty","Reregistering geofences");
				removeGeofences(getTransitionPendingIntent());
				mReRegister = false;
			}
			
		}

		@Override
		public void onConnectionSuspended(int cause) {
			
		}

		
}
