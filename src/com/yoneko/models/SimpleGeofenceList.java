package com.yoneko.models;

import java.util.ArrayList;

import com.google.android.gms.location.Geofence;

public class SimpleGeofenceList {
	// Instance variables
	private ArrayList<SimpleGeofence> mGeofences; 

	/**
	 * @param geofenceId The Geofence's request ID
	 * @param latitude Latitude of the Geofence's center.
	 * @param longitude Longitude of the Geofence's center.
	 * @param radius Radius of the geofence circle.
	 * @param expiration Geofence expiration duration
	 * @param transition Type of Geofence transition.
	 */
	public SimpleGeofenceList(ArrayList<SimpleGeofence> geoFences) {
		this.mGeofences = geoFences;
	}
	// Instance field getters
	public ArrayList<SimpleGeofence> getGeoFences() {
		return this.mGeofences;
	}
	public void setGeofences(ArrayList<SimpleGeofence> geofences) {
		this.mGeofences = geofences;
	}
}