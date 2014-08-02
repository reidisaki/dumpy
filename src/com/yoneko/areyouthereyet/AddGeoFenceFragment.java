package com.yoneko.areyouthereyet;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link AddGeoFenceFragment.OnFragmentInteractionListener} interface to handle
 * interaction events. Use the {@link AddGeoFenceFragment#newInstance} factory
 * method to create an instance of this fragment.
 * 
 */
public class AddGeoFenceFragment extends DialogFragment  {
	Button mapButton, saveButton;
	EditText latEdit, lonEdit, radiusEdit,messageEdit;
	RadioButton enter, exit;
	RadioGroup enter_exit;
	TextView radius_text;
	int radius = 100;
	public String TAG = "Reid";
	public final static int MAP_RESULT_CODE  = 99;


	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	private OnFragmentInteractionListener mListener;

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
		TableLayout addGeoFenceView = (TableLayout)inflater.inflate(R.layout.fragment_add_geo_fence, container,false); 
		latEdit = (EditText)addGeoFenceView.findViewById(R.id.lat_edit);
		lonEdit = (EditText)addGeoFenceView.findViewById(R.id.lon_edit);
		radius_text = (TextView)addGeoFenceView.findViewById(R.id.radius_text);
		enter_exit = (RadioGroup)addGeoFenceView.findViewById(R.id.enter_exit);
		enter_exit.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId) {
				case R.id.radio_enter:
					Log.i(TAG,"Enter clicked ");
					break;
				case R.id.radio_exit:
					Log.i(TAG,"Exit clicked");
					break;

				}
			}
		});
		// Inflate the layout for this fragment
		((Button)addGeoFenceView.findViewById(R.id.map_button)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mapButtonClicked(v);
			}
		});
		
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
		// TODO Auto-generated method stub
		Log.i("Reid","save thsi junks now");
		
	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			mListener.onFragmentInteraction(uri);
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
			latEdit.setText(String.valueOf(data.getDoubleExtra("lat", 0.0)));
			lonEdit.setText(String.valueOf(data.getDoubleExtra("lon", 0.0)));
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
			mListener = (OnFragmentInteractionListener) activity;
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
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		public void onFragmentInteraction(Uri uri);
	}

}
