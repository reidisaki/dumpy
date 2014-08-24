package com.yoneko.models;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private long mExpirationDuration;
    private int mTransitionType;
    private String message;
    private String emailPhone;
    private String title;

public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
/**
 * @param geofenceId The Geofence's request ID
 * @param latitude Latitude of the Geofence's center.
 * @param longitude Longitude of the Geofence's center.
 * @param radius Radius of the geofence circle.
 * @param expiration Geofence expiration duration
 * @param transition Type of Geofence transition.
 */
public SimpleGeofence(
        String geofenceId,
        double latitude,
        double longitude,
        float radius,
        long expiration,
        int transition,
        String message,
        String email,
        String title) {
    // Set the instance fields from the constructor
    this.mId = geofenceId;
    this.mLatitude = latitude;
    this.mLongitude = longitude;
    this.mRadius = radius;
    this.mExpirationDuration = expiration;
    this.mTransitionType = transition;
    this.message = message;
    this.emailPhone = email;
    this.title = title;
}
// Instance field getters
public String getId() {
    return mId;
}
public String getEmailPhone() {
    return emailPhone;
}
public String getMessage() {
    return message;
}
public double getLatitude() {
    return mLatitude;
}
public double getLongitude() {
    return mLongitude;
}
public float getRadius() {
    return mRadius;
}
public long getExpirationDuration() {
    return mExpirationDuration;
}
public int getTransitionType() {
    return mTransitionType;
}
/**
 * Creates a Location Services Geofence object from a
 * SimpleGeofence.
 *
 * @return A Geofence object
 */
public Geofence toGeofence() {
    // Build a new Geofence object
    return new Geofence.Builder()
            .setRequestId(getId())
            .setTransitionTypes(mTransitionType)
            .setCircularRegion(
                    getLatitude(), getLongitude(), getRadius())
            .setExpirationDuration(mExpirationDuration)
            .build();
}
}