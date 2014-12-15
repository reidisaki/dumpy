package com.yoneko.models;



import java.util.List;

import com.google.android.gms.location.Geofence;

public class SimpleGeofence {
    // Instance variables
    private final String mId;
    private final double mLatitude;
    private final double mLongitude;
    private final float mRadius;
    private boolean checked; 
    private long lastSent;
    private boolean shouldSend;
    private boolean isActive = true; //default to active
    public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public fencetype getFenceType() {
		return fenceType;
	}
	public void setFenceType(fencetype fenceType) {
		this.fenceType = fenceType;
	}
	public fencetype fenceType = fencetype.ONE_TIME; //default to recurring
    public enum fencetype {ONE_TIME, RECURRING, SCHEDULED};
    private List<PhoneContact> phoneContacts; //send to multiple contacts if needed
    public List<PhoneContact> getPhoneContacts() {
		return phoneContacts;
	}
	public void setPhoneContacts(List<PhoneContact> phoneContacts) {
		this.phoneContacts = phoneContacts;
	}
	public long getLastSent() {
		return lastSent;
	}
	public void setLastSent(long lastSent) {
		this.lastSent = lastSent;
	}
	public void setChecked(boolean b) {
    	checked = b;
	}
    public boolean isChecked() {
    	return checked;
    }
	private String phoneDisplay;
    public String getPhoneDisplay() {
		return phoneDisplay;
	}
	public void setPhoneDisplay(String phoneDisplay) {
		this.phoneDisplay = phoneDisplay;
	}
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
        String title,
        String phoneDisplay, long lastSent) {
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
    this.phoneDisplay = phoneDisplay;
    this.lastSent = lastSent;
    this.isActive = true;
    this.fenceType = fenceType.SCHEDULED;
}
public SimpleGeofence(String string) {
	this.title = string;
	this.mRadius = 0;
    this.mId = null;
    this.mLatitude = 0;
    this.mLongitude = 0;
    this.mExpirationDuration = 0;
    this.mTransitionType = 0;
    this.message = "";
    this.emailPhone = "";
    this.phoneDisplay = "";
    this.lastSent = -1;
    this.isActive = true;
    this.fenceType = fenceType.SCHEDULED;
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
public boolean isShouldSend() {
	return shouldSend;
}
public void setShouldSend(boolean shouldSend) {
	this.shouldSend = shouldSend;
}
}