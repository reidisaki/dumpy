//showcase view background color : F02173AD
package com.yoneko.areyouthereyet.update;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

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
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.targets.ViewTarget;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.yoneko.areyouthereyet.update.AddGeoFenceFragment.onEditTextClicked;
import com.yoneko.models.Prediction;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofenceList;
import com.yoneko.models.SimpleGeofenceStore;

public class MapActivity extends Activity implements OnMapLongClickListener, OnMarkerClickListener, 
onEditTextClicked,ConnectionCallbacks, OnConnectionFailedListener, OnMyLocationChangeListener, OnMyLocationButtonClickListener,
OnAddGeofencesResultListener, LocationListener, OnRemoveGeofencesResultListener, OnMapLoadedCallback, OnItemClickListener, OnMapClickListener {
	private int REQUEST_CODE = 9090;// search request code
	private static final long SECONDS_PER_HOUR = 60;
	private static final long LOCATION_UPDATE_INTERVAL = 30;
	private static final long MILLISECONDS_PER_SECOND = 1000;
	private static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;
	public static  String GEO_FENCES = "geofences";
	public static final int DIALOG_FRAGMENT = 100;
	public static String GEO_FENCE_KEY_LIST = "geoFenceList";
	private IntentFilter mIntentFilter;
	public static final float RADIUS_METER = 130;
	private static String TAG = "Reid";
	public LocationManager locationManager;
	// Holds the location client
	private LocationClient mLocationClient;
	// Stores the PendingIntent used to request geofence monitoring
	private PendingIntent mTransitionPendingIntent =null;
	private PendingIntent mRemoveIntent;

	// Defines the allowable request types.
	public enum REQUEST_TYPE {ADD, REMOVE_INTENT, REMOVE_LIST };
	public enum TRANSIENT_TYPE { TRANSIENT_ENTER, TRANSIENT_EXIT};
	public List<String> mGeofencesToRemove;
	private REQUEST_TYPE mRequestType;
	// Flag that indicates if a request is underway.
	private boolean mInProgress, mToggleInfoWindowShown = false,usedAutoComplete = false;
	private List<SimpleGeofence> drawerStringList;
	private GeofenceSampleReceiver mBroadcastReceiver;
	private Intent pendingIntent;
	private LocationRequest mLocationRequest;
	private Marker myLocationMarker;
	private Location location;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private ImageButton clearTextImage,searchButton,voiceButton,trashDrawer;
	private Button feedbackBtn,drawer_clear, helpBtn;
	private Geocoder geocoder;
	//analytic crap
	public String flurryKey = "XJRXSKKC6JFGGZP5DF68";


	LinearLayout footerView,searchBar,adView_layout, headerView;
	public String title;
	GoogleMap mMap;
	Marker currentMarker = null;
	Circle myCircle = null, newCircle =null;
	MarkerOptions markerOptions;
	AdView adView;
	TextView slide_tab_text;
	AutoCompleteTextView searchEdit;
	ImageView ic_drawer, arrow;
	FragmentManager fm;
	DrawerItemAdapter drawerAdapter;
	AddGeoFenceFragment addGeofenceFragment;
	RelativeLayout map_detail_layout,drawer_icon_layout;
	int animateSpeed = 400, animateFast = 200, _radiusChanged =20, screenWidth, screenHeight;
	SlidingUpPanelLayout slidePanelLayout;
	LatLng latLng = null;
	float EXPANDED_PERCENT =  .7f;
	boolean editable = true, isMapLoaded = false, isPanelExpanded,isArrowUp = true,navigateToMyLocation = true, isLongClick = false;
	public static String tag = "Reid";
	int selectedRadius = 75, mapOffset, appOpenNumber=1, NUM_TIMES_TO_SHOW_ADD =2, MIN_RADIUS = 50;
	Spinner spinner;
	private List<SimpleGeofence> mSimpleGeoFenceList;     
	public static boolean isActive = false;

	public void o (String s) {
		Log.i(tag,"output s: " + s);
	}
	public void onNothingSelected(AdapterView<?> parent) {
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i("Reid1", "movetoback is null? " + String.valueOf(getIntent().getExtras() == null));
		if(getIntent().getExtras() != null && getIntent().getExtras().getBoolean("moveToBack")) {
			moveTaskToBack(true);
		} else {
			Log.i("Reid1","keep the app in front as normal");
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);

		startService(new Intent(this, SafetyService.class));
		mInProgress = false;

		//		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		//			@Override
		//			public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
		//				SmsManager manager = SmsManager.getDefault();
		//				Throwable e = paramThrowable;
		//				String SMS_SENT = "ConfirmSentActivity";
		//				String SMS_DELIVERED = "DevliveredActivty";
		//				PendingIntent piSend = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_SENT), 0);
		//				PendingIntent piDelivered = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_DELIVERED), 0);
		//				
		//
		//				
		//				StackTraceElement[] arr = e.getStackTrace();
		//		        final StringBuffer report = new StringBuffer(e.toString());
		//		        report.append("--------- Stack trace ---------\n\n");
		//		        for (int i = 0; i < arr.length; i++) {
		//		            report.append( "  \n  ");
		//		            report.append(arr[i].toString());
		//		        }
		//		        // If the exception was thrown in a background thread inside
		//		        // AsyncTask, then the actual exception can be found with getCause
		//		        report.append("--------- Cause ---------\n\n");
		//		        Throwable cause = e.getCause();
		//		        if (cause != null) {
		//		            report.append(cause.toString());
		//		            arr = cause.getStackTrace();
		//		            for (int i = 0; i < arr.length; i++) {
		//		                report.append("   \n ");
		//		                report.append(arr[i].toString());
		//		            }
		//		        }
		//		        // Getting the Device brand,model and sdk verion details.
		////		        report.append("--------- Device ---------\n\n");
		////		        report.append("Brand: ");
		////		        report.append(Build.BRAND);
		////		        report.append("Device: ");
		////		        report.append(Build.DEVICE);
		////		        report.append("Model: ");
		////		        report.append(Build.MODEL);
		////		        report.append("Id: ");
		////		        report.append(Build.ID);
		////		        report.append("Product: ");
		////		        report.append(Build.PRODUCT);
		////		        report.append("--------- Firmware ---------\n\n");
		////		        report.append("SDK: ");
		////		        report.append(Build.VERSION.SDK);
		////		        report.append("Release: ");
		////		        report.append(Build.VERSION.RELEASE);
		////		        report.append("Incremental: ");
		////		        report.append(Build.VERSION.INCREMENTAL);
		//
		//				ArrayList<String> messagelist = manager.divideMessage(report.toString());
		////				manager.sendMultipartTextMessage("3233098967", null, messagelist, null, null);
		////				Log.i("Reid",output);
		//
		//		        Log.i("Reid", report.toString());
		//			}
		//		});

		//		ArrayList<String> test = null;
		//		test.add("tafasd");
		mGeofencesToRemove = new ArrayList<String>();
		mBroadcastReceiver = new GeofenceSampleReceiver();
		mIntentFilter = new IntentFilter();
		//		pendingIntent = new Intent(this,ReceiveTransitionsIntentService.class);	
		mLocationRequest = LocationRequest.create();
		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		//try using this:
		mLocationRequest.setFastestInterval(1);
		// Set the update interval to 50 seconds
		mLocationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
		mSimpleGeoFenceList = getGeoFenceFromCache(getApplicationContext()).getGeoFences();
//		mGeofenceStorage = new SimpleGeofenceStore(this);

		int resultCode =
				GooglePlayServicesUtil.
				isGooglePlayServicesAvailable(this);
		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status
			Log.d(TAG,
					"Google Play services is available.");
		}

		adView = (AdView)findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder()
		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		.addTestDevice("deviceid")
		.build();
		adView.loadAd(adRequest);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		screenWidth = size.x;
		screenHeight = size.y;
		initMap();

		geocoder = new Geocoder(getApplicationContext()) ;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		
		location = locationManager.getLastKnownLocation(bestProvider);
		fm = getFragmentManager();

		//default display size width for device

		mapOffset = (int)(screenHeight * -.25f) + 120;
		initLeftDrawer();
		initViews();
		setListeners();

		fm.beginTransaction()
		.hide(addGeofenceFragment)
		.commit();
	}
	
	private void initMap() {
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.getUiSettings().setRotateGesturesEnabled(false);
		mMap.setMyLocationEnabled(true);
		mMap.setOnMarkerClickListener(this);
		mMap.setOnMyLocationChangeListener(this); 
		mMap.setOnMyLocationButtonClickListener(this);
		mMap.setOnMapLoadedCallback(this);




		//Position LocationButton
		// Get the button view 
		//		View locationButton = ((View)findViewById(1).getParent()).findViewById(2);
		//		// and next place it, for exemple, on bottom right (as Google Maps app)
		//		RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
		//		// position on right bottom
		//		rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
		//		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		//		rlp.setMargins(0, 0, 0, 200);

	}
	private void initShowView() {

		//		l.setBackgroundColor(Color.parseColor("#FF000000"));
		//		View showcasedView = findViewById(R.id.drawer_icon_layout);
		//		ViewTarget target = new ViewTarget(showcasedView);
		//		ShowcaseView.insertShowcaseView(target, this,"Click here","See all your saved geo fences");
		int[] posXY = new int[2];
		ic_drawer.getLocationOnScreen(posXY);
		int y = posXY[1];

		Log.i("Reid1","y coordinate:" + y);
		View showcasedView2 = findViewById(R.id.ic_drawer);
		ViewTarget target2 = new ViewTarget(showcasedView2);
		ShowcaseView sv = ShowcaseView.insertShowcaseView(target2, this,getResources().getString(R.string.showcase_title), getResources().getString(R.string.showcase_message));

		sv.animateGesture(screenWidth/2, screenHeight/2-convertDiptoPix(100), screenWidth/2, (screenHeight/2)-convertDiptoPix(100));

		ShowcaseView.ConfigOptions options = sv.getConfigOptions();
		options.centerText=true;
		options.hideOnClickOutside=true;
		options.block = true;

		//		new ShowcaseView.Builder(this)
		//	    .setTarget(new ActionViewTarget(this, ActionViewTarget.Type.HOME))
		//	    .setContentTitle("ShowcaseView")
		//	    .setContentText("This is highlighting the Home button")
		//	    .hideOnTouchOutside()
		//	    .build();		
	}

	@Override
	public void onWindowFocusChanged (boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			Log.i("Reid","on windowFocusedChanged: "+appOpenNumber);
			SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
			boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
			SharedPreferences.Editor editor = wmbPreference.edit();
			// Code to run once
			if (isFirstRun)
			{
				initShowView();
				editor.putBoolean("FIRSTRUN", false);
			}

			if(appOpenNumber % NUM_TIMES_TO_SHOW_ADD == 1) {
				adView.setVisibility(View.VISIBLE);
			} else {
				adView.setVisibility(View.GONE);
			}

			editor.commit();
			LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, mIntentFilter);
			View locationButton = ((View)findViewById(1).getParent()).findViewById(2);
			// and next place it, for exemple, on bottom right (as Google Maps app)
			RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
			// position on right bottom
			rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			rlp.setMargins(0, 0, convertDiptoPix(10), convertDiptoPix(100));
		}

	}

	private void initLeftDrawer() {

		drawerStringList = mSimpleGeoFenceList;//new ArrayList<SimpleGeofence>();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		footerView =  (LinearLayout)((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_footer_view, null, false);
		headerView=  (LinearLayout)((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.drawer_header_view, null, false);
		feedbackBtn = (Button)footerView.findViewById(R.id.drawer_feedback);
		helpBtn = (Button)footerView.findViewById(R.id.drawer_help);
		mDrawerList.addFooterView(footerView);
		mDrawerList.addHeaderView(headerView);
		// Set the adapter for the list view
		drawerAdapter = new DrawerItemAdapter(this,
				R.layout.drawer_list_item, drawerStringList);
		mDrawerList.setAdapter(drawerAdapter);
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
			String selectedItemTitle = ((TextView)(view).findViewById(R.id.drawer_text)).getText().toString();
			int index = indexOfItemInGeofenceList(selectedItemTitle);
			SimpleGeofence item = mSimpleGeoFenceList.get(index);
			latLng = new LatLng(item.getLatitude(),
					item.getLongitude());
			createRadiusCircle(latLng);
			//check the View if they clicked hte text item or if they clicked the X icon.
			mDrawerLayout.closeDrawers();
		}

	}
	public int indexOfItemInGeofenceList(String title) {
		for(int i =0; i < mSimpleGeoFenceList.size(); i++) {
			if(title.equals(mSimpleGeoFenceList.get(i).getTitle())) {
				return i;
			}
		}
		return 0;
	}
	private void initViews() { 
		//		BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.ic_drawer);
		//		GroundOverlayOptions groundOverlay = new GroundOverlayOptions()
		//        .image(image)
		//        .position(new LatLng(40.714086, -74.228697), 500f)
		//        .transparency(0.5f);
		//        mMap.addGroundOverlay(groundOverlay);
		adView_layout = (LinearLayout)findViewById(R.id.adView_layout);
		searchBar = (LinearLayout)findViewById(R.id.searchBar);
		trashDrawer = (ImageButton)footerView.findViewById(R.id.drawer_trash);
		ic_drawer = (ImageView)findViewById(R.id.ic_drawer);
		arrow = (ImageView)findViewById(R.id.arrow);
		drawer_clear = (Button)footerView.findViewById(R.id.drawer_clear);
		clearTextImage = (ImageButton)findViewById(R.id.clearTextImage);
		searchButton = (ImageButton)findViewById(R.id.searchButton);
		voiceButton =(ImageButton)findViewById(R.id.voiceButton);
		// Set the list's click listener
		slide_tab_text = (TextView)findViewById(R.id.slide_tab_text);
		searchEdit =  (AutoCompleteTextView)findViewById(R.id.location_edit);
		spinner = (Spinner) findViewById(R.id.radius_spinner);
		slidePanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
		map_detail_layout = (RelativeLayout)findViewById(R.id.map_detail_layout);
		drawer_icon_layout = (RelativeLayout)findViewById(R.id.drawer_icon_layout);
		addGeofenceFragment = (AddGeoFenceFragment)getFragmentManager().findFragmentById(R.id.fragment_add_geo_fence);
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

		Prediction p = (Prediction) adapterView.getItemAtPosition(position);
		Log.i("Reid","item selected" + p.getDescription() + " latitude: " + p.getlatitude());
		searchEdit.setText(p.getDescription());
		title = p.getDescription();
		usedAutoComplete = true;
		hideKeyboard();
		if(p.getDescription().toLowerCase().startsWith("15912 s manhattan")) {
			showAnimal("joey");
		}
		if(p.getDescription().toLowerCase().startsWith("1086 s mansfield")) {
			showAnimal("lynx");
		}
		if(p.getDescription().toLowerCase().startsWith("138 asby bay")) {
			showAnimal("bailey");
		}
		new GeocoderAutoCompleteTask().execute(p);

	}
	public void clearAllGeoFences() {
		drawerStringList.clear();
		SharedPreferences sp = this.getSharedPreferences(GEO_FENCES, MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.clear();
		spe.commit();
		Log.i("Reid", "clearing list");
		//remove geo fences
		drawerAdapter.notifyDataSetChanged();
		removeGeofences(getTransitionPendingIntent());
	}
	@Override
	public void onBackPressed() {

		if(slidePanelLayout != null && slidePanelLayout.isPanelExpanded() || slidePanelLayout.isPanelAnchored()) {
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

	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, flurryKey);
	}
	private void showAddGeoFenceFragment() {

		slide_tab_text.setText("");
		arrow.setImageDrawable(getResources().getDrawable(R.drawable.down));
		fm.beginTransaction()
		.show(addGeofenceFragment)
		.commit();
		isPanelExpanded = true;
	}
	private void setListeners() {

		drawer_clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				new AlertDialog.Builder(MapActivity.this) 
				//set message, title, and icon
				.setTitle("Delete All Geofences") 
				.setMessage("Do you want to remove all geofences?") 
				//				.setIcon(R.drawable.delete)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) { 
						clearAllGeoFences();
						slidePanelLayout.collapsePanel();
						//reset items
						addGeofenceFragment.nicknameEdit.setText("");
						addGeofenceFragment.messageEdit.setText("");
						addGeofenceFragment.emailEdit.setText("");
						addGeofenceFragment.radius_seek.setProgress(75);
						searchEdit.setText("");
						mMap.clear();
						dialog.dismiss();
					}   
				})

				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create()
				.show();
			}
		});
		feedbackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "reidisaki@gmail.com", "pchung528@gmail.com" });
				intent.putExtra(Intent.EXTRA_SUBJECT, "location app feedback");
				startActivity(Intent.createChooser(intent, ""));				
			}
		});
		helpBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDrawerLayout.closeDrawer(mDrawerList);
				initShowView();
			}
		});
		arrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("Reid", "clicked on arrow");
				isLongClick = false;
				if(isArrowUp) {

					if(slidePanelLayout.isPanelAnchored()){
						o("expand panel all the way");
						slidePanelLayout.expandPanel();
					} else {
						o("expand panel half way");
						slidePanelLayout.expandPanel(.5f);
					}
				} else {
					o("collapsing panel");
					slidePanelLayout.collapsePanel();
				}
			}
		});
		trashDrawer.setOnClickListener(new OnClickListener() {
			@Override  
			public void onClick(View v) {
				new AlertDialog.Builder(MapActivity.this) 
				//set message, title, and icon
				.setTitle("Delete selected Geofences") 
				.setMessage("Do you want to remove selected geofences?") 
				//				.setIcon(R.drawable.delete)
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int whichButton) { 
						drawerStringList.size();
						ArrayList<String> geoFenceIdToRemoveList = new ArrayList<String>();
						SparseBooleanArray sbArray = mDrawerList.getCheckedItemPositions();
						o(sbArray.size() + "sparseBoolean array");
						boolean isCurrentGeofenceAffected = false;
						for(int i=drawerStringList.size()-1; i >= 0; i--){
							o("i IS: " + i);

							SimpleGeofence fence = ((SimpleGeofence)drawerAdapter.getItem(i));
							if(fence.isChecked()) {
								//								Log.i("Reid","removing item");
								drawerStringList.remove(i);
								if(addGeofenceFragment.nicknameEdit.getText().toString().equals(fence.getTitle())){
									isCurrentGeofenceAffected = true;
								}						
								geoFenceIdToRemoveList.add(fence.getId());
							} 
						}
						//if current geofence is the selected geoFence then clear out all the crap
						if(isCurrentGeofenceAffected){
							mMap.clear();
							if(myCircle != null) {
								myCircle.remove();
							}
							if(newCircle != null) {
								newCircle.remove();
							}
							clearAddGeoFenceFragment();
							addGeofenceFragment.nicknameEdit.setText("");
							searchEdit.setText("");
						}

						SimpleGeofenceList mSimpleGeofenceList = new SimpleGeofenceList(drawerStringList);
						storeJSON(mSimpleGeofenceList, getApplicationContext());
						removeGeofences(geoFenceIdToRemoveList);

						drawerAdapter.notifyDataSetChanged();
						mDrawerList.clearChoices();
					}   
				})

				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create()
				.show();
			}
		});
		searchEdit.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.places_list_item));
		searchEdit.setThreshold(4);
		searchEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if(!s.toString().equals("")) {
					//					Log.i("Reid", "show clear text");
					clearTextImage.setVisibility(View.VISIBLE);
					voiceButton.setVisibility(View.GONE);
				}  else {
					//					Log.i("Reid", "hide clear text");
					clearTextImage.setVisibility(View.GONE);
					voiceButton.setVisibility(View.VISIBLE);
				}
				//				Log.i("Reid","onAfter text changed:" + s.toString());
			}
		});
		searchEdit.setOnKeyListener(new OnKeyListener()
		{
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN)
				{
					switch (keyCode)
					{

					case KeyEvent.KEYCODE_DPAD_CENTER:
					case KeyEvent.KEYCODE_ENTER:
						onSearchEditButtonClicked();
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		voiceButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startVoiceRecognitionActivity();				
			}
		});
		searchButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				onSearchEditButtonClicked();

			}
		});
		clearTextImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				searchEdit.setText("");
			}
		});
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

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				_radiusChanged = progress + MIN_RADIUS;

				if(myCircle != null) {
					myCircle.remove();
				}

				if(newCircle != null) {
					newCircle.remove();
				}

				if(latLng != null) {
					CircleOptions circleOptions = new CircleOptions()
					.center(latLng)   //set center
					.radius(_radiusChanged)   //set radius in meters  make this configurable
					.strokeColor(Color.MAGENTA)
					.strokeWidth(5);
					addGeofenceFragment.radius_text.setText("Radius  " + _radiusChanged + "m");
					if(mMap != null && circleOptions != null) {
						newCircle = mMap.addCircle(circleOptions);
					}
				}

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
					slidePanelLayout.collapsePanel();
				}

			}
		});
		searchEdit.setOnItemClickListener(this);
		if(editable) {
			mMap.setOnMapLongClickListener(this);
			mMap.setOnMarkerClickListener(this);
			mMap.setOnMapClickListener(this);

			slidePanelLayout.setAnchorPoint(.5f);
			slidePanelLayout.setPanelSlideListener(new PanelSlideListener() {

				@Override
				public void onPanelSlide(View panel, float slideOffset) {
					slide_tab_text.setText("");
					arrow.setImageDrawable(getResources().getDrawable(R.drawable.down));
					isArrowUp =false;
				}

				@Override
				public void onPanelHidden(View panel) {
				}

				@Override
				public void onPanelExpanded(View panel) {
					showAddGeoFenceFragment();
				}

				@Override
				public void onPanelCollapsed(View panel) {

					Animation fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
					searchBar.startAnimation(fadeIn);
					fadeIn.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
							searchBar.setVisibility(View.VISIBLE);
						}

						@Override
						public void onAnimationEnd(Animation animation) {

						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});

					hideKeyboard();

					fm.beginTransaction()
					.hide(addGeofenceFragment)
					.commit();

					slide_tab_text.setText("Slide up to add a Geofence");
					arrow.setImageDrawable(getResources().getDrawable(R.drawable.up));
					isArrowUp =true;
					if(latLng != null) {
						mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng),animateSpeed, null);
					}

				}

				@Override
				public void onPanelAnchored(View panel) {
					animateCameraOffset();
					showAddGeoFenceFragment();
					Animation fadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
					searchBar.startAnimation(fadeOut);
					fadeOut.setAnimationListener(new Animation.AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							searchBar.setVisibility(View.GONE);
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
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

	protected void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(slidePanelLayout.getWindowToken(), 0);
	}
	protected void animateCameraOffset() {
		Point p = mMap.getProjection().toScreenLocation(latLng);
		p.set(p.x, p.y-mapOffset);

		CameraUpdate update = CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(p));
		mMap.animateCamera(update, animateSpeed, null);		
	}
	@Override
	public void onResume() {
		super.onResume();
		// Start loading the ad in the background.

		final LocationManager manager = (LocationManager)getSystemService(Context.LOCATION_SERVICE );
		try {
			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE) != 3) {
				displayPromptForEnablingGPS();
			} 
		}catch (SettingNotFoundException e) {
			e.printStackTrace();
		}

		if(appOpenNumber % NUM_TIMES_TO_SHOW_ADD == 1) {
			adView.setVisibility(View.VISIBLE);
		} else {
			adView.setVisibility(View.GONE);
		}
		SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		appOpenNumber = wmbPreference.getInt("numTimesAppOpened", 0);
		Log.i("Reid", "number of times app opened: " + appOpenNumber);
		SharedPreferences.Editor editor = wmbPreference.edit();
		int newOpenAppNumber = appOpenNumber+1;
		editor.putInt("numTimesAppOpened", newOpenAppNumber);
		editor.commit();

		if (adView != null) {
			adView.resume();
		}
		isActive = true;
		navigateToMyLocation = true;
	}
	protected void onSearchEditButtonClicked() {
		String location = searchEdit.getText().toString();
		if(location!=null && !location.equals("")){
			if(location.toLowerCase().startsWith("15912 s manhattan")) {
				showAnimal("joey");
			}
			if(location.toLowerCase().startsWith("1086 s mansfield")) {
				showAnimal("lynx");
			}
			if(location.toLowerCase().startsWith("138 asby bay")) {
				showAnimal("bailey");
			}
			//if it's from the placesApi, then just use the address data back otherwise users didn't select the drop down list
			new GeocoderTask().execute(location);
		}				
	} 

	private void showAnimal(String animal) {
		AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);

		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View layout = inflater.inflate(R.layout.animal, null);
		ImageView image = (ImageView) layout.findViewById(R.id.animal_image);
		TextView text = (TextView) layout.findViewById(R.id.animal_text);
		if(animal.equals("joey")){
			image.setImageDrawable(getResources().getDrawable(R.drawable.joey));	
		} 
		if(animal.equals("lynx")){
			image.setImageDrawable(getResources().getDrawable(R.drawable.lynx));
		}
		if(animal.equals("bailey")){
			image.setImageDrawable(getResources().getDrawable(R.drawable.bailey));
		}
		text.setText(animal + " lives on!");

		final AlertDialog alert = imageDialog.create();
		alert.setView(layout,0,0,0,0);

		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setCanceledOnTouchOutside(true);
		alert.show();

		image.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		}) ;

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

			animateCameraOffset();

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
		isActive = false;
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
	public void handlePoint(Marker marker) {
		slidePanelLayout.expandPanel(.5f);
	}


	@Override
	public boolean onMarkerClick(Marker marker) {
		o("Clicked marker");
		if (marker.equals(myLocationMarker)) {
			Uri uri = Uri.parse("smsto:");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			Geocoder geo = new Geocoder(getApplicationContext());
			String currentLocationText = "";
			List<Address> addressList;
			try {
				addressList = geo.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
				if(addressList.size() > 0) {
					Address address = addressList.get(0);
					currentLocationText =  address.getAddressLine(0) + " " + address.getLocality() + " " + (address.getPostalCode() == null ? "" : address.getPostalCode());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			intent.putExtra("sms_body", currentLocationText + "\n\n http://maps.google.com/?q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
			startActivityForResult(intent, 1234);

			//Send out text message to someone who your location
		} else {
			handlePoint(marker);
		}
		return false;
	}

	@Override    
	public void onMapLongClick(LatLng point) {
		latLng = point;
		isLongClick = true;
		createRadiusCircle(point);
	}
	private void startVoiceRecognitionActivity()
	{
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
		startActivityForResult(intent, REQUEST_CODE);
	}

	/**
	 * Handle the results from the voice recognition activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == REQUEST_CODE  && resultCode == RESULT_OK)
		{
			// Populate the wordsList with the String values the recognition engine thought it heard
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			if(matches.size() >0) {
				searchEdit.setText(matches.get(0));
				onSearchEditButtonClicked();
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	public void addMarker(LatLng latLng) {

		int radius;
		Geocoder geo = new Geocoder(getApplicationContext());
		try {
			List<Address> addressList = geo.getFromLocation(latLng.latitude, latLng.longitude, 1);
			if(addressList.size() > 0) {
				Address address = addressList.get(0);
				Log.i("Reid","thoroughfare: " + address.getThoroughfare());
				Log.i("Reid","premises:" + address.getPremises());
				Log.i("Reid","locality:" + address.getLocality());
				if(!usedAutoComplete) {
					title =  address.getAddressLine(0) + " " + address.getLocality() + " " + (address.getPostalCode() == null ? "" : address.getPostalCode());
					searchEdit.setText(title);
				}
				addGeofenceFragment.nicknameEdit.setText(title);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		usedAutoComplete = false;
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
			addGeofenceFragment.emailEdit.setText(fence.getPhoneDisplay());
			addGeofenceFragment.emailOrPhone = fence.getEmailPhone();
			addGeofenceFragment.enter_exit.check(fence.getTransitionType() == 1 ? R.id.radio_enter : R.id.radio_exit);
			addGeofenceFragment.radius_seek.setProgress(radius);
			addGeofenceFragment.radius_text.setText("Radius " + radius + "m");

		} else {
			//clear the drawer data to be empty except the title
			clearAddGeoFenceFragment();
		}

		currentMarker = mMap.addMarker(mo);
		boolean panelWillExpand = true;
		animateToLocation(panelWillExpand);
	}
	private void clearAddGeoFenceFragment() {
		addGeofenceFragment.messageEdit.setText("");
		addGeofenceFragment.enter_exit.check(R.id.radio_enter);
		addGeofenceFragment.radius_seek.setProgress(75);
		addGeofenceFragment.emailEdit.setText("");		
	}
	@Override
	public void onMapClick(LatLng point) {
		slidePanelLayout.collapsePanel();
		if(currentMarker != null) {

			if(mToggleInfoWindowShown) {
				currentMarker.hideInfoWindow();
				mToggleInfoWindowShown = false;
			} else {
				currentMarker.showInfoWindow();
				mToggleInfoWindowShown = true;
			}

		}
	}
	public void createRadiusCircle(LatLng latLng) {
		if(myCircle != null) {
			myCircle.remove();
		}
		if(newCircle != null) {
			newCircle.remove();
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
		//				CameraUpdate update = CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(p));
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 15.0f);

		mMap.animateCamera(update, animateSpeed, cameraCallBack);
	}

	private class GeocoderAutoCompleteTask extends AsyncTask<Prediction, Void, Prediction>{
		@Override
		protected Prediction doInBackground(Prediction... p) {

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json");
				sb.append("?key=" + PlacesAutoCompleteAdapter.API_KEY);
				//	        sb.append("&components=country:uk");
				sb.append("&placeid=" + p[0].getPlaceId());
				//				Log.i("Reid","API URL: " + sb.toString());
				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				InputStreamReader in = new InputStreamReader(conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
			} catch (MalformedURLException e) {
			} catch (IOException e) {
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj;
			try {

				jsonObj = new JSONObject(jsonResults.toString());
				JSONObject latLngObj = jsonObj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
				p[0].setlatitude(latLngObj.getDouble("lat"));
				p[0].setLongitude(latLngObj.getDouble("lng"));
			} catch (JSONException e) {
				e.printStackTrace();
			}


			return p[0];
		}
		protected void onPostExecute(Prediction p) {
			Address a = new Address(null);
			a.setLatitude(p.getlatitude());
			a.setLongitude(p.getLongitude());
			setMarkerFromSearch(p.getDescription(), a);
			hideKeyboard();
		}
	}

	//Prompt user for gps if they have it disabled
	public void displayPromptForEnablingGPS()
	{
		final AlertDialog.Builder builder =
				new AlertDialog.Builder(this);
		final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
		final String message = "Enable GPS and set to High Accuracy";

		builder.setMessage(message)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int id) {
				startActivity(new Intent(action));
				d.dismiss();
			}
		})
		.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface d, int id) {
				d.cancel();
				finish();
			}
		});
		builder.create().show();
	}


	private class GeocoderTask extends AsyncTask<String, Void, List<Address>>{

		@Override
		protected List<Address> doInBackground(String... locationName) {
			// Creating an instance of Geocoder class
			Geocoder geocoder = new Geocoder(getBaseContext());
			List<Address> addresses = null;
			Log.i("Reid","selected location: " + locationName[0]);
			try {
				// Getting a maximum of 3 Address that matches the input text
				addresses = geocoder.getFromLocationName(URLEncoder.encode(locationName[0], "utf8"), 5);

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
			hideKeyboard();
			// Clears all the existing markers on the map

			// Adding Markers on Google Map for each matching address
			for(int i=0;i<addresses.size() && addresses != null;i++){

				Address address = (Address) addresses.get(i);
				// Creating an instance of GeoPoint, to display in Google Map

				String addressText = String.format("%s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getCountryName());
				setMarkerFromSearch(addressText, address);
			}
		}
	}

	public void setMarkerFromSearch(String addressText, Address address) {
		latLng = new LatLng(address.getLatitude(), address.getLongitude());
		markerOptions = new MarkerOptions();
		markerOptions.position(latLng);
		markerOptions.title(addressText);
		if(latLng != null) {
			createRadiusCircle(latLng);
		} else {
			Toast.makeText(this, "Can't find: " + addressText, Toast.LENGTH_LONG).show();
		}
	}
	@Override
	public void editTextClicked() {
		slidePanelLayout.expandPanel(1f);
	}
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		mDrawerLayout.isDrawerOpen(mDrawerList);
		//		menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onItemSaved(SimpleGeofence oldItem, SimpleGeofence newItem, List<SimpleGeofence> newList, boolean isUpdate) {
		slidePanelLayout.collapsePanel();
		//update the cached version of hte list.
		Log.i("Reid","newItem: " +newItem.getTitle());
		//add new item, remove old item from simpleGeoFence and from drawer
		if(oldItem != null) {
			for(int i=0; i < drawerStringList.size(); i++) {
				SimpleGeofence updateItem = drawerStringList.get(i);
				//item exists update
				if(updateItem.getTitle().equals(oldItem.getTitle())) {
					drawerStringList.set(i,oldItem);
				}
			}
		} else {
			drawerStringList.add(newItem);
		}
		mSimpleGeoFenceList = drawerStringList;
		//		drawerStringList.remove(drawerStringList.size()-2);
		drawerAdapter.notifyDataSetChanged();
		mDrawerList.setItemChecked(drawerStringList.indexOf(newItem.getTitle()), true);
		addGeofences();
	}

	public String getNickName() {
		return title;
	}

	protected void onStop() {
		// Disconnecting the client invalidates it.
		FlurryAgent.onEndSession(this);
		navigateToMyLocation = false;
		if (mLocationClient !=null && mLocationClient.isConnected()) {
			/*
			 * Remove location updates for a listener.
			 * The current Activity is the listener, so
			 * the argument is "this".
			 */
			//dont need
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		} else {
			Log.i(TAG,"client is not connected()");
		}
		super.onStop();
	}

	public void storeJSON(SimpleGeofenceList list, Context context)
	{
		//clear out the stuff first
		SharedPreferences sp = context.getSharedPreferences(GEO_FENCES, MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		Gson gson = new Gson();
		String jsonString = gson.toJson(list);
		spe.putString(GEO_FENCE_KEY_LIST, jsonString);
		spe.commit();
	}

	/*
	 * 
	 * 	Do we even need to show Long/lat editable fields? just hide it completely. 	 
	 * 	 need to create an alarm manager to be able to enable /disable when the geo fences are being sent (every friday at 4pm - 10 pm
	 */

	public SimpleGeofenceList getGeoFenceFromCache(Context context)
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

	//GEO fence is triggered
	private PendingIntent getTransitionPendingIntent() {


		// Create an Intent pointing to the IntentService

		//              Intent intent = new Intent(context, ReceiveTransitionsIntentService.class);
		/*
		 * Return a PendingIntent to start the IntentService.
		 * Always create a PendingIntent sent to Location Services
		 * with FLAG_UPDATE_CURRENT, so that sending the PendingIntent
		 * again updates the original. Otherwise, Location Services
		 * can't match the PendingIntent to requests made with it.
		 */

		// If the PendingIntent already exists
		if (null != mTransitionPendingIntent) {

			// Return the existing intent
			return mTransitionPendingIntent;

			// If no PendingIntent exists
		} else {
			Intent intent = new Intent("com.yoneko.areyouthereyet.ACTION_RECEIVE_GEOFENCE");
			return PendingIntent.getBroadcast(
					this,
					0,
					intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
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



		if (!mInProgress) {
			mLocationClient = new LocationClient(this, this, this);
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
			mLocationClient.disconnect();
			mLocationClient.connect();
			Log.v(TAG,"tryign to reconnect");
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		// Request a connection from the client to Location Services
		Log.v(TAG," For reals connected");
		//		Location location = mLocationClient.getLastLocation();
		//		LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
		//		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
		//		    mMap.animateCamera(cameraUpdate);

		mLocationClient.requestLocationUpdates(mLocationRequest,this);
		Log.v(TAG,"onConnected request type: " + mRequestType);
		switch (mRequestType) {
		case ADD :
			// Send a request to add the current geofences
			ArrayList<Geofence> geoFences = new ArrayList<Geofence>();
			mTransitionPendingIntent =
					getTransitionPendingIntent();
			//			for(int i=0; i<mSimpleGeoFenceList.size();i++) {
			//add items to geoFences;
			try {
				geoFences.add(mSimpleGeoFenceList.get(mSimpleGeoFenceList.size() -1).toGeofence());
			} catch (IllegalArgumentException e) {
				Log.v(TAG,"illegal long/ lat combination not found...");	
			}
			//			}

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
			if(geoFences.size() > 0) {
				Log.i("Reid","adding all geofences to GOOGLE");

				mLocationClient.addGeofences(
						geoFences, mTransitionPendingIntent, this);
			}

			break;
		case REMOVE_INTENT :
			Log.i(TAG,"Removing all geo fences for reals on google");
			if(mRemoveIntent != null) {
				mLocationClient.removeGeofences(
						mRemoveIntent, this);
			}
			break;
		case REMOVE_LIST :
			Log.i(TAG,"Removing CERTAIN not all. geo fences for reals on google");
			if(mGeofencesToRemove != null && mGeofencesToRemove.size() > 0 ) {
				mLocationClient.removeGeofences(
						mGeofencesToRemove, this);
			}
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
			Log.v(TAG,"GEO FENCE FAILURE YOU SUCK" + statusCode);
			// If adding the geofences failed
			/*
			 * Report errors here.
			 * You can log the error using Log.e() or update
			 * the UI.
			 */
		}
		// Turn off the in progress flag and disconnect the client
		mLocationClient.disconnect();
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
			mRemoveIntent = requestIntent;
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
		//		Log.v(TAG,"Location CHanged: " + String.valueOf(arg0.getLongitude()) +","+String.valueOf(arg0.getLatitude())) 	;

	}

	@Override
	public void onRemoveGeofencesByPendingIntentResult(int statusCode,
			PendingIntent pendingIntent) {

		// If removing the geofences was successful
		if (statusCode == LocationStatusCodes.SUCCESS) {
			o("SUCCESS removing geo fences" + statusCode);
			/*
			 * Handle successful removal of geofences here.
			 * You can send out a broadcast intent or update the UI.
			 * geofences into the Intent's extended data.
			 */
		} else {
			o("failure removing geo fences" + statusCode);
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
	////END MERGE

	@Override
	public void onMyLocationChange(Location _location) {
		//		Log.i("Reid","location changed!");
		location = _location;
		if(location != null && navigateToMyLocation) {
			navigateToMyLocation = false;
			LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 14.0f));

			//cool animation but kinda slow.
			//			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 14.0f));
		}
		// Remove the old marker object
		if(myLocationMarker != null) {
			myLocationMarker.remove(); 
		}

		// Add a new marker object at the new (My Location dot) location
		myLocationMarker = mMap.addMarker(new MarkerOptions() 
		.position(new LatLng(location.getLatitude(),location.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))) ;
	}
	@Override
	public boolean onMyLocationButtonClick() {
		if(location != null) {
			Point p = mMap.getProjection().toScreenLocation(new LatLng(location.getLatitude(), location.getLongitude()));
			boolean panelExpanded = true;
			if(panelExpanded) {
				p.set(p.x, p.y-mapOffset);
			} 

			mMap.animateCamera(CameraUpdateFactory.newLatLng(mMap.getProjection().fromScreenLocation(p)));
		}
		boolean returnValue =  (slidePanelLayout.isPanelAnchored() || slidePanelLayout.isPanelExpanded()) ? true : false;
		Log.i("Reid","Return value onMyLocationButtonClick  " + returnValue);
		return returnValue;
	}
	@Override
	public void onMapLoaded() {
		isMapLoaded = true; 	
	}
	
	///helper methods
	public static float getDensity(Context context){
		float scale = context.getResources().getDisplayMetrics().density;       
		return scale;
	}

	public  int convertDiptoPix(int dip){
		float scale = getDensity(this);
		return (int) (dip * scale + 0.5f);
	}
	public  int convertPixtoDip(int pixel){
		float scale = getDensity(this);
		return (int)((pixel - 0.5f)/scale);
	}

	public  boolean isTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout
				& Configuration.SCREENLAYOUT_SIZE_MASK)
				>= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
}
