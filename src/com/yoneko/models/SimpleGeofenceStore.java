package com.yoneko.models;

import com.google.android.gms.location.Geofence;
import com.yoneko.models.SimpleGeofence.fencetype;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SimpleGeofenceStore {
	// Keys for flattened geofences stored in SharedPreferences
	public static final String KEY_LATITUDE =
			"com.example.android.geofence.KEY_LATITUDE";
	public static final String KEY_LONGITUDE =
			"com.example.android.geofence.KEY_LONGITUDE";
	public static final String KEY_RADIUS =
			"com.example.android.geofence.KEY_RADIUS";
	public static final String KEY_EXPIRATION_DURATION =
			"com.example.android.geofence.KEY_EXPIRATION_DURATION";
	public static final String KEY_TRANSITION_TYPE =
			"com.example.android.geofence.KEY_TRANSITION_TYPE";
	public static final String KEY_EMAIL =
			"com.example.android.geofence.KEY_EMAIL";
	public static final String KEY_MESSAGE =
			"com.example.android.geofence.KEY_MESSAGE";
	public static final String KEY_NICKNAME =
			"com.example.android.geofence.KEY_NICKNAME";
	public static final String KEY_DISPLAY_PHONE =
			"com.example.android.geofence.KEY_DISPLAY_PHONE";
	public static final String KEY_FENCE_TYPE =
			"com.example.android.geofence.KEY_FENCE_TYPE";
	public static final String KEY_FENCE_ACTIVE =
			"com.example.android.geofence.KEY_FENCE_ACTIVE";

	// The prefix for flattened geofence keys
	public static final String KEY_PREFIX =
			"com.example.android.geofence.KEY";
	/*
	 * Invalid values, used to test geofence storage when
	 * retrieving geofences
	 */
	public static final long INVALID_LONG_VALUE = -999l;
	public static final float INVALID_FLOAT_VALUE = -999.0f;
	public static final int INVALID_INT_VALUE = -999;
	public static final int INVALID_STRING_VALUE = -999;
	// The SharedPreferences object in which geofences are stored
	private final SharedPreferences mPrefs;
	// The name of the SharedPreferences
	private static final String SHARED_PREFERENCES =
			"SharedPreferences";
	// Create the SharedPreferences storage with private access only
	public SimpleGeofenceStore(Context context) {
		mPrefs =
				context.getSharedPreferences(
						SHARED_PREFERENCES,
						Context.MODE_PRIVATE);
	}
	/**
	 * Returns a stored geofence by its id, or returns null
	 * if it's not found.
	 *
	 * @param id The ID of a stored geofence
	 * @return A geofence defined by its center and radius. See
	 */
	public SimpleGeofence getGeofence(String id) {
		/*
		 * Get the latitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		double lat = mPrefs.getFloat(
				getGeofenceFieldKey(id, KEY_LATITUDE),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the longitude for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		double lng = mPrefs.getFloat(
				getGeofenceFieldKey(id, KEY_LONGITUDE),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the radius for the geofence identified by id, or
		 * INVALID_FLOAT_VALUE if it doesn't exist
		 */
		float radius = mPrefs.getFloat(
				getGeofenceFieldKey(id, KEY_RADIUS),
				INVALID_FLOAT_VALUE);
		/*
		 * Get the expiration duration for the geofence identified
		 * by id, or INVALID_LONG_VALUE if it doesn't exist
		 */
		long expirationDuration = mPrefs.getLong(
				getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				INVALID_LONG_VALUE);
		/*
		 * Get the transition type for the geofence identified by
		 * id, or INVALID_INT_VALUE if it doesn't exist
		 */
		int transitionType = mPrefs.getInt(
				getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
				INVALID_INT_VALUE);
		
		String email = mPrefs.getString(getGeofenceFieldKey(id, KEY_EMAIL),"NOT FOUND");
		String nickname = mPrefs.getString(getGeofenceFieldKey(id, KEY_NICKNAME),"NOT FOUND");
		String message = mPrefs.getString(getGeofenceFieldKey(id,KEY_MESSAGE), "NOT FOUND");
		String displayPhone = mPrefs.getString(getGeofenceFieldKey(id, KEY_DISPLAY_PHONE),"NOT FOUND");
		fencetype fenceType = fencetype.values()[mPrefs.getInt(getGeofenceFieldKey(id, KEY_FENCE_TYPE), 0)]; //TODO: research how to cast ints to enums but this should be fine 
		boolean isFenceActive  = mPrefs.getBoolean(getGeofenceFieldKey(id, KEY_FENCE_ACTIVE), true);
		// If none of the values is incorrect, return the object
//		if (				
//				lat != GeofenceUtils.INVALID_FLOAT_VALUE &&
//				lng != GeofenceUtils.INVALID_FLOAT_VALUE &&
//				radius != GeofenceUtils.INVALID_FLOAT_VALUE &&
//				expirationDuration !=
//				GeofenceUtils.INVALID_LONG_VALUE &&
//				transitionType != GeofenceUtils.INVALID_INT_VALUE) {

			// Return a true Geofence object
			return new SimpleGeofence(
					id, lat, lng, radius, expirationDuration,
					transitionType, message, email, nickname, displayPhone,-1, fenceType);
			// Otherwise, return null.
//		} else {
//			return null;
//		}
	}
	/**
	 * Save a geofence.
	 * @param geofence The SimpleGeofence containing the
	 * values you want to save in SharedPreferences
	 */
	public void setGeofence(String id, SimpleGeofence geofence) {
		/*
		 * Get a SharedPreferences editor instance. Among other
		 * things, SharedPreferences ensures that updates are atomic
		 * and non-concurrent
		 */
		Editor editor = mPrefs.edit();
		// Write the Geofence values to SharedPreferences
		editor.putFloat(
				getGeofenceFieldKey(id, KEY_LATITUDE),
				(float) geofence.getLatitude());
		editor.putFloat(
				getGeofenceFieldKey(id, KEY_LONGITUDE),
				(float) geofence.getLongitude());
		editor.putFloat(
				getGeofenceFieldKey(id, KEY_RADIUS),
				geofence.getRadius());
		editor.putString(
				getGeofenceFieldKey(id, KEY_EMAIL),
				geofence.getEmailPhone());
		editor.putString(
				getGeofenceFieldKey(id, KEY_MESSAGE),
				geofence.getMessage());
		editor.putLong(
				getGeofenceFieldKey(id, KEY_EXPIRATION_DURATION),
				geofence.getExpirationDuration());
		editor.putInt(
				getGeofenceFieldKey(id, KEY_TRANSITION_TYPE),
				geofence.getTransitionType());
		editor.putString(
				getGeofenceFieldKey(id, KEY_DISPLAY_PHONE),
				geofence.getPhoneDisplay());
		
		// Commit the changes
		editor.commit();
	}
	public void clearGeofence(String id) {
		/*
		 * Remove a flattened geofence object from storage by
		 * removing all of its keys
		 */
		Editor editor = mPrefs.edit();
		editor.remove(getGeofenceFieldKey(id, KEY_LATITUDE));
		editor.remove(getGeofenceFieldKey(id, KEY_LONGITUDE));
		editor.remove(getGeofenceFieldKey(id, KEY_RADIUS));
		editor.remove(getGeofenceFieldKey(id, KEY_EMAIL));
		editor.remove(getGeofenceFieldKey(id, KEY_MESSAGE));
		editor.remove(getGeofenceFieldKey(id, KEY_DISPLAY_PHONE));
		editor.remove(getGeofenceFieldKey(id,
				KEY_EXPIRATION_DURATION));
		editor.remove(getGeofenceFieldKey(id, KEY_TRANSITION_TYPE));
		editor.commit();
	}
	/**
	 * Given a Geofence object's ID and the name of a field
	 * (for example, KEY_LATITUDE), return the key name of the
	 * object's values in SharedPreferences.
	 *
	 * @param id The ID of a Geofence object
	 * @param fieldName The field represented by the key
	 * @return The full key name of a value in SharedPreferences
	 */
	private String getGeofenceFieldKey(String id,
			String fieldName) {
		return KEY_PREFIX + "_" + id + "_" + fieldName;
	}

}