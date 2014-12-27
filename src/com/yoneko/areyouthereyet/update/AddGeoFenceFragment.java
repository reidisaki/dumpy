package com.yoneko.areyouthereyet.update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.maps.model.LatLng;
import com.yoneko.areyouthereyet.update.debug.R;
import com.yoneko.models.PhoneContact;
import com.yoneko.models.SimpleGeofence;
import com.yoneko.models.SimpleGeofence.fencetype;
import com.yoneko.models.SimpleGeofenceList;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass. Activities that
 * contain this fragment must implement the
 * {@link AddGeoFenceFragment.onDialogDismissed} interface to handle interaction
 * events. Use the {@link AddGeoFenceFragment#newInstance} factory method to
 * create an instance of this fragment.
 * 
 */
public class AddGeoFenceFragment extends DialogFragment {
	Button mapButton;
	EditText latEdit, lonEdit, radiusEdit, messageEdit, nicknameEdit;
	AutoCompleteTextView emailEdit;
	RadioGroup enter_exit;
	TextView radius_text;
	SeekBar radius_seek;
	LinearLayout contact_button_layout;
	Switch toggle_switch;
	boolean enter = true, one_time = true;
	int radius = 150;
	public String TAG = "Reid";
	public final static int MAP_RESULT_CODE = 99;
	private ArrayAdapter<String> adapter;

	// Store contacts values in these arraylist
	public static ArrayList<String> phoneValueArr = new ArrayList<String>(),
			nameValueArr = new ArrayList<String>();
	List<PhoneContact> contacts;
	EditText toNumber = null;
	String toNumberValue = "", emailOrPhone = "";

	HashMap<String, PhoneContact> contactMap;
	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";
	double latitude, longitude;
	// TODO: Rename and change types of parameters
	private String mParam1, title, mParam2;

	private onEditTextClicked mListener;
	private SimpleGeofenceList cachedList;
	private float radiusPercentage = 1.1f;
	public RadioGroup fence_type;

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

	private static final String[] PROJECTION = new String[] {
			ContactsContract.Contacts._ID,
			ContactsContract.Contacts.DISPLAY_NAME,
			ContactsContract.Contacts.HAS_PHONE_NUMBER,
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.LABEL };

	private void readContactData() {

		try {

			/*********** Reading Contacts Name And Number **********/

			ContentResolver cr = getActivity().getContentResolver();

			// Query to get contact name
			Cursor cur = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					PROJECTION, null, null, null);

			// If data data found in contacts
			if (cur.getCount() > 0) {

				Log.i("AutocompleteContacts", "Reading   contacts........");

				// //edit
				// final int contactIdIndex =
				// cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
				// final int displayNameIndex =
				// cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				// final int emailIndex =
				// cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
				// long contactId;
				// String displayName, address;
				// while (cursor.moveToNext()) {
				// contactId = cursor.getLong(contactIdIndex);
				// displayName = cursor.getString(displayNameIndex);
				// address = cursor.getString(emailIndex);
				//
				// end edit
				int contactId;
				final int contactIdIndex = cur
						.getColumnIndex(ContactsContract.Contacts._ID);
				final int displayNameIndex = cur
						.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				final int phoneNumberIndex = cur
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				final int phoneTypeIndex = cur
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
				final int customLabelIndex = cur
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);
				final int hasPhoneNumberIndex = cur
						.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);

				while (cur.moveToNext()) {

					// Check contact have phone number
					if (cur.getLong(hasPhoneNumberIndex) > 0) {
						int phonetype = cur.getInt(phoneTypeIndex);
						String customLabel = cur.getString(customLabelIndex);
						String phoneNumbers = cur.getString(phoneNumberIndex);
						String phoneLabel = (String) ContactsContract.CommonDataKinds.Phone
								.getTypeLabel(this.getResources(), phonetype,
										customLabel);
						String name = cur.getString(displayNameIndex);
						// Log.e(TAG, "Phone Number: " + phoneNumbers +
						// " Selected Phone Label: " + phoneLabel);

						// Sometimes get multiple data
						// Get Phone number
						String phoneNumber = ""
								+ cur.getString(phoneNumberIndex);

						phoneNumber = phoneNumber.replace("(", "")
								.replace(")", "").replace("-", "")
								.replace(".", "").replace(" ", "").toString();
						// Add contacts names to adapter
						// adapter.add(phoneNumber);
						adapter.add(name + "(" + phoneLabel + ")");
						adapter.add(phoneNumber + "|" + name + "(" + phoneLabel
								+ ")");
						// Add ArrayList names to adapter
						phoneValueArr.add(phoneNumber);
						nameValueArr.add(name + "(" + phoneLabel + ")");
					} // End if

				} // End while loop

			} // End Cursor value check
			cur.close();

		} catch (Exception e) {
			Log.i("AutocompleteContacts", "Exception : " + e);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView addGeoFenceView = (ScrollView) inflater.inflate(
				R.layout.fragment_add_geo_fence, container, false);
		// getDialog().setTitle("Add Geofence");
		emailEdit = (AutoCompleteTextView) addGeoFenceView
				.findViewById(R.id.email_edit);
		radius_text = (TextView) addGeoFenceView.findViewById(R.id.radius_text);
		enter_exit = (RadioGroup) addGeoFenceView.findViewById(R.id.enter_exit);
		messageEdit = (EditText) addGeoFenceView
				.findViewById(R.id.message_edit);
		nicknameEdit = (EditText) addGeoFenceView
				.findViewById(R.id.nickname_edit);
		radius_seek = (SeekBar) addGeoFenceView
				.findViewById(R.id.radius_seekBar);
		contact_button_layout = (LinearLayout) addGeoFenceView
				.findViewById(R.id.contact_button_layout);
		toggle_switch = (Switch)addGeoFenceView.findViewById(R.id.toggle_switch);
		toggle_switch.setChecked(true);
		enter_exit
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						switch (checkedId) {
						case R.id.radio_enter:
							enter = true;
							break;
						case R.id.radio_exit:
							enter = false;
							break;

						}
					}
				});
		fence_type = (RadioGroup)addGeoFenceView.findViewById(R.id.fence_type_radiogroup);
		fence_type
		.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radio_one_time:
					one_time = true;
					break;
				case R.id.radio_reoccuring:
					one_time = false;
					break;

				}
			}
		});
		OnFocusChangeListener expandPanelListener = new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {

				if (hasFocus) {
					Log.i("Reid", "View has focus");
					mListener.editTextClicked();

				}
			}
		};
		nicknameEdit.setOnFocusChangeListener(expandPanelListener);
		messageEdit.setOnFocusChangeListener(expandPanelListener);
		emailEdit.setOnFocusChangeListener(expandPanelListener);

		contacts = new ArrayList<PhoneContact>();
		contactMap = new HashMap<String, PhoneContact>();
		// Create adapter
		adapter = new ArrayAdapter<String>(getActivity(),
				R.layout.simple_contact_dropdown, new ArrayList<String>());
		emailEdit.setThreshold(1);
		// Set adapter to AutoCompleteTextView
		emailEdit.setAdapter(adapter);
		emailEdit.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
				case EditorInfo.IME_ACTION_DONE:
					String phoneNumber = v.getText().toString();
					phoneNumber = phoneNumber.replaceAll("[*a-zA-Z]", "");
					addPhoneContactButton(phoneNumber, phoneNumber);
					break;
				case EditorInfo.IME_ACTION_NEXT:
					// next stuff
					break;
				}
				return false;
			}
		});

		emailEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView, View view,
					int index, long arg3) {
				// Get Array index value for selected name
				String s = adapterView.getItemAtPosition(index).toString();
				Log.i("Reid", "OnItemClick string: " + s);

				// check if this is a phone number search or a name search
				if (!Character.isDigit(s.charAt(0))) {
					s = s.substring(0, s.lastIndexOf(")") + 1);
				} else {
					s = s.substring(s.lastIndexOf("|") + 1, s.length());
				}
				int i = nameValueArr.indexOf(s);
				// Get Phone Number
				emailOrPhone = phoneValueArr.get(i);

				Log.i("Reid", "phone number: " + emailOrPhone);
				String outputString = nameValueArr.get(i).toString();
				// String addComma = emailEdit.getText().toString().equals("") ?
				// "" : ",";
				// toPhone = emailEdit.getT + outputString;

				// now we will add a button the layout to show new people
				addPhoneContactButton(emailOrPhone, outputString);
				// emailEdit.setText(outputString);

			}
		});
		// emailEdit.setOnItemSelectedListener(this);
		// emailEdit.setOnItemClickListener(this);

		// Read contact data and add data to ArrayAdapter
		// ArrayAdapter used by AutoCompleteTextView
		readContactData();
		((Button) addGeoFenceView.findViewById(R.id.save_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						saveGeoFence(v);
					}
				});

		// Log.i("Reid","is add geofence view null " +
		// String.valueOf(addGeoFenceView == null));
		// addGeoFenceView.setOnClickListener(new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Log.i("Reid","view clicked");
		// switch (v.getId()) {
		// case R.id.map_button:
		// mapButtonClicked(v);
		// break;
		// case R.id.save_button:
		// saveGeoFence(v);
		// break;
		//
		// default:
		// Log.i("Reid", "Unknown: " + v.getId());
		// break;
		// }
		// }
		// });
		return addGeoFenceView;
	}

	protected void addPhoneContactButton(String phoneNumber, String displayName) {
		// strip out all alpha characters in the number

		if (!phoneNumber.trim().equals("")) {
			PhoneContact phoneContact = new PhoneContact(displayName,
					phoneNumber, displayName);
			addContactAsButtonToLayout(displayName, phoneContact);
		}
		emailEdit.setText("");
	}

	protected void addContactAsButtonToLayout(String formattedName,
			PhoneContact phone) {
		if (contactMap.get(phone.getNumber()) == null) {
			Button b = new Button(getActivity());

			b.setCompoundDrawablesWithIntrinsicBounds(null, null,
					getResources().getDrawable(R.drawable.clear), null);
			// b.setBackground(getResources().getDrawable(R.id.clearTextImage));

			b.setText(formattedName);
			b.setTag(phone.getNumber());
			b.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));
			contactMap.put(phone.getNumber(), phone);
			b.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					contact_button_layout.removeView(v);
					contactMap.remove(v.getTag());
				}
			});
			contact_button_layout.addView(b, 0);
		}
	}

	protected SimpleGeofence getItemInGeoFenceListByLatLng(LatLng _latLng) {
		SimpleGeofence returnItem = null;
		// SimpleGeofenceList cachedList =
		// MainActivity.getGeoFenceFromCache(getActivity());

		for (SimpleGeofence i : cachedList.getGeoFences()) {
			if (i.getLatitude() == _latLng.latitude
					&& i.getLongitude() == _latLng.longitude) {
				returnItem = i;
				break;
			}
		}
		return returnItem;
	}

	protected SimpleGeofence getItemInGeoFenceList(SimpleGeofence item) {
		SimpleGeofence returnItem = null;
		// cachedList = MainActivity.getGeoFenceFromCache(getActivity());
		for (int i = 0; i < cachedList.getGeoFences().size(); i++) {
			SimpleGeofence currentGeofence = cachedList.getGeoFences().get(i);
			if (item.getLatitude() == currentGeofence.getLatitude()
					&& item.getLongitude() == currentGeofence.getLongitude()
					&& item.getTitle().equals(currentGeofence.getTitle())) {
				returnItem = currentGeofence;
				// exists update the item
				if (cachedList.getGeoFences() != null) {
					// Toast.makeText(getActivity(), "updating existingItem"
					// ,Toast.LENGTH_SHORT).show();
					cachedList.getGeoFences().set(i, item);
					// MainActivity.storeJSON(cachedList, getActivity());
				}
				;
				break;
			}
		}
		return returnItem;
	}

	// need to update item in the list and save it.
	// populate the item if there is a simpleGeoFence.

	protected void saveGeoFence(View v) {
		// save geoFence to mainActivity save json method
		//TODO: save the onetime/recurring active/inactive stuffs
		boolean isUpdate = false, errors = false;
		;

		List<SimpleGeofence> list = new ArrayList<SimpleGeofence>();
		String geoFenceId, message, email, nickname, displayPhone, errorMessage = "";

		float r;
		long expiration;
		int transition;

		String phoneEditTextValue = emailEdit.getText().toString();
		if (phoneEditTextValue.length() > 0
				&& Character.isDigit(phoneEditTextValue.charAt(0))) {
			addPhoneContactButton(phoneEditTextValue, phoneEditTextValue);
		}

		r = radius_seek.getProgress();
		Log.i("Reid", "radius in saveGeoFence: " + r);
		expiration = Geofence.NEVER_EXPIRE;
		transition = enter ? Geofence.GEOFENCE_TRANSITION_ENTER
				: Geofence.GEOFENCE_TRANSITION_EXIT;
		Log.i("Reid", "Transition type: " + transition);
		message = messageEdit.getText().toString();
		nickname = nicknameEdit.getText().toString();
		if (nickname.equals("")) {
			nickname = ((MapActivity) getActivity()).getNickName();
		}
		displayPhone = emailEdit.getText().toString();
		Log.i("Reid", "Nickname is: " + nickname);
		LatLng latLng = ((MapActivity) getActivity()).getLatLng();

		if (!emailEdit.getText().toString().contains("(")) {
			emailOrPhone = emailEdit.getText().toString();
		}
		if (latLng == null) {
			Toast.makeText(getActivity(),
					"Longitude and latitude need to be real values :( ",
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		SimpleGeofence geofence = new SimpleGeofence(
				MapActivity.createGeoFenceId(nickname, latLng.latitude,
						latLng.longitude), latLng.latitude, latLng.longitude,
				(r + MapActivity.MIN_RADIUS) * radiusPercentage, expiration,
				transition, message, emailOrPhone, nickname, displayPhone, -1, (one_time ? fencetype.ONE_TIME : fencetype.RECURRING));
		geofence.setActive(toggle_switch.isChecked());
		cachedList = MapActivity.getGeoFenceFromCache(getActivity());
		// geoFence replaces oldfence in the cache but you might want to handle
		// stuff with the old item ie: update drawers and lists in the activity
		contacts.clear();
		contacts.addAll(contactMap.values());
		Log.i("Reid", "Saving contacts size: " + contacts.size());
		geofence.setPhoneContacts(contacts);
		SimpleGeofence oldfence = getItemInGeoFenceList(geofence);

		// Adding a new item
		if (oldfence == null) {
			// Toast.makeText(getActivity(), "adding new Item"
			// ,Toast.LENGTH_SHORT).show();
			cachedList.getGeoFences().add(geofence);
		} else {
			isUpdate = true;

		}

		// update cache regardless if this is an update or a new item
		MapActivity.storeJSON(cachedList, getActivity());
		if (contacts.size() == 0) {
			errors = true;
			errorMessage = "Please select at least one contact\n";
		}
		if (geofence.getMessage().equals("")) {
			errors = true;
			errorMessage += "message can't be blank";
		}
		if (!errors) {
			// Toast.makeText(getActivity(), "Size of cache : "+
			// MainActivity.getGeoFenceFromCache(getActivity()).getGeoFences().size()
			// + " Number saved:  " +
			// geofence.getEmailPhone(),Toast.LENGTH_SHORT).show();
			Log.i("Reid","saving geo fence is it null?: " + (geofence == null));
			mListener.onItemSaved(oldfence, geofence,
					cachedList.getGeoFences(), isUpdate);
		} else {
			Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT)
					.show();
		}
		// mListener.dialogDismissed();

	}

	// TODO: Rename method, update argument and hook method into UI event
	public void onButtonPressed(Uri uri) {
		if (mListener != null) {
			// mListener.dialogDismissed();
		}
	}

	public void mapButtonClicked(View v) {
		Log.i("Reid",
				"map button clicked startactivity for result and populate the form.");
		Intent mapIntent = new Intent(getActivity(), MapActivity.class);
		startActivityForResult(mapIntent, MAP_RESULT_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case MAP_RESULT_CODE:
			latitude = data.getDoubleExtra("lat", 0.0);
			longitude = data.getDoubleExtra("lon", 0.0);
			// latEdit.setText(String.valueOf(data.getDoubleExtra("lat", 0.0)));
			// lonEdit.setText(String.valueOf(data.getDoubleExtra("lon", 0.0)));
			radius = data.getIntExtra("radius", 150);
			radius_text.setText("Radius " + radius + "meters");
			break;
		}
		Log.i("Reid", "onActivityForResult");
	}

	// this connects the interface to the activity and fragment.
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

	public void onRadioButtonClicked(View v) {

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
		public void editTextClicked();

		public void onItemSaved(SimpleGeofence oldFence,
				SimpleGeofence newFence, List<SimpleGeofence> newList,
				boolean isUpdate);
	}

}
