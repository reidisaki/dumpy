package com.yoneko.areyouthereyet;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.yoneko.areyouthereyet.AddGeoFenceFragment.onDialogDismissed;

public class MapActivity extends Activity implements OnMapLongClickListener, OnMarkerClickListener, OnItemSelectedListener, onDialogDismissed {

	GoogleMap mMap;
	Marker currentMarker = null;
	Circle myCircle = null;
	MarkerOptions markerOptions;
	AdView adView;
	EditText searchEdit;
	Button searchButton;
	FragmentManager fm;
	Fragment addGeofenceFragment;
	RelativeLayout map_detail_layout;
	SlidingUpPanelLayout slidePanelLayout;
	LatLng latLng = null;
	float EXPANDED_PERCENT =  .7f;
	boolean editable = true;
	public static String tag = "Reid";
	int selectedRadius = 100;
	Spinner spinner;     
	public void onItemSelected(AdapterView<?> parent, View view, 
			int pos, long id) {
		switch(pos) {
		case 1: 
			selectedRadius = 5;
			break;
		case 2: 
			selectedRadius = 20;
			break;
		case 3: 
			selectedRadius = 50;
			break;
		case 4: 
			selectedRadius = 100;
			break;
		case 5: 
			selectedRadius = 150;
			break;
		case 6: 
			selectedRadius = 200;
			break;
		case 7: 
			selectedRadius = 500;
			break;
		default: 
			selectedRadius = 100;
			break;
		}
		o("selected radius: " + selectedRadius);
		if(latLng != null) {
			createRadiusCircle(latLng);
		}
	}

	public void o (String s) {
		Log.i(tag,"output s: " + s);
	}
	public void onNothingSelected(AdapterView<?> parent) {
	}

	public int getSelectedPositionInSpinnerByValue(int selectedValue) {
		int retvalue;
		switch(selectedValue) {
		case 5: 
			retvalue = 1;
			break;
		case 20: 
			retvalue = 2;
			break;
		case 50: 
			retvalue = 3;
			break;
		case 100: 
			retvalue = 4;
			break;
		case 150: 
			retvalue = 5;
			break;
		case 200: 
			retvalue = 6;
			break;
		case 500: 
			retvalue = 7;
			break;
		default: 
			retvalue = 4;
			break;
		}
		return retvalue;

	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		.addTestDevice("deviceid")
		.build();
		adView = (AdView)findViewById(R.id.adView);
		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		
		mMap.setMyLocationEnabled(true);
		Criteria criteria = new Criteria();
		LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if( location != null) {
			mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14.0f) );
		}
		Bundle b = getIntent().getExtras();
		if(b != null){
			editable = b.getBoolean("editable",true);
			if(!editable) {
				selectedRadius = (int) b.getFloat("radius");
				LatLng p = new LatLng(b.getDouble("lat"),b.getDouble("lon"));
				onMapLongClick(p);
			}
		}
		fm = getFragmentManager();
		
		initViews();
		setListeners();
		
		
		fm.beginTransaction()
        .hide(addGeofenceFragment)
        .commit();
		//		CameraPosition cameraPosition = new CameraPosition.Builder().target(
		//                new LatLng(-118.256, 33.5847)).zoom(15).build();
		//
		//		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));       
		//		
	}

	private void initViews() {
		searchEdit =  (EditText)findViewById(R.id.location_edit);
		searchButton = (Button)findViewById(R.id.btn_find);
		spinner = (Spinner) findViewById(R.id.radius_spinner);
		slidePanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		
		addGeofenceFragment = (Fragment)getFragmentManager().findFragmentById(R.id.fragement_add_geo_fence);
		

		if(!editable) {
			searchEdit.setFocusable(false); searchEdit.setClickable(false);
			searchButton.setFocusable(false); searchButton.setClickable(false);
			spinner.setFocusable(false); spinner.setClickable(false);
			spinner.setSelection(getSelectedPositionInSpinnerByValue(selectedRadius));
		}


	}
	private void setListeners() {
		if(editable) {
			spinner.setOnItemSelectedListener(this);
			mMap.setOnMapLongClickListener(this);
			mMap.setOnMarkerClickListener(this);
			//		mMap.setOnMapClickListener(this);
			slidePanelLayout.setPanelSlideListener(new PanelSlideListener() {
				
				@Override
				public void onPanelSlide(View panel, float slideOffset) {
					Log.i("Reid","panel is sliding"); 
				}
				
				@Override
				public void onPanelHidden(View panel) {
					Log.i("Reid","panel is hidden");
				}
				
				@Override
				public void onPanelExpanded(View panel) {
					Log.i("Reid","panel is expanded");
					if(latLng != null) {
						mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latLng.latitude - .007, latLng.longitude)));
					}
					fm.beginTransaction()
			        .show(addGeofenceFragment)
			        .commit();
				}
				
				@Override
				public void onPanelCollapsed(View panel) {
					Log.i("Reid","panel is collapsed");
					if(latLng != null) {
						mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
					}
					fm.beginTransaction()
			        .hide(addGeofenceFragment)
			        .commit();
				}
				
				@Override
				public void onPanelAnchored(View panel) {
					Log.i("Reid","panel is anchored");
				}
			});
			searchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Getting user input location
					String location = searchEdit.getText().toString();

					if(location!=null && !location.equals("")){
						new GeocoderTask().execute(location);
					}				
				}
			});
		}
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				o("Clicked the info window");
				if(!editable) {
					finish();
				} else {
					handlePoint(marker);
				}
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
	public void handlePoint(Marker marker) {
		Intent resultIntent = new Intent();
		Bundle b = new Bundle();
		b.putDouble("lon", marker.getPosition().longitude);
		b.putDouble("lat", marker.getPosition().latitude);
		b.putInt("radius", selectedRadius);
		resultIntent.putExtras(b);
		setResult(AddGeoFenceFragment.MAP_RESULT_CODE, resultIntent);
		finish();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		o("Clicked marker");
		handlePoint(marker);
		return false;
	}

	@Override    
	public void onMapLongClick(LatLng point) {
		latLng = point;
		createRadiusCircle(point);
		//		MarkerOptions mo = new MarkerOptions()
		//		.position(point)
		//		.title( point.latitude + ", " + point.longitude)           
		//		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		//		mMap.addMarker(mo).showInfoWindow();
		//		mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
	}

	public void addMarker(LatLng latLng) {
		
		String title ="not set yet";
		Geocoder geo = new Geocoder(getApplicationContext());
		try {
			List<Address> addressList = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
			if(addressList.size() > 0) {
				Address address = addressList.get(0);
				title = address.getFeatureName() == null ? address.getAddressLine(0) + " \n" + address.getLocality() + "," + address.getPostalCode() : address.getFeatureName();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MarkerOptions mo = new MarkerOptions()
		.position(latLng)
		.title(title)//latLng.latitude + ", " + latLng.longitude)           
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		if(currentMarker != null) {
			currentMarker.remove();
		}
		currentMarker = mMap.addMarker(mo);
		mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
	}
	//	@Override
	//	public void onMapClick(LatLng point) {
	//		CircleOptions circleOptions = new CircleOptions()
	//		.center(point)   //set center
	//		.radius(500)   //set radius in meters
	//		.fillColor(Color.TRANSPARENT)  //default
	//		.strokeColor(Color.MAGENTA)
	//		.strokeWidth(5);
	//		Log.i(TAG,"map clicked");
	//		myCircle = mMap.addCircle(circleOptions);
	//	}
	public void createRadiusCircle(LatLng latLng) {
		if(myCircle != null) {
			myCircle.remove();
		}
		//		mMap.clear();
		addMarker(latLng);
		currentMarker.showInfoWindow();
		o("selected radius in createRadiusCircle " + selectedRadius);
		CircleOptions circleOptions = new CircleOptions()
		.center(latLng)   //set center
		.radius(selectedRadius)   //set radius in meters  make this configurable
		.fillColor(0x408A2BE2) 
		.strokeColor(Color.MAGENTA)
		.strokeWidth(5);

		myCircle = mMap.addCircle(circleOptions);
	}
	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;

			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(locationName[0], 3);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return addresses;
		}

		@Override
		protected void onPostExecute(List<Address> addresses) {

			if(addresses==null || addresses.size()==0){
				Toast.makeText(getBaseContext(), "No Location found", Toast.LENGTH_SHORT).show();
			}

			// Clears all the existing markers on the map

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size();i++){

				Address address = (Address) addresses.get(i);

				// Creating an instance of GeoPoint, to display in Google Map
				latLng = new LatLng(address.getLatitude(), address.getLongitude());

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getCountryName());
				markerOptions = new MarkerOptions();
				markerOptions.position(latLng);
				markerOptions.title(addressText);
				createRadiusCircle(latLng);
				// Locate the first location
				if(i==0) {
					mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				}
			}
		}
	}
	@Override
	public void dialogDismissed() {
		
	}
}
