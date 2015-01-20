package com.yoneko.areyouthereyet.update;

import java.util.HashMap;

import android.app.Application;

import com.yoneko.areyouthereyet.update.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

public class AreYouThereYet extends Application {
	private static final String PROPERTY_ID = "UA-54368949-1";


//	public enum TrackerName {
//		APP_TRACKER, // Tracker used only in this app.
//		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
//		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
//	}
//
//	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
//
//	synchronized Tracker getTracker(TrackerName trackerId) {
//		if (!mTrackers.containsKey(trackerId)) {
//
//			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
//			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
//					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
//							: analytics.newTracker(R.xml.global_tracker);
//					mTrackers.put(trackerId, t);
//
//		}
//		return mTrackers.get(trackerId);
//	}

	public AreYouThereYet() {
		super();
	}
}
