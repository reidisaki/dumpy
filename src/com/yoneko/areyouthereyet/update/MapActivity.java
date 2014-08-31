package com.yoneko.areyouthereyet.update;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
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
import com.yoneko.areyouthereyet.update.AddGeoFenceFragment.onEditTextClicked;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofenceList;

public class MapActivity extends Activity implements OnMapLongClickListener, OnMarkerClickListener, OnItemSelectedListener, onEditTextClicked {


	private String[] mPlanetTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	GoogleMap mMap;
	Marker currentMarker = null;
	Circle myCircle = null, newCircle =null;
	MarkerOptions markerOptions;
	AdView adView;
	TextView slide_tab_text;
	EditText searchEdit;
	ImageView ic_drawer;
	Button searchButton;
	FragmentManager fm;
	AddGeoFenceFragment addGeofenceFragment;
	RelativeLayout map_detail_layout,drawer_icon_layout;
	int animateSpeed = 800, animateFast = 200, _radiusChanged =20;
	SlidingUpPanelLayout slidePanelLayout;
	LatLng latLng = null;
	float EXPANDED_PERCENT =  .7f;
	boolean editable = true;
	public static String tag = "Reid";
	int selectedRadius = 100, mapOffset;;
	Spinner spinner;
	protected boolean isPanelExpanded;
	private List<SimpleGeofence> mSimpleGeoFenceList;     
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
		mMap.getUiSettings().setRotateGesturesEnabled(false);
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


		//default display size width for device
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		mapOffset = (int)(height * -.25f) + 60;
		initLeftDrawer();
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


	private void initLeftDrawer() {
		SimpleGeofenceList listObj  = MainActivity.getGeoFenceFromCache(getApplicationContext());
		mSimpleGeoFenceList = listObj.getGeoFences();
		int geoFenceSize = mSimpleGeoFenceList.size();
		String[] drawerStringArray = new String[geoFenceSize];
		for(int i=0; i < geoFenceSize; i++) {
			drawerStringArray[i] = mSimpleGeoFenceList.get(i).getTitle();
		}

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, drawerStringArray));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle("areyou there yet clsoed");
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle("are you ther eyet open");
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};		
	}
	private  class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			//check the View if they clicked hte text item or if they clicked the X icon.
			mDrawerLayout.closeDrawers();
			latLng = new LatLng(mSimpleGeoFenceList.get(position).getLatitude(),
					mSimpleGeoFenceList.get(position).getLongitude());
			createRadiusCircle(latLng);

		}

	}
	private void initViews() {
		//		BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.ic_drawer);
		//		GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
		//        .image(image)
		//        .position(new LatLng(40.714086, -74.228697), 500f)
		//        .transparency(0.5f);



		//        mMap.addGroundOverlay(groundOverlay);



		ic_drawer = (ImageView)findViewById(R.id.ic_drawer);

		// Set the list's click listener
		slide_tab_text = (TextView)findViewById(R.id.slide_tab_text);
		searchEdit =  (EditText)findViewById(R.id.location_edit);
		searchButton = (Button)findViewById(R.id.btn_find);
		spinner = (Spinner) findViewById(R.id.radius_spinner);
		slidePanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		map_detail_layout = (RelativeLayout)findViewById(R.id.map_detail_layout);
		drawer_icon_layout = (RelativeLayout)findViewById(R.id.drawer_icon_layout);
		addGeofenceFragment = (AddGeoFenceFragment)getFragmentManager().findFragmentById(R.id.fragment_add_geo_fence);


		if(!editable) {
			searchEdit.setFocusable(false); searchEdit.setClickable(false);
			searchButton.setFocusable(false); searchButton.setClickable(false);
//			spinner.setFocusable(false); spinner.setClickable(false);
//			spinner.setSelection(getSelectedPositionInSpinnerByValue(selectedRadius));
		}
	}
	@Override
	public void onBackPressed() {

		if(slidePanelLayout != null && slidePanelLayout.isPanelExpanded() || slidePanelLayout.isPanelAnchored()) {
			////			slidePanelLayout.expandPanel(.5f);
			//			slidePanelLayout.setAnchorPoint(.5f);
			//			slidePanelLayout.anchorPanel();
			//			Log.i("Reid","half expand");
			//		} else if (slidePanelLayout.isPanelAnchored()) {
			slidePanelLayout.collapsePanel();
			Log.i("Reid","closed");
		}
		else {
			super.onBackPressed();
		}
	}
	public LatLng getLatLng() {
		return latLng;
	}
	private void showAddGeoFenceFragment() {
		if(latLng != null) {
			boolean panelWillExpand = true;
			animateToLocation(panelWillExpand);
		}
		slide_tab_text.setText("");
		fm.beginTransaction()
		.show(addGeofenceFragment)
		.commit();
		isPanelExpanded = true;
	}
	private void setListeners() {
		
		addGeofenceFragment.radius_seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(newCircle != null) {
					newCircle.remove();
				}
				
				CircleOptions circleOptions = new CircleOptions()
				.center(latLng)   //set center
				.radius(_radiusChanged)   //set radius in meters  make this configurable
				.fillColor(0x408A2BE2) 
				.strokeColor(Color.MAGENTA)
				.strokeWidth(5);

				newCircle = mMap.addCircle(circleOptions);				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				_radiusChanged = progress;
				if(myCircle != null) {
					myCircle.remove();
				}
				if(newCircle != null) {
					newCircle.remove();
				}
				CircleOptions circleOptions = new CircleOptions()
				.center(latLng)   //set center
				.radius(_radiusChanged)   //set radius in meters  make this configurable
				.strokeColor(Color.MAGENTA)
				.strokeWidth(5);
				addGeofenceFragment.radius_text.setText("Radius: " + _radiusChanged + "m");
				newCircle = mMap.addCircle(circleOptions);
				
			}
		});
		ic_drawer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mDrawerLayout.isDrawerOpen(mDrawerList)){
					mDrawerLayout.closeDrawer(mDrawerList);
				} else {
					mDrawerLayout.openDrawer(mDrawerList);
				}

			}
		});
		searchEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(slidePanelLayout.isPanelExpanded()) {
					Log.i("Reid","collapsing panel closing keyboard");
					slidePanelLayout.collapsePanel();
				}

			}
		});
		if(editable) {
			spinner.setOnItemSelectedListener(this);
			mMap.setOnMapLongClickListener(this);
			mMap.setOnMarkerClickListener(this);
			//		mMap.setOnMapClickListener(this);

			slidePanelLayout.setAnchorPoint(.5f);
			slidePanelLayout.setPanelSlideListener(new PanelSlideListener() {

				@Override
				public void onPanelSlide(View panel, float slideOffset) {
					slide_tab_text.setText("");
					Log.i("Reid","onPanelSlide: " + slideOffset);
				}

				@Override
				public void onPanelHidden(View panel) {
					Log.i("Reid","panel is hidden");
				}

				@Override
				public void onPanelExpanded(View panel) {
					Log.i("Reid","onPanelExpanded 332");
					showAddGeoFenceFragment();
				}

				@Override
				public void onPanelCollapsed(View panel) {
					Log.i("Reid","panel is collapsed");
					  
					InputMethodManager imm = (InputMethodManager)getSystemService(
					      Context.INPUT_METHOD_SERVICE);
					
					imm.hideSoftInputFromWindow(slidePanelLayout.getWindowToken(), 0);
					
					fm.beginTransaction()
					.hide(addGeofenceFragment)
					.commit();

					slide_tab_text.setText("Slide up to add a Geofence");
					if(latLng != null) {
						mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
					}

				}

				@Override
				public void onPanelAnchored(View panel) {
					Log.i("Reid","onPanelAnchored 352");
					showAddGeoFenceFragment();
				}
			});
			searchButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Getting user input location
					String location = searchEdit.getText().toString();
					location = location.equals("") ? "9453 Vollmerhausen drive, 21046" : "";
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

	public CancelableCallback cameraCallBack = new CancelableCallback() {

		@Override
		public void onFinish() {
			
			if(!slidePanelLayout.isPanelExpanded() || slidePanelLayout.isPanelHidden()) {
				map_detail_layout.setVisibility(View.VISIBLE);
				slidePanelLayout.expandPanel(.5f);
			}
		}

		@Override
		public void onCancel() {
			if(!slidePanelLayout.isPanelExpanded() || slidePanelLayout.isPanelHidden()) {
				map_detail_layout.setVisibility(View.VISIBLE);
				slidePanelLayout.expandPanel(.5f);
			}
		}
	};

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

		slidePanelLayout.expandPanel(.5f);
		//		Intent resultIntent = new Intent();
		//		Bundle b = new Bundle();
		//		b.putDouble("lon", marker.getPosition().longitude);
		//		b.putDouble("lat", marker.getPosition().latitude);
		//		b.putInt("radius", selectedRadius);
		//		resultIntent.putExtras(b);
		//		setResult(AddGeoFenceFragment.MAP_RESULT_CODE, resultIntent);
		//		finish();
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
		int radius;
		Geocoder geo = new Geocoder(getApplicationContext());
		try {
			List<Address> addressList = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
			if(addressList.size() > 0) {
				Address address = addressList.get(0);
				Log.i("Reid","thoroughfare: " + address.getThoroughfare());
				Log.i("Reid","premises:" + address.getPremises());
				Log.i("Reid","locality:" + address.getLocality());
				title =  address.getAddressLine(0) + " " + address.getLocality() + " " + (address.getPostalCode() == null ? "" : address.getPostalCode());
				addGeofenceFragment.nicknameEdit.setText(title);
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

		//Check if the geofence item is in the cache/saved list by latLng, if it is populate the fragment_add_geo_fence
		SimpleGeofence fence = addGeofenceFragment.getItemInGeoFenceListByLatLng(latLng);
		//populate data drawer
		if(fence != null) {
			radius = (int)fence.getRadius();
			addGeofenceFragment.nicknameEdit.setText(fence.getTitle());
			addGeofenceFragment.messageEdit.setText(fence.getMessage());
			addGeofenceFragment.emailEdit.setText(fence.getEmailPhone());
			addGeofenceFragment.enter_exit.check(fence.getTransitionType() == 1 ? R.id.radio_enter : R.id.radio_enter);
			addGeofenceFragment.radius_seek.setProgress(radius);
			addGeofenceFragment.radius_text.setText(  radius + "m");
			
		} else {
			//clear the drawer data to be empty except the title
			addGeofenceFragment.messageEdit.setText("");
			addGeofenceFragment.radius_seek.setProgress(100);
			addGeofenceFragment.emailEdit.setText("");
		}
		
		currentMarker = mMap.addMarker(mo);
		boolean panelWillExpand = true;
		animateToLocation(panelWillExpand);
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
		.radius(_radiusChanged)   //set radius in meters  make this configurable
		.fillColor(0x408A2BE2) 
		.strokeColor(Color.MAGENTA)
		.strokeWidth(5);

		myCircle = mMap.addCircle(circleOptions);
	}

	public void animateToLocation(boolean panelExpanded) {
		Point p = mMap.getProjection().toScreenLocation(latLng);
		if(panelExpanded) {
			p.set(p.x, p.y-mapOffset);
		} 

		mMap.animateCamera(CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(p)), animateSpeed, cameraCallBack);
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
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
			// Clears all the existing markers on the map

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size() && addresses != null;i++){

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
				//				if(i==0) {
				//					boolean panelWillExpand = true;
				//					animateToLocation(panelWillExpand);
				//					
				//				}
			}
		}
	}
	@Override
	public void editTextClicked() {
		Log.i("Reid","expand panel all the way ");
		slidePanelLayout.expandPanel(1f);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		//		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onItemSaved() {

		slidePanelLayout.collapsePanel();
	}
}
