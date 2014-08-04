package com.yoneko.models;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.location.Geofence;

public class SimpleGeofenceList {
	// Instance variables
	private List<SimpleGeofence> mGeofences; 

	/**
	 * @param geofenceId The Geofence's request ID
	 * @param latitude Latitude of the Geofence's center.
	 * @param longitude Longitude of the Geofence's center.
	 * @param radius Radius of the geofence circle.
	 * @param expiration Geofence expiration duration
	 * @param transition Type of Geofence transition.
	 */
	public SimpleGeofenceList(List<SimpleGeofence> geoFences) {
		this.mGeofences = geoFences;
	}
	// Instance field getters
	public List<SimpleGeofence> getGeoFences() {
		return this.mGeofences;
	}
	public void setGeofences(List<SimpleGeofence> geofences) {
		this.mGeofences = geofences;
	}
	public void add(SimpleGeofence g) {
		this.mGeofences.add(g);
	}
}