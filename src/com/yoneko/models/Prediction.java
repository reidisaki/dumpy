package com.yoneko.models;

public class Prediction {

	String mDescription;
	String mPlaceId;
	double mlatitude;
	double mLongitude;
	
	public Prediction() {};
	public String getDescription() {
		return mDescription;
	}
	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	public String getPlaceId() {
		return mPlaceId;
	}
	public void setPlaceId(String mPlaceId) {
		this.mPlaceId = mPlaceId;
	}
	public double getlatitude() {
		return mlatitude;
	}
	public void setlatitude(double mlatitude) {
		this.mlatitude = mlatitude;
	}
	public double getLongitude() {
		return mLongitude;
	}
	public void setLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}
	public Prediction(String description, String placeid) {
		this.mDescription = description;
		this.mPlaceId = placeid;
	}
}
