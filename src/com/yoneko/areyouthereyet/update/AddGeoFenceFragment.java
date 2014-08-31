package com.yoneko.areyouthereyet.update;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofenceList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link AddGeoFenceFragment.onDialogDismissed} interface to handle
 * interaction events. Use the {@link AddGeoFenceFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class AddGeoFenceFragment extends DialogFragment  {
	Button mapButton, saveButton;
	EditText latEdit, lonEdit, radiusEdit,messageEdit,emailEdit,nicknameEdit;

	RadioGroup enter_exit;
	TextView radius_text;
	boolean enter = true;
	int radius = 100;
	public String TAG = "Reid";
	public final static int MAP_RESULT_CODE  = 99;


	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	double latitude, longitude;
	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private onEditTextClicked mListener;

	/**
	 * Use this factory method to create a new instance of this fragment using
	 * the provided parameters.
	 * 
	 * @param param1
	 *            Parameter 1.
	 * @param param2
	 *            Parameter 2.
	 * @return A new instance of fragment AddGeoFenceFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static AddGeoFenceFragment newInstance(String param1, String param2) {
		AddGeoFenceFragment fragment = new AddGeoFenceFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);

		return fragment;
	}

	public AddGeoFenceFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView addGeoFenceView = (ScrollView)inflater.inflate(R.layout.fragment_add_geo_fence, container,false);
		//		getDialog().setTitle("Add Geofence");
		emailEdit = (EditText)addGeoFenceView.findViewById(R.id.email_edit);
		latEdit = (EditText)addGeoFenceView.findViewById(R.id.lat_edit);
		lonEdit = (EditText)addGeoFenceView.findViewById(R.id.lon_edit);
		radius_text = (TextView)addGeoFenceView.findViewById(R.id.radius_text);
		enter_exit = (RadioGroup)addGeoFenceView.findViewById(R.id.enter_exit);
		messageEdit = (EditText)addGeoFenceView.findViewById(R.id.message_edit);
		nicknameEdit= (EditText)addGeoFenceView.findViewById(R.id.nickname_edit);
		enter_exit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.radio_enter:
					enter = true;
					break;
				case R.id.radio_exit:
					enter = false;
					break;

				}
			}
		});
		nicknameEdit.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
					mListener.editTextClicked();

				}
			}
		});
		// Inflate the layout for this fragment
		//		((Button)addGeoFenceView.findViewById(R.id.map_button)).setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				mapButtonClicked(v);
		//			}
		//		});

		((Button)addGeoFenceView.findViewById(R.id.save_button)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				saveGeoFence(v);
			}
		});

		//		Log.i("Reid","is add geofence view null " + String.valueOf(addGeoFenceView == null));
		//		addGeoFenceView.setOnClickListener(new View.OnClickListener() {
		//			
		//			@Override
		//			public void onClick(View v) {
		//				Log.i("Reid","view clicked");
		//				switch (v.getId()) {
		//				case R.id.map_button:
		//					mapButtonClicked(v);
		//					break;
		//				case R.id.save_button:
		//					saveGeoFence(v);
		//					break;
		//
		//				default:
		//					Log.i("Reid", "Unknown: " + v.getId());
		//					break;
		//				}
		//			}
		//		});
		return addGeoFenceView;
	}

	protected void saveGeoFence(View v) {
		//save geoFence to mainActivity save json method

		SimpleGeofenceList cachedList = MainActivity.getGeoFenceFromCache(getActivity().getApplicationContext());

		List<SimpleGeofence> list = new ArrayList<SimpleGeofence>();
		String geoFenceId,message,email,nickname;

		float r;
		long expiration;
		int transition;


		r = Float.valueOf(radius);
		expiration = Geofence.NEVER_EXPIRE;
		transition = enter ? Geofence.GEOFENCE_TRANSITION_ENTER : Geofence.GEOFENCE_TRANSITION_EXIT;
		message =  messageEdit.getText().toString();
		email =  emailEdit.getText().toString();
		nickname = nicknameEdit.getText().toString();
		Log.i("Reid","Nickname is: " + nickname);
		LatLng latLng = ((MapActivity)getActivity()).getLatLng();
		if(latLng == null) {
			Toast.makeText(getActivity().getApplicationContext(), "Longitude and latitude need to be real values :( " ,Toast.LENGTH_SHORT).show();
			return;
		}
		SimpleGeofence geofence = new SimpleGeofence(MainActivity.createGeoFenceId(latLng.latitude,latLng.longitude), latLng.latitude, latLng.longitude, r, expiration, transition, message, email, nickname);

		if(cachedList == null) {
			Toast.makeText(getActivity().getApplicationContext(), "cached list was null" ,Toast.LENGTH_SHORT).show();
			list.add(geofence); 
			cachedList= new SimpleGeofenceList(list);
		} else {
			cachedList.add(geofence);
		}
		MainActivity.storeJSON(cachedList, getActivity().getApplicationContext());

		Toast.makeText(getActivity().getApplicationContext(), "Size of cache : " + MainActivity.getGeoFenceFromCache(getActivity().getApplicationContext()).getGeoFences().size(),Toast.LENGTH_SHORT).show();
		//		mListener.dialogDismissed();
		
		Toast.makeText(getActivity().getApplicationContext(), "SAVED!! " ,Toast.LENGTH_SHORT).show();
		mListener.onItemSaved();
	}


	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			//			mListener.dialogDismissed();
		}
	}

	public void mapButtonClicked(View v){
		Log.i("Reid","map button clicked startactivity for result and populate the form.");
		Intent mapIntent= new Intent(getActivity(), MapActivity.class);
		startActivityForResult(mapIntent, MAP_RESULT_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(resultCode) {
		case MAP_RESULT_CODE:
			latitude = data.getDoubleExtra("lat", 0.0);
			longitude = data.getDoubleExtra("lon", 0.0);
			//			latEdit.setText(String.valueOf(data.getDoubleExtra("lat", 0.0)));
			//			lonEdit.setText(String.valueOf(data.getDoubleExtra("lon", 0.0)));
			radius = data.getIntExtra("radius", 100);
			radius_text.setText("Radius: " + radius + "meters");
			break;
		}
		Log.i("Reid","onActivityForResult");
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (onEditTextClicked) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnFragmentInteractionListener");
		}
	}

	public void onRadioButtonClicked (View v) {

	}
	@Override
	public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	/**
	 * This interface must be implemented by activities that contain this
	 * fragment to allow an interaction in this fragment to be communicated to
	 * the activity and potentially other fragments contained in that activity.
	 * <p>
	 * See the Android Training lesson <a href=
	 * "http://developer.android.com/training/basics/fragments/communicating.html"
	 * >Communicating with Other Fragments</a> for more information.
	 */
	public interface onEditTextClicked {
		// TODO: Update argument type and name
		public void editTextClicked();
		public void onItemSaved();
	}

}
