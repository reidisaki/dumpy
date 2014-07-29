package com.yoneko.areyouthereyet;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
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
import android.widget.Spinner;
import android.widget.Toast;

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

public class MapActivity extends Activity implements OnMapLongClickListener, OnMarkerClickListener, OnItemSelectedListener {

	GoogleMap mMap;
	Circle myCircle;
	MarkerOptions markerOptions;
	EditText searchEdit;
	Button searchButton;
	LatLng latLng;
	int selectedRadius = 100;
	Spinner spinner;     
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
    	switch(pos) {
    	case 1: 
    		selectedRadius = 5;
    		break;
    	case 2: 
    		selectedRadius = 10;
    		break;
    	case 3: 
    		selectedRadius = 20;
    		break;
    	case 4: 
    		selectedRadius = 100;
    		break;
    	case 5: 
    		selectedRadius = 200;
    		break;
    	}
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }
	
	public static String TAG = "yoneko";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		Criteria criteria = new Criteria();
		LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		if( location != null) {
			Log.i(TAG, "Its not NULL!");
			mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 14.0f) );
		}
		initViews();
		setListeners();

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
		

	}
	private void setListeners() {
		spinner.setOnItemSelectedListener(this);
		mMap.setOnMapLongClickListener(this);
		mMap.setOnMarkerClickListener(this);
		//		mMap.setOnMapClickListener(this);
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
		mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				handlePoint(marker);


			}
		});

	}

	public void handlePoint(Marker marker) {
		Intent resultIntent = new Intent();
		Bundle b = new Bundle();
		b.putDouble("lon", marker.getPosition().longitude);
		b.putDouble("lat", marker.getPosition().latitude);
		resultIntent.putExtras(b);
		setResult(AddGeoFenceFragment.MAP_RESULT_CODE, resultIntent);Log.i(TAG,"Handle this");
		finish();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		handlePoint(marker);
		return false;
	}

	@Override    
	public void onMapLongClick(LatLng point) {
		Log.i(TAG,"clearing marker");
		createRadiusCircle(point);
		MarkerOptions mo = new MarkerOptions()
		.position(point)
		.title( point.latitude + ", " + point.longitude)           
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
		mMap.addMarker(mo).showInfoWindow();
		mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
		Log.i(TAG,"Location is here: " + point.latitude + ", " + point.longitude);
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
		mMap.clear();
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
			Log.i(TAG,"Clearing all markers on post execute");

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
				mMap.addMarker(markerOptions).showInfoWindow();
				// Locate the first location
				if(i==0) {
					Log.i(TAG,"scrolling to first location");
					mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
				}
			}
		}
	}
}
